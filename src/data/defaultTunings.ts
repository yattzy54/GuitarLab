import { Tuning, TuningNote } from '../types';

export function midiToHz(midi: number, a4 = 440): number {
  return a4 * Math.pow(2.0, (midi - 69) / 12.0);
}

function createTuning(
  id: string,
  name: string,
  category: Tuning['category'],
  notesData: Array<[string, number, number]>, // [NoteName, Octave, Midi]
  a4 = 440
): Tuning {
  const notes: TuningNote[] = notesData.map(([noteName, octave, midiNote], index) => ({
    stringNumber: index + 1,
    noteName,
    octave,
    targetFrequencyHz: midiToHz(midiNote, a4),
    midiNote,
    displayLabel: `${noteName}${octave}`,
  }));

  return {
    id,
    name,
    category,
    stringCount: notes.length,
    notes,
  };
}

export function getAllTunings(a4 = 440): Tuning[] {
  return [
    createTuning(
      'standard_e',
      'Standard E',
      'Standard',
      [
        ['E', 4, 64],
        ['B', 3, 59],
        ['G', 3, 55],
        ['D', 3, 50],
        ['A', 2, 45],
        ['E', 2, 40],
      ],
      a4
    ),
    createTuning(
      'half_step_down',
      'Half Step Down (Eb)',
      'Standard',
      [
        ['D♯', 4, 63],
        ['A♯', 3, 58],
        ['F♯', 3, 54],
        ['C♯', 3, 49],
        ['G♯', 2, 44],
        ['D♯', 2, 39],
      ],
      a4
    ),
    createTuning(
      'drop_d',
      'Drop D',
      'Drop',
      [
        ['E', 4, 64],
        ['B', 3, 59],
        ['G', 3, 55],
        ['D', 3, 50],
        ['A', 2, 45],
        ['D', 2, 38],
      ],
      a4
    ),
    createTuning(
      'drop_c',
      'Drop C',
      'Drop',
      [
        ['D', 4, 62],
        ['A', 3, 57],
        ['F', 3, 53],
        ['C', 3, 48],
        ['G', 2, 43],
        ['C', 2, 36],
      ],
      a4
    ),
    createTuning(
      'drop_b',
      'Drop B',
      'Drop',
      [
        ['C♯', 4, 61],
        ['G♯', 3, 56],
        ['E', 3, 52],
        ['B', 2, 47],
        ['F♯', 2, 42],
        ['B', 1, 35],
      ],
      a4
    ),
    createTuning(
      'drop_a',
      'Drop A',
      'Drop',
      [
        ['B', 3, 59],
        ['F♯', 3, 54],
        ['D', 3, 50],
        ['A', 2, 45],
        ['E', 2, 40],
        ['A', 1, 33],
      ],
      a4
    ),
    createTuning(
      'open_d',
      'Open D',
      'Open',
      [
        ['D', 4, 62],
        ['A', 3, 57],
        ['F♯', 3, 54],
        ['D', 3, 50],
        ['A', 2, 45],
        ['D', 2, 38],
      ],
      a4
    ),
    createTuning(
      'open_g',
      'Open G',
      'Open',
      [
        ['D', 4, 62],
        ['B', 3, 59],
        ['G', 3, 55],
        ['D', 3, 50],
        ['G', 2, 43],
        ['D', 2, 38],
      ],
      a4
    ),
    createTuning(
      'dadgad',
      'DADGAD',
      'Alternate',
      [
        ['D', 4, 62],
        ['A', 3, 57],
        ['G', 3, 55],
        ['D', 3, 50],
        ['A', 2, 45],
        ['D', 2, 38],
      ],
      a4
    ),
    createTuning(
      'std_7string',
      '7-String Standard',
      '7-String',
      [
        ['E', 4, 64],
        ['B', 3, 59],
        ['G', 3, 55],
        ['D', 3, 50],
        ['A', 2, 45],
        ['E', 2, 40],
        ['B', 1, 35],
      ],
      a4
    ),
    createTuning(
      'drop_a_7string',
      '7-String Drop A',
      '7-String',
      [
        ['E', 4, 64],
        ['B', 3, 59],
        ['G', 3, 55],
        ['D', 3, 50],
        ['A', 2, 45],
        ['E', 2, 40],
        ['A', 1, 33],
      ],
      a4
    ),
    createTuning(
      'std_8string',
      '8-String Standard',
      '8-String',
      [
        ['E', 4, 64],
        ['B', 3, 59],
        ['G', 3, 55],
        ['D', 3, 50],
        ['A', 2, 45],
        ['E', 2, 40],
        ['B', 1, 35],
        ['F♯', 1, 30],
      ],
      a4
    ),
    createTuning(
      'drop_e_8string',
      '8-String Drop E',
      '8-String',
      [
        ['E', 4, 64],
        ['B', 3, 59],
        ['G', 3, 55],
        ['D', 3, 50],
        ['A', 2, 45],
        ['E', 2, 40],
        ['B', 1, 35],
        ['E', 1, 28],
      ],
      a4
    ),
  ];
}
