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
  Layers,
} from 'lucide-react';
import { TuxGuitarIcon } from './tuxguitar/TuxGuitarIcon';
import { Studio3DBadge, StudioAccent } from './common/Studio3DComponents';
import { useLanguage } from '../i18n/LanguageContext';
import { TranslationKey } from '../i18n/types';
import { AppFlavor, getAppFlavor, setAppFlavor, TAB_EXCLUSIVE_ROUTES } from '../config/flavor';

export type AppDestination =
  | 'player'
  | 'guitartabedit'
  | 'tuxguitar'
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
  isTabFeature?: boolean;
}

export const NAV_ITEM_CONFIGS: NavItemConfig[] = [
  {
    id: 'tabs',
    titleKey: 'nav_tabs',
    subtitleKey: 'nav_tabs_sub',
    icon: Music,
    accent: 'amber',
    isTabFeature: true,
  },
  {
    id: 'guitartabedit',
    titleKey: 'nav_guitartabedit',
    subtitleKey: 'nav_guitartabedit_sub',
    icon: TuxGuitarIcon,
    accent: 'amber',
    isTabFeature: true,
  },
  {
    id: 'tuxguitar',
    titleKey: 'nav_tuxguitar',
    subtitleKey: 'nav_tuxguitar_sub',
    icon: TuxGuitarIcon,
    accent: 'amber',
    isTabFeature: true,
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
  const currentFlavor = getAppFlavor();

  // Filter items strictly by flavor:
  // In tabs flavor: ONLY show the 3 tab sections (Tabs, GuitarTabEdit, TabLab)
  // In standard flavor: HIDE the 3 tab sections
  const filteredNavItems = NAV_ITEM_CONFIGS.filter((item) => {
    if (currentFlavor === 'tabs') {
      return item.isTabFeature === true;
    } else {
      return !item.isTabFeature;
    }
  });

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
            <Studio3DBadge
              icon={currentFlavor === 'tabs' ? Music : Volume2}
              accent={currentFlavor === 'tabs' ? 'amber' : 'teal'}
              size="md"
            />
            <div>
              <h2 className="font-black text-lg tracking-tight text-white flex items-center gap-1.5">
                {currentFlavor === 'tabs' ? (
                  <>Tab<span className="text-amber-400">Lab</span></>
                ) : (
                  <>Guitar<span className="text-teal-400">Lab</span></>
                )}
              </h2>
              <p className="text-xs text-zinc-400 font-semibold">
                {currentFlavor === 'tabs' ? 'Tab Suite Edition' : "Pro Musician's Suite"}
              </p>
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

        {/* Flavor Switcher Segment */}
        <div className="px-4 py-3 bg-[#0A0D14] border-b border-[#222B3D]">
          <div className="text-[10px] uppercase tracking-wider text-zinc-400 font-bold mb-1.5 flex items-center gap-1.5">
            <Layers className="w-3 h-3 text-amber-400" />
            <span>App Flavor</span>
          </div>
          <div className="grid grid-cols-2 gap-1.5 p-1 bg-[#141B28] rounded-xl border border-[#232F42]">
            <button
              onClick={() => setAppFlavor('standard')}
              className={`py-1.5 px-2 rounded-lg text-xs font-bold transition-all cursor-pointer text-center ${
                currentFlavor === 'standard'
                  ? 'bg-teal-500/20 text-teal-300 border border-teal-500/50 shadow-sm'
                  : 'text-zinc-400 hover:text-zinc-200'
              }`}
            >
              Main Flavor
            </button>
            <button
              onClick={() => setAppFlavor('tabs')}
              className={`py-1.5 px-2 rounded-lg text-xs font-bold transition-all cursor-pointer text-center ${
                currentFlavor === 'tabs'
                  ? 'bg-amber-500/20 text-amber-300 border border-amber-500/50 shadow-sm'
                  : 'text-zinc-400 hover:text-zinc-200'
              }`}
            >
              Tabs Flavor
            </button>
          </div>
          <div className="text-[10px] text-zinc-400 mt-1.5 text-center">
            {currentFlavor === 'tabs'
              ? 'Showing ONLY 3 Tab sections'
              : 'Main flavor: 3 Tab sections hidden'}
          </div>
        </div>

        {/* Navigation Items List */}
        <nav className="flex-1 overflow-y-auto p-3 space-y-1.5">
          <div className="px-3 py-1.5 text-[11px] font-bold text-zinc-500 uppercase tracking-wider">
            {currentFlavor === 'tabs' ? 'Tab Suite (3 Sections)' : t('all_tools')}
          </div>

          {filteredNavItems.map((item) => {
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
          {currentFlavor !== 'tabs' && (
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
          )}

          <div className="flex items-center justify-between text-xs text-zinc-500 pt-1">
            <span>{currentFlavor === 'tabs' ? 'TabLab Flavor' : 'GuitarLab Flavor'}</span>
            <span className="px-2.5 py-0.5 rounded-full bg-[#161D2B] border border-[#27344D] text-amber-400 font-mono text-[10px] font-bold">
              {currentFlavor === 'tabs' ? 'TABS' : 'MAIN'}
            </span>
          </div>
        </div>
      </aside>
    </>
  );
};
