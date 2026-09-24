import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Play, Pause, Plus, Minus, Volume2, Sparkles, Gauge, Touchpad, Music } from 'lucide-react';
import { MetronomeConfig, MetronomeBeat, TimeSignature } from '../../types';
import { MetronomeEngine } from '../../audio/metronomeEngine';
import { Studio3DBadge, StudioCard, StudioPill } from '../common/Studio3DComponents';

const TIME_SIGNATURES: TimeSignature[] = [
  { id: '2_4', beatsPerBar: 2, beatUnit: 4, accentBeats: [1], label: '2/4' },
  { id: '3_4', beatsPerBar: 3, beatUnit: 4, accentBeats: [1], label: '3/4' },
  { id: '4_4', beatsPerBar: 4, beatUnit: 4, accentBeats: [1], label: '4/4' },
  { id: '5_4', beatsPerBar: 5, beatUnit: 4, accentBeats: [1, 4], label: '5/4' },
  { id: '6_8', beatsPerBar: 6, beatUnit: 8, accentBeats: [1, 4], label: '6/8' },
  { id: '7_8', beatsPerBar: 7, beatUnit: 8, accentBeats: [1, 4], label: '7/8' },
];

const TEMPO_MARKINGS = [
  { label: 'Largo', bpm: 50 },
  { label: 'Adagio', bpm: 68 },
  { label: 'Andante', bpm: 84 },
  { label: 'Moderato', bpm: 108 },
  { label: 'Allegro', bpm: 132 },
  { label: 'Vivace', bpm: 160 },
  { label: 'Presto', bpm: 184 },
];

interface MetronomeScreenProps {
  onGoToTrainer?: () => void;
}

export const MetronomeScreen: React.FC<MetronomeScreenProps> = ({ onGoToTrainer }) => {
  const [bpm, setBpm] = useState(120);
  const [isPlaying, setIsPlaying] = useState(false);
  const [timeSignature, setTimeSignature] = useState<TimeSignature>(TIME_SIGNATURES[2]); // 4/4
  const [volume, setVolume] = useState(0.85);
  const [currentBeat, setCurrentBeat] = useState<MetronomeBeat | null>(null);

  const tapTimesRef = useRef<number[]>([]);
  const engineRef = useRef<MetronomeEngine | null>(null);

  useEffect(() => {
    const config: MetronomeConfig = {
      bpm,
      timeSignature,
      volume,
      trainer: {
        enabled: false,
        startBpm: bpm,
        targetBpm: 140,
        incrementBpm: 2,
        intervalKind: 'BARS',
        intervalValue: 4,
      },
    };

    const engine = new MetronomeEngine(config, (beat) => {
      setCurrentBeat(beat);
    });

    engineRef.current = engine;

    return () => {
      engine.stop();
    };
  }, []);

  useEffect(() => {
    if (engineRef.current) {
      engineRef.current.updateConfig({
        bpm,
        timeSignature,
        volume,
        trainer: {
          enabled: false,
          startBpm: bpm,
          targetBpm: 140,
          incrementBpm: 2,
          intervalKind: 'BARS',
          intervalValue: 4,
        },
      });
    }
  }, [bpm, timeSignature, volume]);

  const togglePlay = useCallback(async () => {
    if (!engineRef.current) return;
    if (isPlaying) {
      engineRef.current.stop();
      setIsPlaying(false);
      setCurrentBeat(null);
    } else {
      await engineRef.current.start();
      setIsPlaying(true);
    }
  }, [isPlaying]);

  const handleTapTempo = () => {
    const now = performance.now();
    const times = tapTimesRef.current;
    if (times.length > 0 && now - times[times.length - 1] > 2000) {
      tapTimesRef.current = [now];
      return;
    }

    times.push(now);
    if (times.length > 4) times.shift();

    if (times.length >= 2) {
      const intervals = [];
      for (let i = 1; i < times.length; i++) {
        intervals.push(times[i] - times[i - 1]);
      }
      const avgInterval = intervals.reduce((a, b) => a + b, 0) / intervals.length;
      const calculatedBpm = Math.round(60000 / avgInterval);
      const clamped = Math.max(30, Math.min(300, calculatedBpm));
      setBpm(clamped);
    }
  };

  const getTempoLabel = (val: number) => {
    if (val < 60) return 'Largo';
    if (val < 76) return 'Adagio';
    if (val < 108) return 'Andante';
    if (val < 120) return 'Moderato';
    if (val < 156) return 'Allegro';
    if (val < 200) return 'Vivace';
    return 'Presto';
  };

  const isAccent = currentBeat?.accent ?? false;

  // Arc calculation for tempo dial (30 to 300 BPM)
  const normProgress = Math.max(0, Math.min(1, (bpm - 30) / (300 - 30)));
  const circumference = 2 * Math.PI * 80;
  const strokeDashoffset = circumference - normProgress * circumference;

  return (
    <div className="max-w-xl mx-auto px-4 py-6 space-y-6">
      {/* Top Header Card */}
      <StudioCard>
        <div className="p-4 sm:p-5 flex items-center justify-between">
          <div className="flex items-center space-x-3.5">
            <Studio3DBadge icon={Music} accent="amber" size="lg" />
            <div>
              <h2 className="text-xl font-bold text-white tracking-tight">Studio Metronome</h2>
              <p className="text-xs text-zinc-400">High-Precision Audio Engine & Pulse Visualizer</p>
            </div>
          </div>
          {onGoToTrainer && (
            <StudioPill
              label="Speed Trainer"
              selected={false}
              onClick={onGoToTrainer}
              icon={Gauge}
              accent="teal"
              size="sm"
            />
          )}
        </div>
      </StudioCard>

      {/* Main Dial Hero Card */}
      <StudioCard glow={isPlaying && isAccent ? 'amber' : isPlaying ? 'teal' : null}>
        <div className="p-6 sm:p-8 flex flex-col items-center">
          {/* Beat Indicator Dots */}
          <div className="flex items-center space-x-3 mb-6">
            {Array.from({ length: timeSignature.beatsPerBar }).map((_, idx) => {
              const beatNumber = idx + 1;
              const isActive = currentBeat?.beatInBar === beatNumber;
              const isAccentBeat = timeSignature.accentBeats.includes(beatNumber);

              return (
                <div
                  key={idx}
                  className={`transition-all duration-75 rounded-full flex items-center justify-center font-black ${
                    isAccentBeat ? 'w-6 h-6 text-[10px]' : 'w-4 h-4 text-[8px]'
                  } ${
                    isActive
                      ? isAccentBeat
                        ? 'bg-amber-400 text-zinc-950 scale-125 shadow-[0_0_16px_rgba(245,158,11,0.9)] ring-2 ring-white/60'
                        : 'bg-teal-400 text-zinc-950 scale-125 shadow-[0_0_14px_rgba(45,212,191,0.8)] ring-2 ring-white/50'
                      : isAccentBeat
                      ? 'bg-amber-500/25 border border-amber-500/40 text-amber-300'
                      : 'bg-zinc-800 border border-zinc-700 text-zinc-500'
                  }`}
                >
                  {isAccentBeat && '1'}
                </div>
              );
            })}
          </div>

          {/* Soundbrenner Circular Pulse Dial */}
          <div className="relative w-56 h-56 flex items-center justify-center select-none">
            {/* SVG Progress Ring */}
            <svg className="w-full h-full transform -rotate-90">
              <circle
                cx="112"
                cy="112"
                r="80"
                stroke="currentColor"
                strokeWidth="10"
                fill="transparent"
                className="text-[#192233]"
              />
              <circle
                cx="112"
                cy="112"
                r="80"
                stroke="currentColor"
                strokeWidth="10"
                fill="transparent"
                strokeDasharray={circumference}
                strokeDashoffset={strokeDashoffset}
                strokeLinecap="round"
                className={`transition-all duration-150 ${
                  isPlaying && isAccent
                    ? 'text-amber-400 drop-shadow-[0_0_12px_rgba(245,158,11,0.8)]'
                    : isPlaying
                    ? 'text-teal-400 drop-shadow-[0_0_10px_rgba(45,212,191,0.6)]'
                    : 'text-amber-500/50'
                }`}
              />
            </svg>

            {/* Inner Dial Face */}
            <div
              className={`absolute inset-4 rounded-full flex flex-col items-center justify-center transition-all duration-75 ${
                isPlaying && isAccent
                  ? 'scale-105 bg-[#182030] shadow-[inset_0_0_20px_rgba(245,158,11,0.2)]'
                  : isPlaying
                  ? 'bg-[#141B29]'
                  : 'bg-[#121622]'
              } border border-[#232F46]`}
            >
              <span
                className={`text-6xl font-black tracking-tighter transition-colors ${
                  isPlaying && isAccent
                    ? 'text-amber-400'
                    : isPlaying
                    ? 'text-teal-300'
                    : 'text-zinc-100'
                }`}
              >
                {bpm}
              </span>
              <span className="text-xs font-bold uppercase tracking-widest text-zinc-400 mt-0.5">
                BPM · {getTempoLabel(bpm)}
              </span>
            </div>
          </div>

          {/* Fine Step Buttons (-5, -1, +1, +5) */}
          <div className="flex items-center space-x-3 mt-6">
            {[-5, -1, 1, 5].map((delta) => (
              <button
                key={delta}
                onClick={() => setBpm((prev) => Math.max(30, Math.min(300, prev + delta)))}
                className="w-12 h-10 rounded-xl bg-gradient-to-b from-[#232C3E] to-[#161D2B] border border-[#2F3C55] text-zinc-200 font-bold text-sm shadow-[0_2px_4px_rgba(0,0,0,0.4),inset_0_1px_1px_rgba(255,255,255,0.1)] active:scale-95 transition-all hover:text-white hover:border-amber-400/40 cursor-pointer"
              >
                {delta > 0 ? `+${delta}` : delta}
              </button>
            ))}
          </div>

          {/* BPM Slider */}
          <div className="w-full max-w-sm mt-6">
            <input
              type="range"
              min={30}
              max={300}
              value={bpm}
              onChange={(e) => setBpm(parseInt(e.target.value))}
              className="w-full h-2 bg-[#1C2538] rounded-lg appearance-none cursor-pointer accent-amber-400 border border-[#27344D]"
            />
            <div className="flex justify-between text-[11px] text-zinc-500 font-mono mt-1.5">
              <span>30</span>
              <span>120</span>
              <span>200</span>
              <span>300</span>
            </div>
          </div>

          {/* Action Row: Tap Tempo + Master 3D Play/Pause Button */}
          <div className="flex items-center justify-center space-x-6 mt-6">
            <button
              onClick={handleTapTempo}
              className="px-5 py-3.5 rounded-2xl bg-gradient-to-b from-[#232C3E] to-[#151C2A] border border-[#2F3C55] text-zinc-200 font-bold text-xs flex items-center space-x-2.5 shadow-[0_4px_10px_rgba(0,0,0,0.4),inset_0_1px_1px_rgba(255,255,255,0.1)] active:scale-95 transition-all hover:border-teal-400/50 cursor-pointer"
            >
              <Touchpad className="w-4 h-4 text-teal-400" />
              <span>TAP TEMPO</span>
            </button>

            {/* Big 3D Glowing Hero Button */}
            <button
              onClick={togglePlay}
              className={`w-20 h-20 rounded-full flex items-center justify-center transition-all transform active:scale-95 cursor-pointer border ${
                isPlaying
                  ? 'bg-gradient-to-b from-rose-500 via-red-600 to-red-700 text-white shadow-[0_6px_24px_rgba(239,68,68,0.5),inset_0_1px_1px_rgba(255,255,255,0.4)] border-rose-400/50 ring-4 ring-rose-500/20'
                  : 'bg-gradient-to-b from-amber-400 via-amber-500 to-amber-600 text-zinc-950 shadow-[0_6px_24px_rgba(245,158,11,0.5),inset_0_1px_1px_rgba(255,255,255,0.4)] border-amber-300/50 ring-4 ring-amber-400/20'
              }`}
            >
              {isPlaying ? (
                <Pause className="w-8 h-8 fill-current" />
              ) : (
                <Play className="w-8 h-8 fill-current ml-1" />
              )}
            </button>
          </div>
        </div>
      </StudioCard>

      {/* Time Signature and Volume Card */}
      <StudioCard>
        <div className="p-5 space-y-4">
          <div>
            <label className="text-xs font-bold text-zinc-400 uppercase tracking-wider block mb-2.5">
              Time Signature
            </label>
            <div className="flex flex-wrap gap-2">
              {TIME_SIGNATURES.map((ts) => (
                <StudioPill
                  key={ts.id}
                  label={ts.label}
                  selected={timeSignature.id === ts.id}
                  onClick={() => setTimeSignature(ts)}
                  accent="amber"
                />
              ))}
            </div>
          </div>

          {/* Volume Slider */}
          <div className="pt-3 border-t border-[#222B3D] flex items-center justify-between gap-4">
            <div className="flex items-center space-x-3 w-full">
              <Volume2 className="w-4 h-4 text-zinc-400 shrink-0" />
              <input
                type="range"
                min={0}
                max={1}
                step={0.05}
                value={volume}
                onChange={(e) => setVolume(parseFloat(e.target.value))}
                className="w-full h-1.5 bg-[#1C2538] rounded-lg appearance-none cursor-pointer accent-amber-400 border border-[#27344D]"
              />
              <span className="text-xs font-mono text-amber-400 font-bold w-10">
                {Math.round(volume * 100)}%
              </span>
            </div>
          </div>
        </div>
      </StudioCard>
    </div>
  );
};
