import React, { useState, useEffect, useRef } from 'react';
import { Play, Pause, RotateCcw, Gauge, ArrowRight, Zap, Target, TrendingUp } from 'lucide-react';
import { MetronomeConfig, MetronomeBeat, TimeSignature, TrainerIntervalKind } from '../../types';
import { MetronomeEngine } from '../../audio/metronomeEngine';
import { Studio3DBadge, StudioCard, StudioPill } from '../common/Studio3DComponents';

const DEFAULT_TIME_SIG: TimeSignature = {
  id: '4_4',
  beatsPerBar: 4,
  beatUnit: 4,
  accentBeats: [1],
  label: '4/4',
};

export const AutoSpeedTrainerScreen: React.FC = () => {
  const [startBpm, setStartBpm] = useState(80);
  const [targetBpm, setTargetBpm] = useState(140);
  const [incrementBpm, setIncrementBpm] = useState(2);
  const [intervalKind, setIntervalKind] = useState<TrainerIntervalKind>('BARS');
  const [intervalValue, setIntervalValue] = useState(4);
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentBeat, setCurrentBeat] = useState<MetronomeBeat | null>(null);
  const [displayBpm, setDisplayBpm] = useState(80);

  const engineRef = useRef<MetronomeEngine | null>(null);

  useEffect(() => {
    const config: MetronomeConfig = {
      bpm: startBpm,
      timeSignature: DEFAULT_TIME_SIG,
      volume: 0.85,
      trainer: {
        enabled: true,
        startBpm,
        targetBpm,
        incrementBpm,
        intervalKind,
        intervalValue,
      },
    };

    const engine = new MetronomeEngine(config, (beat) => {
      setCurrentBeat(beat);
      setDisplayBpm(beat.bpm);
    });

    engineRef.current = engine;

    return () => {
      engine.stop();
    };
  }, []);

  useEffect(() => {
    if (engineRef.current && !isPlaying) {
      setDisplayBpm(startBpm);
      engineRef.current.updateConfig({
        bpm: startBpm,
        timeSignature: DEFAULT_TIME_SIG,
        volume: 0.85,
        trainer: {
          enabled: true,
          startBpm,
          targetBpm,
          incrementBpm,
          intervalKind,
          intervalValue,
        },
      });
    }
  }, [startBpm, targetBpm, incrementBpm, intervalKind, intervalValue, isPlaying]);

  const togglePlay = async () => {
    if (!engineRef.current) return;
    if (isPlaying) {
      engineRef.current.stop();
      setIsPlaying(false);
      setCurrentBeat(null);
    } else {
      await engineRef.current.start();
      setIsPlaying(true);
    }
  };

  const resetTrainer = () => {
    if (engineRef.current) {
      engineRef.current.stop();
      setIsPlaying(false);
      setDisplayBpm(startBpm);
      setCurrentBeat(null);
      engineRef.current.updateConfig({
        bpm: startBpm,
        timeSignature: DEFAULT_TIME_SIG,
        volume: 0.85,
        trainer: {
          enabled: true,
          startBpm,
          targetBpm,
          incrementBpm,
          intervalKind,
          intervalValue,
        },
      });
    }
  };

  const progressTotal = Math.max(
    0,
    Math.min(100, ((displayBpm - startBpm) / Math.max(1, targetBpm - startBpm)) * 100)
  );

  const stepProgress = currentBeat?.progressToNextJump ? currentBeat.progressToNextJump * 100 : 0;

  return (
    <div className="max-w-xl mx-auto px-4 py-6 space-y-6">
      {/* Header Card */}
      <StudioCard>
        <div className="p-4 sm:p-5 flex items-center justify-between">
          <div className="flex items-center space-x-3.5">
            <Studio3DBadge icon={TrendingUp} accent="teal" size="lg" />
            <div>
              <h2 className="text-xl font-bold text-white tracking-tight">Auto-Speed Trainer</h2>
              <p className="text-xs text-zinc-400">Progressive Tempo Ramp & Muscle Memory Builder</p>
            </div>
          </div>
          <div className="px-3 py-1 rounded-full bg-teal-500/15 border border-teal-500/30 text-teal-300 font-bold text-xs">
            {intervalValue} {intervalKind.toLowerCase()}
          </div>
        </div>
      </StudioCard>

      {/* Main Live Trainer Dashboard Card */}
      <StudioCard glow={isPlaying ? 'teal' : null}>
        <div className="p-6 sm:p-8 flex flex-col items-center">
          {/* Target Ramp Indicator */}
          <div className="w-full flex items-center justify-between px-3 text-xs text-zinc-400 font-medium mb-3">
            <span className="flex items-center gap-1.5">
              <span className="w-2 h-2 rounded-full bg-zinc-500" />
              Start: <b className="text-zinc-200">{startBpm} BPM</b>
            </span>
            <span className="flex items-center gap-1 text-teal-400 font-bold">
              +{incrementBpm} BPM step
            </span>
            <span className="flex items-center gap-1.5">
              Target: <b className="text-amber-400">{targetBpm} BPM</b>
              <span className="w-2 h-2 rounded-full bg-amber-400" />
            </span>
          </div>

          {/* Overall Progress Bar */}
          <div className="w-full bg-[#182030] h-2.5 rounded-full overflow-hidden border border-[#27344D] mb-8">
            <div
              className="bg-gradient-to-r from-teal-400 to-amber-400 h-full rounded-full transition-all duration-300 shadow-[0_0_10px_rgba(45,212,191,0.5)]"
              style={{ width: `${progressTotal}%` }}
            />
          </div>

          {/* Big Live BPM Display */}
          <div className="relative w-48 h-48 rounded-full bg-[#141B29] border border-[#232F46] flex flex-col items-center justify-center shadow-xl shadow-black/50 mb-6">
            <span className="text-6xl font-black text-transparent bg-clip-text bg-gradient-to-b from-white via-zinc-100 to-zinc-300">
              {displayBpm}
            </span>
            <span className="text-xs font-bold text-teal-400 uppercase tracking-widest mt-1">
              CURRENT BPM
            </span>

            {/* Pulsing beat indicator dot */}
            <div
              className={`absolute top-4 w-3.5 h-3.5 rounded-full transition-transform ${
                currentBeat?.accent
                  ? 'bg-amber-400 scale-125 shadow-[0_0_10px_rgba(245,158,11,0.9)]'
                  : currentBeat
                  ? 'bg-teal-400 scale-110 shadow-[0_0_8px_rgba(45,212,191,0.7)]'
                  : 'bg-zinc-700'
              }`}
            />
          </div>

          {/* Countdown to Next Step Bar */}
          <div className="w-full max-w-sm space-y-1.5 mb-8">
            <div className="flex justify-between text-xs">
              <span className="text-zinc-400">Progress to next +{incrementBpm} BPM step</span>
              <span className="text-teal-400 font-mono font-bold">{Math.round(stepProgress)}%</span>
            </div>
            <div className="w-full bg-[#192233] h-2 rounded-full overflow-hidden border border-[#28354E]">
              <div
                className="bg-teal-400 h-full rounded-full transition-all duration-150"
                style={{ width: `${stepProgress}%` }}
              />
            </div>
          </div>

          {/* Controls: Reset + Big 3D Play Button */}
          <div className="flex items-center space-x-6">
            <button
              onClick={resetTrainer}
              className="p-3.5 rounded-2xl bg-gradient-to-b from-[#232C3E] to-[#151C2A] border border-[#2F3C55] text-zinc-300 hover:text-white shadow-[0_4px_10px_rgba(0,0,0,0.4)] active:scale-95 transition-all cursor-pointer"
              title="Reset to Starting BPM"
            >
              <RotateCcw className="w-5 h-5" />
            </button>

            {/* Big 3D Glowing Hero Button */}
            <button
              onClick={togglePlay}
              className={`w-20 h-20 rounded-full flex items-center justify-center transition-all transform active:scale-95 cursor-pointer border ${
                isPlaying
                  ? 'bg-gradient-to-b from-rose-500 via-red-600 to-red-700 text-white shadow-[0_6px_24px_rgba(239,68,68,0.5),inset_0_1px_1px_rgba(255,255,255,0.4)] border-rose-400/50 ring-4 ring-rose-500/20'
                  : 'bg-gradient-to-b from-cyan-400 via-teal-500 to-teal-600 text-zinc-950 shadow-[0_6px_24px_rgba(20,184,166,0.5),inset_0_1px_1px_rgba(255,255,255,0.4)] border-cyan-300/50 ring-4 ring-teal-400/20'
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

      {/* Trainer Configuration Settings */}
      <StudioCard>
        <div className="p-5 space-y-5">
          <label className="text-xs font-bold text-zinc-400 uppercase tracking-wider block">
            Speed Ramp Configuration
          </label>

          {/* Start and Target Sliders */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="bg-[#161D2B] border border-[#253046] rounded-xl p-4 space-y-2">
              <div className="flex justify-between items-center">
                <span className="text-xs font-bold text-zinc-400">Starting Tempo</span>
                <span className="font-mono font-bold text-teal-400 text-sm">{startBpm} BPM</span>
              </div>
              <input
                type="range"
                min={40}
                max={220}
                value={startBpm}
                disabled={isPlaying}
                onChange={(e) => setStartBpm(parseInt(e.target.value, 10))}
                className="w-full h-1.5 bg-[#1C2538] rounded-lg appearance-none cursor-pointer accent-teal-400"
              />
            </div>

            <div className="bg-[#161D2B] border border-[#253046] rounded-xl p-4 space-y-2">
              <div className="flex justify-between items-center">
                <span className="text-xs font-bold text-zinc-400">Target Tempo</span>
                <span className="font-mono font-bold text-amber-400 text-sm">{targetBpm} BPM</span>
              </div>
              <input
                type="range"
                min={60}
                max={260}
                value={targetBpm}
                disabled={isPlaying}
                onChange={(e) => setTargetBpm(parseInt(e.target.value, 10))}
                className="w-full h-1.5 bg-[#1C2538] rounded-lg appearance-none cursor-pointer accent-amber-400"
              />
            </div>
          </div>

          {/* Step Increment */}
          <div className="bg-[#161D2B] border border-[#253046] rounded-xl p-4 space-y-2.5">
            <span className="text-xs font-bold text-zinc-400 block">Tempo Increase Per Step</span>
            <div className="flex items-center space-x-2">
              {[1, 2, 4, 5, 10].map((inc) => (
                <StudioPill
                  key={inc}
                  label={`+${inc} BPM`}
                  selected={incrementBpm === inc}
                  onClick={() => !isPlaying && setIncrementBpm(inc)}
                  accent="teal"
                  size="sm"
                  className="flex-1"
                />
              ))}
            </div>
          </div>

          {/* Interval Trigger: Bars or Minutes */}
          <div className="bg-[#161D2B] border border-[#253046] rounded-xl p-4 space-y-2.5">
            <span className="text-xs font-bold text-zinc-400 block">Step Trigger Interval</span>
            <div className="flex flex-wrap gap-2">
              {[
                { label: 'Every 2 Bars', kind: 'BARS' as TrainerIntervalKind, val: 2 },
                { label: 'Every 4 Bars', kind: 'BARS' as TrainerIntervalKind, val: 4 },
                { label: 'Every 8 Bars', kind: 'BARS' as TrainerIntervalKind, val: 8 },
                { label: 'Every 1 Min', kind: 'MINUTES' as TrainerIntervalKind, val: 1 },
              ].map((item) => (
                <StudioPill
                  key={item.label}
                  label={item.label}
                  selected={intervalKind === item.kind && intervalValue === item.val}
                  onClick={() => {
                    if (!isPlaying) {
                      setIntervalKind(item.kind);
                      setIntervalValue(item.val);
                    }
                  }}
                  accent="amber"
                  size="sm"
                  className="flex-1 min-w-[100px]"
                />
              ))}
            </div>
          </div>
        </div>
      </StudioCard>
    </div>
  );
};
