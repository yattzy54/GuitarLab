// Songsterr / Guitar Pro Advanced Tab Player Types

export type StemDuration = 'w' | 'h' | 'q' | 'e' | 's' | 't'; // whole, half, quarter, eighth, sixteenth, thirty-second

export interface AdvancedNote {
  stringIndex: number; // 0 = high E (or highest pitch), 5 = low C/E
  fret: number;        // 0..24, or -1 for ghost/mute
  slide?: 'up' | 'down' | 'none';
  bend?: 'half' | 'full' | 'release' | 'none';
  vibrato?: boolean;
  hammerOn?: boolean;
  pullOff?: boolean;
  deadNote?: boolean;
  ghost?: boolean;
}

export interface AdvancedBeat {
  id: string;
  notes: AdvancedNote[];
  duration: StemDuration;
  durationValue: number; // in quarter notes: w=4, h=2, q=1, e=0.5, s=0.25
  isRest?: boolean;
  textAnnotation?: string; // e.g. "Let Ring"
}

export interface AdvancedMeasure {
  number: number;
  timeSignature: [number, number]; // [4, 4], [3, 4], [6, 8]
  tempoBpm?: number;
  palmMute?: boolean; // P.M. -----|
  palmMuteLabel?: string;
  repeatStart?: boolean;
  repeatEnd?: boolean;
  beats: AdvancedBeat[];
}

export interface TabTrackInfo {
  id: string;
  name: string; // e.g. "Overdriven Guitar / Guitar 1"
  instrument: string; // "Electric Guitar (clean)", "Overdriven Guitar", "Electric Bass", "Drums"
  tuningName: string; // e.g. "Drop C"
  tuningNotes: string[]; // e.g. ["D4", "A3", "F3", "C3", "G2", "C2"]
  volume: number; // 0..1
  isMuted: boolean;
  isSolo: boolean;
  measures: AdvancedMeasure[];
}

export interface SongTabScore {
  id: string;
  title: string;          // e.g. "Dark Clouds"
  artist: string;         // e.g. "Adept"
  revisionDate: string;   // e.g. "26.06.2018"
  defaultTempo: number;   // e.g. 140
  tracks: TabTrackInfo[];
  originalAudioUrl?: string; // Original audio track for [ORIG.] mode
}
