import React from 'react';
import { ModalBottomSheet } from './ModalBottomSheet';
import { TabTrackInfo } from '../../../types/tabPlayer';
import { Eye, Check } from 'lucide-react';

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
      subtitle="Компактная консоль дорожек (не более 100px в ширину)"
    >
      {/* Compact channel strips with horizontal scrolling */}
      <div className="flex items-start space-x-2.5 overflow-x-auto pb-4 pt-1 px-0.5 scrollbar-none" style={{ WebkitOverflowScrolling: 'touch' }}>
        {(tracks || []).map((track) => {
          const isActive = track.id === activeTrackId;
          return (
            <div
              key={track.id}
              className={`w-[92px] shrink-0 p-2 rounded-xl border flex flex-col items-center transition-all ${
                isActive
                  ? 'bg-zinc-800/95 border-amber-500/80 shadow-md ring-1 ring-amber-500/40'
                  : 'bg-zinc-950/80 border-zinc-800/90 hover:border-zinc-700'
              }`}
            >
              {/* Channel title */}
              <h4 className="text-[11px] font-bold text-white truncate w-full text-center" title={track.name}>
                {track.name}
              </h4>
              <span className="text-[9px] text-zinc-500 font-mono truncate w-full text-center mt-0.5">
                {track.tuningName || track.instrument}
              </span>

              {/* View Tab switch button */}
              <button
                onClick={() => onSelectActiveTrack(track.id)}
                className={`w-full mt-2 py-1 rounded-md text-[10px] font-bold flex items-center justify-center space-x-1 transition-colors ${
                  isActive
                    ? 'bg-amber-500 text-zinc-950 font-black'
                    : 'bg-zinc-800/90 text-zinc-300 hover:bg-zinc-700'
                }`}
              >
                {isActive ? <span>ТАБ ✓</span> : <span>Таб</span>}
              </button>

              {/* Mute & Solo row */}
              <div className="flex items-center space-x-1.5 w-full mt-2">
                <button
                  onClick={() => onToggleTrackMute(track.id)}
                  className={`flex-1 py-1 rounded-md text-[10px] font-mono font-black transition-colors ${
                    track.isMuted
                      ? 'bg-rose-600 text-white'
                      : 'bg-zinc-800/80 text-zinc-400 hover:text-white'
                  }`}
                >
                  M
                </button>
                <button
                  onClick={() => onToggleTrackSolo(track.id)}
                  className={`flex-1 py-1 rounded-md text-[10px] font-mono font-black transition-colors ${
                    track.isSolo
                      ? 'bg-emerald-600 text-white'
                      : 'bg-zinc-800/80 text-zinc-400 hover:text-white'
                  }`}
                >
                  S
                </button>
              </div>

              {/* Volume percentage */}
              <span className="text-[10px] font-mono font-bold text-amber-400 mt-2">
                {Math.round(track.volume * 100)}%
              </span>

              {/* Compact Volume slider */}
              <div className="w-full mt-1">
                <input
                  type="range"
                  min={0}
                  max={1}
                  step={0.05}
                  value={track.volume}
                  onChange={(e) => onUpdateTrackVolume(track.id, parseFloat(e.target.value))}
                  className="w-full h-1.5 bg-zinc-800 rounded-lg appearance-none cursor-pointer accent-amber-400"
                />
              </div>
            </div>
          );
        })}
      </div>
    </ModalBottomSheet>
  );
};
