import React from 'react';
import {
  SlidersHorizontal,
  Repeat,
  Play,
  Pause,
  MoreHorizontal,
  Gauge,
  Music,
  Radio,
} from 'lucide-react';

export type AudioSourceType = 'ORIG' | 'SYNTH';

interface BottomControlBarProps {
  isPlaying: boolean;
  onTogglePlay: () => void;
  speedRatio: number; // e.g. 1.0 (100%), 0.75 (75%)
  onChangeSpeed: (newSpeed: number) => void;
  audioSource: AudioSourceType;
  onToggleAudioSource: () => void;
  isLoopActive: boolean;
  onToggleLoop: () => void;
  onOpenMixer: () => void;
  onOpenMoreMenu: () => void;
  onOpenTempoPicker: () => void;
  trackCount: number;
}

export const BottomControlBar: React.FC<BottomControlBarProps> = ({
  isPlaying,
  onTogglePlay,
  speedRatio,
  audioSource,
  onToggleAudioSource,
  isLoopActive,
  onToggleLoop,
  onOpenMixer,
  onOpenMoreMenu,
  onOpenTempoPicker,
  trackCount,
}) => {
  return (
    <div className="fixed bottom-0 left-0 right-0 z-40 p-3 sm:p-4 pointer-events-none">
      <div className="max-w-3xl mx-auto pointer-events-auto">
        {/* Floating Dock Capsule */}
        <div className="bg-zinc-900/95 backdrop-blur-xl border border-zinc-800 rounded-3xl shadow-2xl p-2 sm:p-2.5 flex items-center justify-between gap-1 sm:gap-3 ring-1 ring-white/5">
          {/* 1. Track Mixer Button */}
          <button
            onClick={onOpenMixer}
            className="p-2 sm:px-3 sm:py-2 rounded-2xl text-zinc-300 hover:text-white hover:bg-zinc-800/80 transition-all flex items-center space-x-1.5 active:scale-95"
            title="Tracks & Mixer"
          >
            <div className="relative">
              <SlidersHorizontal className="w-5 h-5 text-amber-400" />
              {trackCount > 1 && (
                <span className="absolute -top-1 -right-1 w-3.5 h-3.5 bg-amber-500 text-zinc-950 font-bold text-[9px] rounded-full flex items-center justify-center">
                  {trackCount}
                </span>
              )}
            </div>
            <span className="text-xs font-semibold hidden md:inline">Tracks</span>
          </button>

          {/* 2. A-B Looping Button */}
          <button
            onClick={onToggleLoop}
            className={`p-2 sm:px-3 sm:py-2 rounded-2xl text-xs font-bold transition-all flex items-center space-x-1.5 active:scale-95 ${
              isLoopActive
                ? 'bg-amber-500/20 text-amber-400 border border-amber-500/40 shadow-xs'
                : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-800/80'
            }`}
            title="Toggle A-B Looping"
          >
            <Repeat className={`w-4 h-4 ${isLoopActive ? 'text-amber-400' : 'text-zinc-400'}`} />
            <span className="hidden sm:inline">Loop</span>
          </button>

          {/* 3. Tempo Indicator & Regulator */}
          <button
            onClick={onOpenTempoPicker}
            className="px-2.5 py-1.5 sm:px-3 sm:py-2 rounded-2xl bg-zinc-800/80 hover:bg-zinc-700/80 border border-zinc-700/80 text-zinc-200 text-xs font-mono font-bold flex items-center space-x-1.5 transition-all active:scale-95"
            title="Tempo Regulator"
          >
            <Gauge className="w-3.5 h-3.5 text-amber-400" />
            <span>{Math.round(speedRatio * 100)}%</span>
          </button>

          {/* 4. Highlighted Play / Pause Circular Button */}
          <button
            onClick={onTogglePlay}
            className={`w-13 h-13 sm:w-14 sm:h-14 rounded-full flex items-center justify-center shadow-xl transition-all transform active:scale-90 ${
              isPlaying
                ? 'bg-emerald-500 text-zinc-950 shadow-emerald-500/40 ring-4 ring-emerald-500/20'
                : 'bg-emerald-500 hover:bg-emerald-400 text-zinc-950 shadow-emerald-500/30 ring-4 ring-emerald-400/20 hover:scale-105'
            }`}
            aria-label={isPlaying ? 'Pause Tab' : 'Play Tab'}
          >
            {isPlaying ? (
              <Pause className="w-6 h-6 fill-current" />
            ) : (
              <Play className="w-6 h-6 fill-current ml-0.5" />
            )}
          </button>

          {/* 5. Audio Source Toggle: [ORIG.] / [SYNTH] */}
          <div className="flex items-center p-1 rounded-2xl bg-zinc-950 border border-zinc-800">
            <button
              onClick={() => audioSource !== 'ORIG' && onToggleAudioSource()}
              className={`px-2.5 py-1 rounded-xl text-[11px] font-bold font-mono transition-all ${
                audioSource === 'ORIG'
                  ? 'bg-amber-500 text-zinc-950 shadow-xs'
                  : 'text-zinc-400 hover:text-zinc-200'
              }`}
            >
              ORIG.
            </button>
            <button
              onClick={() => audioSource !== 'SYNTH' && onToggleAudioSource()}
              className={`px-2.5 py-1 rounded-xl text-[11px] font-bold font-mono transition-all ${
                audioSource === 'SYNTH'
                  ? 'bg-amber-500 text-zinc-950 shadow-xs'
                  : 'text-zinc-400 hover:text-zinc-200'
              }`}
            >
              SYNTH
            </button>
          </div>

          {/* 6. More Options (...) Button */}
          <button
            onClick={onOpenMoreMenu}
            className="p-2 sm:px-3 sm:py-2 rounded-2xl text-zinc-400 hover:text-white hover:bg-zinc-800/80 transition-all flex items-center active:scale-95"
            title="More Options (Tuner, Transpose, Export)"
          >
            <MoreHorizontal className="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
  );
};
