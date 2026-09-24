import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { NavigationDrawer, AppDestination } from './components/NavigationDrawer';
import { SongsterrPlayer } from './components/player/SongsterrPlayer';
import { TunerScreen } from './components/tuner/TunerScreen';
import { MetronomeScreen } from './components/metronome/MetronomeScreen';
import { AutoSpeedTrainerScreen } from './components/metronome/AutoSpeedTrainerScreen';
import { TabViewerScreen } from './components/tab/TabViewerScreen';
import { TuxGuitarScreen } from './components/tuxguitar/TuxGuitarScreen';
import { ChordScaleScreen } from './components/fretboard/ChordScaleScreen';
import { ReverseChordFinderScreen } from './components/fretboard/ReverseChordFinderScreen';
import { DrumsScreen } from './components/drums/DrumsScreen';
import { SlowDownerScreen } from './components/practice/SlowDownerScreen';
import { RiffRecorderScreen } from './components/practice/RiffRecorderScreen';
import { PracticeTrackerScreen } from './components/practice/PracticeTrackerScreen';
import { LanguageScreen } from './components/settings/LanguageScreen';
import { LanguageProvider, useLanguage } from './i18n/LanguageContext';
import { Tuning } from './types';
import { getAllTunings } from './data/defaultTunings';
import {
  loadSavedTuningId,
  saveTuningId,
  loadA4Pitch,
  saveA4Pitch,
} from './data/storage';
import {
  PlayCircle,
  Activity,
  Timer,
  Grid,
  Menu,
} from 'lucide-react';
import { TuxGuitarIcon } from './components/tuxguitar/TuxGuitarIcon';
import { Studio3DBadge } from './components/common/Studio3DComponents';

function AppContent() {
  const { t } = useLanguage();
  const [currentRoute, setCurrentRoute] = useState<AppDestination>('player');
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [a4Pitch, setA4Pitch] = useState(440);

  const [activeTuning, setActiveTuning] = useState<Tuning>(() => {
    const tunings = getAllTunings(440);
    const savedId = loadSavedTuningId();
    return tunings.find((t) => t.id === savedId) || tunings[0];
  });

  useEffect(() => {
    const savedA4 = loadA4Pitch();
    if (savedA4) {
      setA4Pitch(savedA4);
    }
  }, []);

  const handleTuningChange = (newTuning: Tuning) => {
    setActiveTuning(newTuning);
    saveTuningId(newTuning.id);
  };

  const handleA4Change = (newA4: number) => {
    setA4Pitch(newA4);
    saveA4Pitch(newA4);
    const updatedTunings = getAllTunings(newA4);
    const matching = updatedTunings.find((t) => t.id === activeTuning.id);
    if (matching) {
      setActiveTuning(matching);
    }
  };

  return (
    <div className="min-h-screen bg-[#0A0D14] text-zinc-100 flex flex-col font-sans selection:bg-amber-400 selection:text-zinc-950">
      {/* Side Navigation Drawer */}
      <NavigationDrawer
        isOpen={isDrawerOpen}
        onClose={() => setIsDrawerOpen(false)}
        currentRoute={currentRoute}
        onNavigate={(route) => setCurrentRoute(route)}
      />

      {/* When in dedicated Studio Tools, show Top Navbar */}
      {currentRoute !== 'player' && (
        <Navbar
          currentRoute={currentRoute}
          onOpenDrawer={() => setIsDrawerOpen(true)}
          activeTuningName={activeTuning.name}
          activeTuning={activeTuning}
          onTuningChange={handleTuningChange}
          a4Pitch={a4Pitch}
          onNavigate={(route) => setCurrentRoute(route)}
        />
      )}

      {/* Main Content Viewport */}
      <main className="flex-1">
        {currentRoute === 'player' && (
          <div className="relative">
            <SongsterrPlayer
              onOpenStudioTools={() => setIsDrawerOpen(true)}
              onOpenTuxGuitar={() => setCurrentRoute('tuxguitar')}
            />
          </div>
        )}

        {currentRoute === 'tuxguitar' && (
          <div className="pb-24 sm:pb-10">
            <TuxGuitarScreen />
          </div>
        )}

        {currentRoute === 'tuner' && (
          <div className="pb-24 sm:pb-10">
            <TunerScreen
              activeTuning={activeTuning}
              onTuningChange={handleTuningChange}
              a4Pitch={a4Pitch}
              onA4Change={handleA4Change}
            />
          </div>
        )}

        {currentRoute === 'metronome' && (
          <div className="pb-24 sm:pb-10">
            <MetronomeScreen onGoToTrainer={() => setCurrentRoute('trainer')} />
          </div>
        )}

        {currentRoute === 'drums' && (
          <div className="pb-24 sm:pb-10">
            <DrumsScreen />
          </div>
        )}

        {currentRoute === 'trainer' && (
          <div className="pb-24 sm:pb-10">
            <AutoSpeedTrainerScreen />
          </div>
        )}

        {currentRoute === 'tabs' && (
          <div className="pb-24 sm:pb-10">
            <TabViewerScreen />
          </div>
        )}

        {currentRoute === 'fretboard' && (
          <div className="pb-24 sm:pb-10">
            <ChordScaleScreen
              activeTuning={activeTuning}
              onTuningChange={handleTuningChange}
            />
          </div>
        )}
        {currentRoute === 'reverse_chord' && (
          <div className="pb-24 sm:pb-10">
            <ReverseChordFinderScreen
              activeTuning={activeTuning}
              onTuningChange={handleTuningChange}
            />
          </div>
        )}

        {currentRoute === 'slowdowner' && (
          <div className="pb-24 sm:pb-10">
            <SlowDownerScreen />
          </div>
        )}

        {currentRoute === 'recorder' && (
          <div className="pb-24 sm:pb-10">
            <RiffRecorderScreen activeTuning={activeTuning} />
          </div>
        )}

        {currentRoute === 'tracker' && (
          <div className="pb-24 sm:pb-10">
            <PracticeTrackerScreen />
          </div>
        )}

        {currentRoute === 'language' && (
          <div className="pb-24 sm:pb-10">
            <LanguageScreen />
          </div>
        )}
      </main>

      {/* Floating Bottom Navigation Bar for Standalone Studio Screens */}
      {currentRoute !== 'player' && (
        <div className="sm:hidden fixed bottom-0 left-0 right-0 z-30 bg-[#0C1018]/95 backdrop-blur-md border-t border-[#222B3D] py-2 px-3 flex items-center justify-around shadow-2xl">
          <button
            onClick={() => setCurrentRoute('player')}
            className="flex flex-col items-center py-1 px-2 rounded-xl text-emerald-400 font-bold transition-all cursor-pointer"
          >
            <Studio3DBadge icon={PlayCircle} accent="green" size="sm" />
            <span className="text-[10px] font-bold mt-1 text-zinc-300">
              {t('nav_player')}
            </span>
          </button>

          <button
            onClick={() => setCurrentRoute('tuxguitar')}
            className="flex flex-col items-center py-1 px-2 rounded-xl transition-all cursor-pointer"
          >
            <Studio3DBadge
              icon={TuxGuitarIcon}
              accent={currentRoute === 'tuxguitar' ? 'amber' : 'slate'}
              size="sm"
            />
            <span
              className={`text-[10px] font-bold mt-1 ${
                currentRoute === 'tuxguitar' ? 'text-amber-400' : 'text-zinc-400'
              }`}
            >
              TabLab
            </span>
          </button>

          <button
            onClick={() => setCurrentRoute('tuner')}
            className="flex flex-col items-center py-1 px-2 rounded-xl transition-all cursor-pointer"
          >
            <Studio3DBadge
              icon={Activity}
              accent={currentRoute === 'tuner' ? 'amber' : 'slate'}
              size="sm"
            />
            <span
              className={`text-[10px] font-bold mt-1 ${
                currentRoute === 'tuner' ? 'text-amber-400' : 'text-zinc-400'
              }`}
            >
              {t('nav_tuner')}
            </span>
          </button>

          <button
            onClick={() => setCurrentRoute('metronome')}
            className="flex flex-col items-center py-1 px-2 rounded-xl transition-all cursor-pointer"
          >
            <Studio3DBadge
              icon={Timer}
              accent={currentRoute === 'metronome' ? 'teal' : 'slate'}
              size="sm"
            />
            <span
              className={`text-[10px] font-bold mt-1 ${
                currentRoute === 'metronome' ? 'text-teal-400' : 'text-zinc-400'
              }`}
            >
              {t('tempo')}
            </span>
          </button>

          <button
            onClick={() => setCurrentRoute('fretboard')}
            className="flex flex-col items-center py-1 px-2 rounded-xl transition-all cursor-pointer"
          >
            <Studio3DBadge
              icon={Grid}
              accent={currentRoute === 'fretboard' ? 'amber' : 'slate'}
              size="sm"
            />
            <span
              className={`text-[10px] font-bold mt-1 ${
                currentRoute === 'fretboard' ? 'text-amber-400' : 'text-zinc-400'
              }`}
            >
              {t('nav_fretboard')}
            </span>
          </button>

          <button
            onClick={() => setIsDrawerOpen(true)}
            className="flex flex-col items-center py-1 px-2 rounded-xl text-zinc-400 hover:text-white transition-all cursor-pointer"
          >
            <Studio3DBadge icon={Menu} accent="slate" size="sm" />
            <span className="text-[10px] font-bold mt-1 text-zinc-400">
              {t('all_tools')}
            </span>
          </button>
        </div>
      )}
    </div>
  );
}

export default function App() {
  return (
    <LanguageProvider>
      <AppContent />
    </LanguageProvider>
  );
}
