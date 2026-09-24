import React, { useState } from 'react';
import { Search, Volume2, Sparkles, Trash2, Music } from 'lucide-react';
import { Tuning, FretPosition } from '../../types';
import {
  reverseLookupChord,
} from '../../data/musicTheory';
import { playGuitarPluck } from '../../audio/guitarSynth';
import { midiToHz } from '../../data/defaultTunings';
import { ensureAudioContextStarted } from '../../audio/audioContext';
import { Studio3DBadge, StudioCard } from '../common/Studio3DComponents';
import { TuningSelectorDropdown } from '../common/TuningSelectorDropdown';

interface ReverseChordFinderScreenProps {
  activeTuning: Tuning;
  onTuningChange?: (tuning: Tuning) => void;
}

export const ReverseChordFinderScreen: React.FC<ReverseChordFinderScreenProps> = ({
  activeTuning,
  onTuningChange,
}) => {
  const [pressedFrets, setPressedFrets] = useState<FretPosition[]>([]);
  const totalFrets = 15;
  const stringCount = activeTuning.stringCount;
  const tuningNotes = activeTuning.notes;

  const handleFretClick = async (stringIndex: number, fret: number, midiNote: number) => {
    await ensureAudioContextStarted();
    playGuitarPluck(midiToHz(midiNote), 1.6, 0.85);
    setPressedFrets((prev) => {
      const existing = prev.find((p) => p.stringIndex === stringIndex && p.fret === fret);
      if (existing) {
        return prev.filter((p) => !(p.stringIndex === stringIndex && p.fret === fret));
      } else {
        const filtered = prev.filter((p) => p.stringIndex !== stringIndex);
        return [...filtered, { stringIndex, fret, midiNote }];
      }
    });
  };

  const detectedChords = reverseLookupChord(
    pressedFrets.map((p) => p.midiNote || 0).filter((n) => n > 0)
  );

  const handleStrum = async () => {
    await ensureAudioContextStarted();
    const sorted = [...pressedFrets].sort((a, b) => b.stringIndex - a.stringIndex);
    sorted.forEach((p, idx) => {
      setTimeout(() => {
        if (p.midiNote) playGuitarPluck(midiToHz(p.midiNote), 2.0, 0.85);
      }, idx * 45);
    });
  };

  return (
    <div className="max-w-5xl mx-auto px-4 py-6 space-y-6">
      {/* Reverse Chord Info Card */}
      <StudioCard>
        <div className="p-5 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center space-x-3.5">
            <Studio3DBadge icon={Search} accent="teal" size="md" />
            <div>
              <div className="text-xs font-bold uppercase tracking-wider text-teal-400">
                Reverse Chord Finder
              </div>
              <h1 className="text-xl sm:text-2xl font-black text-white">
                Распознавание аккордов
              </h1>
              <p className="text-xs text-zinc-400 mt-0.5">
                Нажимайте на лады на грифе (до 1 ноты на струну), чтобы определить аккорд
              </p>
            </div>
          </div>

          <div className="flex flex-wrap items-center gap-2">
            {onTuningChange && (
              <TuningSelectorDropdown
                activeTuning={activeTuning}
                onTuningChange={onTuningChange}
                variant="pill"
              />
            )}
            <button
              onClick={handleStrum}
              disabled={pressedFrets.length === 0}
              className="px-4 py-2.5 rounded-xl bg-[#1B2536] hover:bg-[#233045] disabled:opacity-40 text-teal-300 border border-teal-500/30 text-xs font-bold flex items-center space-x-2 transition-all cursor-pointer"
            >
              <Volume2 className="w-4 h-4" />
              <span>Проиграть перебором</span>
            </button>
            {pressedFrets.length > 0 && (
              <button
                onClick={() => setPressedFrets([])}
                className="p-2.5 rounded-xl bg-red-500/10 hover:bg-red-500/20 text-red-400 border border-red-500/30 text-xs transition-colors cursor-pointer"
                title="Очистить все ноты"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            )}
          </div>
        </div>
      </StudioCard>

      {/* Detection Results */}
      <StudioCard>
        <div className="p-4 sm:p-5 flex items-center justify-between min-h-[84px] h-[84px]">
          <div className="flex flex-col justify-center min-w-0 pr-4">
            <div className="text-xs font-bold text-zinc-400 uppercase tracking-wider mb-0.5">
              Определенный аккорд
            </div>
            {detectedChords.length > 0 ? (
              <div className="flex items-baseline space-x-2 truncate">
                <span className="text-2xl sm:text-3xl font-black text-teal-300">
                  {detectedChords[0]}
                </span>
                {detectedChords.length > 1 && (
                  <span className="text-xs text-zinc-400 truncate">
                    или {detectedChords.slice(1).join(', ')}
                  </span>
                )}
              </div>
            ) : (
              <div className="text-sm text-zinc-500 italic truncate">
                {pressedFrets.length < 2
                  ? 'Зажмите минимум 2 ноты на грифе'
                  : 'Аккорд не найден (попробуйте изменить лады)'}
              </div>
            )}
          </div>
          <div className="text-right shrink-0">
            <div className="text-[11px] text-zinc-500 font-bold uppercase tracking-wider">
              Выбрано нот
            </div>
            <div className="text-lg font-mono font-bold text-zinc-200">
              {pressedFrets.length} / {stringCount}
            </div>
          </div>
        </div>
      </StudioCard>

      {/* Fretboard Wood Neck */}
      <StudioCard>
        <div className="p-4 sm:p-6 overflow-x-auto">
          <div className="min-w-[760px] select-none py-2">
            {/* Fret number markers */}
            <div className="flex ml-14 mb-2 text-xs font-mono font-bold text-zinc-500">
              <div className="w-12 text-center text-amber-400/80">0 (Откр)</div>
              {Array.from({ length: totalFrets }, (_, i) => i + 1).map((f) => (
                <div
                  key={f}
                  className={`w-12 text-center ${
                    [3, 5, 7, 9, 12, 15].includes(f) ? 'text-zinc-300 font-extrabold' : ''
                  }`}
                >
                  {f}
                </div>
              ))}
            </div>

            {/* Neck board */}
            <div className="relative rounded-2xl bg-gradient-to-r from-[#18130E] via-[#241C15] to-[#18130E] p-3 border-2 border-[#382B21] shadow-2xl">
              {Array.from({ length: stringCount }, (_, sIdx) => {
                const noteObj = tuningNotes[sIdx];
                const baseMidi = noteObj ? noteObj.midiNote : 64 - sIdx * 5;
                const stringName = noteObj ? noteObj.noteName : 'E';

                return (
                  <div
                    key={sIdx}
                    className="relative flex items-center h-9 border-b border-[#30251B] last:border-b-0"
                  >
                    {/* String label */}
                    <div className="w-11 font-mono text-xs font-bold text-amber-200/90 text-right pr-3 shrink-0">
                      {stringName}
                    </div>

                    {/* Open string 0 */}
                    <div className="w-12 flex justify-center shrink-0">
                      {(() => {
                        const isPressed = pressedFrets.some(
                          (p) => p.stringIndex === sIdx && p.fret === 0
                        );
                        return (
                          <button
                            onClick={() => handleFretClick(sIdx, 0, baseMidi)}
                            className={`w-7 h-7 rounded-full text-xs font-bold flex items-center justify-center transition-all cursor-pointer ${
                              isPressed
                                ? 'bg-teal-400 text-zinc-950 ring-2 ring-teal-200 scale-110 shadow-lg shadow-teal-500/50'
                                : 'bg-[#151D2A] text-zinc-400 hover:text-white border border-[#26354D]'
                            }`}
                          >
                            0
                          </button>
                        );
                      })()}
                    </div>

                    {/* Frets 1 to 15 */}
                    {Array.from({ length: totalFrets }, (_, fIdx) => {
                      const fret = fIdx + 1;
                      const midi = baseMidi + fret;
                      const isPressed = pressedFrets.some(
                        (p) => p.stringIndex === sIdx && p.fret === fret
                      );

                      return (
                        <div
                          key={fret}
                          className="w-12 flex justify-center items-center shrink-0 border-r border-[#4A382A]/50 relative"
                        >
                          <button
                            onClick={() => handleFretClick(sIdx, fret, midi)}
                            className={`w-7 h-7 rounded-full text-xs font-bold flex items-center justify-center transition-all cursor-pointer ${
                              isPressed
                                ? 'bg-teal-400 text-zinc-950 ring-2 ring-teal-200 scale-110 shadow-lg shadow-teal-500/50 z-10'
                                : 'hover:bg-white/10 text-transparent hover:text-zinc-300'
                            }`}
                          >
                            {isPressed ? fret : '•'}
                          </button>
                        </div>
                      );
                    })}
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      </StudioCard>
    </div>
  );
};
