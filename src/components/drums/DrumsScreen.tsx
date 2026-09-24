import React, { useState, useEffect, useRef, useMemo, useCallback } from 'react';
import {
  Drum,
  Play,
  Pause,
  RotateCcw,
  Volume2,
  VolumeX,
  Search,
  Sparkles,
  Timer,
  Sliders,
  Music,
  Heart,
  Flame,
  Check,
  Zap,
} from 'lucide-react';
import {
  DRUM_PATTERNS,
  DRUM_GENRES,
  DrumPattern,
} from '../../data/drumPatterns';
import { drumEngine, DrumInstrument, DrumKitType } from '../../audio/drumAudioEngine';
import { Studio3DBadge, StudioCard, StudioPill } from '../common/Studio3DComponents';

const FAVORITES_KEY = 'guitarlab_favorite_drum_patterns';

export const DrumsScreen: React.FC = () => {
  // State
  const [selectedPattern, setSelectedPattern] = useState<DrumPattern>(() => DRUM_PATTERNS[0]);
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentKit, setCurrentKit] = useState<DrumKitType>('rock');
  const [bpm, setBpm] = useState<number>(() => DRUM_PATTERNS[0].bpm);
  const [currentStep, setCurrentStep] = useState<number>(-1);
  const [selectedGenre, setSelectedGenre] = useState<string>('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [swingAmount, setSwingAmount] = useState(0);
  const [isMetronomeOn, setIsMetronomeOn] = useState(false);
  const [volume, setVolume] = useState(85);
  const [activePad, setActivePad] = useState<DrumInstrument | null>(null);

  // Editable sequence tracks (cloned from selected pattern)
  const [activeTracks, setActiveTracks] = useState(() => ({
    ...DRUM_PATTERNS[0].tracks,
  }));

  // Tap tempo state
  const tapTimesRef = useRef<number[]>([]);

  // Favorite patterns in localStorage
  const [favorites, setFavorites] = useState<string[]>(() => {
    try {
      const saved = localStorage.getItem(FAVORITES_KEY);
      return saved ? JSON.parse(saved) : [];
    } catch {
      return [];
    }
  });

  const toggleFavorite = (id: string, e?: React.MouseEvent) => {
    e?.stopPropagation();
    setFavorites((prev) => {
      const next = prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id];
      localStorage.setItem(FAVORITES_KEY, JSON.stringify(next));
      return next;
    });
  };

  // Connect drumEngine callbacks
  useEffect(() => {
    drumEngine.setCallbacks(
      (step) => setCurrentStep(step),
      (playing) => setIsPlaying(playing)
    );
    return () => {
      drumEngine.stop();
    };
  }, []);

  // Update engine tempo when bpm changes
  useEffect(() => {
    drumEngine.setTempo(bpm);
  }, [bpm]);

  // Update engine swing
  useEffect(() => {
    drumEngine.setSwing(swingAmount / 100);
  }, [swingAmount]);

  // Update engine metronome
  useEffect(() => {
    drumEngine.setMetronome(isMetronomeOn);
  }, [isMetronomeOn]);

  // Update engine volume
  useEffect(() => {
    drumEngine.setMasterVolume(volume / 100);
  }, [volume]);

  // Synchronize engine pattern when activeTracks changes
  useEffect(() => {
    if (selectedPattern) {
      const updatedPattern: DrumPattern = {
        ...selectedPattern,
        tracks: activeTracks,
      };
      drumEngine.setPattern(updatedPattern);
    }
  }, [activeTracks, selectedPattern]);

  // Select pattern handler
  const handleSelectPattern = useCallback(
    (pattern: DrumPattern) => {
      const wasPlaying = isPlaying;
      if (isPlaying) {
        drumEngine.stop();
      }
      setSelectedPattern(pattern);
      setBpm(pattern.bpm);
      setActiveTracks({ ...pattern.tracks });

      if (wasPlaying) {
        setTimeout(() => {
          drumEngine.start(pattern, pattern.bpm);
        }, 60);
      }
    },
    [isPlaying]
  );

  // Play / Pause toggle
  const togglePlay = async () => {
    if (isPlaying) {
      drumEngine.stop();
    } else {
      const currentPatternWithTracks: DrumPattern = {
        ...selectedPattern,
        tracks: activeTracks,
      };
      await drumEngine.start(currentPatternWithTracks, bpm);
    }
  };

  // Tap tempo handler
  const handleTapTempo = () => {
    const now = performance.now();
    const taps = tapTimesRef.current;
    if (taps.length > 0 && now - taps[taps.length - 1] > 2000) {
      taps.length = 0; // reset if paused > 2s
    }
    taps.push(now);
    if (taps.length > 4) taps.shift();

    if (taps.length >= 2) {
      const intervals: number[] = [];
      for (let i = 1; i < taps.length; i++) {
        intervals.push(taps[i] - taps[i - 1]);
      }
      const avgInterval = intervals.reduce((a, b) => a + b, 0) / intervals.length;
      const calculatedBpm = Math.round(60000 / avgInterval);
      if (calculatedBpm >= 40 && calculatedBpm <= 260) {
        setBpm(calculatedBpm);
      }
    }
  };

  // Reset pattern to default steps
  const handleResetPattern = () => {
    setActiveTracks({ ...selectedPattern.tracks });
    setBpm(selectedPattern.bpm);
  };

  // Toggle single step in grid
  const handleToggleStep = (trackName: keyof typeof activeTracks, stepIdx: number) => {
    setActiveTracks((prev) => {
      const currentArr = [...(prev[trackName] || [])];
      currentArr[stepIdx] = !currentArr[stepIdx];
      return {
        ...prev,
        [trackName]: currentArr,
      };
    });
  };

  // Trigger manual drum hit pad
  const handlePadHit = (inst: DrumInstrument) => {
    setActivePad(inst);
    drumEngine.playVoice(inst, undefined, 0.95);
    setTimeout(() => {
      setActivePad((curr) => (curr === inst ? null : curr));
    }, 120);
  };

  // Filtered patterns
  const filteredPatterns = useMemo(() => {
    return DRUM_PATTERNS.filter((pattern) => {
      const matchesGenre =
        selectedGenre === 'All'
          ? true
          : selectedGenre === 'Favorites'
          ? favorites.includes(pattern.id)
          : pattern.genre === selectedGenre;

      const q = searchQuery.toLowerCase().trim();
      const matchesSearch =
        !q ||
        pattern.name.toLowerCase().includes(q) ||
        pattern.genre.toLowerCase().includes(q) ||
        pattern.description.toLowerCase().includes(q) ||
        (pattern.artistRef && pattern.artistRef.toLowerCase().includes(q));

      return matchesGenre && matchesSearch;
    });
  }, [selectedGenre, searchQuery, favorites]);

  // Step labels (e.g. 1 2 3 4 for 16ths)
  const stepsCount = selectedPattern.stepsCount || 16;
  const isCompound = selectedPattern.timeSignature === '6/8' || selectedPattern.timeSignature === '12/8';

  const trackLabels: Array<{
    key: keyof typeof activeTracks;
    label: string;
    sublabel: string;
    color: string;
    instrument: DrumInstrument;
  }> = [
    { key: 'crash', label: 'Crash', sublabel: 'Тарелка', color: 'border-yellow-400/40 bg-yellow-500/20 text-yellow-300', instrument: 'crash' },
    { key: 'ride', label: 'Ride', sublabel: 'Тарелка', color: 'border-sky-400/40 bg-sky-500/20 text-sky-300', instrument: 'ride' },
    { key: 'hihatOpen', label: 'HH Open', sublabel: 'Открытый', color: 'border-cyan-400/40 bg-cyan-500/20 text-cyan-300', instrument: 'hihatOpen' },
    { key: 'hihatClosed', label: 'HH Close', sublabel: 'Хай-хэт', color: 'border-teal-400/40 bg-teal-500/20 text-teal-300', instrument: 'hihatClosed' },
    { key: 'snare', label: 'Snare', sublabel: 'Малый', color: 'border-rose-400/40 bg-rose-500/20 text-rose-300', instrument: 'snare' },
    { key: 'tomHigh', label: 'Tom Hi', sublabel: 'Альт', color: 'border-purple-400/40 bg-purple-500/20 text-purple-300', instrument: 'tomHigh' },
    { key: 'tomLow', label: 'Tom Low', sublabel: 'Флор том', color: 'border-indigo-400/40 bg-indigo-500/20 text-indigo-300', instrument: 'tomLow' },
    { key: 'kick', label: 'Kick', sublabel: 'Бочка', color: 'border-amber-400/40 bg-amber-500/20 text-amber-300', instrument: 'kick' },
    { key: 'percussion', label: 'Perc', sublabel: 'Clap / Cowbell', color: 'border-emerald-400/40 bg-emerald-500/20 text-emerald-300', instrument: 'percussion' },
  ];

  return (
    <div className="max-w-6xl mx-auto px-4 py-6 space-y-6">
      {/* Header Banner */}
      <StudioCard>
        <div className="p-5 flex flex-col md:flex-row md:items-center justify-between gap-5">
          <div className="flex items-center space-x-4">
            <Studio3DBadge icon={Drum} accent="ruby" size="lg" />
            <div>
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold uppercase tracking-wider text-rose-400">
                  Drum Machine & Rhythm Studio
                </span>
                <span className="px-2 py-0.5 rounded-full bg-rose-500/10 text-rose-400 border border-rose-500/30 text-[10px] font-bold">
                  100 Паттернов
                </span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-black text-white tracking-tight flex items-center gap-2">
                {selectedPattern.name}
              </h1>
              <p className="text-xs text-zinc-400 mt-1 max-w-xl">
                {selectedPattern.description}{' '}
                {selectedPattern.artistRef && (
                  <span className="text-amber-400 font-semibold">
                    (в стиле {selectedPattern.artistRef})
                  </span>
                )}
              </p>
            </div>
          </div>

          {/* Master Transport Button */}
          <div className="flex items-center space-x-3">
            <button
              onClick={togglePlay}
              className={`px-6 py-3.5 rounded-2xl font-black text-sm flex items-center space-x-3 shadow-xl transition-all transform active:scale-95 cursor-pointer ${
                isPlaying
                  ? 'bg-gradient-to-r from-rose-500 to-red-600 text-white shadow-[0_0_20px_rgba(244,63,94,0.5)] border border-rose-400'
                  : 'bg-gradient-to-r from-emerald-500 to-teal-500 text-zinc-950 shadow-[0_0_20px_rgba(16,185,129,0.4)] border border-emerald-300 hover:brightness-110'
              }`}
            >
              {isPlaying ? (
                <>
                  <Pause className="w-5 h-5 fill-current" />
                  <span>ПАУЗА</span>
                </>
              ) : (
                <>
                  <Play className="w-5 h-5 fill-current" />
                  <span>ИГРАТЬ РИТМ</span>
                </>
              )}
            </button>
            <button
              onClick={handleResetPattern}
              className="p-3.5 rounded-2xl bg-[#151C2A] hover:bg-[#1E273A] border border-[#27344D] text-zinc-400 hover:text-white transition-colors cursor-pointer"
              title="Сбросить к исходному паттерну"
            >
              <RotateCcw className="w-5 h-5" />
            </button>
          </div>
        </div>
      </StudioCard>

      {/* Drum Kits Selector Bar */}
      <StudioCard>
        <div className="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div>
            <div className="text-xs font-black text-amber-400 tracking-wider uppercase">
              DRUM KIT
            </div>
            <div className="text-xs text-zinc-400 mt-0.5">
              {currentKit === 'rock' && 'Rock Kit — акустический панчевый бас и кленовый малый барабан'}
              {currentKit === 'metal' && 'Metal Kit — кликающий скоростной триггер и острый стальной малый'}
              {currentKit === 'pop' && 'Pop Studio — современный плотный округлый панч и мягкий клэп'}
              {currentKit === 'electronic' && 'Electronic — глубокий 808 саб-бас и аналоговые 909 звуки'}
            </div>
          </div>
          <div className="flex items-center gap-2">
            {[
              { id: 'rock', label: 'Rock' },
              { id: 'metal', label: 'Metal' },
              { id: 'pop', label: 'Pop' },
              { id: 'electronic', label: 'Electronic' },
            ].map((kit) => {
              const isSelected = currentKit === kit.id;
              return (
                <button
                  key={kit.id}
                  onClick={() => {
                    setCurrentKit(kit.id as DrumKitType);
                    drumEngine.setDrumKit(kit.id as DrumKitType);
                  }}
                  className={`px-3.5 py-2 rounded-xl text-xs font-black transition-all cursor-pointer border ${
                    isSelected
                      ? 'bg-amber-500/20 text-amber-300 border-amber-500/60 shadow-[0_0_12px_rgba(245,158,11,0.25)]'
                      : 'bg-[#151C2A] text-zinc-400 border-[#27344D] hover:text-white hover:border-zinc-600'
                  }`}
                >
                  {kit.label}
                </button>
              );
            })}
          </div>
        </div>
      </StudioCard>

      {/* Main Studio Controls: Tempo & Playback Controls */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Card 1: Tempo / BPM Control */}
        <StudioCard className="lg:col-span-2">
          <div className="p-5 space-y-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2.5">
                <Timer className="w-5 h-5 text-amber-400" />
                <span className="text-sm font-bold text-white uppercase tracking-wider">
                  Темп воспроизведения ударных
                </span>
              </div>
              <div className="flex items-center space-x-2">
                <button
                  onClick={() => setBpm((b) => Math.max(30, Math.round(b / 2)))}
                  className="px-2.5 py-1 rounded-lg bg-[#18202E] border border-[#2B374C] text-[11px] font-bold text-zinc-300 hover:text-white hover:bg-[#202A3D] cursor-pointer"
                  title="Half-time (0.5x)"
                >
                  0.5x
                </button>
                <button
                  onClick={() => setBpm(selectedPattern.bpm)}
                  className="px-2.5 py-1 rounded-lg bg-[#18202E] border border-[#2B374C] text-[11px] font-bold text-amber-400 hover:bg-[#202A3D] cursor-pointer"
                  title="Оригинальный темп паттерна"
                >
                  Orig ({selectedPattern.bpm})
                </button>
                <button
                  onClick={() => setBpm((b) => Math.min(260, Math.round(b * 2)))}
                  className="px-2.5 py-1 rounded-lg bg-[#18202E] border border-[#2B374C] text-[11px] font-bold text-zinc-300 hover:text-white hover:bg-[#202A3D] cursor-pointer"
                  title="Double-time (2x)"
                >
                  2.0x
                </button>
              </div>
            </div>

            {/* Large BPM Display & Increments */}
            <div className="flex flex-col sm:flex-row items-center justify-between gap-4 p-4 rounded-2xl bg-[#0D121B] border border-[#20293B]">
              <div className="flex items-center space-x-4">
                <div className="text-center sm:text-left">
                  <div className="text-4xl sm:text-5xl font-black text-amber-400 font-mono tracking-tight leading-none">
                    {bpm}
                  </div>
                  <div className="text-[11px] text-zinc-400 font-bold uppercase tracking-wider mt-1">
                    BPM (Ударов в минуту)
                  </div>
                </div>
              </div>

              {/* Step Buttons */}
              <div className="flex items-center space-x-1.5">
                <button
                  onClick={() => setBpm((b) => Math.max(30, b - 5))}
                  className="w-10 h-10 rounded-xl bg-[#161D2B] hover:bg-[#202A3D] text-zinc-300 font-bold text-xs border border-[#263246] transition-colors cursor-pointer"
                >
                  -5
                </button>
                <button
                  onClick={() => setBpm((b) => Math.max(30, b - 1))}
                  className="w-10 h-10 rounded-xl bg-[#161D2B] hover:bg-[#202A3D] text-zinc-300 font-bold text-sm border border-[#263246] transition-colors cursor-pointer"
                >
                  -1
                </button>
                <button
                  onClick={() => setBpm((b) => Math.min(260, b + 1))}
                  className="w-10 h-10 rounded-xl bg-[#161D2B] hover:bg-[#202A3D] text-zinc-300 font-bold text-sm border border-[#263246] transition-colors cursor-pointer"
                >
                  +1
                </button>
                <button
                  onClick={() => setBpm((b) => Math.min(260, b + 5))}
                  className="w-10 h-10 rounded-xl bg-[#161D2B] hover:bg-[#202A3D] text-zinc-300 font-bold text-xs border border-[#263246] transition-colors cursor-pointer"
                >
                  +5
                </button>

                {/* Tap Tempo Button */}
                <button
                  onClick={handleTapTempo}
                  className="px-4 h-10 rounded-xl bg-gradient-to-r from-amber-500 to-amber-600 text-zinc-950 font-black text-xs uppercase tracking-wider shadow-md hover:brightness-110 active:scale-95 transition-all cursor-pointer flex items-center space-x-1.5"
                >
                  <Zap className="w-3.5 h-3.5 fill-current" />
                  <span>TAP TEMPO</span>
                </button>
              </div>
            </div>

            {/* Slider */}
            <div className="space-y-1.5">
              <input
                type="range"
                min="40"
                max="240"
                value={bpm}
                onChange={(e) => setBpm(Number(e.target.value))}
                className="w-full h-2 bg-[#1A2232] rounded-lg appearance-none cursor-pointer accent-amber-400"
              />
              <div className="flex justify-between text-[10px] text-zinc-500 font-mono">
                <span>40 BPM (Largo)</span>
                <span>80 (Andante)</span>
                <span>120 (Moderato)</span>
                <span>160 (Allegro)</span>
                <span>240 (Presto)</span>
              </div>
            </div>

            {/* Quick BPM Presets */}
            <div className="flex flex-wrap items-center gap-1.5 pt-1">
              <span className="text-[11px] font-bold text-zinc-400 mr-1">Пресеты:</span>
              {[60, 75, 90, 105, 120, 135, 150, 175].map((presetBpm) => (
                <button
                  key={presetBpm}
                  onClick={() => setBpm(presetBpm)}
                  className={`px-2.5 py-1 rounded-lg text-xs font-semibold border transition-all cursor-pointer ${
                    bpm === presetBpm
                      ? 'bg-amber-400 text-zinc-950 border-amber-300 font-bold'
                      : 'bg-[#151D2A] border-[#253043] text-zinc-400 hover:text-white hover:bg-[#1E2838]'
                  }`}
                >
                  {presetBpm}
                </button>
              ))}
            </div>
          </div>
        </StudioCard>

        {/* Card 2: Swing, Volume & Metronome Overlays */}
        <StudioCard>
          <div className="p-5 space-y-4">
            <div className="flex items-center space-x-2">
              <Sliders className="w-4 h-4 text-teal-400" />
              <span className="text-sm font-bold text-white uppercase tracking-wider">
                Грув и микс
              </span>
            </div>

            {/* Master Volume */}
            <div>
              <div className="flex items-center justify-between text-xs mb-1.5">
                <span className="text-zinc-400 font-semibold flex items-center gap-1.5">
                  <Volume2 className="w-3.5 h-3.5 text-zinc-400" />
                  Громкость ударных
                </span>
                <span className="text-white font-mono font-bold">{volume}%</span>
              </div>
              <input
                type="range"
                min="0"
                max="100"
                value={volume}
                onChange={(e) => setVolume(Number(e.target.value))}
                className="w-full h-1.5 bg-[#1A2232] rounded-lg appearance-none cursor-pointer accent-teal-400"
              />
            </div>

            {/* Swing / Groove Slider */}
            <div>
              <div className="flex items-center justify-between text-xs mb-1.5">
                <span className="text-zinc-400 font-semibold">
                  Свинг / Шаффл (Groove)
                </span>
                <span className="text-white font-mono font-bold">
                  {swingAmount === 0 ? 'Прямой (0%)' : `${swingAmount}%`}
                </span>
              </div>
              <input
                type="range"
                min="0"
                max="50"
                step="5"
                value={swingAmount}
                onChange={(e) => setSwingAmount(Number(e.target.value))}
                className="w-full h-1.5 bg-[#1A2232] rounded-lg appearance-none cursor-pointer accent-teal-400"
              />
              <div className="flex justify-between text-[10px] text-zinc-500 mt-0.5">
                <span>Straight 16ths</span>
                <span>Swung / Shuffle</span>
              </div>
            </div>

            {/* Metronome Click Overlay Toggle */}
            <div className="pt-2 border-t border-[#20293B]">
              <button
                onClick={() => setIsMetronomeOn(!isMetronomeOn)}
                className={`w-full p-2.5 rounded-xl border flex items-center justify-between text-xs font-bold transition-all cursor-pointer ${
                  isMetronomeOn
                    ? 'bg-teal-500/20 border-teal-400 text-teal-300'
                    : 'bg-[#151D2A] border-[#253043] text-zinc-400 hover:text-white'
                }`}
              >
                <div className="flex items-center space-x-2">
                  <Timer className="w-4 h-4" />
                  <span>Метроном поверх ударных</span>
                </div>
                <div
                  className={`w-4 h-4 rounded-full border flex items-center justify-center ${
                    isMetronomeOn
                      ? 'border-teal-400 bg-teal-400 text-zinc-950'
                      : 'border-zinc-600'
                  }`}
                >
                  {isMetronomeOn && <Check className="w-3 h-3 stroke-[3]" />}
                </div>
              </button>
            </div>
          </div>
        </StudioCard>
      </div>

      {/* 16-Step Sequencer Grid & Visualizer */}
      <StudioCard>
        <div className="p-5 space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-[#202A3D]">
            <div>
              <div className="text-sm font-bold text-white flex items-center gap-2">
                <span>16-Шаговый Секвенсор & Сетка Ритма</span>
                <span className="px-2 py-0.5 rounded bg-zinc-800 text-[10px] text-zinc-400 font-mono">
                  {selectedPattern.timeSignature} · {stepsCount} шагов
                </span>
              </div>
              <p className="text-xs text-zinc-400 mt-0.5">
                Нажимайте на клетки сетки, чтобы включать или выключать инструменты на любом шаге!
              </p>
            </div>
            <div className="flex items-center space-x-2 text-xs">
              <span className="text-zinc-500">Доли такта:</span>
              <span className="px-2 py-0.5 rounded bg-amber-500/20 text-amber-300 font-bold font-mono">
                1
              </span>
              <span className="px-2 py-0.5 rounded bg-zinc-800 text-zinc-300 font-mono">2</span>
              <span className="px-2 py-0.5 rounded bg-zinc-800 text-zinc-300 font-mono">3</span>
              <span className="px-2 py-0.5 rounded bg-zinc-800 text-zinc-300 font-mono">4</span>
            </div>
          </div>

          {/* Interactive Matrix Grid */}
          <div className="overflow-x-auto pb-2">
            <div className="min-w-[640px]">
              {/* Top Step Number Header */}
              <div className="flex items-center mb-2">
                <div className="w-24 shrink-0 text-[11px] font-bold text-zinc-500 uppercase tracking-wider">
                  Инструмент
                </div>
                <div className="flex-1 grid gap-1.5" style={{ gridTemplateColumns: `repeat(${stepsCount}, minmax(0, 1fr))` }}>
                  {Array.from({ length: stepsCount }).map((_, stepIdx) => {
                    const isQuarter = isCompound ? stepIdx % 3 === 0 : stepIdx % 4 === 0;
                    const beatNumber = isCompound ? Math.floor(stepIdx / 3) + 1 : Math.floor(stepIdx / 4) + 1;
                    const isCurrent = currentStep === stepIdx;

                    return (
                      <div
                        key={stepIdx}
                        className={`text-center py-1 rounded text-[10px] font-mono font-bold transition-all ${
                          isCurrent
                            ? 'bg-amber-400 text-zinc-950 shadow-[0_0_8px_rgba(245,158,11,0.8)] scale-105'
                            : isQuarter
                            ? 'bg-[#182334] text-amber-300 border border-amber-500/30'
                            : 'bg-[#101622] text-zinc-500'
                        }`}
                      >
                        {isQuarter ? beatNumber : '·'}
                      </div>
                    );
                  })}
                </div>
              </div>

              {/* Instrument Rows */}
              <div className="space-y-1.5">
                {trackLabels.map((track) => {
                  const patternSteps = activeTracks[track.key] || [];
                  const isPadActive = activePad === track.instrument;

                  return (
                    <div key={track.key} className="flex items-center">
                      {/* Instrument trigger button */}
                      <button
                        onClick={() => handlePadHit(track.instrument)}
                        className={`w-24 shrink-0 px-2 py-1.5 rounded-xl border text-left flex items-center justify-between transition-all cursor-pointer ${
                          isPadActive
                            ? 'bg-amber-400 text-zinc-950 border-amber-300 font-bold scale-95 shadow-lg'
                            : 'bg-[#131A26] hover:bg-[#1C2638] border-[#243044] text-zinc-300'
                        }`}
                        title={`Нажмите для пробного звука: ${track.label}`}
                      >
                        <span className="text-xs font-bold truncate">{track.label}</span>
                        <span className="w-1.5 h-1.5 rounded-full bg-zinc-600" />
                      </button>

                      {/* Step grid buttons */}
                      <div
                        className="flex-1 grid gap-1.5 ml-2"
                        style={{ gridTemplateColumns: `repeat(${stepsCount}, minmax(0, 1fr))` }}
                      >
                        {Array.from({ length: stepsCount }).map((_, stepIdx) => {
                          const isActive = !!patternSteps[stepIdx];
                          const isCurrent = currentStep === stepIdx;
                          const isBeatStart = isCompound ? stepIdx % 3 === 0 : stepIdx % 4 === 0;

                          return (
                            <button
                              key={stepIdx}
                              onClick={() => handleToggleStep(track.key, stepIdx)}
                              className={`h-9 rounded-lg border transition-all cursor-pointer flex items-center justify-center ${
                                isActive
                                  ? `${track.color} border shadow-sm ${
                                      isCurrent
                                        ? 'ring-2 ring-white brightness-125 scale-105 z-10'
                                        : ''
                                    }`
                                  : isCurrent
                                  ? 'bg-white/20 border-white/50'
                                  : isBeatStart
                                  ? 'bg-[#161D2B] border-[#2A364C] hover:bg-[#202A3D]'
                                  : 'bg-[#0E131E] border-[#1C2433] hover:bg-[#17202E]'
                              }`}
                              title={`${track.label} — шаг ${stepIdx + 1}`}
                            >
                              {isActive && (
                                <div
                                  className={`w-2.5 h-2.5 rounded-sm ${
                                    isCurrent ? 'bg-white shadow-[0_0_8px_white]' : 'bg-current opacity-80'
                                  }`}
                                />
                              )}
                            </button>
                          );
                        })}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
      </StudioCard>

      {/* Interactive Quick Drum Pads (for manual finger-drumming with guitar) */}
      <StudioCard>
        <div className="p-5 space-y-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <Sparkles className="w-4 h-4 text-amber-400" />
              <span className="text-xs font-bold text-white uppercase tracking-wider">
                Интерактивные пэды ударных (простукивание пальцами)
              </span>
            </div>
            <span className="text-[11px] text-zinc-500 hidden sm:inline">
              Кликайте по пэдам, чтобы проверить звучание или подыгрывать вживую
            </span>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-8 gap-2.5">
            {[
              { id: 'kick' as DrumInstrument, name: 'BASS DRUM', sub: 'Kick', color: 'from-amber-500 to-amber-600' },
              { id: 'snare' as DrumInstrument, name: 'SNARE', sub: 'Малый', color: 'from-rose-500 to-rose-600' },
              { id: 'hihatClosed' as DrumInstrument, name: 'HH CLOSED', sub: 'Хэт закрытый', color: 'from-teal-500 to-teal-600' },
              { id: 'hihatOpen' as DrumInstrument, name: 'HH OPEN', sub: 'Хэт открытый', color: 'from-cyan-500 to-cyan-600' },
              { id: 'crash' as DrumInstrument, name: 'CRASH', sub: 'Крэш тарелка', color: 'from-yellow-500 to-yellow-600' },
              { id: 'ride' as DrumInstrument, name: 'RIDE', sub: 'Райд тарелка', color: 'from-sky-500 to-sky-600' },
              { id: 'tomHigh' as DrumInstrument, name: 'HIGH TOM', sub: 'Альт', color: 'from-purple-500 to-purple-600' },
              { id: 'percussion' as DrumInstrument, name: 'CLAP / PERC', sub: 'Хлопок / Ковбелл', color: 'from-emerald-500 to-emerald-600' },
            ].map((pad) => {
              const isHit = activePad === pad.id;
              return (
                <button
                  key={pad.id}
                  onClick={() => handlePadHit(pad.id)}
                  className={`h-20 rounded-2xl p-2.5 flex flex-col items-center justify-center text-center border transition-all transform active:scale-95 cursor-pointer ${
                    isHit
                      ? `bg-gradient-to-b ${pad.color} text-zinc-950 border-white shadow-xl scale-95`
                      : 'bg-[#131926] hover:bg-[#1A2335] border-[#253246] text-zinc-200'
                  }`}
                >
                  <span className="text-xs font-black tracking-tight">{pad.name}</span>
                  <span className="text-[10px] text-zinc-400 mt-1">{pad.sub}</span>
                </button>
              );
            })}
          </div>
        </div>
      </StudioCard>

      {/* 100 Popular Drum Patterns Browser */}
      <StudioCard>
        <div className="p-5 space-y-4">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <div className="flex items-center space-x-2">
                <Music className="w-5 h-5 text-amber-400" />
                <h2 className="text-lg font-bold text-white">
                  Каталог 100 ритмов для разных стилей
                </h2>
              </div>
              <p className="text-xs text-zinc-400 mt-0.5">
                Выберите любой ритм для репетиций риффов, гамм, соло и джема
              </p>
            </div>

            {/* Search Box */}
            <div className="relative w-full md:w-72">
              <Search className="w-4 h-4 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Поиск по названию или исполнителю..."
                className="w-full pl-9 pr-3 py-2 rounded-xl bg-[#0E1420] border border-[#232F42] text-xs text-zinc-100 placeholder-zinc-500 focus:outline-hidden focus:border-amber-400 transition-colors"
              />
            </div>
          </div>

          {/* Genre Filter Tabs */}
          <div className="flex items-center space-x-1.5 overflow-x-auto pb-1 scrollbar-none">
            <button
              onClick={() => setSelectedGenre('Favorites')}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold flex items-center space-x-1.5 transition-all cursor-pointer whitespace-nowrap ${
                selectedGenre === 'Favorites'
                  ? 'bg-rose-500 text-white shadow-md'
                  : 'bg-[#151C2A] text-zinc-400 hover:text-white border border-[#243044]'
              }`}
            >
              <Heart className="w-3.5 h-3.5 fill-current text-rose-400" />
              <span>Избранное ({favorites.length})</span>
            </button>

            {DRUM_GENRES.map((genre) => {
              const isSelected = selectedGenre === genre;
              return (
                <button
                  key={genre}
                  onClick={() => setSelectedGenre(genre)}
                  className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer whitespace-nowrap ${
                    isSelected
                      ? 'bg-gradient-to-b from-amber-400 to-amber-500 text-zinc-950 shadow-md font-black'
                      : 'bg-[#151C2A] text-zinc-300 hover:text-white hover:bg-[#1E273A] border border-[#243044]'
                  }`}
                >
                  {genre}
                </button>
              );
            })}
          </div>

          {/* Patterns Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3 max-h-[520px] overflow-y-auto pr-1">
            {filteredPatterns.length === 0 ? (
              <div className="col-span-full py-12 text-center text-zinc-500 text-sm">
                Паттерны не найдены. Попробуйте изменить фильтр жанра или поисковый запрос.
              </div>
            ) : (
              filteredPatterns.map((pattern) => {
                const isSelected = selectedPattern.id === pattern.id;
                const isFav = favorites.includes(pattern.id);

                return (
                  <div
                    key={pattern.id}
                    onClick={() => handleSelectPattern(pattern)}
                    className={`p-3.5 rounded-2xl border transition-all cursor-pointer flex flex-col justify-between ${
                      isSelected
                        ? 'bg-[#1B2436] border-amber-400 shadow-[0_0_16px_rgba(245,158,11,0.2)]'
                        : 'bg-[#111724] hover:bg-[#172030] border-[#222E42] text-zinc-300'
                    }`}
                  >
                    <div>
                      <div className="flex items-center justify-between">
                        <div className="flex items-center space-x-2">
                          <span
                            className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                              pattern.genre === 'Rock'
                                ? 'bg-amber-500/20 text-amber-300'
                                : pattern.genre === 'Metal'
                                ? 'bg-red-500/20 text-red-300'
                                : pattern.genre === 'Pop'
                                ? 'bg-pink-500/20 text-pink-300'
                                : pattern.genre === 'Funk & R&B'
                                ? 'bg-purple-500/20 text-purple-300'
                                : pattern.genre === 'Blues'
                                ? 'bg-blue-500/20 text-blue-300'
                                : pattern.genre === 'Jazz'
                                ? 'bg-emerald-500/20 text-emerald-300'
                                : pattern.genre === 'Hip-Hop & Trap'
                                ? 'bg-orange-500/20 text-orange-300'
                                : 'bg-cyan-500/20 text-cyan-300'
                            }`}
                          >
                            {pattern.genre}
                          </span>
                          <span className="text-[10px] font-mono text-zinc-400">
                            {pattern.timeSignature}
                          </span>
                        </div>

                        <div className="flex items-center space-x-1.5">
                          <span className="text-xs font-mono font-bold text-amber-400">
                            {pattern.bpm} BPM
                          </span>
                          <button
                            onClick={(e) => toggleFavorite(pattern.id, e)}
                            className="p-1 text-zinc-500 hover:text-rose-400 transition-colors cursor-pointer"
                            title="Добавить в избранное"
                          >
                            <Heart
                              className={`w-3.5 h-3.5 ${
                                isFav ? 'fill-rose-500 text-rose-500' : ''
                              }`}
                            />
                          </button>
                        </div>
                      </div>

                      <h3 className="font-bold text-sm text-white mt-2 leading-tight">
                        {pattern.name}
                      </h3>

                      {pattern.artistRef && (
                        <p className="text-[11px] text-amber-400/90 font-medium mt-0.5">
                          {pattern.artistRef}
                        </p>
                      )}

                      <p className="text-xs text-zinc-400 line-clamp-2 mt-1.5 leading-snug">
                        {pattern.description}
                      </p>
                    </div>

                    <div className="pt-3 mt-3 border-t border-[#1F2B3E] flex items-center justify-between">
                      <span className="text-[10px] font-semibold text-zinc-500">
                        Сложность: {pattern.difficulty}
                      </span>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleSelectPattern(pattern);
                          if (!isPlaying) togglePlay();
                        }}
                        className={`px-3 py-1 rounded-xl text-xs font-bold flex items-center space-x-1 transition-all cursor-pointer ${
                          isSelected && isPlaying
                            ? 'bg-rose-500 text-white'
                            : 'bg-[#1A2335] hover:bg-amber-400 hover:text-zinc-950 text-amber-300 border border-amber-500/30'
                        }`}
                      >
                        {isSelected && isPlaying ? (
                          <>
                            <Pause className="w-3 h-3 fill-current" />
                            <span>Стоп</span>
                          </>
                        ) : (
                          <>
                            <Play className="w-3 h-3 fill-current" />
                            <span>Слушать</span>
                          </>
                        )}
                      </button>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </StudioCard>
    </div>
  );
};
