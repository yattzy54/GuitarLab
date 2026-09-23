import React from 'react';
import { ModalBottomSheet } from './ModalBottomSheet';
import { SongTabScore } from '../../../types/tabPlayer';
import { SONGS_CATALOG } from '../../../data/songsLibrary';
import { Music, Check, Sparkles } from 'lucide-react';

interface SongSelectSheetProps {
  isOpen: boolean;
  onClose: () => void;
  currentSongId: string;
  onSelectSong: (song: SongTabScore) => void;
}

export const SongSelectSheet: React.FC<SongSelectSheetProps> = ({
  isOpen,
  onClose,
  currentSongId,
  onSelectSong,
}) => {
  return (
    <ModalBottomSheet
      isOpen={isOpen}
      onClose={onClose}
      title="Каталог табулатур"
      subtitle="Профессиональные партитуры с дорожками и аудио"
    >
      <div className="space-y-2.5">
        {SONGS_CATALOG.map((song) => {
          const isSelected = song.id === currentSongId;
          const leadTrack = song.tracks[0];

          return (
            <button
              key={song.id}
              onClick={() => {
                onSelectSong(song);
                onClose();
              }}
              className={`w-full p-3.5 rounded-2xl border text-left flex items-center justify-between transition-all ${
                isSelected
                  ? 'bg-amber-500/15 border-amber-500/50 shadow-md ring-1 ring-amber-500/30'
                  : 'bg-zinc-950/60 border-zinc-800 hover:border-zinc-700 hover:bg-zinc-800/40'
              }`}
            >
              <div className="flex items-center space-x-3 min-w-0">
                <div
                  className={`w-10 h-10 rounded-xl flex items-center justify-center shrink-0 ${
                    isSelected ? 'bg-amber-500 text-zinc-950' : 'bg-zinc-800 text-zinc-400'
                  }`}
                >
                  <Music className="w-5 h-5" />
                </div>

                <div className="min-w-0">
                  <div className="flex items-center space-x-2">
                    <h4 className="text-sm font-bold text-white truncate">{song.title}</h4>
                    <span className="text-xs text-zinc-400">· {song.artist}</span>
                  </div>
                  <div className="flex items-center space-x-2 text-[11px] text-zinc-400 font-mono mt-0.5">
                    <span className="text-amber-400 font-medium">
                      {leadTrack?.tuningName || 'Standard'}
                    </span>
                    <span>•</span>
                    <span>{song.defaultTempo} BPM</span>
                    <span>•</span>
                    <span>РЕВ: {song.revisionDate}</span>
                  </div>
                </div>
              </div>

              {isSelected && <Check className="w-5 h-5 text-amber-400 shrink-0 ml-2" />}
            </button>
          );
        })}
      </div>
    </ModalBottomSheet>
  );
};
