import React, { useState, useRef, useEffect } from 'react';
import { Menu, Globe, Check, ChevronDown } from 'lucide-react';
import { AppDestination, NAV_ITEM_CONFIGS } from './NavigationDrawer';
import { Studio3DBadge } from './common/Studio3DComponents';
import { TuningSelectorDropdown } from './common/TuningSelectorDropdown';
import { Tuning } from '../types';
import { useLanguage } from '../i18n/LanguageContext';
import { SUPPORTED_LANGUAGES } from '../i18n/languages';

interface NavbarProps {
  currentRoute: AppDestination;
  onOpenDrawer: () => void;
  activeTuningName?: string;
  activeTuning?: Tuning;
  onTuningChange?: (tuning: Tuning) => void;
  a4Pitch?: number;
  onNavigate?: (route: AppDestination) => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  currentRoute,
  onOpenDrawer,
  activeTuningName,
  activeTuning,
  onTuningChange,
  a4Pitch = 440,
  onNavigate,
}) => {
  const { t, language, languageInfo, setLanguage } = useLanguage();
  const [isLangMenuOpen, setIsLangMenuOpen] = useState(false);
  const langMenuRef = useRef<HTMLDivElement>(null);

  // Close language menu on outside click
  useEffect(() => {
    const handleOutsideClick = (e: MouseEvent) => {
      if (langMenuRef.current && !langMenuRef.current.contains(e.target as Node)) {
        setIsLangMenuOpen(false);
      }
    };
    if (isLangMenuOpen) {
      document.addEventListener('mousedown', handleOutsideClick);
    }
    return () => {
      document.removeEventListener('mousedown', handleOutsideClick);
    };
  }, [isLangMenuOpen]);

  const currentItem =
    NAV_ITEM_CONFIGS.find((item) => item.id === currentRoute) || NAV_ITEM_CONFIGS[0];
  const Icon = currentItem.icon;
  const title = t(currentItem.titleKey);
  const subtitle =
    currentItem.id === 'language'
      ? `${languageInfo.flag} ${languageInfo.nativeName}`
      : t(currentItem.subtitleKey);

  // Show tuning selector prominently on chord finder, fretboard, chords, tuner, recorder
  const showTuningSelector =
    activeTuning &&
    onTuningChange &&
    ['fretboard', 'reverse_chord', 'tuner', 'recorder'].includes(currentRoute);

  return (
    <header className="sticky top-0 z-30 bg-[#0C1018]/90 backdrop-blur-md border-b border-[#222B3D] px-4 py-3">
      <div className="max-w-6xl mx-auto flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <button
            onClick={onOpenDrawer}
            className="p-2 -ml-1 rounded-xl text-zinc-300 hover:text-white hover:bg-[#182030] transition-colors cursor-pointer"
            aria-label="Open menu"
            title="Menu"
          >
            <Menu className="w-5 h-5 text-amber-400" />
          </button>
          <div className="flex items-center space-x-2.5">
            <Studio3DBadge icon={Icon} accent={currentItem.accent} size="sm" />
            <div>
              <h1 className="text-base font-bold text-white tracking-tight leading-none">
                {title}
              </h1>
              <p className="text-[11px] text-zinc-400 hidden sm:block leading-none mt-1">
                {subtitle}
              </p>
            </div>
          </div>
        </div>

        {/* Right Toolbar: Guitar Tuning Selector & Language Selector */}
        <div className="flex items-center space-x-2">
          {showTuningSelector ? (
            <TuningSelectorDropdown
              activeTuning={activeTuning!}
              onTuningChange={onTuningChange!}
              variant="toolbar"
              a4Pitch={a4Pitch}
            />
          ) : activeTuningName ? (
            <div className="hidden sm:flex px-3 py-1 rounded-full bg-[#151C2A] border border-[#263146] text-xs font-bold text-amber-400 items-center gap-1.5 shadow-sm">
              <span className="w-2 h-2 rounded-full bg-amber-400 shadow-[0_0_8px_rgba(245,158,11,0.8)] animate-pulse" />
              <span>{activeTuningName}</span>
            </div>
          ) : null}

          {/* Quick Language Dropdown */}
          <div className="relative" ref={langMenuRef}>
            <button
              onClick={() => setIsLangMenuOpen(!isLangMenuOpen)}
              className="px-2.5 py-1.5 rounded-xl bg-[#141B28] hover:bg-[#1E273A] border border-[#243044] text-xs font-bold text-zinc-300 hover:text-white flex items-center space-x-1.5 transition-all shadow-sm cursor-pointer"
              title={t('language_title')}
            >
              <span className="text-sm leading-none">{languageInfo.flag}</span>
              <span className="font-mono uppercase text-[11px] text-zinc-200">
                {languageInfo.code}
              </span>
              <ChevronDown className="w-3 h-3 text-zinc-400" />
            </button>

            {isLangMenuOpen && (
              <div className="absolute right-0 mt-2 w-64 max-h-80 overflow-y-auto rounded-2xl bg-[#0F1420] border border-[#27344D] shadow-2xl p-1.5 z-50 animate-fadeIn">
                <div className="p-2 border-b border-[#1E293B] flex items-center justify-between text-[11px] font-bold text-zinc-400">
                  <span className="flex items-center gap-1.5">
                    <Globe className="w-3.5 h-3.5 text-teal-400" />
                    {t('language_title')}
                  </span>
                  {onNavigate && (
                    <button
                      onClick={() => {
                        setIsLangMenuOpen(false);
                        onNavigate('language');
                      }}
                      className="text-teal-400 hover:underline cursor-pointer"
                    >
                      {t('all')} 20
                    </button>
                  )}
                </div>

                <div className="space-y-0.5 py-1">
                  {SUPPORTED_LANGUAGES.map((l) => {
                    const isSelected = language === l.code;
                    return (
                      <button
                        key={l.code}
                        onClick={() => {
                          setLanguage(l.code);
                          setIsLangMenuOpen(false);
                        }}
                        className={`w-full px-2.5 py-1.5 rounded-xl text-left flex items-center justify-between text-xs transition-colors cursor-pointer ${
                          isSelected
                            ? 'bg-teal-500/20 text-teal-300 font-bold'
                            : 'text-zinc-300 hover:bg-[#172030] hover:text-white'
                        }`}
                      >
                        <div className="flex items-center space-x-2">
                          <span className="text-base">{l.flag}</span>
                          <span>{l.nativeName}</span>
                          <span className="text-[10px] text-zinc-500">({l.name})</span>
                        </div>
                        {isSelected && <Check className="w-3.5 h-3.5 text-teal-400 stroke-[3]" />}
                      </button>
                    );
                  })}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};
