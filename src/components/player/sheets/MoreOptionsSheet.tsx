import React from 'react';
import { ModalBottomSheet } from './ModalBottomSheet';
import {
  Activity,
  Music2,
  Timer,
  Volume2,
  Copy,
  Check,
  FolderOpen,
  Wrench,
  CheckCircle2,
} from 'lucide-react';
import { TuxGuitarIcon } from '../../tuxguitar/TuxGuitarIcon';

interface MoreOptionsSheetProps {
  isOpen: boolean;
  onClose: () => void;
  onOpenTuner: () => void;
  onOpenTransposition: () => void;
  onOpenSongSelect: () => void;
  countInEnabled: boolean;
  onToggleCountIn: () => void;
  metronomeClickEnabled: boolean;
  onToggleMetronomeClick: () => void;
  onCopyTab: () => void;
  isCopied: boolean;
  onOpenStudioTools: () => void;
  onOpenTuxGuitar?: () => void;
}

export const MoreOptionsSheet: React.FC<MoreOptionsSheetProps> = ({
  isOpen,
  onClose,
  onOpenTuner,
  onOpenTransposition,
  onOpenSongSelect,
  countInEnabled,
  onToggleCountIn,
  metronomeClickEnabled,
  onToggleMetronomeClick,
  onCopyTab,
  isCopied,
  onOpenStudioTools,
  onOpenTuxGuitar,
}) => {
  return (
    <ModalBottomSheet
      isOpen={isOpen}
      onClose={onClose}
      title="Дополнительные инструменты"
      subtitle="Настройки плеера, тюнинг, метроном и студийные опции"
    >
      <div className="space-y-3">
        {/* TuxGuitar Studio Quick Access */}
        {onOpenTuxGuitar && (
          <button
            onClick={() => {
              onClose();
              onOpenTuxGuitar();
            }}
            className="w-full p-3 rounded-2xl bg-gradient-to-r from-amber-500/20 via-orange-500/15 to-amber-500/20 hover:from-amber-500/30 hover:to-orange-500/30 border border-amber-500/50 text-left flex items-center justify-between transition-all group shadow-md"
          >
            <div className="flex items-center space-x-3">
              <div className="p-2.5 rounded-xl bg-amber-500/20 text-amber-400 border border-amber-500/40 group-hover:scale-105 transition-transform shadow-sm">
                <TuxGuitarIcon className="w-5 h-5 text-amber-400" />
              </div>
              <div>
                <div className="flex items-center space-x-2">
                  <span className="text-sm font-bold text-white group-hover:text-amber-300 transition-colors">
                    TuxGuitar Studio
                  </span>
                  <span className="text-[10px] font-black uppercase tracking-wider px-1.5 py-0.5 rounded bg-amber-500/30 text-amber-300 border border-amber-500/40">
                    GP5 / GP4 / GP3
                  </span>
                </div>
                <div className="text-[11px] text-zinc-400 mt-0.5">
                  Мультитрековый редактор табулатур, матрица нот, гриф и пианино
                </div>
              </div>
            </div>
            <span className="text-xs font-bold text-amber-300 bg-amber-500/20 hover:bg-amber-500/30 px-3 py-1.5 rounded-xl border border-amber-500/40 shrink-0 ml-2">
              Открыть
            </span>
          </button>
        )}

        {/* Row 1: Tuner & Transpose */}
        <div className="grid grid-cols-2 gap-2.5">
          <button
            onClick={() => {
              onClose();
              onOpenTuner();
            }}
            className="p-3.5 rounded-2xl bg-zinc-800/80 hover:bg-zinc-700/80 border border-zinc-700 text-left transition-colors flex flex-col justify-between space-y-2 group"
          >
            <div className="p-2 rounded-xl bg-amber-500/15 text-amber-400 border border-amber-500/30 w-fit group-hover:scale-105 transition-transform">
              <Activity className="w-4 h-4" />
            </div>
            <div>
              <div className="text-sm font-bold text-white">Тюнер</div>
              <div className="text-[11px] text-zinc-400">Настройка инструмента</div>
            </div>
          </button>

          <button
            onClick={() => {
              onClose();
              onOpenTransposition();
            }}
            className="p-3.5 rounded-2xl bg-zinc-800/80 hover:bg-zinc-700/80 border border-zinc-700 text-left transition-colors flex flex-col justify-between space-y-2 group"
          >
            <div className="p-2 rounded-xl bg-amber-500/15 text-amber-400 border border-amber-500/30 w-fit group-hover:scale-105 transition-transform">
              <Music2 className="w-4 h-4" />
            </div>
            <div>
              <div className="text-sm font-bold text-white">Транспозиция</div>
              <div className="text-[11px] text-zinc-400">Смещение тона и строй</div>
            </div>
          </button>
        </div>

        {/* Toggles: Count-In and Metronome click */}
        <div className="bg-zinc-950/60 border border-zinc-800 rounded-2xl p-3.5 space-y-3">
          {/* Count-In Toggle */}
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2.5">
              <Timer className="w-4 h-4 text-amber-400" />
              <div>
                <div className="text-xs font-bold text-white">Отсчёт перед стартом (Count-in)</div>
                <div className="text-[11px] text-zinc-400">1 такт метронома перед началом игры</div>
              </div>
            </div>

            <button
              onClick={onToggleCountIn}
              className={`w-11 h-6 rounded-full transition-colors relative p-0.5 ${
                countInEnabled ? 'bg-emerald-500' : 'bg-zinc-800'
              }`}
            >
              <div
                className={`w-5 h-5 rounded-full bg-white transition-transform ${
                  countInEnabled ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>

          <div className="h-[1px] bg-zinc-800/80" />

          {/* Metronome Click Toggle */}
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2.5">
              <Volume2 className="w-4 h-4 text-amber-400" />
              <div>
                <div className="text-xs font-bold text-white">Клик метронома в табах</div>
                <div className="text-[11px] text-zinc-400">Звук клика поверх воспроизведения нот</div>
              </div>
            </div>

            <button
              onClick={onToggleMetronomeClick}
              className={`w-11 h-6 rounded-full transition-colors relative p-0.5 ${
                metronomeClickEnabled ? 'bg-emerald-500' : 'bg-zinc-800'
              }`}
            >
              <div
                className={`w-5 h-5 rounded-full bg-white transition-transform ${
                  metronomeClickEnabled ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>
        </div>

        {/* Quick action buttons */}
        <div className="space-y-2 pt-1">
          <button
            onClick={() => {
              onClose();
              onOpenSongSelect();
            }}
            className="w-full p-3 rounded-xl bg-zinc-800/60 hover:bg-zinc-700/80 border border-zinc-700 text-left flex items-center justify-between text-xs font-semibold text-zinc-200 transition-colors"
          >
            <div className="flex items-center space-x-2.5">
              <FolderOpen className="w-4 h-4 text-amber-400" />
              <span>Каталог композиций (Сменить песню)</span>
            </div>
            <span className="text-zinc-500 text-[11px]">Выбрать</span>
          </button>

          <button
            onClick={onCopyTab}
            className="w-full p-3 rounded-xl bg-zinc-800/60 hover:bg-zinc-700/80 border border-zinc-700 text-left flex items-center justify-between text-xs font-semibold text-zinc-200 transition-colors"
          >
            <div className="flex items-center space-x-2.5">
              {isCopied ? (
                <Check className="w-4 h-4 text-emerald-400" />
              ) : (
                <Copy className="w-4 h-4 text-amber-400" />
              )}
              <span>{isCopied ? 'Табулатура скопирована!' : 'Скопировать ASCII табулатуру'}</span>
            </div>
          </button>

          <button
            onClick={() => {
              onClose();
              onOpenStudioTools();
            }}
            className="w-full p-3 rounded-xl bg-amber-500/10 hover:bg-amber-500/20 border border-amber-500/30 text-left flex items-center justify-between text-xs font-bold text-amber-300 transition-colors"
          >
            <div className="flex items-center space-x-2.5">
              <Wrench className="w-4 h-4 text-amber-400" />
              <span>Мастерская GuitarLab (Гриф, Диктофон, Дневник)</span>
            </div>
          </button>
        </div>
      </div>
    </ModalBottomSheet>
  );
};
