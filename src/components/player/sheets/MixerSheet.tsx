import React from 'react';
import { ModalBottomSheet } from './ModalBottomSheet';
import { TabTrackInfo } from '../../../types/tabPlayer';
import { Volume2, VolumeX, Check, Eye } from 'lucide-react';

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
      title="Микшер инструментов и дорожек"
      subtitle="Выбор активной партии для просмотра табов и баланс громкости"
    >
      <div className="space-y-3">
        {tracks.map((track) => {
          const isActive = track.id === activeTrackId;

          return (
            <div
              key={track.id}
              className={`p-3.5 rounded-2xl border transition-all ${
                isActive
                  ? 'bg-zinc-800/90 border-amber-500/50 shadow-md ring-1 ring-amber-500/30'
                  : 'bg-zinc-950/60 border-zinc-800 hover:border-zinc-700'
              }`}
            >
              {/* Top row: Track info & View Tab button */}
              <div className="flex items-center justify-between gap-3 mb-2.5">
                <div className="min-w-0">
                  <div className="flex items-center space-x-2">
                    <h4 className="text-sm font-bold text-white truncate">{track.name}</h4>
                    {isActive && (
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30">
                        АКТИВНАЯ
                      </span>
                    )}
                  </div>
                  <div className="text-[11px] text-zinc-400 font-mono mt-0.5">
                    {track.instrument} · {track.tuningName}
                  </div>
                </div>

                {/* Show tab button */}
                {!isActive && (
                  <button
                    onClick={() => onSelectActiveTrack(track.id)}
                    className="px-2.5 py-1 rounded-xl bg-zinc-800 hover:bg-zinc-700 text-xs font-semibold text-zinc-200 border border-zinc-700 flex items-center space-x-1.5 transition-colors shrink-0"
                  >
                    <Eye className="w-3.5 h-3.5 text-amber-400" />
                    <span>Показать таб</span>
                  </button>
                )}
              </div>

              {/* Volume Slider and Mute / Solo controls */}
              <div className="flex items-center space-x-3 pt-1 border-t border-zinc-800/60">
                <button
                  onClick={() => onToggleTrackMute(track.id)}
                  className={`px-2.5 py-1 rounded-lg text-xs font-bold font-mono transition-colors ${
                    track.isMuted
                      ? 'bg-rose-600 text-white'
                      : 'bg-zinc-800 text-zinc-400 hover:text-white'
                  }`}
                >
                  MUTE
                </button>

                <button
                  onClick={() => onToggleTrackSolo(track.id)}
                  className={`px-2.5 py-1 rounded-lg text-xs font-bold font-mono transition-colors ${
                    track.isSolo
                      ? 'bg-amber-500 text-zinc-950 font-black'
                      : 'bg-zinc-800 text-zinc-400 hover:text-white'
                  }`}
                >
                  SOLO
                </button>

                <div className="flex-1 flex items-center space-x-2">
                  <Volume2 className="w-3.5 h-3.5 text-zinc-500 shrink-0" />
                  <input
                    type="range"
                    min={0}
                    max={1}
                    step={0.05}
                    value={track.volume}
                    onChange={(e) => onUpdateTrackVolume(track.id, parseFloat(e.target.value))}
                    className="w-full h-1.5 bg-zinc-800 rounded-lg appearance-none cursor-pointer accent-amber-400"
                  />
                  <span className="text-[11px] font-mono text-zinc-400 w-8 text-right">
                    {Math.round(track.volume * 100)}%
                  </span>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </ModalBottomSheet>
  );
};
