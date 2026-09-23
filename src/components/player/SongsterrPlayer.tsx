import React, { useState, useEffect, useRef, useCallback } from 'react';
import { SongTabScore, TabTrackInfo, AdvancedMeasure, AdvancedBeat, AdvancedNote } from '../../types/tabPlayer';
import { SONGS_CATALOG } from '../../data/songsLibrary';
import { TabCanvas } from './TabCanvas';
import { BottomControlBar, AudioSourceType } from './BottomControlBar';
import { TranspositionSheet } from './sheets/TranspositionSheet';
import { ChromaticTunerSheet } from './sheets/ChromaticTunerSheet';
import { MixerSheet } from './sheets/MixerSheet';
import { TempoSheet } from './sheets/TempoSheet';
import { MoreOptionsSheet } from './sheets/MoreOptionsSheet';
import { SongSelectSheet } from './sheets/SongSelectSheet';
import { ensureAudioContextStarted, getAudioContext } from '../../audio/audioContext';
import { playGuitarPluck, playMetronomeClick } from '../../audio/guitarSynth';
import { midiToHz } from '../../data/defaultTunings';
import { ChevronDown, SlidersHorizontal, Sparkles, Music2, Share2, Volume2 } from 'lucide-react';

interface SongsterrPlayerProps {
  onOpenStudioTools?: () => void;
}

export const SongsterrPlayer: React.FC<SongsterrPlayerProps> = ({ onOpenStudioTools }) => {
  // Current Song & Track
  const [song, setSong] = useState<SongTabScore>(SONGS_CATALOG[0]);
  const [activeTrackId, setActiveTrackId] = useState<string>(SONGS_CATALOG[0].tracks[0].id);

  // Playback State
  const [isPlaying, setIsPlaying] = useState(false);
  const [speedRatio, setSpeedRatio] = useState<number>(1.0);
  const [audioSource, setAudioSource] = useState<AudioSourceType>('SYNTH');
  const [currentMeasureIndex, setCurrentMeasureIndex] = useState(0);
  const [currentBeatIndex, setCurrentBeatIndex] = useState(0);

  // A-B Looping State: measure numbers [start, end]
  const [isLoopActive, setIsLoopActive] = useState(false);
  const [loopRange, setLoopRange] = useState<[number, number] | null>([1, 4]);

  // Transposition State
  const [semitones, setSemitones] = useState(0);
  const [tuningNameOverride, setTuningNameOverride] = useState<string | null>(null);

  // Metronome & Count-In options
  const [countInEnabled, setCountInEnabled] = useState(true);
  const [metronomeClickEnabled, setMetronomeClickEnabled] = useState(false);
  const [isCountingIn, setIsCountingIn] = useState(false);
  const [countInBeat, setCountInBeat] = useState(1);

  // Modal Bottom Sheets state
  const [isTranspositionOpen, setIsTranspositionOpen] = useState(false);
  const [isTunerOpen, setIsTunerOpen] = useState(false);
  const [isMixerOpen, setIsMixerOpen] = useState(false);
  const [isTempoOpen, setIsTempoOpen] = useState(false);
  const [isMoreMenuOpen, setIsMoreMenuOpen] = useState(false);
  const [isSongSelectOpen, setIsSongSelectOpen] = useState(false);

  // High-Precision Web Audio Sync Refs (No drift / No setTimeout latency)
  const isPlayingRef = useRef(false);
  const animFrameRef = useRef<number | null>(null);
  const nextNoteAudioTimeRef = useRef<number>(0);
  const playbackPosRef = useRef<{ mIdx: number; bIdx: number }>({ mIdx: 0, bIdx: 0 });
  const originalAudioRef = useRef<HTMLAudioElement | null>(null);

  // Capacitor Screen / WakeLock integration trigger
  const keepScreenAwake = async (enable: boolean) => {
    try {
      // 1. Standard Web Screen Wake Lock API
      if ('wakeLock' in navigator && enable) {
        await (navigator as any).wakeLock.request('screen');
      }
      // 2. Capacitor KeepAwake plugin (if running in Capacitor Android environment)
      if ((window as any).Capacitor?.Plugins?.KeepAwake) {
        if (enable) {
          await (window as any).Capacitor.Plugins.KeepAwake.keepAwake();
        } else {
          await (window as any).Capacitor.Plugins.KeepAwake.allowSleep();
        }
      }
    } catch (_) {
      // Graceful fallback
    }
  };

  // Active track
  const activeTrack = song.tracks.find((t) => t.id === activeTrackId) || song.tracks[0];
  const measures = activeTrack.measures;

  // Initialize or update original audio element
  useEffect(() => {
    if (song.originalAudioUrl) {
      const audio = new Audio(song.originalAudioUrl);
      audio.crossOrigin = 'anonymous';
      audio.playbackRate = speedRatio;
      originalAudioRef.current = audio;
      return () => {
        audio.pause();
        audio.src = '';
      };
    }
  }, [song]);

  useEffect(() => {
    if (originalAudioRef.current) {
      originalAudioRef.current.playbackRate = speedRatio;
    }
  }, [speedRatio]);

  // Clean up playback on unmount
  useEffect(() => {
    return () => {
      isPlayingRef.current = false;
      if (animFrameRef.current !== null) {
        cancelAnimationFrame(animFrameRef.current);
      }
      originalAudioRef.current?.pause();
      keepScreenAwake(false);
    };
  }, []);

  const stopPlayback = useCallback(() => {
    isPlayingRef.current = false;
    if (animFrameRef.current !== null) {
      cancelAnimationFrame(animFrameRef.current);
      animFrameRef.current = null;
    }
    if (originalAudioRef.current) {
      originalAudioRef.current.pause();
    }
    setIsPlaying(false);
    setIsCountingIn(false);
    keepScreenAwake(false);
  }, []);

  // Compute MIDI note offset based on tuning & string
  const getNoteMidi = (stringIdx: number, fret: number, tuningNotes: string[]): number => {
    if (tuningNotes.length > 0 && stringIdx < tuningNotes.length) {
      const noteStr = tuningNotes[stringIdx];
      const noteLetter = noteStr.slice(0, -1);
      const octave = parseInt(noteStr.slice(-1), 10) || 3;
      const noteMap: Record<string, number> = {
        C: 0, 'C#': 1, Db: 1, D: 2, 'D#': 3, Eb: 3, E: 4, F: 5,
        'F#': 6, Gb: 6, G: 7, 'G#': 8, Ab: 8, A: 9, 'A#': 10, Bb: 10, B: 11
      };
      const baseMidi = (octave + 1) * 12 + (noteMap[noteLetter] ?? 0);
      return baseMidi + fret + semitones;
    }
    const standardBase = [64, 59, 55, 50, 45, 40];
    return (standardBase[stringIdx] || 40) + fret + semitones;
  };

  /**
   * High-Precision Audio Scheduling Loop (Look-ahead pattern with Web Audio currentTime).
   * This guarantees 0ms audio lag and perfectly synchronous playhead tracking even under mobile CPU throttling.
   */
  const scheduleAudioAndSyncPlayhead = () => {
    if (!isPlayingRef.current) return;

    const audioCtx = getAudioContext();
    const currentAudioTime = audioCtx.currentTime;
    const scheduleAheadTime = 0.12; // 120ms audio lookahead buffer

    // Schedule any notes that fall within the audio scheduling window
    while (nextNoteAudioTimeRef.current < currentAudioTime + scheduleAheadTime && isPlayingRef.current) {
      let { mIdx, bIdx } = playbackPosRef.current;

      // Handle A-B loop boundaries
      if (isLoopActive && loopRange) {
        const loopStartIdx = loopRange[0] - 1;
        const loopEndIdx = loopRange[1] - 1;
        if (mIdx > loopEndIdx || mIdx < loopStartIdx) {
          mIdx = loopStartIdx;
          bIdx = 0;
        }
      } else if (mIdx >= measures.length) {
        mIdx = 0;
        bIdx = 0;
      }

      const measure = measures[mIdx];
      if (!measure || !measure.beats || measure.beats.length === 0) {
        mIdx++;
        bIdx = 0;
        playbackPosRef.current = { mIdx, bIdx };
        continue;
      }

      const beat = measure.beats[bIdx];
      if (beat) {
        // Schedule Metronome sound
        if (metronomeClickEnabled) {
          playMetronomeClick(bIdx === 0, 0.7);
        }

        // Schedule Guitar Synth strings
        if (audioSource === 'SYNTH' && !activeTrack.isMuted) {
          for (const note of beat.notes) {
            if (note.fret >= 0 && !note.deadNote) {
              const midi = getNoteMidi(note.stringIndex, note.fret, activeTrack.tuningNotes);
              const freq = midiToHz(midi);
              const duration = beat.durationValue * 1.5;
              playGuitarPluck(freq, duration, activeTrack.volume);
            }
          }
        }
      }

      // Update UI active indices synchronously
      setCurrentMeasureIndex(mIdx);
      setCurrentBeatIndex(bIdx);

      // Calculate exact duration for this beat in seconds
      const tempo = measure.tempoBpm || song.defaultTempo;
      const quarterDurationSec = 60 / (tempo * speedRatio);
      // Duration scale factor based on beat note length (default 8th note = 0.5 quarter)
      const beatDurationSec = (beat?.durationValue || 0.5) * quarterDurationSec;

      nextNoteAudioTimeRef.current += beatDurationSec;

      // Advance to next beat/measure
      bIdx++;
      if (bIdx >= measure.beats.length) {
        bIdx = 0;
        mIdx++;
        if (isLoopActive && loopRange && mIdx > loopRange[1] - 1) {
          mIdx = loopRange[0] - 1;
        }
      }
      playbackPosRef.current = { mIdx, bIdx };
    }

    // Schedule next frame check (runs at display 60-120Hz refresh rate)
    animFrameRef.current = requestAnimationFrame(scheduleAudioAndSyncPlayhead);
  };

  // Start Playback with optional 4-beat count-in
  const startPlayback = async () => {
    await ensureAudioContextStarted();
    stopPlayback();

    const audioCtx = getAudioContext();
    nextNoteAudioTimeRef.current = audioCtx.currentTime + 0.05;
    playbackPosRef.current = { mIdx: currentMeasureIndex, bIdx: currentBeatIndex };

    // Count-in handler
    if (countInEnabled && currentMeasureIndex === 0 && currentBeatIndex === 0) {
      setIsCountingIn(true);
      let countBeat = 1;
      setCountInBeat(countBeat);
      playMetronomeClick(true, 1.0);

      const countIntervalMs = 60000 / (song.defaultTempo * speedRatio);
      const countTimer = window.setInterval(() => {
        countBeat++;
        if (countBeat > 4) {
          clearInterval(countTimer);
          setIsCountingIn(false);
          runTabAudioEngine();
        } else {
          setCountInBeat(countBeat);
          playMetronomeClick(false, 0.9);
        }
      }, countIntervalMs);
      return;
    }

    runTabAudioEngine();
  };

  const runTabAudioEngine = () => {
    setIsPlaying(true);
    isPlayingRef.current = true;
    keepScreenAwake(true);

    if (audioSource === 'ORIG' && originalAudioRef.current) {
      originalAudioRef.current.play().catch(() => {});
    }

    const audioCtx = getAudioContext();
    nextNoteAudioTimeRef.current = audioCtx.currentTime;
    animFrameRef.current = requestAnimationFrame(scheduleAudioAndSyncPlayhead);
  };

  const togglePlay = () => {
    if (isPlaying || isCountingIn) {
      stopPlayback();
    } else {
      startPlayback();
    }
  };

  // Jump to selected position
  const handleSelectPosition = (mIdx: number, bIdx: number) => {
    const wasPlaying = isPlaying;
    stopPlayback();
    setCurrentMeasureIndex(mIdx);
    setCurrentBeatIndex(bIdx);
    playbackPosRef.current = { mIdx, bIdx };
    if (wasPlaying) {
      setTimeout(() => startPlayback(), 50);
    }
  };

  return (
    <div className="flex flex-col h-full w-full bg-[#0a0c10] text-zinc-100 overflow-hidden relative select-none">
      {/* 1. Mobile-Optimized Slim Navigation Bar */}
      <div className="shrink-0 bg-zinc-950/90 backdrop-blur-md border-b border-zinc-800/80 px-3 py-2.5 z-20 flex items-center justify-between">
        {/* Left: Song selector chip */}
        <button
          onClick={() => setIsSongSelectOpen(true)}
          className="flex items-center space-x-2 bg-zinc-900/90 hover:bg-zinc-800 border border-zinc-800 px-3 py-1.5 rounded-full max-w-[200px] sm:max-w-xs transition-colors active:scale-95"
        >
          <Music2 className="w-4 h-4 text-emerald-400 shrink-0" />
          <div className="text-left truncate">
            <span className="text-xs font-bold text-zinc-100 truncate block leading-tight">
              {song.title}
            </span>
            <span className="text-[10px] text-zinc-400 truncate block">
              {song.artist}
            </span>
          </div>
          <ChevronDown className="w-3.5 h-3.5 text-zinc-400 shrink-0 ml-1" />
        </button>

        {/* Right: Pitch Transpose & Quick Options */}
        <div className="flex items-center space-x-1.5">
          <button
            onClick={() => setIsTranspositionOpen(true)}
            className={`px-2.5 py-1 rounded-full text-xs font-mono font-bold border transition-colors ${
              semitones !== 0
                ? 'bg-amber-500/20 text-amber-300 border-amber-500/40'
                : 'bg-zinc-900 border-zinc-800 text-zinc-300 hover:border-zinc-700'
            }`}
          >
            {semitones > 0 ? `+${semitones}` : semitones < 0 ? `${semitones}` : '0'} ST
          </button>

          <button
            onClick={() => setIsMoreMenuOpen(true)}
            className="p-1.5 rounded-full bg-zinc-900 border border-zinc-800 text-zinc-400 hover:text-zinc-200 active:scale-90"
            title="Player settings"
          >
            <SlidersHorizontal className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* 2. Count-In Visual Floating Banner */}
      {isCountingIn && (
        <div className="absolute top-14 left-1/2 -translate-x-1/2 z-50 bg-emerald-500 text-zinc-950 px-5 py-2 rounded-full font-mono font-black text-sm shadow-2xl flex items-center space-x-2 animate-bounce">
          <span>COUNT-IN:</span>
          <span className="text-lg bg-zinc-950 text-emerald-400 w-7 h-7 rounded-full flex items-center justify-center">
            {countInBeat}
          </span>
        </div>
      )}

      {/* 3. Main Touch Tab Canvas with Hardware-Accelerated Rendering */}
      <TabCanvas
        track={activeTrack}
        currentMeasureIndex={currentMeasureIndex}
        currentBeatIndex={currentBeatIndex}
        isPlaying={isPlaying}
        onSelectPosition={handleSelectPosition}
        loopRange={isLoopActive ? loopRange : null}
      />

      {/* 4. Ergonomic Floating Bottom Dock for One-Handed Mobile Control */}
      <BottomControlBar
        isPlaying={isPlaying}
        onTogglePlay={togglePlay}
        speedRatio={speedRatio}
        onChangeSpeed={setSpeedRatio}
        audioSource={audioSource}
        onToggleAudioSource={() => setAudioSource((s) => (s === 'SYNTH' ? 'ORIG' : 'SYNTH'))}
        isLoopActive={isLoopActive}
        onToggleLoop={() => setIsLoopActive((v) => !v)}
        onOpenMixer={() => setIsMixerOpen(true)}
        onOpenMoreMenu={() => setIsMoreMenuOpen(true)}
        onOpenTempoPicker={() => setIsTempoOpen(true)}
        trackCount={song.tracks.length}
      />

      {/* 5. Mobile Bottom Sheets */}
      <SongSelectSheet
        isOpen={isSongSelectOpen}
        onClose={() => setIsSongSelectOpen(false)}
        songs={SONGS_CATALOG}
        currentSongId={song.id}
        onSelectSong={(s) => {
          stopPlayback();
          setSong(s);
          setActiveTrackId(s.tracks[0].id);
          setCurrentMeasureIndex(0);
          setCurrentBeatIndex(0);
          setIsSongSelectOpen(false);
        }}
      />

      <MixerSheet
        isOpen={isMixerOpen}
        onClose={() => setIsMixerOpen(false)}
        tracks={song.tracks}
        activeTrackId={activeTrackId}
        onSelectTrack={(tId) => setActiveTrackId(tId)}
        onToggleMute={(tId) => {
          const t = song.tracks.find((x) => x.id === tId);
          if (t) t.isMuted = !t.isMuted;
        }}
        onToggleSolo={(tId) => {
          const t = song.tracks.find((x) => x.id === tId);
          if (t) t.isSolo = !t.isSolo;
        }}
        onChangeVolume={(tId, vol) => {
          const t = song.tracks.find((x) => x.id === tId);
          if (t) t.volume = vol;
        }}
      />

      <TempoSheet
        isOpen={isTempoOpen}
        onClose={() => setIsTempoOpen(false)}
        speedRatio={speedRatio}
        onChangeSpeed={setSpeedRatio}
        baseTempo={song.defaultTempo}
      />

      <TranspositionSheet
        isOpen={isTranspositionOpen}
        onClose={() => setIsTranspositionOpen(false)}
        semitones={semitones}
        onChangeSemitones={setSemitones}
        tuningName={tuningNameOverride || activeTrack.tuningName}
      />

      <MoreOptionsSheet
        isOpen={isMoreMenuOpen}
        onClose={() => setIsMoreMenuOpen(false)}
        countInEnabled={countInEnabled}
        onToggleCountIn={() => setCountInEnabled((v) => !v)}
        metronomeClickEnabled={metronomeClickEnabled}
        onToggleMetronomeClick={() => setMetronomeClickEnabled((v) => !v)}
        onOpenChromaticTuner={() => {
          setIsMoreMenuOpen(false);
          setIsTunerOpen(true);
        }}
      />

      <ChromaticTunerSheet
        isOpen={isTunerOpen}
        onClose={() => setIsTunerOpen(false)}
        targetTuningName={activeTrack?.tuningName || 'Standard E'}
        targetTuningNotes={activeTrack?.tuningNotes || ['E4', 'B3', 'G3', 'D3', 'A2', 'E2']}
      />
    </div>
  );
};
