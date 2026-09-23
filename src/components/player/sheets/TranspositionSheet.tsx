import React from 'react';
import { ModalBottomSheet } from './ModalBottomSheet';
import { Minus, Plus, RotateCcw, Sparkles } from 'lucide-react';
import { NOTE_NAMES } from '../../../data/musicTheory';

interface TranspositionSheetProps {
  isOpen: boolean;
  onClose: () => void;
  semitones: number;
  onSemitonesChange?: (semitones: number) => void;
  onChangeSemitones?: (semitones: number) => void;
  currentTuning?: string;
  tuningName?: string;
  onApplyTuningPreset?: (presetName: string, notes: string[]) => void;
  onSelectTuning?: (presetName: string, notes: string[]) => void;
}

const TUNING_PRESETS = [
  { name: 'Standard E', notes: ['E4', 'B3', 'G3', 'D3', 'A2', 'E2'] },
  { name: 'Drop D (D A D G B e)', notes: ['E4', 'B3', 'G3', 'D3', 'A2', 'D2'] },
  { name: 'Drop C (C G C F A d)', notes: ['D4', 'A3', 'F3', 'C3', 'G2', 'C2'] },
  { name: 'Half Step Down (Eb)', notes: ['Eb4', 'Bb3', 'Gb3', 'Db3', 'Ab2', 'Eb2'] },
  { name: 'Drop B (B F# B E G# c#)', notes: ['Db4', 'Ab3', 'E3', 'B2', 'F#2', 'B1'] },
  { name: 'DADGAD', notes: ['D4', 'A3', 'G3', 'D3', 'A2', 'D2'] },
];

export const TranspositionSheet: React.FC<TranspositionSheetProps> = ({
  isOpen,
  onClose,
  semitones,
  onSemitonesChange,
  onChangeSemitones,
  currentTuning,
  tuningName,
  onApplyTuningPreset,
  onSelectTuning,
}) => {
  const activeTuning = currentTuning || tuningName || '';
  const changeSemitones = onSemitonesChange || onChangeSemitones || (() => {});
  const applyPreset = onApplyTuningPreset || onSelectTuning || (() => {});
  return (
    <ModalBottomSheet
      isOpen={isOpen}
      onClose={onClose}
      title="Смещение тона и строй"
      subtitle="Транспонирование табулатуры и аудиодорожки"
    >
      {/* 1. Semitone Indicator & Steppers */}
      <div className="bg-zinc-950/60 border border-zinc-800 rounded-2xl p-4 flex flex-col items-center justify-center space-y-3">
        <span className="text-xs font-semibold text-zinc-400 uppercase tracking-wider">
          Текущее смещение
        </span>

        <div className="flex items-center space-x-6">
          <button
            onClick={() => changeSemitones(semitones - 1)}
            disabled={semitones <= -12}
            className="w-10 h-10 rounded-xl bg-zinc-800 hover:bg-zinc-700 disabled:opacity-30 border border-zinc-700 flex items-center justify-center text-white transition-colors"
          >
            <Minus className="w-5 h-5" />
          </button>

          <div className="text-center min-w-28">
            <span className="text-3xl font-black font-mono text-amber-400">
              {semitones > 0 ? `+${semitones}` : semitones}
            </span>
            <div className="text-xs text-zinc-400 font-medium mt-0.5">
              {semitones === 0
                ? '0 полутонов'
                : Math.abs(semitones) === 1
                ? '1 полутон'
                : `${Math.abs(semitones)} полутона`}
            </div>
          </div>

          <button
            onClick={() => changeSemitones(semitones + 1)}
            disabled={semitones >= 12}
            className="w-10 h-10 rounded-xl bg-zinc-800 hover:bg-zinc-700 disabled:opacity-30 border border-zinc-700 flex items-center justify-center text-white transition-colors"
          >
            <Plus className="w-5 h-5" />
          </button>
        </div>

        {semitones !== 0 && (
          <button
            onClick={() => changeSemitones(0)}
            className="text-xs text-zinc-400 hover:text-white flex items-center gap-1 transition-colors pt-1"
          >
            <RotateCcw className="w-3.5 h-3.5" />
            <span>Сбросить в оригинал (0)</span>
          </button>
        )}
      </div>

      {/* 2. Pitch Class Matrix (A, A#, B, C, C#...) */}
      <div className="space-y-2">
        <label className="text-xs font-semibold text-zinc-400 uppercase tracking-wider block">
          Интерактивная матрица нот
        </label>
        <div className="grid grid-cols-4 sm:grid-cols-6 gap-2">
          {NOTE_NAMES.map((note, idx) => {
            const isOriginalC = idx === 0;
            return (
              <button
                key={note}
                onClick={() => {
                  // Direct transpose to key offset
                  const offset = ((idx - 0 + 12) % 12);
                  changeSemitones(offset > 6 ? offset - 12 : offset);
                }}
                className={`py-2 px-1 rounded-xl text-xs font-mono font-bold border transition-colors ${
                  semitones === ((idx > 6 ? idx - 12 : idx))
                    ? 'bg-amber-500 text-zinc-950 border-amber-400 shadow-sm'
                    : 'bg-zinc-800/80 border-zinc-700 text-zinc-200 hover:bg-zinc-700 hover:text-white'
                }`}
              >
                {note}
              </button>
            );
          })}
        </div>
      </div>

      {/* 3. Popular Tuning Presets */}
      <div className="space-y-2 pt-2 border-t border-zinc-800">
        <label className="text-xs font-semibold text-zinc-400 uppercase tracking-wider block">
          Быстрое перестроение (Пресеты строя)
        </label>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
          {TUNING_PRESETS.map((p) => {
            const isCurrent = Boolean(activeTuning) && (activeTuning === p.name || (typeof activeTuning === 'string' && activeTuning.startsWith(p.name.split(' ')[0])));
            return (
              <button
                key={p.name}
                onClick={() => {
                  applyPreset(p.name, p.notes);
                  onClose();
                }}
                className={`p-3 rounded-xl border text-left flex items-center justify-between transition-colors ${
                  isCurrent
                    ? 'bg-amber-500/15 border-amber-500/40 text-amber-300'
                    : 'bg-zinc-800/50 border-zinc-700/80 text-zinc-300 hover:bg-zinc-800 hover:text-white'
                }`}
              >
                <div>
                  <div className="text-xs font-bold leading-tight">{p.name}</div>
                  <div className="text-[10px] text-zinc-400 font-mono mt-0.5">
                    {p.notes.map((n) => n.replace(/[0-9]/g, '')).join(' ')}
                  </div>
                </div>
                {isCurrent && <Sparkles className="w-3.5 h-3.5 text-amber-400 shrink-0" />}
              </button>
            );
          })}
        </div>
      </div>
    </ModalBottomSheet>
  );
};
