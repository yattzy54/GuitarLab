import { SongTabScore, TabTrackInfo } from '../../types/tabPlayer';
import { SONGS_CATALOG } from '../../data/songsLibrary';

export interface TuxPresetItem {
  id: string;
  title: string;
  artist: string;
  genre: string;
  bpm: number;
  tracksCount: number;
  description: string;
  score: SongTabScore;
}

export const BLANK_SCORE: SongTabScore = {
  id: 'blank_project',
  title: 'Untitled Composition',
  artist: 'User Musician',
  revisionDate: new Date().toLocaleDateString(),
  defaultTempo: 120,
  tracks: [
    {
      id: 'trk_guitar_1',
      name: 'Electric Guitar (Lead)',
      instrument: 'Distortion Guitar',
      tuningName: 'Standard E',
      tuningNotes: ['E4', 'B3', 'G3', 'D3', 'A2', 'E2'],
      volume: 0.9,
      isMuted: false,
      isSolo: false,
      measures: [
        {
          number: 1,
          timeSignature: [4, 4],
          beats: [
            { id: 'b1_1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 0 }, { stringIndex: 4, fret: 2 }] },
            { id: 'b1_2', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 3 }, { stringIndex: 4, fret: 5 }] },
            { id: 'b1_3', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 5 }, { stringIndex: 4, fret: 7 }] },
            { id: 'b1_4', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 3 }, { stringIndex: 4, fret: 5 }] },
          ],
        },
        {
          number: 2,
          timeSignature: [4, 4],
          beats: [
            { id: 'b2_1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 0 }, { stringIndex: 4, fret: 2 }] },
            { id: 'b2_2', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 3 }, { stringIndex: 4, fret: 5 }] },
            { id: 'b2_3', duration: 'h', durationValue: 2.0, notes: [{ stringIndex: 5, fret: 6, vibrato: true }] },
          ],
        },
        {
          number: 3,
          timeSignature: [4, 4],
          beats: [
            { id: 'b3_1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 0 }, { stringIndex: 4, fret: 2 }] },
            { id: 'b3_2', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 3 }, { stringIndex: 4, fret: 5 }] },
            { id: 'b3_3', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 5 }, { stringIndex: 4, fret: 7 }] },
            { id: 'b3_4', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 5, fret: 3 }] },
          ],
        },
        {
          number: 4,
          timeSignature: [4, 4],
          beats: [
            { id: 'b4_1', duration: 'w', durationValue: 4.0, notes: [{ stringIndex: 5, fret: 0 }, { stringIndex: 4, fret: 2 }, { stringIndex: 3, fret: 2 }] },
          ],
        },
      ],
    },
    {
      id: 'trk_bass_1',
      name: 'Electric Bass',
      instrument: 'Electric Bass (Finger)',
      tuningName: 'Bass Standard',
      tuningNotes: ['G2', 'D2', 'A1', 'E1'],
      volume: 0.85,
      isMuted: false,
      isSolo: false,
      measures: [
        {
          number: 1,
          timeSignature: [4, 4],
          beats: [
            { id: 'bb1_1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 0 }] },
            { id: 'bb1_2', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'bb1_3', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 5 }] },
            { id: 'bb1_4', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 3 }] },
          ],
        },
        {
          number: 2,
          timeSignature: [4, 4],
          beats: [
            { id: 'bb2_1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 0 }] },
            { id: 'bb2_2', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'bb2_3', duration: 'h', durationValue: 2.0, notes: [{ stringIndex: 3, fret: 6 }] },
          ],
        },
        {
          number: 3,
          timeSignature: [4, 4],
          beats: [
            { id: 'bb3_1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 0 }] },
            { id: 'bb3_2', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'bb3_3', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 5 }] },
            { id: 'bb3_4', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 3 }] },
          ],
        },
        {
          number: 4,
          timeSignature: [4, 4],
          beats: [
            { id: 'bb4_1', duration: 'w', durationValue: 4.0, notes: [{ stringIndex: 3, fret: 0 }] },
          ],
        },
      ],
    },
  ],
};

export const SMOKE_ON_THE_WATER: SongTabScore = {
  id: 'smoke_on_the_water',
  title: 'Smoke on the Water',
  artist: 'Deep Purple',
  revisionDate: '1972',
  defaultTempo: 112,
  tracks: [
    {
      id: 'sow_g1',
      name: 'Ritchie Blackmore (Guitar)',
      instrument: 'Distortion Guitar',
      tuningName: 'Standard E',
      tuningNotes: ['E4', 'B3', 'G3', 'D3', 'A2', 'E2'],
      volume: 0.95,
      isMuted: false,
      isSolo: false,
      measures: [
        {
          number: 1,
          timeSignature: [4, 4],
          beats: [
            { id: 'sb1_1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 0 }, { stringIndex: 2, fret: 0 }] },
            { id: 'sb1_2', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 3 }, { stringIndex: 2, fret: 3 }] },
            { id: 'sb1_3', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 5 }, { stringIndex: 2, fret: 5 }] },
            { id: 'sb1_4', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 0 }, { stringIndex: 2, fret: 0 }] },
          ],
        },
        {
          number: 2,
          timeSignature: [4, 4],
          beats: [
            { id: 'sb2_1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 3 }, { stringIndex: 2, fret: 3 }] },
            { id: 'sb2_2', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 6 }, { stringIndex: 2, fret: 6 }] },
            { id: 'sb2_3', duration: 'h', durationValue: 2.0, notes: [{ stringIndex: 3, fret: 5, vibrato: true }, { stringIndex: 2, fret: 5 }] },
            { id: 'sb2_4', duration: 'e', durationValue: 0.5, isRest: true, notes: [] },
          ],
        },
        {
          number: 3,
          timeSignature: [4, 4],
          beats: [
            { id: 'sb3_1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 0 }, { stringIndex: 2, fret: 0 }] },
            { id: 'sb3_2', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 3 }, { stringIndex: 2, fret: 3 }] },
            { id: 'sb3_3', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 5 }, { stringIndex: 2, fret: 5 }] },
            { id: 'sb3_4', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 3, fret: 3 }, { stringIndex: 2, fret: 3 }] },
          ],
        },
        {
          number: 4,
          timeSignature: [4, 4],
          beats: [
            { id: 'sb4_1', duration: 'w', durationValue: 4.0, notes: [{ stringIndex: 3, fret: 0, vibrato: true }, { stringIndex: 2, fret: 0 }] },
          ],
        },
      ],
    },
    {
      id: 'sow_b1',
      name: 'Roger Glover (Bass)',
      instrument: 'Electric Bass (Finger)',
      tuningName: 'Bass Standard',
      tuningNotes: ['G2', 'D2', 'A1', 'E1'],
      volume: 0.9,
      isMuted: false,
      isSolo: false,
      measures: [
        {
          number: 1,
          timeSignature: [4, 4],
          beats: [
            { id: 'sbb1_1', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb1_2', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb1_3', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb1_4', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb1_5', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb1_6', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb1_7', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb1_8', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
          ],
        },
        {
          number: 2,
          timeSignature: [4, 4],
          beats: [
            { id: 'sbb2_1', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb2_2', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb2_3', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb2_4', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb2_5', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb2_6', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb2_7', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb2_8', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
          ],
        },
        {
          number: 3,
          timeSignature: [4, 4],
          beats: [
            { id: 'sbb3_1', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb3_2', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb3_3', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb3_4', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb3_5', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb3_6', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb3_7', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
            { id: 'sbb3_8', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 3, fret: 3 }] },
          ],
        },
        {
          number: 4,
          timeSignature: [4, 4],
          beats: [
            { id: 'sbb4_1', duration: 'w', durationValue: 4.0, notes: [{ stringIndex: 3, fret: 3 }] },
          ],
        },
      ],
    },
  ],
};

export const NOTHING_ELSE_MATTERS: SongTabScore = {
  id: 'nothing_else_matters',
  title: 'Nothing Else Matters',
  artist: 'Metallica',
  revisionDate: '1991',
  defaultTempo: 142,
  tracks: [
    {
      id: 'nem_g1',
      name: 'James Hetfield (Acoustic)',
      instrument: 'Acoustic Guitar (Steel)',
      tuningName: 'Standard E',
      tuningNotes: ['E4', 'B3', 'G3', 'D3', 'A2', 'E2'],
      volume: 0.95,
      isMuted: false,
      isSolo: false,
      measures: [
        {
          number: 1,
          timeSignature: [6, 8],
          beats: [
            { id: 'nb1_1', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 5, fret: 0 }] },
            { id: 'nb1_2', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 2, fret: 0 }] },
            { id: 'nb1_3', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 1, fret: 0 }] },
            { id: 'nb1_4', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 0, fret: 0 }] },
            { id: 'nb1_5', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 1, fret: 0 }] },
            { id: 'nb1_6', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 2, fret: 0 }] },
          ],
        },
        {
          number: 2,
          timeSignature: [6, 8],
          beats: [
            { id: 'nb2_1', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 5, fret: 0 }] },
            { id: 'nb2_2', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 2, fret: 0 }] },
            { id: 'nb2_3', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 1, fret: 0 }] },
            { id: 'nb2_4', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 0, fret: 0 }] },
            { id: 'nb2_5', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 1, fret: 0 }] },
            { id: 'nb2_6', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 2, fret: 0 }] },
          ],
        },
        {
          number: 3,
          timeSignature: [6, 8],
          beats: [
            { id: 'nb3_1', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 0, fret: 7 }] },
            { id: 'nb3_2', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 0, fret: 0 }] },
            { id: 'nb3_3', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 0, fret: 7 }] },
            { id: 'nb3_4', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 0, fret: 8 }] },
            { id: 'nb3_5', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 0, fret: 7 }] },
          ],
        },
        {
          number: 4,
          timeSignature: [6, 8],
          beats: [
            { id: 'nb4_1', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 0, fret: 5, slide: 'down' }] },
            { id: 'nb4_2', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 0, fret: 3 }] },
            { id: 'nb4_3', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 0, fret: 0 }] },
            { id: 'nb4_4', duration: 'q', durationValue: 1.0, notes: [{ stringIndex: 0, fret: 2, vibrato: true }] },
            { id: 'nb4_5', duration: 'e', durationValue: 0.5, notes: [{ stringIndex: 0, fret: 0 }] },
          ],
        },
      ],
    },
  ],
};

export const TUX_PRESETS: TuxPresetItem[] = [
  {
    id: 'sow',
    title: 'Smoke on the Water',
    artist: 'Deep Purple',
    genre: 'Classic Rock',
    bpm: 112,
    tracksCount: 2,
    description: 'Легендарный 4-тактовый гитарный рифф с партией бас-гитары.',
    score: SMOKE_ON_THE_WATER,
  },
  {
    id: 'nem',
    title: 'Nothing Else Matters',
    artist: 'Metallica',
    genre: 'Metal Ballad',
    bpm: 142,
    tracksCount: 1,
    description: 'Вступление на открытых струнах в размере 6/8 с мелодией на первой струне.',
    score: NOTHING_ELSE_MATTERS,
  },
  {
    id: 'mop',
    title: 'Master of Puppets',
    artist: 'Metallica',
    genre: 'Thrash Metal',
    bpm: 212,
    tracksCount: 1,
    description: 'Скоростной даунпикинг-рифф в темпе 212 BPM с палм-мьютом.',
    score: SONGS_CATALOG.find((s) => s.id === 'metallica_puppets') || BLANK_SCORE,
  },
  {
    id: 'dc',
    title: 'Dark Clouds',
    artist: 'Adept',
    genre: 'Metalcore',
    bpm: 140,
    tracksCount: 2,
    description: 'Тяжелый рифф в строе Drop C со слайдами и бендами.',
    score: SONGS_CATALOG.find((s) => s.id === 'adept_dark_clouds') || BLANK_SCORE,
  },
  {
    id: 'blank',
    title: 'Blank Studio Project',
    artist: 'My Own Tab',
    genre: 'Custom Composition',
    bpm: 120,
    tracksCount: 2,
    description: 'Чистый 4-тактовый проект с гитарой и басом для написания своей песни.',
    score: BLANK_SCORE,
  },
];
