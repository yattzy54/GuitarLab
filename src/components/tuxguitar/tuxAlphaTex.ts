import { SongTabScore, TabTrackInfo, AdvancedMeasure, AdvancedBeat, AdvancedNote, StemDuration } from '../../types/tabPlayer';
import { midiToHz } from '../../data/defaultTunings';
import { playGuitarPluck, playDrumHit } from '../../audio/guitarSynth';

// MIDI Base Note values for common tunings (high string to low string)
export const TUNING_PRESETS: Record<string, { label: string; notes: string[]; midis: number[] }> = {
  'standard_e': {
    label: 'Standard E (E A D G B E)',
    notes: ['E4', 'B3', 'G3', 'D3', 'A2', 'E2'],
    midis: [64, 59, 55, 50, 45, 40],
  },
  'drop_d': {
    label: 'Drop D (D A D G B E)',
    notes: ['E4', 'B3', 'G3', 'D3', 'A2', 'D2'],
    midis: [64, 59, 55, 50, 45, 38],
  },
  'drop_c': {
    label: 'Drop C (C G C F A D)',
    notes: ['D4', 'A3', 'F3', 'C3', 'G2', 'C2'],
    midis: [62, 57, 53, 48, 43, 36],
  },
  'half_step_down': {
    label: 'Eb / D# Standard',
    notes: ['Eb4', 'Bb3', 'Gb3', 'Db3', 'Ab2', 'Eb2'],
    midis: [63, 58, 54, 49, 44, 39],
  },
  'seven_string_standard': {
    label: '7-String Standard (B E A D G B E)',
    notes: ['E4', 'B3', 'G3', 'D3', 'A2', 'E2', 'B1'],
    midis: [64, 59, 55, 50, 45, 40, 35],
  },
  'bass_standard': {
    label: 'Bass Standard 4-String (E A D G)',
    notes: ['G2', 'D2', 'A1', 'E1'],
    midis: [43, 38, 33, 28],
  },
  'bass_five_string': {
    label: 'Bass 5-String (B E A D G)',
    notes: ['G2', 'D2', 'A1', 'E1', 'B0'],
    midis: [43, 38, 33, 28, 23],
  },
};

export const INSTRUMENT_PRESETS = [
  { id: 'dist_guitar', name: 'Distortion Guitar', category: 'guitar', midiProgram: 29 },
  { id: 'od_guitar', name: 'Overdriven Guitar', category: 'guitar', midiProgram: 29 },
  { id: 'clean_guitar', name: 'Electric Guitar (Clean)', category: 'guitar', midiProgram: 27 },
  { id: 'acoustic_guitar', name: 'Acoustic Guitar (Steel)', category: 'guitar', midiProgram: 25 },
  { id: 'electric_bass', name: 'Electric Bass (Finger)', category: 'bass', midiProgram: 33 },
  { id: 'pick_bass', name: 'Electric Bass (Pick)', category: 'bass', midiProgram: 34 },
  { id: 'drums', name: 'Standard Drum Kit', category: 'percussion', midiProgram: 0 },
  { id: 'piano', name: 'Acoustic Grand Piano', category: 'piano', midiProgram: 0 },
];

export const DURATION_VALUES: Record<StemDuration, number> = {
  w: 4.0,
  h: 2.0,
  q: 1.0,
  e: 0.5,
  s: 0.25,
  t: 0.125,
};

export const DURATION_MAP: Record<StemDuration, string> = {
  w: '1',
  h: '2',
  q: '4',
  e: '8',
  s: '16',
  t: '32',
};

/**
 * Converts internal SongTabScore to AlphaTex string
 */
export function convertSongScoreToAlphaTex(
  song: SongTabScore,
  activeTrackOnly: boolean = false,
  selectedTrackId?: string
): string {
  let tex = `\\title "${(song.title || 'Untitled Tab').replace(/"/g, '')}"\n`;
  if (song.artist) {
    tex += `\\artist "${song.artist.replace(/"/g, '')}"\n`;
  }
  tex += `\\tempo ${song.defaultTempo || 120}\n.\n`;

  const tracksToConvert =
    activeTrackOnly && selectedTrackId
      ? song.tracks.filter((t) => t.id === selectedTrackId)
      : song.tracks;

  for (const track of tracksToConvert) {
    const trackName = (track.name || 'Guitar').replace(/"/g, '');
    tex += `\\track "${trackName}"\n`;

    const inst = (track.instrument || '').toLowerCase();
    if (inst.includes('bass')) {
      tex += `\\instrument 34\n`;
    } else if (inst.includes('distort') || inst.includes('overdrive')) {
      tex += `\\instrument 29\n`;
    } else if (inst.includes('acoustic')) {
      tex += `\\instrument 25\n`;
    } else if (inst.includes('drum')) {
      // alphaTab handles drums via percussion staff
    } else {
      tex += `\\instrument 27\n`;
    }

    if (track.tuningNotes && track.tuningNotes.length > 0) {
      tex += `\\tuning ${track.tuningNotes.map((n) => n.toLowerCase()).join(' ')}\n`;
    }

    tex += `\n:4\n`;

    const measures = track.measures || [];
    for (let mIdx = 0; mIdx < measures.length; mIdx++) {
      const measure = measures[mIdx];
      let barStr = '';

      if (measure.repeatStart) {
        barStr += '|: ';
      }

      for (const beat of measure.beats || []) {
        const dur = DURATION_MAP[beat.duration] || '4';

        if (beat.isRest || !beat.notes || beat.notes.length === 0) {
          barStr += `r.${dur} `;
          continue;
        }

        const noteTokens = beat.notes.map((note) => {
          const atString = note.stringIndex !== undefined ? note.stringIndex + 1 : 6;
          let token = note.deadNote || note.fret < 0 ? `x.${atString}` : `${note.fret}.${atString}`;

          const effects: string[] = [];
          if (measure.palmMute) effects.push('pm');
          if (note.vibrato) effects.push('v');
          if (note.slide === 'up' || note.slide === 'down') effects.push('sl');
          if (note.bend === 'full') effects.push('b(0 4)');
          else if (note.bend === 'half') effects.push('b(0 2)');
          if (note.ghost) effects.push('g');

          if (effects.length > 0) {
            token += `{${effects.join(' ')}}`;
          }
          return token;
        });

        if (noteTokens.length === 1) {
          barStr += `${noteTokens[0]}.${dur} `;
        } else {
          barStr += `(${noteTokens.join(' ')}).${dur} `;
        }
      }

      if (measure.repeatEnd) {
        barStr += ':|\n';
      } else {
        barStr += '|\n';
      }

      tex += barStr;
    }

    tex += '\n';
  }

  return tex;
}

/**
 * Plays a quick preview sound for a given note (string + fret)
 */
export function playNotePreview(
  stringIndex: number,
  fret: number,
  tuningKey: string = 'standard_e',
  instrument: string = 'guitar'
) {
  if (fret < 0) return;

  const tuning = TUNING_PRESETS[tuningKey] || TUNING_PRESETS['standard_e'];
  const baseMidi = tuning.midis[stringIndex] ?? (tuning.midis[0] || 64);
  const noteMidi = baseMidi + fret;
  const freq = midiToHz(noteMidi);

  if (instrument.toLowerCase().includes('drum')) {
    playDrumHit(fret > 5 ? 2 : 1, 0.9);
  } else {
    playGuitarPluck(freq, 1.2, 0.85);
  }
}

/**
 * Exports SongTabScore to standard ASCII tab representation
 */
export function exportSongToAscii(song: SongTabScore): string {
  let ascii = `========================================================\n`;
  ascii += `  ${song.title} - ${song.artist}\n`;
  ascii += `  Tempo: ${song.defaultTempo} BPM | Transcribed in TuxGuitar Studio\n`;
  ascii += `========================================================\n\n`;

  for (const track of song.tracks) {
    ascii += `--- TRACK: ${track.name} [${track.instrument}] ---\n`;
    ascii += `Tuning: ${track.tuningName} (${(track.tuningNotes || []).join(' ')})\n\n`;

    const stringCount = track.tuningNotes?.length || 6;
    const defaultLabels = ['e', 'B', 'G', 'D', 'A', 'E', 'B'];

    for (let mIdx = 0; mIdx < track.measures.length; mIdx += 4) {
      const chunk = track.measures.slice(mIdx, mIdx + 4);
      const lines = Array.from({ length: stringCount }, (_, i) => {
        const lbl = track.tuningNotes?.[i]?.slice(0, 1) || defaultLabels[i] || '-';
        return `${lbl}|`;
      });

      for (const m of chunk) {
        for (let s = 0; s < stringCount; s++) {
          for (const b of m.beats) {
            if (b.isRest) {
              lines[s] += '---';
            } else {
              const note = b.notes.find((n) => n.stringIndex === s);
              if (!note || note.fret < 0) {
                lines[s] += '---';
              } else if (note.deadNote) {
                lines[s] += '-x-';
              } else {
                const fStr = note.fret.toString();
                lines[s] += fStr.length === 1 ? `-${fStr}-` : `${fStr}-`;
              }
            }
          }
          lines[s] += '|';
        }
      }

      ascii += `Bar ${mIdx + 1} - ${Math.min(mIdx + 4, track.measures.length)}:\n`;
      ascii += lines.join('\n') + '\n\n';
    }
    ascii += '\n';
  }

  return ascii;
}

/**
 * Parses user-pasted ASCII tab lines into measures for SongTabScore
 */
export function parseAsciiToTrack(asciiText: string, trackName: string = 'Guitar'): AdvancedMeasure[] {
  const lines = asciiText
    .split('\n')
    .map((l) => l.trim())
    .filter((l) => l.length > 0 && l.includes('|'));

  if (lines.length < 4) {
    // Return a default measure if not enough lines
    return [
      {
        number: 1,
        timeSignature: [4, 4],
        beats: [
          { id: 'b1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 0 }] },
          { id: 'b2', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 4, fret: 2 }] },
          { id: 'b3', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 2 }] },
          { id: 'b4', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 0 }] },
        ],
      },
    ];
  }

  const rawStaffLines = lines.slice(0, 6);
  const segments = rawStaffLines.map((l) => {
    const pipeIdx = l.indexOf('|');
    return l.substring(pipeIdx + 1);
  });

  const maxLen = Math.max(...segments.map((s) => s.length));
  const measures: AdvancedMeasure[] = [];
  let currentBeats: AdvancedBeat[] = [];
  let barNum = 1;

  for (let col = 0; col < maxLen; col++) {
    // Check if this column is a bar separator
    if (segments.every((s) => s[col] === '|')) {
      if (currentBeats.length > 0) {
        measures.push({
          number: barNum++,
          timeSignature: [4, 4],
          beats: currentBeats,
        });
        currentBeats = [];
      }
      continue;
    }

    const notesInCol: AdvancedNote[] = [];
    for (let sIdx = 0; sIdx < segments.length; sIdx++) {
      const char = segments[sIdx][col];
      if (char !== undefined && char !== '-' && char !== '|' && char !== ' ') {
        const fret = parseInt(char, 10);
        if (!isNaN(fret)) {
          notesInCol.push({ stringIndex: sIdx, fret });
        } else if (char.toLowerCase() === 'x') {
          notesInCol.push({ stringIndex: sIdx, fret: 0, deadNote: true });
        }
      }
    }

    if (notesInCol.length > 0) {
      currentBeats.push({
        id: `imp_${barNum}_${col}`,
        duration: 'q',
        durationValue: 1.0,
        notes: notesInCol,
      });
    }
  }

  if (currentBeats.length > 0) {
    measures.push({
      number: barNum,
      timeSignature: [4, 4],
      beats: currentBeats,
    });
  }

  return measures.length > 0
    ? measures
    : [
        {
          number: 1,
          timeSignature: [4, 4],
          beats: [{ id: 'b1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 0 }] }],
        },
      ];
}
