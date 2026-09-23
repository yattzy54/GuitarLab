// GuitarLab Domain Models & Types

export interface TuningNote {
  stringNumber: number; // 1-indexed (1 is high E, N is lowest string)
  noteName: string;      // e.g. "E", "A", "D"
  octave: number;        // e.g. 2, 3, 4
  targetFrequencyHz: number;
  midiNote: number;
  displayLabel?: string;
}

export interface Tuning {
  id: string;
  name: string;
  category: 'Standard' | 'Drop' | 'Open' | 'Alternate' | '7-String' | '8-String';
  stringCount: number;
  notes: TuningNote[];
  isFavorite?: boolean;
}

export interface DetectedPitch {
  frequencyHz: number;
  midiNote: number;
  noteName: string;
  octave: number;
  cents: number;
  clarity: number;
  inTune: boolean; // abs(cents) <= 5
}

export interface TimeSignature {
  id: string;
  beatsPerBar: number;
  beatUnit: number;
  accentBeats: number[]; // 1-indexed beats that get accents (e.g. [1])
  label: string;
}

export type TrainerIntervalKind = 'BARS' | 'MINUTES';

export interface TrainerConfig {
  enabled: boolean;
  startBpm: number;
  targetBpm: number;
  incrementBpm: number;
  intervalKind: TrainerIntervalKind;
  intervalValue: number;
}

export interface MetronomeConfig {
  bpm: number;
  timeSignature: TimeSignature;
  volume: number;
  trainer: TrainerConfig;
}

export interface MetronomeBeat {
  beatInBar: number;
  barIndex: number;
  accent: boolean;
  bpm: number;
  progressToNextJump: number;
  beatsUntilJump?: number;
  millisUntilJump?: number;
}

export type FretboardMode = 'CHORD_SCALE_FINDER' | 'REVERSE_LOOKUP';

export interface ChordFormula {
  name: string;
  intervals: number[]; // semitones relative to root [0, 4, 7]
  symbol?: string;
}

export interface ScaleFormula {
  name: string;
  intervals: number[];
}

export interface FretPosition {
  stringIndex: number; // 0 = highest pitch string
  fret: number;        // 0 = open string, 1..24
  midiNote?: number;
}

// Tablature models
export type NoteEffect = 'NONE' | 'SLIDE' | 'BEND' | 'HAMMER_ON' | 'PULL_OFF' | 'PALM_MUTE' | 'VIBRATO' | 'LET_RING' | 'DEAD_NOTE';

export interface TabNote {
  stringIndex: number; // 0 = highest pitch (high E), 5 = lowest (low E)
  fret: number;        // 0..24, or -1 for rest
  effect?: NoteEffect;
  startBeat?: number;
  durationBeats?: number;
  velocity?: number;
}

export interface TabBeat {
  notes: TabNote[];
  startBeat: number;
  durationBeats: number;
}

export interface TabMeasure {
  number: number;
  timeSignatureNumerator?: number;
  timeSignatureDenominator?: number;
  tempoBpm?: number;
  beats: TabBeat[];
}

export interface TabTrack {
  id: string;
  name: string;
  stringCount: number;
  stringLabels: string[];
  volume: number;
  pan: number;
  measures: TabMeasure[];
}

export interface TabScore {
  id: string;
  title: string;
  artist: string;
  tempo: number;
  timeSignatureNumerator: number;
  timeSignatureDenominator: number;
  tracks: TabTrack[];
  rawAsciiContent?: string;
}

// Practice Tracker
export interface PracticeSession {
  id: string;
  dateMillis: number;
  durationMinutes: number;
  category: string;
  notes: string;
}

export interface PracticeStats {
  streakDays: number;
  totalMinutes: number;
  sessionsCount: number;
}

// Riff Recording
export interface RiffRecording {
  id: string;
  title: string;
  tuningName: string;
  bpm: number;
  durationMs: number;
  timestamp: number;
  audioBlobUrl?: string;
  audioBase64?: string;
}
