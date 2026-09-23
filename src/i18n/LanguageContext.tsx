import React, { createContext, useContext, useState, useEffect, useCallback, useMemo } from 'react';
import { SupportedLanguageCode, TranslationKey, LanguageInfo } from './types';
import { SUPPORTED_LANGUAGES } from './languages';
import { TRANSLATIONS } from './translations';

const STORAGE_KEY = 'guitarlab_language';

export function detectDeviceLanguage(): SupportedLanguageCode {
  try {
    const raw = (
      navigator.language ||
      (navigator as any).userLanguage ||
      (navigator.languages && navigator.languages[0]) ||
      'en'
    ).toLowerCase();

    // Check exact match first
    const exact = SUPPORTED_LANGUAGES.find((l) => l.code === raw);
    if (exact) return exact.code;

    // Check prefix (e.g., 'es-MX' -> 'es', 'zh-TW' -> 'zh', 'pt-BR' -> 'pt')
    const prefix = raw.split(/[-_]/)[0];
    const match = SUPPORTED_LANGUAGES.find((l) => l.code === prefix);
    if (match) return match.code;
  } catch (err) {
    console.warn('Failed to detect device language, fallback to English:', err);
  }
  return 'en';
}

interface LanguageContextType {
  language: SupportedLanguageCode;
  languageInfo: LanguageInfo;
  detectedLanguage: SupportedLanguageCode;
  isSystemLanguageUsed: boolean;
  setLanguage: (lang: SupportedLanguageCode) => void;
  resetToSystemLanguage: () => void;
  t: (key: TranslationKey) => string;
}

const LanguageContext = createContext<LanguageContextType | undefined>(undefined);

export const LanguageProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const detected = useMemo(() => detectDeviceLanguage(), []);

  const [language, setLanguageState] = useState<SupportedLanguageCode>(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY) as SupportedLanguageCode | null;
      if (saved && SUPPORTED_LANGUAGES.some((l) => l.code === saved)) {
        return saved;
      }
    } catch {
      // ignore
    }
    return detected;
  });

  const [isSystemLanguageUsed, setIsSystemLanguageUsed] = useState<boolean>(() => {
    try {
      return !localStorage.getItem(STORAGE_KEY);
    } catch {
      return true;
    }
  });

  // Current language info (name, flag, rtl/ltr direction)
  const languageInfo = useMemo(() => {
    return (
      SUPPORTED_LANGUAGES.find((l) => l.code === language) ||
      SUPPORTED_LANGUAGES[0] // fallback english
    );
  }, [language]);

  // Update HTML tag attributes (lang and dir)
  useEffect(() => {
    if (typeof document !== 'undefined') {
      document.documentElement.lang = language;
      document.documentElement.dir = languageInfo.dir;
    }
  }, [language, languageInfo.dir]);

  const setLanguage = useCallback((lang: SupportedLanguageCode) => {
    setLanguageState(lang);
    setIsSystemLanguageUsed(false);
    try {
      localStorage.setItem(STORAGE_KEY, lang);
    } catch (err) {
      console.warn('Failed to save language preference:', err);
    }
  }, []);

  const resetToSystemLanguage = useCallback(() => {
    const sysLang = detectDeviceLanguage();
    setLanguageState(sysLang);
    setIsSystemLanguageUsed(true);
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch (err) {
      console.warn('Failed to remove saved language preference:', err);
    }
  }, []);

  const t = useCallback(
    (key: TranslationKey): string => {
      const currentDict = TRANSLATIONS[language];
      if (currentDict && currentDict[key]) {
        return currentDict[key];
      }
      // Fallback to English
      const enDict = TRANSLATIONS['en'];
      if (enDict && enDict[key]) {
        return enDict[key];
      }
      return key;
    },
    [language]
  );

  return (
    <LanguageContext.Provider
      value={{
        language,
        languageInfo,
        detectedLanguage: detected,
        isSystemLanguageUsed,
        setLanguage,
        resetToSystemLanguage,
        t,
      }}
    >
      {children}
    </LanguageContext.Provider>
  );
};

export function useLanguage(): LanguageContextType {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error('useLanguage must be used within a LanguageProvider');
  }
  return context;
}
