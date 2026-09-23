import { SongTabScore, TabTrackInfo } from '../types/tabPlayer';

/**
 * Converts internal SongTabScore and TabTrackInfo to AlphaTex format
 * for professional sheet music rendering and audio playback in alphaTab.
 */
export function convertTrackToAlphaTex(song: SongTabScore, track: TabTrackInfo): string {
  let tex = `\\title "${song.title.replace(/"/g, '')}"\n`;
  tex += `\\artist "${song.artist.replace(/"/g, '')}"\n`;
  tex += `\\tempo ${song.defaultTempo || 120}\n.\n`;

  const trackName = (track.name || 'Guitar').replace(/"/g, '');
  tex += `\\track "${trackName}"\n`;

  if (track.tuningNotes && track.tuningNotes.length > 0) {
    const tuningStr = track.tuningNotes.map((n) => n.toLowerCase()).join(' ');
    tex += `\\tuning ${tuningStr}\n`;
  }

  const inst = (track.instrument || '').toLowerCase();
  if (inst.includes('bass')) {
    tex += `\\instrument 34\n`; // Electric Bass (pick)
  } else if (inst.includes('distort') || inst.includes('overdrive')) {
    tex += `\\instrument 29\n`; // Overdriven Guitar
  } else {
    tex += `\\instrument 27\n`; // Electric Guitar (clean)
  }

  tex += `\n:4\n`;

  const durationMap: Record<string, string> = {
    w: '1',
    h: '2',
    q: '4',
    e: '8',
    s: '16',
    t: '32',
  };

  const measures = track.measures || [];
  for (let mIdx = 0; mIdx < measures.length; mIdx++) {
    const measure = measures[mIdx];
    let barStr = '';

    for (const beat of measure.beats || []) {
      const dur = durationMap[beat.duration] || '4';

      if (beat.isRest || !beat.notes || beat.notes.length === 0) {
        barStr += `r.${dur} `;
        continue;
      }

      const noteTokens = beat.notes.map((note) => {
        // stringIndex in app: 0 = high string (e.g. 1st string high E), 5 = low string (6th string low E/C)
        // in alphaTab: string 1 is highest string, string 6 is lowest string!
        const atString = note.stringIndex !== undefined ? note.stringIndex + 1 : 6;
        let token = note.deadNote || note.fret < 0 ? `x.${atString}` : `${note.fret}.${atString}`;

        const effects: string[] = [];
        if (measure.palmMute) effects.push('pm');
        if (note.vibrato) effects.push('v');
        if (note.slide === 'up' || note.slide === 'down') effects.push('sl');
        if (note.bend === 'full') effects.push('b(0 4)');
        else if (note.bend === 'half') effects.push('b(0 2)');

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

    if (measure.repeatStart) {
      tex += `|: `;
    }
    tex += `${barStr.trim()} `;
    if (measure.repeatEnd) {
      tex += `:|\n`;
    } else {
      tex += `|\n`;
    }
  }

  return tex;
}

import { TabScore } from '../types';

/**
 * Converts legacy/sample TabScore to AlphaTex format.
 */
export function convertTabScoreToAlphaTex(score: TabScore): string {
  let tex = `\\title "${score.title.replace(/"/g, '')}"\n`;
  tex += `\\artist "${score.artist.replace(/"/g, '')}"\n`;
  tex += `\\tempo ${score.tempo || 120}\n.\n`;
  tex += `\\track "Guitar"\n`;
  tex += `\\instrument 29\n`;
  tex += `\\tuning e4 b3 g3 d3 a2 e2\n\n:4\n`;

  const track = score.tracks[0];
  if (!track) return tex;

  for (const measure of track.measures) {
    let barStr = '';
    for (const beat of measure.beats) {
      let dur = '4';
      if (beat.durationBeats === 0.5) dur = '8';
      else if (beat.durationBeats === 0.25) dur = '16';
      else if (beat.durationBeats === 1.5) dur = '4.';
      else if (beat.durationBeats === 2) dur = '2';
      else if (beat.durationBeats === 3) dur = '2.';
      else if (beat.durationBeats === 4) dur = '1';

      if (!beat.notes || beat.notes.length === 0) {
        barStr += `r.${dur} `;
        continue;
      }

      const noteTokens = beat.notes.map((n) => `${n.fret}.${n.stringIndex + 1}`);
      if (noteTokens.length === 1) {
        barStr += `${noteTokens[0]}.${dur} `;
      } else {
        barStr += `(${noteTokens.join(' ')}).${dur} `;
      }
    }
    tex += `${barStr.trim()} |\n`;
  }
  return tex;
}
