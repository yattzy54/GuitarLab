import React from 'react';
import {
  PlayCircle,
  Activity,
  Timer,
  Gauge,
  Music,
  Grid,
  FastForward,
  Mic,
  TrendingUp,
  X,
  Volume2,
} from 'lucide-react';
import { Studio3DBadge, StudioAccent } from './common/Studio3DComponents';

export type AppDestination =
  | 'player'
  | 'tuner'
  | 'metronome'
  | 'trainer'
  | 'tabs'
  | 'fretboard'
  | 'slowdowner'
  | 'recorder'
  | 'tracker';

interface NavItem {
  id: AppDestination;
  title: string;
  subtitle: string;
  icon: any;
  accent: StudioAccent;
}

export const NAV_ITEMS: NavItem[] = [
  {
    id: 'player',
    title: 'Табы',
    subtitle: 'Профессиональный плеер табов (Songsterr)',
    icon: PlayCircle,
    accent: 'green',
  },
  {
    id: 'tuner',
    title: 'Guitar Tuner',
    subtitle: 'Chromatic YIN Pitch Tracker',
    icon: Activity,
    accent: 'amber',
  },
  {
    id: 'metronome',
    title: 'Metronome',
    subtitle: 'Precision Click & Time Signatures',
    icon: Timer,
    accent: 'teal',
  },
  {
    id: 'trainer',
    title: 'Auto-Speed Trainer',
    subtitle: 'Progressive Tempo Builder',
    icon: Gauge,
    accent: 'teal',
  },
  {
    id: 'tabs',
    title: 'Tab Editor & Studio',
    subtitle: 'Редактор табов и парсер ASCII',
    icon: Music,
    accent: 'amber',
  },
  {
    id: 'fretboard',
    title: 'Fretboard & Chords',
    subtitle: 'Scales & Reverse Chord Lookup',
    icon: Grid,
    accent: 'amber',
  },
  {
    id: 'slowdowner',
    title: 'Audio Slow-Downer',
    subtitle: 'Pitch-Preserved A-B Looper',
    icon: FastForward,
    accent: 'teal',
  },
  {
    id: 'recorder',
    title: 'Riff Quick Recorder',
    subtitle: 'Mic Memos & Idea Capturer',
    icon: Mic,
    accent: 'ruby',
  },
  {
    id: 'tracker',
    title: 'Session Tracker',
    subtitle: 'Daily Streaks & Practice Log',
    icon: TrendingUp,
    accent: 'green',
  },
];

interface NavigationDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  currentRoute: AppDestination;
  onNavigate: (route: AppDestination) => void;
}

export const NavigationDrawer: React.FC<NavigationDrawerProps> = ({
  isOpen,
  onClose,
  currentRoute,
  onNavigate,
}) => {
  return (
    <>
      {/* Backdrop */}
      {isOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/70 backdrop-blur-xs transition-opacity"
          onClick={onClose}
        />
      )}

      {/* Drawer */}
      <aside
        className={`fixed top-0 bottom-0 left-0 z-50 w-72 sm:w-80 bg-[#0F131D] border-r border-[#222B3D] text-zinc-100 flex flex-col transform transition-transform duration-300 ease-in-out shadow-2xl shadow-black ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        {/* Header */}
        <div className="p-5 flex items-center justify-between border-b border-[#222B3D]">
          <div className="flex items-center space-x-3">
            <Studio3DBadge icon={Volume2} accent="amber" size="md" />
            <div>
              <h2 className="font-black text-lg tracking-tight text-white flex items-center gap-1.5">
                Guitar<span className="text-amber-400">Lab</span>
              </h2>
              <p className="text-xs text-teal-400 font-semibold">Pro Musician's Suite</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-xl text-zinc-400 hover:text-white hover:bg-[#1E273A] transition-colors cursor-pointer"
            aria-label="Close menu"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Navigation Items List */}
        <nav className="flex-1 overflow-y-auto p-3 space-y-1.5">
          <div className="px-3 py-1.5 text-[11px] font-bold text-zinc-500 uppercase tracking-wider">
            Studio Instruments & Practice
          </div>

          {NAV_ITEMS.map((item) => {
            const Icon = item.icon;
            const isActive = currentRoute === item.id;
            return (
              <button
                key={item.id}
                onClick={() => {
                  onNavigate(item.id);
                  onClose();
                }}
                className={`w-full text-left px-3 py-2.5 rounded-2xl flex items-center space-x-3.5 transition-all cursor-pointer ${
                  isActive
                    ? 'bg-[#1D2536] border border-amber-500/40 text-amber-300 shadow-[0_2px_12px_rgba(245,158,11,0.15)]'
                    : 'text-zinc-300 hover:bg-[#151C2A] hover:text-white border border-transparent'
                }`}
              >
                <Studio3DBadge
                  icon={Icon}
                  accent={isActive ? item.accent : 'slate'}
                  size="sm"
                />
                <div className="flex-1 min-w-0">
                  <div className="text-sm font-bold leading-tight">{item.title}</div>
                  <div className="text-[11px] text-zinc-400 truncate mt-0.5">{item.subtitle}</div>
                </div>
              </button>
            );
          })}
        </nav>

        {/* Footer info */}
        <div className="p-4 border-t border-[#222B3D] bg-[#0A0D14]">
          <div className="flex items-center justify-between text-xs text-zinc-400">
            <span>GuitarLab Studio</span>
            <span className="px-2.5 py-0.5 rounded-full bg-[#161D2B] border border-[#27344D] text-amber-400 font-mono text-[10px] font-bold">
              v2.5 PRO
            </span>
          </div>
        </div>
      </aside>
    </>
  );
};
