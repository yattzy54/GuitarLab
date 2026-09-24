import { ChordFormula, ScaleFormula } from '../types';

export const NOTE_NAMES = ['C', 'C♯', 'D', 'D♯', 'E', 'F', 'F♯', 'G', 'G♯', 'A', 'A♯', 'B'];

export const CHORD_FORMULAS: ChordFormula[] = [
  { name: 'Major', symbol: '', intervals: [0, 4, 7] },
  { name: 'Minor', symbol: 'm', intervals: [0, 3, 7] },
  { name: '5 (Power Chord)', symbol: '5', intervals: [0, 7] },
  { name: '7 (Dominant)', symbol: '7', intervals: [0, 4, 7, 10] },
  { name: 'maj7', symbol: 'maj7', intervals: [0, 4, 7, 11] },
  { name: 'm7', symbol: 'm7', intervals: [0, 3, 7, 10] },
  { name: 'sus4', symbol: 'sus4', intervals: [0, 5, 7] },
  { name: 'sus2', symbol: 'sus2', intervals: [0, 2, 7] },
  { name: 'diminished', symbol: 'dim', intervals: [0, 3, 6] },
  { name: 'augmented', symbol: 'aug', intervals: [0, 4, 8] },
  { name: 'm7b5 (Half Dim)', symbol: 'm7b5', intervals: [0, 3, 6, 10] },
  { name: 'add9', symbol: 'add9', intervals: [0, 4, 7, 14 % 12] },
];

export const SCALE_FORMULAS: ScaleFormula[] = [
  { name: 'Minor Pentatonic', intervals: [0, 3, 5, 7, 10] },
  { name: 'Major Pentatonic', intervals: [0, 2, 4, 7, 9] },
  { name: 'Blues Scale', intervals: [0, 3, 5, 6, 7, 10] },
  { name: 'Major (Ionian)', intervals: [0, 2, 4, 5, 7, 9, 11] },
  { name: 'Natural Minor (Aeolian)', intervals: [0, 2, 3, 5, 7, 8, 10] },
  { name: 'Dorian', intervals: [0, 2, 3, 5, 7, 9, 10] },
  { name: 'Mixolydian', intervals: [0, 2, 4, 5, 7, 9, 10] },
  { name: 'Harmonic Minor', intervals: [0, 2, 3, 5, 7, 8, 11] },
  { name: 'Lydian', intervals: [0, 2, 4, 6, 7, 9, 11] },
  { name: 'Phrygian', intervals: [0, 1, 3, 5, 7, 8, 10] },
];

export function getMidiNoteIndex(noteName: string): number {
  const index = NOTE_NAMES.indexOf(noteName);
  return index !== -1 ? index : 0;
}

export function intervalName(semitones: number): string {
  const map: Record<number, string> = {
    0: 'R',
    1: '♭2',
    2: '2',
    3: '♭3',
    4: '3',
    5: '4',
    6: '♭5',
    7: '5',
    8: '♭6',
    9: '6',
    10: '♭7',
    11: '7',
  };
  return map[semitones % 12] ?? '';
}

/**
 * Identifies chords from a set of selected MIDI notes
 * Ported from FretboardModels.kt reverseLookupChord
 */
export function reverseLookupChord(selectedMidiNotes: number[]): string[] {
  if (selectedMidiNotes.length === 0) return [];

  const lowestMidi = Math.min(...selectedMidiNotes);
  const bassPitch = ((lowestMidi % 12) + 12) % 12;
  const bassName = NOTE_NAMES[bassPitch];

  const pitchClasses = Array.from(new Set(selectedMidiNotes.map(n => ((n % 12) + 12) % 12)));
  if (pitchClasses.length === 0) return [];

  const exactMatches: string[] = [];
  const partialMatches: string[] = [];

  for (const rootPitch of pitchClasses) {
    const rootName = NOTE_NAMES[rootPitch];
    const intervals = pitchClasses.map(p => (p - rootPitch + 12) % 12);
    const intervalSet = new Set(intervals);

    for (const formula of CHORD_FORMULAS) {
      const formulaSet = new Set(formula.intervals);

      // Exact match (exact pitch classes match formula)
      if (formula.intervals.length === pitchClasses.length && formula.intervals.every(i => intervalSet.has(i))) {
        const chordTitle = bassPitch !== rootPitch && formula.intervals.length > 2
          ? `${rootName} ${formula.name}/${bassName}`
          : `${rootName} ${formula.name}`;
        exactMatches.push(chordTitle);
      } else if (formula.intervals.every(i => intervalSet.has(i))) {
        const chordTitle = bassPitch !== rootPitch && formula.intervals.length > 2
          ? `${rootName} ${formula.name}/${bassName}`
          : `${rootName} ${formula.name}`;
        partialMatches.push(chordTitle);
      }
    }
  }

  const results = exactMatches.length > 0 ? exactMatches : partialMatches;
  return Array.from(new Set(results));
}