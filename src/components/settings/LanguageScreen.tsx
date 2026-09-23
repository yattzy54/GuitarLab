import React, { useState, useMemo } from 'react';
import {
  Globe,
  Check,
  Search,
  RotateCcw,
  Sparkles,
  Smartphone,
  CheckCircle2,
} from 'lucide-react';
import { useLanguage } from '../../i18n/LanguageContext';
import { SUPPORTED_LANGUAGES } from '../../i18n/languages';
import { SupportedLanguageCode, LanguageInfo } from '../../i18n/types';
import { Studio3DBadge, StudioCard } from '../common/Studio3DComponents';

export const LanguageScreen: React.FC = () => {
  const {
    language,
    languageInfo,
    detectedLanguage,
    isSystemLanguageUsed,
    setLanguage,
    resetToSystemLanguage,
    t,
  } = useLanguage();

  const [searchQuery, setSearchQuery] = useState('');
  const [justApplied, setJustApplied] = useState(false);

  // Filter languages by search query
  const filteredLanguages = useMemo(() => {
    const q = searchQuery.toLowerCase().trim();
    if (!q) return SUPPORTED_LANGUAGES;
    return SUPPORTED_LANGUAGES.filter(
      (l) =>
        l.name.toLowerCase().includes(q) ||
        l.nativeName.toLowerCase().includes(q) ||
        l.code.toLowerCase().includes(q)
    );
  }, [searchQuery]);

  const detectedInfo = useMemo(() => {
    return (
      SUPPORTED_LANGUAGES.find((l) => l.code === detectedLanguage) ||
      SUPPORTED_LANGUAGES[0]
    );
  }, [detectedLanguage]);

  const handleSelectLanguage = (code: SupportedLanguageCode) => {
    setLanguage(code);
    setJustApplied(true);
    setTimeout(() => {
      setJustApplied(false);
    }, 2500);
  };

  const handleResetToSystem = () => {
    resetToSystemLanguage();
    setJustApplied(true);
    setTimeout(() => {
      setJustApplied(false);
    }, 2500);
  };

  return (
    <div className="max-w-5xl mx-auto px-4 py-6 space-y-6">
      {/* Header Banner */}
      <StudioCard>
        <div className="p-5 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center space-x-4">
            <Studio3DBadge icon={Globe} accent="teal" size="lg" />
            <div>
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold uppercase tracking-wider text-teal-400">
                  {t('language_title')}
                </span>
                <span className="px-2 py-0.5 rounded-full bg-teal-500/10 text-teal-300 border border-teal-500/30 text-[10px] font-bold">
                  20 Языков / Languages
                </span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-black text-white tracking-tight mt-1">
                {t('language_title')}
              </h1>
              <p className="text-xs text-zinc-400 mt-1">
                {t('language_subtitle')}
              </p>
            </div>
          </div>

          {/* Quick status pill */}
          <div className="flex items-center space-x-3">
            <div className="p-3 rounded-2xl bg-[#0D121B] border border-[#222E42] flex items-center space-x-3">
              <span className="text-2xl leading-none">{languageInfo.flag}</span>
              <div>
                <div className="text-xs font-black text-white">
                  {languageInfo.nativeName}
                </div>
                <div className="text-[10px] text-zinc-400">
                  {languageInfo.name} ({languageInfo.code.toUpperCase()})
                </div>
              </div>
            </div>
          </div>
        </div>
      </StudioCard>

      {/* Applied Success Alert */}
      {justApplied && (
        <div className="p-4 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 flex items-center space-x-3 shadow-lg animate-fadeIn">
          <CheckCircle2 className="w-5 h-5 shrink-0" />
          <div className="text-xs font-bold">
            {t('language_title')}: {languageInfo.nativeName} ({languageInfo.name}) — {t('current_active')}!
          </div>
        </div>
      )}

      {/* Device Auto-detection banner & reset button */}
      <StudioCard>
        <div className="p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center space-x-3.5">
            <div className="w-10 h-10 rounded-xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-center text-amber-400 shrink-0">
              <Smartphone className="w-5 h-5" />
            </div>
            <div>
              <div className="text-xs font-bold text-zinc-300 flex items-center gap-2">
                <span>{t('device_language_detected')}:</span>
                <span className="font-mono text-amber-400 font-black">
                  {detectedInfo.flag} {detectedInfo.nativeName} ({detectedInfo.code})
                </span>
              </div>
              <p className="text-[11px] text-zinc-400 mt-0.5">
                {isSystemLanguageUsed
                  ? 'Приложение сейчас использует язык вашего браузера/устройства.'
                  : 'Вы выбрали язык вручную. Вы можете вернуться к автоопределению языка системы в любой момент.'}
              </p>
            </div>
          </div>

          <button
            onClick={handleResetToSystem}
            className="px-4 py-2.5 rounded-xl bg-[#151C2A] hover:bg-[#1E273B] border border-[#27354D] text-xs font-bold text-zinc-300 hover:text-white transition-all flex items-center space-x-2 shrink-0 cursor-pointer"
          >
            <RotateCcw className="w-3.5 h-3.5 text-amber-400" />
            <span>{t('reset_to_system')}</span>
          </button>
        </div>
      </StudioCard>

      {/* Language List / Grid with Search */}
      <StudioCard>
        <div className="p-5 space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div>
              <h2 className="text-sm font-bold text-white uppercase tracking-wider">
                {t('language_title')}
              </h2>
              <p className="text-xs text-zinc-400 mt-0.5">
                Кликните на любой язык, чтобы мгновенно применить его к интерфейсу
              </p>
            </div>

            {/* Search Filter */}
            <div className="relative w-full sm:w-72">
              <Search className="w-4 h-4 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder={t('lang_search_placeholder')}
                className="w-full pl-9 pr-3 py-2 rounded-xl bg-[#0E1420] border border-[#232F42] text-xs text-zinc-100 placeholder-zinc-500 focus:outline-hidden focus:border-teal-400 transition-colors"
              />
            </div>
          </div>

          {/* Languages Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 pt-2">
            {filteredLanguages.map((langItem: LanguageInfo) => {
              const isSelected = language === langItem.code;

              return (
                <button
                  key={langItem.code}
                  onClick={() => handleSelectLanguage(langItem.code)}
                  className={`p-4 rounded-2xl border text-left transition-all transform active:scale-98 cursor-pointer flex items-center justify-between group ${
                    isSelected
                      ? 'bg-gradient-to-r from-teal-500/20 to-emerald-500/20 border-teal-400 shadow-[0_0_15px_rgba(20,184,166,0.3)] ring-1 ring-teal-400/50'
                      : 'bg-[#111724] hover:bg-[#182133] border-[#222E42] text-zinc-300'
                  }`}
                >
                  <div className="flex items-center space-x-3.5">
                    <span className="text-3xl leading-none drop-shadow-sm select-none">
                      {langItem.flag}
                    </span>
                    <div>
                      <div className="text-sm font-black text-white group-hover:text-teal-300 transition-colors">
                        {langItem.nativeName}
                      </div>
                      <div className="text-[11px] text-zinc-400 mt-0.5 flex items-center gap-1.5">
                        <span>{langItem.name}</span>
                        <span className="text-zinc-600 font-mono">·</span>
                        <span className="font-mono uppercase text-zinc-500 text-[10px]">
                          {langItem.code}
                        </span>
                        {langItem.dir === 'rtl' && (
                          <span className="px-1.5 py-0.2 rounded bg-zinc-800 text-[9px] text-amber-400">
                            RTL
                          </span>
                        )}
                      </div>
                    </div>
                  </div>

                  <div className="pl-2">
                    {isSelected ? (
                      <div className="w-7 h-7 rounded-full bg-teal-400 text-zinc-950 flex items-center justify-center shadow-md">
                        <Check className="w-4 h-4 stroke-[3]" />
                      </div>
                    ) : (
                      <div className="w-7 h-7 rounded-full border border-zinc-700 group-hover:border-teal-400/50 transition-colors flex items-center justify-center text-zinc-600 group-hover:text-teal-400">
                        <span className="text-xs font-bold font-mono">+</span>
                      </div>
                    )}
                  </div>
                </button>
              );
            })}
          </div>
        </div>
      </StudioCard>
    </div>
  );
};
