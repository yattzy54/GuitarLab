import React from 'react';
import {
  PlayCircle,
  Activity,
  Timer,
  Gauge,
  Music,
  Grid,
  Search,
  FastForward,
  Mic,
  TrendingUp,
  X,
  Volume2,
  Drum,
  Globe,
} from 'lucide-react';
import { Studio3DBadge, StudioAccent } from './common/Studio3DComponents';
import { useLanguage } from '../i18n/LanguageContext';
import { TranslationKey } from '../i18n/types';

export type AppDestination =
  | 'player'
  | 'tuner'
  | 'metronome'
  | 'drums'
  | 'trainer'
  | 'tabs'
  | 'fretboard'
  | 'reverse_chord'
  | 'slowdowner'
  | 'recorder'
  | 'tracker'
  | 'language';

interface NavItemConfig {
  id: AppDestination;
  titleKey: TranslationKey;
  subtitleKey: TranslationKey;
  icon: any;
  accent: StudioAccent;
}

export const NAV_ITEM_CONFIGS: NavItemConfig[] = [
  {
    id: 'player',
    titleKey: 'nav_player',
    subtitleKey: 'nav_player_sub',
    icon: PlayCircle,
    accent: 'green',
  },
  {
    id: 'tuner',
    titleKey: 'nav_tuner',
    subtitleKey: 'nav_tuner_sub',
    icon: Activity,
    accent: 'amber',
  },
  {
    id: 'metronome',
    titleKey: 'nav_metronome',
    subtitleKey: 'nav_metronome_sub',
    icon: Timer,
    accent: 'teal',
  },
  {
    id: 'drums',
    titleKey: 'nav_drums',
    subtitleKey: 'nav_drums_sub',
    icon: Drum,
    accent: 'ruby',
  },
  {
    id: 'trainer',
    titleKey: 'nav_trainer',
    subtitleKey: 'nav_trainer_sub',
    icon: Gauge,
    accent: 'teal',
  },
  {
    id: 'tabs',
    titleKey: 'nav_tabs',
    subtitleKey: 'nav_tabs_sub',
    icon: Music,
    accent: 'amber',
  },
  {
    id: 'fretboard',
    titleKey: 'nav_fretboard',
    subtitleKey: 'nav_fretboard_sub',
    icon: Grid,
    accent: 'amber',
  },
  {
    id: 'reverse_chord',
    titleKey: 'nav_reverse_chord',
    subtitleKey: 'nav_reverse_chord_sub',
    icon: Search,
    accent: 'teal',
  },
  {
    id: 'slowdowner',
    titleKey: 'nav_slowdowner',
    subtitleKey: 'nav_slowdowner_sub',
    icon: FastForward,
    accent: 'teal',
  },
  {
    id: 'recorder',
    titleKey: 'nav_recorder',
    subtitleKey: 'nav_recorder_sub',
    icon: Mic,
    accent: 'ruby',
  },
  {
    id: 'tracker',
    titleKey: 'nav_tracker',
    subtitleKey: 'nav_tracker_sub',
    icon: TrendingUp,
    accent: 'green',
  },
  {
    id: 'language',
    titleKey: 'nav_language',
    subtitleKey: 'nav_language_sub',
    icon: Globe,
    accent: 'teal',
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
  const { t, languageInfo } = useLanguage();

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
            {t('all_tools')}
          </div>

          {NAV_ITEM_CONFIGS.map((item) => {
            const Icon = item.icon;
            const isActive = currentRoute === item.id;
            const title = t(item.titleKey);
            const subtitle = item.id === 'language' ? `${languageInfo.flag} ${languageInfo.nativeName}` : t(item.subtitleKey);

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
                  <div className="text-sm font-bold leading-tight flex items-center justify-between">
                    <span>{title}</span>
                    {item.id === 'language' && (
                      <span className="text-sm">{languageInfo.flag}</span>
                    )}
                  </div>
                  <div className="text-[11px] text-zinc-400 truncate mt-0.5">{subtitle}</div>
                </div>
              </button>
            );
          })}
        </nav>

        {/* Footer info & Quick Language Switcher Button */}
        <div className="p-4 border-t border-[#222B3D] bg-[#0A0D14] space-y-2">
          <button
            onClick={() => {
              onNavigate('language');
              onClose();
            }}
            className="w-full py-2 px-3 rounded-xl bg-[#141B28] hover:bg-[#1C2538] border border-[#232F42] text-xs font-bold text-zinc-300 flex items-center justify-between transition-colors cursor-pointer"
          >
            <div className="flex items-center space-x-2">
              <Globe className="w-3.5 h-3.5 text-teal-400" />
              <span>{t('language_title')}</span>
            </div>
            <div className="flex items-center space-x-1.5 font-semibold text-white">
              <span>{languageInfo.flag}</span>
              <span>{languageInfo.nativeName}</span>
            </div>
          </button>

          <div className="flex items-center justify-between text-xs text-zinc-500 pt-1">
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
