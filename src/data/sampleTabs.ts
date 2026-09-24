import { TabScore, TabMeasure, TabBeat, TabNote } from '../types';

export const SAMPLE_TABS: TabScore[] = [
  {
    id: 'smoke_on_the_water',
    title: 'Smoke on the Water (Intro Riff)',
    artist: 'Deep Purple',
    tempo: 112,
    timeSignatureNumerator: 4,
    timeSignatureDenominator: 4,
    tracks: [
      {
        id: 'track_1',
        name: 'Electric Guitar',
        stringCount: 6,
        stringLabels: ['e', 'B', 'G', 'D', 'A', 'E'],
        volume: 1.0,
        pan: 0.0,
        measures: [
          {
            number: 1,
            beats: [
              { startBeat: 0, durationBeats: 1, notes: [{ stringIndex: 2, fret: 0 }, { stringIndex: 3, fret: 0 }] },
              { startBeat: 1, durationBeats: 1, notes: [{ stringIndex: 2, fret: 3 }, { stringIndex: 3, fret: 3 }] },
              { startBeat: 2, durationBeats: 1.5, notes: [{ stringIndex: 2, fret: 5 }, { stringIndex: 3, fret: 5 }] },
              { startBeat: 3.5, durationBeats: 0.5, notes: [{ stringIndex: 2, fret: 0 }, { stringIndex: 3, fret: 0 }] },
            ],
          },
          {
            number: 2,
            beats: [
              { startBeat: 4, durationBeats: 1, notes: [{ stringIndex: 2, fret: 3 }, { stringIndex: 3, fret: 3 }] },
              { startBeat: 5, durationBeats: 0.5, notes: [{ stringIndex: 2, fret: 6 }, { stringIndex: 3, fret: 6 }] },
              { startBeat: 5.5, durationBeats: 1.5, notes: [{ stringIndex: 2, fret: 5 }, { stringIndex: 3, fret: 5 }] },
              { startBeat: 7, durationBeats: 1, notes: [] }, // Rest
            ],
          },
          {
            number: 3,
            beats: [
              { startBeat: 8, durationBeats: 1, notes: [{ stringIndex: 2, fret: 0 }, { stringIndex: 3, fret: 0 }] },
              { startBeat: 9, durationBeats: 1, notes: [{ stringIndex: 2, fret: 3 }, { stringIndex: 3, fret: 3 }] },
              { startBeat: 10, durationBeats: 1.5, notes: [{ stringIndex: 2, fret: 5 }, { stringIndex: 3, fret: 5 }] },
              { startBeat: 11.5, durationBeats: 0.5, notes: [{ stringIndex: 2, fret: 3 }, { stringIndex: 3, fret: 3 }] },
            ],
          },
          {
            number: 4,
            beats: [
              { startBeat: 12, durationBeats: 3, notes: [{ stringIndex: 2, fret: 0 }, { stringIndex: 3, fret: 0 }] },
              { startBeat: 15, durationBeats: 1, notes: [] },
            ],
          },
        ],
      },
    ],
    rawAsciiContent: `e|--------------------|--------------------|--------------------|--------------------|
B|--------------------|--------------------|--------------------|--------------------|
G|--0---3---5---0-----|--3---6-5-----------|--0---3---5---3-----|--0-----------------|
D|--0---3---5---0-----|--3---6-5-----------|--0---3---5---3-----|--0-----------------|
A|--------------------|--------------------|--------------------|--------------------|
E|--------------------|--------------------|--------------------|--------------------|`,
  },
  {
    id: 'fingerpicking_arpeggio',
    title: 'Acoustic Fingerstyle Pattern',
    artist: 'GuitarLab Study',
    tempo: 96,
    timeSignatureNumerator: 4,
    timeSignatureDenominator: 4,
    tracks: [
      {
        id: 'track_2',
        name: 'Acoustic Guitar',
        stringCount: 6,
        stringLabels: ['e', 'B', 'G', 'D', 'A', 'E'],
        volume: 1.0,
        pan: 0.0,
        measures: [
          {
            number: 1,
            beats: [
              { startBeat: 0, durationBeats: 0.5, notes: [{ stringIndex: 5, fret: 0 }, { stringIndex: 0, fret: 0 }] },
              { startBeat: 0.5, durationBeats: 0.5, notes: [{ stringIndex: 1, fret: 0 }] },
              { startBeat: 1.0, durationBeats: 0.5, notes: [{ stringIndex: 2, fret: 0 }] },
              { startBeat: 1.5, durationBeats: 0.5, notes: [{ stringIndex: 3, fret: 2 }] },
              { startBeat: 2.0, durationBeats: 0.5, notes: [{ stringIndex: 4, fret: 2 }] },
              { startBeat: 2.5, durationBeats: 0.5, notes: [{ stringIndex: 3, fret: 2 }] },
              { startBeat: 3.0, durationBeats: 0.5, notes: [{ stringIndex: 2, fret: 0 }] },
              { startBeat: 3.5, durationBeats: 0.5, notes: [{ stringIndex: 1, fret: 0 }] },
            ],
          },
          {
            number: 2,
            beats: [
              { startBeat: 4.0, durationBeats: 0.5, notes: [{ stringIndex: 4, fret: 3 }, { stringIndex: 1, fret: 1 }] },
              { startBeat: 4.5, durationBeats: 0.5, notes: [{ stringIndex: 2, fret: 0 }] },
              { startBeat: 5.0, durationBeats: 0.5, notes: [{ stringIndex: 3, fret: 2 }] },
              { startBeat: 5.5, durationBeats: 0.5, notes: [{ stringIndex: 0, fret: 0 }] },
              { startBeat: 6.0, durationBeats: 0.5, notes: [{ stringIndex: 1, fret: 1 }] },
              { startBeat: 6.5, durationBeats: 0.5, notes: [{ stringIndex: 2, fret: 0 }] },
              { startBeat: 7.0, durationBeats: 0.5, notes: [{ stringIndex: 3, fret: 2 }] },
              { startBeat: 7.5, durationBeats: 0.5, notes: [{ stringIndex: 2, fret: 0 }] },
            ],
          },
        ],
      },
    ],
    rawAsciiContent: `e|--0-----------------|----------0---------|
B|----0-----------0---|----1-------1-------|
G|------0---0---0---0-|------0-------0-----|
D|--------2---2-------|--------2-------2---|
A|----------2---------|--3---------------3-|
E|--0-----------------|--------------------|`,
  },
  {
    id: 'blues_pentatonic_lick',
    title: 'Texas Blues Rock Lick',
    artist: 'Stevie Style',
    tempo: 105,
    timeSignatureNumerator: 4,
    timeSignatureDenominator: 4,
    tracks: [
      {
        id: 'track_3',
        name: 'Lead Guitar',
        stringCount: 6,
        stringLabels: ['e', 'B', 'G', 'D', 'A', 'E'],
        volume: 1.0,
        pan: 0.0,
        measures: [
          {
            number: 1,
            beats: [
              { startBeat: 0, durationBeats: 0.5, notes: [{ stringIndex: 2, fret: 7 }] },
              { startBeat: 0.5, durationBeats: 0.5, notes: [{ stringIndex: 2, fret: 8 }] },
              { startBeat: 1.0, durationBeats: 0.5, notes: [{ stringIndex: 1, fret: 8 }] },
              { startBeat: 1.5, durationBeats: 0.5, notes: [{ stringIndex: 0, fret: 8 }] },
              { startBeat: 2.0, durationBeats: 1.0, notes: [{ stringIndex: 0, fret: 10 }] },
              { startBeat: 3.0, durationBeats: 0.5, notes: [{ stringIndex: 0, fret: 8 }] },
              { startBeat: 3.5, durationBeats: 0.5, notes: [{ stringIndex: 1, fret: 10 }] },
            ],
          },
          {
            number: 2,
            beats: [
              { startBeat: 4.0, durationBeats: 1.0, notes: [{ stringIndex: 1, fret: 8 }] },
              { startBeat: 5.0, durationBeats: 0.5, notes: [{ stringIndex: 2, fret: 9 }] },
              { startBeat: 5.5, durationBeats: 0.5, notes: [{ stringIndex: 3, fret: 7 }] },
              { startBeat: 6.0, durationBeats: 2.0, notes: [{ stringIndex: 3, fret: 5 }] },
            ],
          },
        ],
      },
    ],
    rawAsciiContent: `e|-------------8--10~--8---|--------------------|
B|----------8-----------10-|--8~----------------|
G|--7--8--9----------------|------9--7----------|
D|-------------------------|------------5~------|
A|-------------------------|--------------------|
E|-------------------------|--------------------|`,
  },
];

/**
 * ASCII Tab Parser
 * Ported from TabParser.kt
 */
export function parseAsciiTab(content: string, title = 'Custom Tab'): TabScore {
  const lines = content.split(/\r?\n/);
  const stringMarkers = [
    { regex: /^[eE]\s*\|/, stringIndex: 0 },
    { regex: /^[bB]\s*\|/, stringIndex: 1 },
    { regex: /^[gG]\s*\|/, stringIndex: 2 },
    { regex: /^[dD]\s*\|/, stringIndex: 3 },
    { regex: /^[aA]\s*\|/, stringIndex: 4 },
    { regex: /^[eE]\s*\|/, stringIndex: 5 },
  ];

  const tabLinesByString: Array<{ stringIndex: number; line: string }> = [];

  for (const line of lines) {
    const trimmed = line.trim();
    if (!trimmed.includes('-') && !trimmed.includes('|')) continue;

    for (let i = 0; i < 6; i++) {
      if (stringMarkers[i].regex.test(trimmed)) {
        const pipeIdx = trimmed.indexOf('|');
        const tabStr = trimmed.substring(pipeIdx + 1);
        tabLinesByString.push({ stringIndex: i, line: tabStr });
        break;
      }
    }
  }

  const measures: TabMeasure[] = [];

  if (tabLinesByString.length >= 4) {
    const grouped = new Map<number, string>();
    for (const item of tabLinesByString) {
      const existing = grouped.get(item.stringIndex) || '';
      grouped.set(item.stringIndex, existing + item.line);
    }

    const maxLen = Math.max(...Array.from(grouped.values()).map((s) => s.length));
    const beats: TabBeat[] = [];
    let beatIdx = 0;

    for (let col = 0; col < maxLen; col++) {
      const notesAtCol: TabNote[] = [];
      for (let sIdx = 0; sIdx < 6; sIdx++) {
        const str = grouped.get(sIdx);
        if (!str || col >= str.length) continue;

        const char = str[col];
        if (char >= '0' && char <= '9') {
          // Check for 2-digit frets (e.g. 10, 12, 14)
          let fretNum = parseInt(char, 10);
          if (col + 1 < str.length && str[col + 1] >= '0' && str[col + 1] <= '9') {
            fretNum = fretNum * 10 + parseInt(str[col + 1], 10);
          }
          notesAtCol.push({
            stringIndex: sIdx,
            fret: fretNum,
            startBeat: beatIdx,
            durationBeats: 0.5,
          });
        }
      }

      if (notesAtCol.length > 0) {
        beats.push({
          notes: notesAtCol,
          startBeat: beatIdx,
          durationBeats: 0.5,
        });
        beatIdx += 0.5;
      }
    }

    if (beats.length > 0) {
      // Chunk into measures of 8 half-beats (4 quarter notes)
      for (let i = 0; i < beats.length; i += 8) {
        measures.push({
          number: Math.floor(i / 8) + 1,
          beats: beats.slice(i, i + 8),
        });
      }
    }
  }

  return {
    id: `tab_${Date.now()}`,
    title,
    artist: 'ASCII Tab Import',
    tempo: 120,
    timeSignatureNumerator: 4,
    timeSignatureDenominator: 4,
    rawAsciiContent: content,
    tracks: [
      {
        id: 'track_imported',
        name: 'Guitar 1',
        stringCount: 6,
        stringLabels: ['e', 'B', 'G', 'D', 'A', 'E'],
        volume: 1.0,
        pan: 0.0,
        measures: measures.length > 0 ? measures : SAMPLE_TABS[0].tracks[0].measures,
      },
    ],
  };
}
