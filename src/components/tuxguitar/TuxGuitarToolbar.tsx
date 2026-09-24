import React, { useState } from 'react';
import {
  Play,
  Pause,
  Square,
  Repeat,
  Volume2,
  FolderOpen,
  FilePlus,
  Save,
  Sliders,
  Sparkles,
  Info,
  ChevronDown,
  Layers,
  Music2,
  Piano,
  RotateCcw,
} from 'lucide-react';
import { TuxGuitarIcon } from './TuxGuitarIcon';
import { StemDuration } from '../../types/tabPlayer';
import { TUX_PRESETS } from './tuxGuitarPresets';

interface TuxGuitarToolbarProps {
  songTitle: string;
  songArtist: string;
  isPlaying: boolean;
  isLooping: boolean;
  tempo: number;
  selectedDuration: StemDuration;
  showFretboard: boolean;
  showPiano: boolean;
  showMixer: boolean;
  onPlayToggle: () => void;
  onStop: () => void;
  onLoopToggle: () => void;
  onTempoChange: (tempo: number) => void;
  onDurationSelect: (dur: StemDuration) => void;
  onToggleFretboard: () => void;
  onTogglePiano: () => void;
  onToggleMixer: () => void;
  onNewProject: () => void;
  onLoadPreset: (presetId: string) => void;
  onOpenImportExport: () => void;
  onOpenSongInfo: () => void;
}

export const TuxGuitarToolbar: React.FC<TuxGuitarToolbarProps> = ({
  songTitle,
  songArtist,
  isPlaying,
  isLooping,
  tempo,
  selectedDuration,
  showFretboard,
  showPiano,
  showMixer,
  onPlayToggle,
  onStop,
  onLoopToggle,
  onTempoChange,
  onDurationSelect,
  onToggleFretboard,
  onTogglePiano,
  onToggleMixer,
  onNewProject,
  onLoadPreset,
  onOpenImportExport,
  onOpenSongInfo,
}) => {
  const [isPresetsOpen, setIsPresetsOpen] = useState(false);

  return (
    <div className="bg-[#0C1019] border border-[#222B3D] rounded-2xl p-3 shadow-xl space-y-3">
      {/* Top Row: Brand, Song Info, Project File Actions */}
      <div className="flex flex-wrap items-center justify-between gap-3 pb-2.5 border-b border-[#1E2638]">
        {/* Brand & Song title */}
        <div className="flex items-center space-x-3">
          <div className="p-1 rounded-xl bg-[#162032] border border-[#273752] shadow-inner">
            <TuxGuitarIcon size={32} />
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <span className="text-xs font-black text-amber-400 tracking-wider uppercase font-mono">
                TabLab Studio
              </span>
              <span className="text-[10px] px-1.5 py-0.5 rounded-full bg-amber-500/20 text-amber-300 font-bold">
                TuxGuitar Engine • GP5 / GP4 / GP3
              </span>
            </div>
            <div className="flex items-center space-x-1.5 cursor-pointer group" onClick={onOpenSongInfo}>
              <span className="text-sm font-bold text-white group-hover:text-amber-300 transition-colors">
                {songTitle || 'Untitled Composition'}
              </span>
              <span className="text-xs text-zinc-500">— {songArtist || 'Guitarist'}</span>
              <Info className="w-3 h-3 text-zinc-500 group-hover:text-amber-400" />
            </div>
          </div>
        </div>

        {/* Project File Actions */}
        <div className="flex items-center space-x-1.5">
          {/* New Project */}
          <button
            onClick={onNewProject}
            className="px-2.5 py-1.5 rounded-xl bg-[#141B28] hover:bg-[#1E273A] border border-[#243044] text-xs font-semibold text-zinc-200 hover:text-white flex items-center space-x-1.5 transition-colors cursor-pointer"
            title="Create blank tab project"
          >
            <FilePlus className="w-3.5 h-3.5 text-amber-400" />
            <span className="hidden sm:inline">New</span>
          </button>

          {/* Presets Dropdown */}
          <div className="relative">
            <button
              onClick={() => setIsPresetsOpen(!isPresetsOpen)}
              className="px-2.5 py-1.5 rounded-xl bg-[#141B28] hover:bg-[#1E273A] border border-[#243044] text-xs font-semibold text-zinc-200 hover:text-white flex items-center space-x-1.5 transition-colors cursor-pointer"
            >
              <Music2 className="w-3.5 h-3.5 text-teal-400" />
              <span>Presets</span>
              <ChevronDown className="w-3 h-3 text-zinc-500" />
            </button>

            {isPresetsOpen && (
              <div className="absolute right-0 mt-2 w-64 rounded-2xl bg-[#121824] border border-[#27344D] shadow-2xl p-2 z-50 space-y-1">
                <div className="px-2 py-1 text-[10px] font-bold text-zinc-500 uppercase tracking-wider">
                  Preset Multi-Track Tabs
                </div>
                {TUX_PRESETS.map((p) => (
                  <button
                    key={p.id}
                    onClick={() => {
                      onLoadPreset(p.id);
                      setIsPresetsOpen(false);
                    }}
                    className="w-full text-left px-2.5 py-2 rounded-xl text-xs hover:bg-[#1C2538] transition-colors flex flex-col space-y-0.5 cursor-pointer"
                  >
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-zinc-100">{p.title}</span>
                      <span className="text-[10px] text-amber-400 font-mono">{p.bpm} BPM</span>
                    </div>
                    <span className="text-[10px] text-zinc-400">{p.artist} · {p.genre}</span>
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Import / Export */}
          <button
            onClick={onOpenImportExport}
            className="px-3 py-1.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-zinc-950 font-bold text-xs flex items-center space-x-1.5 transition-all shadow-md shadow-amber-500/20 cursor-pointer"
          >
            <FolderOpen className="w-3.5 h-3.5" />
            <span>I/O File</span>
          </button>
        </div>
      </div>

      {/* Middle Row: Playback Transport, BPM, and Auxiliary Toggles */}
      <div className="flex flex-wrap items-center justify-between gap-3">
        {/* Playback Controls */}
        <div className="flex items-center space-x-2">
          {/* Play/Pause */}
          <button
            onClick={onPlayToggle}
            className={`px-4 py-2 rounded-xl text-xs font-black flex items-center space-x-2 transition-all cursor-pointer shadow-md ${
              isPlaying
                ? 'bg-rose-500 text-white shadow-rose-500/30 ring-2 ring-rose-400'
                : 'bg-emerald-500 hover:bg-emerald-400 text-zinc-950 shadow-emerald-500/30 ring-2 ring-emerald-400/50'
            }`}
          >
            {isPlaying ? (
              <>
                <Pause className="w-4 h-4 fill-current" />
                <span>Pause</span>
              </>
            ) : (
              <>
                <Play className="w-4 h-4 fill-current ml-0.5" />
                <span>Play (Space)</span>
              </>
            )}
          </button>

          {/* Stop / Rewind */}
          <button
            onClick={onStop}
            className="p-2 rounded-xl bg-[#141B28] hover:bg-[#1E273A] border border-[#243044] text-zinc-300 hover:text-white transition-colors cursor-pointer"
            title="Stop & Rewind to Beginning"
          >
            <Square className="w-4 h-4 fill-current" />
          </button>

          {/* Loop playback */}
          <button
            onClick={onLoopToggle}
            className={`p-2 rounded-xl border transition-colors cursor-pointer ${
              isLooping
                ? 'bg-amber-500/20 text-amber-300 border-amber-500/50 ring-1 ring-amber-400'
                : 'bg-[#141B28] text-zinc-400 border-[#243044] hover:text-white'
            }`}
            title="Toggle Loop Playback"
          >
            <Repeat className="w-4 h-4" />
          </button>

          {/* Tempo BPM Adjuster */}
          <div className="flex items-center bg-[#141B28] border border-[#243044] rounded-xl px-2.5 py-1 space-x-2">
            <span className="text-[10px] font-bold text-zinc-500 uppercase">BPM</span>
            <input
              type="range"
              min="40"
              max="260"
              value={tempo}
              onChange={(e) => onTempoChange(parseInt(e.target.value, 10))}
              className="w-16 sm:w-20 h-1 bg-zinc-700 rounded-lg appearance-none cursor-pointer accent-amber-400"
            />
            <span className="font-mono text-xs font-black text-amber-400 w-8 text-right">
              {tempo}
            </span>
            <div className="flex flex-col space-y-0.5">
              <button
                onClick={() => onTempoChange(Math.min(300, tempo + 5))}
                className="text-[9px] font-bold text-zinc-400 hover:text-white px-1 leading-none"
              >
                +5
              </button>
              <button
                onClick={() => onTempoChange(Math.max(40, tempo - 5))}
                className="text-[9px] font-bold text-zinc-400 hover:text-white px-1 leading-none"
              >
                -5
              </button>
            </div>
          </div>
        </div>

        {/* View Toggles (Fretboard, Piano, Mixer) */}
        <div className="flex items-center space-x-1.5">
          <button
            onClick={onToggleFretboard}
            className={`px-2.5 py-1.5 rounded-xl border text-xs font-bold flex items-center space-x-1.5 transition-colors cursor-pointer ${
              showFretboard
                ? 'bg-amber-500/20 text-amber-300 border-amber-500/50'
                : 'bg-[#141B28] text-zinc-400 border-[#243044] hover:text-white'
            }`}
            title="Toggle 24-Fret Virtual Fretboard"
          >
            <Layers className="w-3.5 h-3.5" />
            <span className="hidden md:inline">Fretboard</span>
          </button>

          <button
            onClick={onTogglePiano}
            className={`px-2.5 py-1.5 rounded-xl border text-xs font-bold flex items-center space-x-1.5 transition-colors cursor-pointer ${
              showPiano
                ? 'bg-teal-500/20 text-teal-300 border-teal-500/50'
                : 'bg-[#141B28] text-zinc-400 border-[#243044] hover:text-white'
            }`}
            title="Toggle Piano Keyboard"
          >
            <Piano className="w-3.5 h-3.5" />
            <span className="hidden md:inline">Piano</span>
          </button>

          <button
            onClick={onToggleMixer}
            className={`px-2.5 py-1.5 rounded-xl border text-xs font-bold flex items-center space-x-1.5 transition-colors cursor-pointer ${
              showMixer
                ? 'bg-indigo-500/20 text-indigo-300 border-indigo-500/50'
                : 'bg-[#141B28] text-zinc-400 border-[#243044] hover:text-white'
            }`}
            title="Toggle Multi-Track Mixer Table"
          >
            <Sliders className="w-3.5 h-3.5" />
            <span className="hidden md:inline">Mixer</span>
          </button>
        </div>
      </div>

      {/* Bottom Row: TuxGuitar Note Duration Palette */}
      <div className="flex flex-wrap items-center justify-between gap-2 pt-2 border-t border-[#1A2334]">
        <div className="flex items-center space-x-1.5">
          <span className="text-[10px] uppercase font-bold text-zinc-500 mr-1">Duration:</span>
          {(['w', 'h', 'q', 'e', 's', 't'] as StemDuration[]).map((dur) => {
            const labels: Record<StemDuration, { name: string; num: string }> = {
              w: { name: 'Whole', num: '1' },
              h: { name: 'Half', num: '1/2' },
              q: { name: 'Quarter', num: '1/4' },
              e: { name: '8th', num: '1/8' },
              s: { name: '16th', num: '1/16' },
              t: { name: '32nd', num: '1/32' },
            };
            const isSelected = selectedDuration === dur;

            return (
              <button
                key={dur}
                onClick={() => onDurationSelect(dur)}
                className={`px-2 py-1 rounded-lg text-xs font-mono font-bold transition-all cursor-pointer border ${
                  isSelected
                    ? 'bg-amber-400 text-zinc-950 border-amber-300 shadow-sm'
                    : 'bg-[#121824] text-zinc-300 border-[#202C3F] hover:text-white hover:border-zinc-500'
                }`}
                title={`${labels[dur].name} note`}
              >
                {labels[dur].num}
              </button>
            );
          })}
        </div>

        <div className="text-[10px] font-mono text-zinc-400 hidden sm:block">
          Shortcuts: Space (Play), 0-9 (Frets), Del (Clear), Arrows (Move)
        </div>
      </div>
    </div>
  );
};
