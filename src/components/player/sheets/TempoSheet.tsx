import React from 'react';
import { ModalBottomSheet } from './ModalBottomSheet';
import { Gauge, RotateCcw } from 'lucide-react';

interface TempoSheetProps {
  isOpen: boolean;
  onClose: () => void;
  speedRatio: number;
  onSpeedRatioChange: (speed: number) => void;
  baseTempoBpm: number;
}

const SPEED_PRESETS = [0.5, 0.65, 0.75, 0.85, 0.9, 1.0, 1.1, 1.25];

export const TempoSheet: React.FC<TempoSheetProps> = ({
  isOpen,
  onClose,
  speedRatio,
  onSpeedRatioChange,
  baseTempoBpm,
}) => {
  const effectiveBpm = Math.round(baseTempoBpm * speedRatio);

  return (
    <ModalBottomSheet
      isOpen={isOpen}
      onClose={onClose}
      title="Регулятор темпа и скорости"
      subtitle="Замедление сложных пассажей без изменения высоты тона"
    >
      {/* Current Speed Big Typography */}
      <div className="bg-zinc-950/70 border border-zinc-800 rounded-2xl p-5 flex flex-col items-center justify-center space-y-2">
        <div className="flex items-baseline space-x-2">
          <span className="text-5xl font-black font-mono text-white">
            {Math.round(speedRatio * 100)}%
          </span>
          <span className="text-xl font-bold text-amber-400 font-mono">
            ({effectiveBpm} BPM)
          </span>
        </div>
        <p className="text-xs text-zinc-400">
          Оригинальный темп: {baseTempoBpm} BPM
        </p>

        {speedRatio !== 1.0 && (
          <button
            onClick={() => onSpeedRatioChange(1.0)}
            className="text-xs text-amber-400 hover:text-amber-300 flex items-center gap-1 pt-1 transition-colors"
          >
            <RotateCcw className="w-3.5 h-3.5" />
            <span>Вернуть 100%</span>
          </button>
        )}
      </div>

      {/* Speed Slider */}
      <div className="space-y-2 px-1">
        <div className="flex justify-between text-xs text-zinc-400 font-medium">
          <span>Медленно (25%)</span>
          <span className="text-amber-400 font-bold">{Math.round(speedRatio * 100)}%</span>
          <span>Быстро (150%)</span>
        </div>
        <input
          type="range"
          min={0.25}
          max={1.5}
          step={0.05}
          value={speedRatio}
          onChange={(e) => onSpeedRatioChange(parseFloat(e.target.value))}
          className="w-full h-2 bg-zinc-800 rounded-lg appearance-none cursor-pointer accent-amber-400"
        />
      </div>

      {/* Speed Presets Grid */}
      <div className="space-y-2 pt-2 border-t border-zinc-800">
        <label className="text-xs font-semibold text-zinc-400 uppercase tracking-wider block">
          Быстрый выбор скорости
        </label>
        <div className="grid grid-cols-4 gap-2">
          {SPEED_PRESETS.map((s) => (
            <button
              key={s}
              onClick={() => onSpeedRatioChange(s)}
              className={`py-2 rounded-xl text-xs font-bold font-mono border transition-all ${
                Math.abs(speedRatio - s) < 0.01
                  ? 'bg-amber-500 text-zinc-950 border-amber-400 shadow-sm'
                  : 'bg-zinc-800/80 border-zinc-700 text-zinc-300 hover:text-white hover:bg-zinc-700'
              }`}
            >
              {Math.round(s * 100)}%
            </button>
          ))}
        </div>
      </div>
    </ModalBottomSheet>
  );
};
