import React, { useState } from 'react';
import { SongTabScore } from '../../types/tabPlayer';
import { X, Check } from 'lucide-react';

interface TuxGuitarSongInfoModalProps {
  score: SongTabScore;
  isOpen: boolean;
  onClose: () => void;
  onSave: (updatedScore: Partial<SongTabScore>) => void;
}

export const TuxGuitarSongInfoModal: React.FC<TuxGuitarSongInfoModalProps> = ({
  score,
  isOpen,
  onClose,
  onSave,
}) => {
  const [title, setTitle] = useState(score.title || '');
  const [artist, setArtist] = useState(score.artist || '');
  const [tempo, setTempo] = useState(score.defaultTempo || 120);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave({
      title: title.trim() || 'Untitled Tab',
      artist: artist.trim() || 'Guitarist',
      defaultTempo: tempo,
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-[#0F1420] border border-[#27344D] rounded-3xl max-w-md w-full p-6 space-y-5 shadow-2xl animate-fadeIn">
        <div className="flex items-center justify-between pb-3 border-b border-[#1E293B]">
          <h3 className="text-base font-black text-white flex items-center gap-2">
            <span>TuxGuitar Song Properties</span>
          </h3>
          <button
            onClick={onClose}
            className="p-1 rounded-xl text-zinc-400 hover:text-white hover:bg-[#1E273A] transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-zinc-300 uppercase tracking-wider mb-1">
              Song Title
            </label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full px-3 py-2 rounded-xl bg-[#090D15] border border-[#222E42] text-white text-sm focus:border-amber-400 focus:outline-hidden"
              placeholder="e.g. Smoke on the Water"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-zinc-300 uppercase tracking-wider mb-1">
              Artist / Band
            </label>
            <input
              type="text"
              value={artist}
              onChange={(e) => setArtist(e.target.value)}
              className="w-full px-3 py-2 rounded-xl bg-[#090D15] border border-[#222E42] text-white text-sm focus:border-amber-400 focus:outline-hidden"
              placeholder="e.g. Deep Purple"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-zinc-300 uppercase tracking-wider mb-1">
              Default Tempo (BPM)
            </label>
            <div className="flex items-center space-x-3">
              <input
                type="range"
                min="40"
                max="260"
                value={tempo}
                onChange={(e) => setTempo(parseInt(e.target.value, 10))}
                className="flex-1 h-1.5 bg-zinc-800 rounded-lg appearance-none cursor-pointer accent-amber-400"
              />
              <span className="font-mono text-sm font-bold text-amber-400 w-16 text-right">
                {tempo} BPM
              </span>
            </div>
          </div>

          <div className="flex justify-end space-x-2 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded-xl text-xs font-semibold bg-[#161D2B] text-zinc-300 hover:bg-[#1E273A]"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-5 py-2 rounded-xl text-xs font-black bg-amber-500 hover:bg-amber-400 text-zinc-950 flex items-center space-x-1.5 shadow-md shadow-amber-500/20"
            >
              <Check className="w-3.5 h-3.5" />
              <span>Apply Changes</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
