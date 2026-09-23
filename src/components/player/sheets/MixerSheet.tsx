import React from 'react';
import { ModalBottomSheet } from './ModalBottomSheet';
import { TabTrackInfo } from '../../../types/tabPlayer';

interface MixerSheetProps {
  isOpen: boolean;
  onClose: () => void;
  tracks: TabTrackInfo[];
  activeTrackId: string;
  onSelectActiveTrack: (trackId: string) => void;
  onUpdateTrackVolume: (trackId: string, volume: number) => void;
  onToggleTrackMute: (trackId: string) => void;
  onToggleTrackSolo: (trackId: string) => void;
}

export const MixerSheet: React.FC<MixerSheetProps> = ({
  isOpen,
  onClose,
  tracks,
  activeTrackId,
  onSelectActiveTrack,
  onUpdateTrackVolume,
  onToggleTrackMute,
  onToggleTrackSolo,
}) => {
  return (
    <ModalBottomSheet
      isOpen={isOpen}
      onClose={onClose}
      title="Микшер инструментов"
      subtitle="Нажмите на элемент списка для выбора активного таба"
    >
      <div className="space-y-2 pb-2">
        {(tracks || []).map((track) => {
          const isActive = track.id === activeTrackId;
          return (
            <div
              key={track.id}
              onClick={() => onSelectActiveTrack(track.id)}
              className={`p-2.5 rounded-xl border cursor-pointer transition-all ${
                isActive
                  ? 'bg-sky-950/40 border-sky-500 shadow-md ring-1 ring-sky-500/30'
                  : 'bg-zinc-950/70 border-zinc-800/80 hover:border-zinc-700 hover:bg-zinc-900/60'
              }`}
            >
              {/* Top row: Radio dot indicator + Track info + Mute/Solo */}
              <div className="flex items-center justify-between gap-2">
                <div className="flex items-center space-x-2.5 min-w-0 flex-1">
                  <div
                    className={`w-4 h-4 rounded-full border flex items-center justify-center shrink-0 transition-colors ${
                      isActive
                        ? 'border-sky-400 bg-sky-400'
                        : 'border-zinc-600 bg-zinc-800'
                    }`}
                  >
                    {isActive && <div className="w-1.5 h-1.5 rounded-full bg-zinc-950" />}
                  </div>

                  <div className="min-w-0">
                    <h4
                      className={`text-xs font-bold truncate leading-tight ${
                        isActive ? 'text-sky-300' : 'text-zinc-100'
                      }`}
                    >
                      {track.name}
                    </h4>
                    <span className="text-[10px] text-zinc-400 font-mono truncate block">
                      {track.tuningName || track.instrument}
                    </span>
                  </div>
                </div>

                {/* Mute & Solo buttons (stops propagation so clicking M/S won't toggle active track) */}
                <div className="flex items-center space-x-1.5 shrink-0">
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      onToggleTrackMute(track.id);
                    }}
                    className={`px-2 py-1 rounded text-[10px] font-mono font-black transition-colors ${
                      track.isMuted
                        ? 'bg-rose-600 text-white'
                        : 'bg-zinc-800/90 text-zinc-400 hover:text-white'
                    }`}
                  >
                    M
                  </button>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      onToggleTrackSolo(track.id);
                    }}
                    className={`px-2 py-1 rounded text-[10px] font-mono font-black transition-colors ${
                      track.isSolo
                        ? 'bg-emerald-600 text-white'
                        : 'bg-zinc-800/90 text-zinc-400 hover:text-white'
                    }`}
                  >
                    S
                  </button>
                </div>
              </div>

              {/* Bottom row: Compact Slider + Volume Percentage */}
              <div
                className="flex items-center space-x-2.5 mt-1.5 pt-1 border-t border-zinc-800/40"
                onClick={(e) => e.stopPropagation()}
              >
                <input
                  type="range"
                  min={0}
                  max={1}
                  step={0.05}
                  value={track.volume}
                  onChange={(e) => onUpdateTrackVolume(track.id, parseFloat(e.target.value))}
                  className="w-full h-1.5 bg-zinc-800 rounded-lg appearance-none cursor-pointer accent-amber-400"
                />
                <span className="text-[10px] font-mono font-bold text-amber-400 w-8 text-right shrink-0">
                  {Math.round(track.volume * 100)}%
                </span>
              </div>
            </div>
          );
        })}
      </div>
    </ModalBottomSheet>
  );
};
