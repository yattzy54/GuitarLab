import { PracticeSession, PracticeStats, RiffRecording, MetronomeConfig, TimeSignature } from '../types';

const STORAGE_KEYS = {
  SESSIONS: 'guitarlab_sessions',
  RIFFS: 'guitarlab_riffs',
  TUNING_ID: 'guitarlab_tuning_id',
  A4_PITCH: 'guitarlab_a4',
  METRONOME: 'guitarlab_metronome',
  LAST_SONG_ID: 'guitarlab_last_song_id',
  HAS_CHOSEN_FIRST_TAB: 'guitarlab_has_chosen_tab',
};

// Initial practice sessions for realistic demo
const INITIAL_SESSIONS: PracticeSession[] = [
  {
    id: 'sess_1',
    dateMillis: Date.now() - 86400000 * 2,
    durationMinutes: 45,
    category: 'Technique',
    notes: 'Major & Minor Pentatonic speed drills at 110-130 BPM.',
  },
  {
    id: 'sess_2',
    dateMillis: Date.now() - 86400000 * 1,
    durationMinutes: 30,
    category: 'Song Practice',
    notes: 'Smoke on the Water and Nothing Else Matters intro acoustic arpeggio.',
  },
  {
    id: 'sess_3',
    dateMillis: Date.now() - 3600000 * 3,
    durationMinutes: 40,
    category: 'Theory & Scales',
    notes: 'Dorian & Mixolydian positions across 6 strings. Chord inversions.',
  },
];

export function loadSessions(): PracticeSession[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.SESSIONS);
    if (!raw) {
      saveSessions(INITIAL_SESSIONS);
      return INITIAL_SESSIONS;
    }
    return JSON.parse(raw);
  } catch {
    return INITIAL_SESSIONS;
  }
}

export function saveSessions(sessions: PracticeSession[]): void {
  try {
    localStorage.setItem(STORAGE_KEYS.SESSIONS, JSON.stringify(sessions));
  } catch {
    // ignore
  }
}

export function calculatePracticeStats(sessions: PracticeSession[]): PracticeStats {
  const totalMinutes = sessions.reduce((acc, s) => acc + s.durationMinutes, 0);

  // Calculate streak
  const days = new Set<string>();
  for (const s of sessions) {
    const d = new Date(s.dateMillis);
    days.add(`${d.getFullYear()}-${d.getMonth()}-${d.getDate()}`);
  }

  // Count consecutive days ending today or yesterday
  let streak = 0;
  const now = new Date();
  for (let i = 0; i < 30; i++) {
    const checkDate = new Date(now.getTime() - i * 86400000);
    const key = `${checkDate.getFullYear()}-${checkDate.getMonth()}-${checkDate.getDate()}`;
    if (days.has(key)) {
      streak++;
    } else if (i > 0) {
      break;
    }
  }

  return {
    streakDays: Math.max(streak, 1),
    totalMinutes,
    sessionsCount: sessions.length,
  };
}

export function loadRiffs(): RiffRecording[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.RIFFS);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export function saveRiffs(riffs: RiffRecording[]): void {
  try {
    // Exclude large blob URLs to prevent quota issues; store metadata
    const storable = riffs.map(r => ({ ...r, audioBlobUrl: undefined }));
    localStorage.setItem(STORAGE_KEYS.RIFFS, JSON.stringify(storable));
  } catch {
    // ignore
  }
}

export function loadSavedTuningId(): string {
  return localStorage.getItem(STORAGE_KEYS.TUNING_ID) || 'standard_e';
}

export function saveTuningId(id: string): void {
  localStorage.setItem(STORAGE_KEYS.TUNING_ID, id);
}

export function loadA4Pitch(): number {
  const val = localStorage.getItem(STORAGE_KEYS.A4_PITCH);
  return val ? parseFloat(val) : 440;
}

export function saveA4Pitch(a4: number): void {
  localStorage.setItem(STORAGE_KEYS.A4_PITCH, a4.toString());
}

export function loadLastOpenedSongId(): string | null {
  try {
    return localStorage.getItem(STORAGE_KEYS.LAST_SONG_ID);
  } catch {
    return null;
  }
}

export function saveLastOpenedSongId(songId: string): void {
  try {
    localStorage.setItem(STORAGE_KEYS.LAST_SONG_ID, songId);
    localStorage.setItem(STORAGE_KEYS.HAS_CHOSEN_FIRST_TAB, 'true');
  } catch {
    // ignore
  }
}

export function hasUserChosenFirstTab(): boolean {
  try {
    return localStorage.getItem(STORAGE_KEYS.HAS_CHOSEN_FIRST_TAB) === 'true';
  } catch {
    return false;
  }
}
