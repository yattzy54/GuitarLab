# GuitarLab 🎸 — Pro Musician's Suite

Мультиплатформенная студия для гитаристов: профессиональный интерактивный плеер табулатур (alphaTab), хроматический тюнер, смарт-метроном с авто-тренером скорости, библиотека строев гитары, драм-машина и мультиязычный интерфейс на **20 языках мира**.

---

## 🌟 Новые возможности (Latest Updates)

### 🌍 Мультиязычность (20 языков / i18n)
Полная локализация интерфейса с автоматическим определением языка устройства (`navigator.language`) и экраном настроек языка:
1. 🇺🇸 **English** (en)
2. 🇪🇸 **Español** (es)
3. 🇨🇳 **中文 (简体)** (zh)
4. 🇮🇳 **हिन्दी** (hi)
5. 🇸🇦 **العربية** (ar) — RTL
6. 🇫🇷 **Français** (fr)
7. 🇧🇷 **Português** (pt)
8. 🇷🇺 **Русский** (ru)
9. 🇩🇪 **Deutsch** (de)
10. 🇯🇵 **日本語** (ja)
11. 🇰🇷 **한국어** (ko)
12. 🇮🇹 **Italiano** (it)
13. 🇹🇷 **Türkçe** (tr)
14. 🇳🇱 **Nederlands** (nl)
15. 🇵🇱 **Polski** (pl)
16. 🇮🇩 **Bahasa Indonesia** (id)
17. 🇻🇳 **Tiếng Việt** (vi)
18. 🇺🇦 **Українська** (uk)
19. 🇸🇪 **Svenska** (sv)
20. 🇮🇷 **فارسی** (fa) — RTL

*Исходный код модуля локализации:* [`src/i18n/`](src/i18n/)  
*Экран настроек:* [`src/components/settings/LanguageScreen.tsx`](src/components/settings/LanguageScreen.tsx)

---

### 🥁 Драм-машина (Drum Grooves Engine)
Полноценный Web Audio синтезатор ударных установок и генератор грувов:
* Стили: **Rock, Metal, Blues, Funk, Jazz, Reggae, Punk, Pop**
* Тонкая настройка темпа (30–300 BPM), свинга (Groove/Swing), громкости и живой визуализатор долей.
* *Код:* [`src/audio/drumAudioEngine.ts`](src/audio/drumAudioEngine.ts), [`src/components/drums/DrumsScreen.tsx`](src/components/drums/DrumsScreen.tsx)

---

### 🎼 Интерактивный плеер табов alphaTab
* Высокоточное векторное отображение табулатур и нот.
* SoundFont синтез Sonivox SF2.
* Регулировка скорости, транспонирование, микшер треков (Solo/Mute), зацикливание фрагментов.
* *Код:* [`src/components/player/`](src/components/player/), [`src/components/tab/AlphaTabViewer.tsx`](src/components/tab/AlphaTabViewer.tsx)

---

### 🎯 Селектор строя гитары (Tuning Quick Selector)
* Быстрый выбор строя прямо из шапки приложения (Standard E, Drop D, DADGAD, Half-Step Down, Full-Step Down, Open D/G и др.).
* Калибровка опорной частоты ля (A4: 415–466 Гц).
* *Код:* [`src/components/common/TuningSelectorDropdown.tsx`](src/components/common/TuningSelectorDropdown.tsx)

---

## 📁 Структура проекта

```
GuitarLab/
├── src/                          # 🌐 Web App (React + Vite + TypeScript + Tailwind)
│   ├── App.tsx                   # Корневой компонент с навигацией
│   ├── i18n/                     # Система интернационализации (20 языков)
│   │   ├── LanguageContext.tsx   # React Context + автоопределение устройства
│   │   ├── languages.ts          # Метаданные 20 языков (флаги, rtl, названия)
│   │   ├── translations.ts       # Словари переводов
│   │   └── types.ts              # TypeScript типы для i18n
│   ├── audio/                    # Звуковые движки (alphaTab, Web Audio, YIN, drums)
│   │   ├── drumAudioEngine.ts    # Синтезатор ударных инструментов
│   │   ├── metronomeEngine.ts    # Движок метронома
│   │   └── yinPitchDetector.ts   # Алгоритм YIN для тюнера
│   ├── components/               # UI компоненты
│   │   ├── settings/             # Экран выбора языка (LanguageScreen)
│   │   ├── drums/                # Драм-машина (DrumsScreen)
│   │   ├── common/               # TuningSelectorDropdown, 3D Badge и др.
│   │   ├── fretboard/            # Интерактивный гриф и обратный поиск аккордов
│   │   ├── player/               # Плеер Songsterr/alphaTab
│   │   └── tuner/                # Хроматический тюнер
│   └── data/                     # Пресеты строев, аккорды, гаммы, ритмы
├── app/                          # 📱 Android App (Jetpack Compose + Kotlin + Hilt)
│   └── src/main/java/com/mmt/guitarlab/
├── public/                       # Шрифты Bravura, SoundFonts (.sf2), манифест PWA
├── package.json                  # Web зависимости
└── vite.config.ts                # Конфигурация Vite
```

---

## 🚀 Запуск и сборка

### Web версия (React + Vite):
```bash
# Установка зависимостей
npm install

# Запуск dev-сервера
npm run dev

# Сборка продакшн бандла
npm run build
```

### Android версия (Kotlin / Gradle):
```bash
./gradlew :app:assembleDebug
```

---

## 🔄 Как подтянуть изменения локально через Git

Если вы клонировали репозиторий на свой компьютер:
```bash
git fetch origin master
git checkout master
git pull origin master
```
