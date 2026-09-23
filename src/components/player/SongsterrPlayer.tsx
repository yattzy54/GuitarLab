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
import { ensureAudioContextStarted } from '../../audio/audioContext';
import { playGuitarPluck, playMetronomeClick } from '../../audio/guitarSynth';
import { midiToHz } from '../../data/defaultTunings';
import { ChevronDown, SlidersHorizontal, Sparkles, Music2, Share2 } from 'lucide-react';

interface SongsterrPlayerProps {
  onOpenStudioTools?: () => void;
}

export const SongsterrPlayer: React.FC<SongsterrPlayerProps> = ({ onOpenStudioTools }) => {
  // Current Song & Track
  const [song, setSong] = useState<SongTabScore>(SONGS_CATALOG[0]); // Dark Clouds by Adept
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
  const [isCopied, setIsCopied] = useState(false);

  // Audio Engine Refs
  const playbackTimerRef = useRef<number | null>(null);
  const originalAudioRef = useRef<HTMLAudioElement | null>(null);

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

  // Sync original audio playback rate with speedRatio
  useEffect(() => {
    if (originalAudioRef.current) {
      originalAudioRef.current.playbackRate = speedRatio;
    }
  }, [speedRatio]);

  // Stop playback on unmount
  useEffect(() => {
    return () => {
      if (playbackTimerRef.current !== null) {
        clearInterval(playbackTimerRef.current);
      }
      originalAudioRef.current?.pause();
    };
  }, []);

  const stopPlayback = useCallback(() => {
    if (playbackTimerRef.current !== null) {
      clearInterval(playbackTimerRef.current);
      playbackTimerRef.current = null;
    }
    if (originalAudioRef.current) {
      originalAudioRef.current.pause();
    }
    setIsPlaying(false);
    setIsCountingIn(false);
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
    // Fallback standard 6-string
    const standardBase = [64, 59, 55, 50, 45, 40];
    return (standardBase[stringIdx] || 40) + fret + semitones;
  };

  // Playback execution loop
  const startPlayback = async () => {
    await ensureAudioContextStarted();
    stopPlayback();

    // Check count-in
    if (countInEnabled && currentMeasureIndex === 0 && currentBeatIndex === 0) {
      setIsCountingIn(true);
      let countBeat = 1;
      setCountInBeat(countBeat);
      playMetronomeClick(true, 1.0);

      const countIntervalMs = (60000 / (song.defaultTempo * speedRatio));
      const countTimer = window.setInterval(() => {
        countBeat++;
        if (countBeat > 4) {
          clearInterval(countTimer);
          setIsCountingIn(false);
          runTabPlayback();
        } else {
          setCountInBeat(countBeat);
          playMetronomeClick(false, 0.9);
        }
      }, countIntervalMs);
      return;
    }

    runTabPlayback();
  };

  const runTabPlayback = () => {
    setIsPlaying(true);

    if (audioSource === 'ORIG' && originalAudioRef.current) {
      originalAudioRef.current.play().catch(() => {});
    }

    let mIdx = currentMeasureIndex;
    let bIdx = currentBeatIndex;

    const tick = () => {
      // Loop boundary check
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
        return;
      }

      const beat = measure.beats[bIdx];
      if (beat) {
        // Metronome click on beat if enabled
        if (metronomeClickEnabled) {
          playMetronomeClick(bIdx === 0, 0.7);
        }

        // Karplus-Strong physical string synthesis in SYNTH mode
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

      setCurrentMeasureIndex(mIdx);
      setCurrentBeatIndex(bIdx);

      bIdx++;
      if (bIdx >= measure.beats.length) {
        bIdx = 0;
        mIdx++;
      }
    };

    // Duration per eighth-note / sixteenth-note slice
    const stepDurationMs = (60000 / (song.defaultTempo * speedRatio)) * 0.5;
    tick();
    playbackTimerRef.current = window.setInterval(tick, stepDurationMs);
  };

  const togglePlay = () => {
    if (isPlaying || isCountingIn) {
      stopPlayback();
    } else {
      startPlayback();
    }
  };

  // Jump to specific measure / beat on tab click
  const handleSelectPosition = (mIdx: number, bIdx: number) => {
    const wasPlaying = isPlaying;
    stopPlayback();
    setCurrentMeasureIndex(mIdx);
    setCurrentBeatIndex(bIdx);
    if (wasPlaying) {
      setTimeout(() => startPlayback(), 50);
    }
  };

  // Toggle A-B Looping
  const handleToggleLoop = () => {
    if (isLoopActive) {
      setIsLoopActive(false);
    } else {
      setIsLoopActive(true);
      // Default loop around current measure
      const start = Math.max(1, currentMeasureIndex + 1);
      const end = Math.min(measures.length, start + 3);
      setLoopRange([start, end]);
    }
  };

  // Copy ASCII Tab to clipboard
  const handleCopyTab = () => {
    let ascii = `${song.title} - ${song.artist}\nTrack: ${activeTrack.name} (${activeTrack.tuningName})\n\n`;
    for (const m of measures) {
      ascii += `Bar ${m.number} [${m.timeSignature[0]}/${m.timeSignature[1]}]\n`;
      const lines = ['e|', 'B|', 'G|', 'D|', 'A|', 'E|'];
      for (let s = 0; s < 6; s++) {
        for (const b of m.beats) {
          const note = b.notes.find((n) => n.stringIndex === s);
          lines[s] += note ? `-${note.fret}-` : '---';
        }
        lines[s] += '|';
      }
      ascii += lines.join('\n') + '\n\n';
    }
    navigator.clipboard.writeText(ascii);
    setIsCopied(true);
    setTimeout(() => setIsCopied(false), 2500);
  };

  return (
    <div className="relative min-h-screen bg-[#0d0f12] text-zinc-100 flex flex-col overflow-hidden">
      {/* 2.А. Верхняя информационная панель (Header) */}
      <header className="sticky top-0 z-30 bg-[#12151a]/95 backdrop-blur-md border-b border-zinc-800/80 px-4 py-3">
        <div className="max-w-6xl mx-auto flex items-center justify-between gap-3">
          {/* Song Title, Artist & Song Selector */}
          <button
            onClick={() => setIsSongSelectOpen(true)}
            className="flex items-center space-x-3 text-left group hover:opacity-90 transition-opacity"
          >
            <div className="w-10 h-10 rounded-xl bg-amber-500/15 border border-amber-500/40 flex items-center justify-center text-amber-400 shrink-0">
              <Music2 className="w-5 h-5" />
            </div>

            <div className="min-w-0">
              {/* Title & Artist */}
              <div className="flex items-center space-x-2">
                <h1 className="text-base sm:text-lg font-black text-white tracking-tight truncate leading-tight">
                  {song.title}
                </h1>
                <span className="text-sm font-semibold text-zinc-400 truncate leading-tight">
                  — {song.artist}
                </span>
                <ChevronDown className="w-4 h-4 text-zinc-500 group-hover:text-amber-400 transition-colors shrink-0" />
              </div>

              {/* Revision Date & Active Track Metadata */}
              <div className="flex flex-wrap items-center gap-x-2 gap-y-0.5 text-[11px] text-zinc-400 font-mono mt-0.5">
                <span className="text-zinc-500 font-bold uppercase tracking-wider">
                  РЕВИЗИЯ ОТ: {song.revisionDate}
                </span>
                <span>•</span>
                <span className="text-amber-400 font-medium truncate">
                  {activeTrack.name} ({tuningNameOverride || activeTrack.tuningName})
                </span>
              </div>
            </div>
          </button>

          {/* Right Header Action: Quick Track Switch / Mixer */}
          <div className="flex items-center space-x-2 shrink-0">
            <button
              onClick={() => setIsMixerOpen(true)}
              className="px-3 py-1.5 rounded-xl bg-zinc-800/80 hover:bg-zinc-700/80 border border-zinc-700 text-xs font-semibold text-zinc-200 flex items-center space-x-1.5 transition-colors"
            >
              <SlidersHorizontal className="w-3.5 h-3.5 text-amber-400" />
              <span className="hidden sm:inline">Партии ({song.tracks.length})</span>
            </button>
          </div>
        </div>
      </header>

      {/* Count-In Banner if active */}
      {isCountingIn && (
        <div className="bg-emerald-500 text-zinc-950 py-1.5 px-4 text-center font-black text-sm tracking-wider flex items-center justify-center space-x-2 animate-pulse z-20">
          <span>ОТСЧЁТ ПЕРЕД СТАРТОМ:</span>
          <span className="text-base font-mono underline">{countInBeat} / 4</span>
        </div>
      )}

      {/* 2.Б. Область отображения табулатуры (Tab Canvas - Maximized Canvas) */}
      <TabCanvas
        track={activeTrack}
        currentMeasureIndex={currentMeasureIndex}
        currentBeatIndex={currentBeatIndex}
        isPlaying={isPlaying}
        onSelectPosition={handleSelectPosition}
        loopRange={isLoopActive ? loopRange : null}
      />

      {/* 2.В. Нижняя плавающая панель управления (Bottom Control Bar) */}
      <BottomControlBar
        isPlaying={isPlaying}
        onTogglePlay={togglePlay}
        speedRatio={speedRatio}
        onChangeSpeed={(newSpeed) => setSpeedRatio(newSpeed)}
        audioSource={audioSource}
        onToggleAudioSource={() => setAudioSource(audioSource === 'ORIG' ? 'SYNTH' : 'ORIG')}
        isLoopActive={isLoopActive}
        onToggleLoop={handleToggleLoop}
        onOpenMixer={() => setIsMixerOpen(true)}
        onOpenMoreMenu={() => setIsMoreMenuOpen(true)}
        onOpenTempoPicker={() => setIsTempoOpen(true)}
        trackCount={song.tracks.length}
      />

      {/* 3. Модальные панели (Bottom Sheets) */}
      {/* А. Панель «Смещение тона» (Transposition / Tuning Sheet) */}
      <TranspositionSheet
        isOpen={isTranspositionOpen}
        onClose={() => setIsTranspositionOpen(false)}
        semitones={semitones}
        onSemitonesChange={(semi) => setSemitones(semi)}
        currentTuning={tuningNameOverride || activeTrack.tuningName}
        onApplyTuningPreset={(name) => {
          setTuningNameOverride(name);
        }}
      />

      {/* Б. Встроенный «Хроматический тюнер» (Chromatic Tuner Sheet) */}
      <ChromaticTunerSheet
        isOpen={isTunerOpen}
        onClose={() => setIsTunerOpen(false)}
        targetTuningName={tuningNameOverride || activeTrack.tuningName}
        targetTuningNotes={activeTrack.tuningNotes}
      />

      {/* В. Панель «Микшер дорожек» (MixerSheet) */}
      <MixerSheet
        isOpen={isMixerOpen}
        onClose={() => setIsMixerOpen(false)}
        tracks={song.tracks}
        activeTrackId={activeTrackId}
        onSelectActiveTrack={(id) => {
          setActiveTrackId(id);
          setIsMixerOpen(false);
        }}
        onUpdateTrackVolume={(id, vol) => {
          setSong((prev) => ({
            ...prev,
            tracks: prev.tracks.map((t) => (t.id === id ? { ...t, volume: vol } : t)),
          }));
        }}
        onToggleTrackMute={(id) => {
          setSong((prev) => ({
            ...prev,
            tracks: prev.tracks.map((t) => (t.id === id ? { ...t, isMuted: !t.isMuted } : t)),
          }));
        }}
        onToggleTrackSolo={(id) => {
          setSong((prev) => ({
            ...prev,
            tracks: prev.tracks.map((t) => (t.id === id ? { ...t, isSolo: !t.isSolo } : t)),
          }));
        }}
      />

      {/* Г. Панель «Темп и скорость» (TempoSheet) */}
      <TempoSheet
        isOpen={isTempoOpen}
        onClose={() => setIsTempoOpen(false)}
        speedRatio={speedRatio}
        onSpeedRatioChange={(speed) => setSpeedRatio(speed)}
        baseTempoBpm={song.defaultTempo}
      />

      {/* Д. Панель «Дополнительные опции» (MoreOptionsSheet) */}
      <MoreOptionsSheet
        isOpen={isMoreMenuOpen}
        onClose={() => setIsMoreMenuOpen(false)}
        onOpenTuner={() => setIsTunerOpen(true)}
        onOpenTransposition={() => setIsTranspositionOpen(true)}
        onOpenSongSelect={() => setIsSongSelectOpen(true)}
        countInEnabled={countInEnabled}
        onToggleCountIn={() => setCountInEnabled(!countInEnabled)}
        metronomeClickEnabled={metronomeClickEnabled}
        onToggleMetronomeClick={() => setMetronomeClickEnabled(!metronomeClickEnabled)}
        onCopyTab={handleCopyTab}
        isCopied={isCopied}
        onOpenStudioTools={() => onOpenStudioTools?.()}
      />

      {/* Каталог композиций (SongSelectSheet) */}
      <SongSelectSheet
        isOpen={isSongSelectOpen}
        onClose={() => setIsSongSelectOpen(false)}
        currentSongId={song.id}
        onSelectSong={(newSong) => {
          stopPlayback();
          setSong(newSong);
          setActiveTrackId(newSong.tracks[0].id);
          setCurrentMeasureIndex(0);
          setCurrentBeatIndex(0);
          setTuningNameOverride(null);
          setSemitones(0);
        }}
      />
    </div>
  );
};
