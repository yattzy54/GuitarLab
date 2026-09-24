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
  Music,
  Drum,
} from 'lucide-react';
import { TuxGuitarIcon } from './components/tuxguitar/TuxGuitarIcon';
import { Studio3DBadge } from './components/common/Studio3DComponents';
import { getAppFlavor, TAB_EXCLUSIVE_ROUTES } from './config/flavor';

function AppContent() {
  const { t } = useLanguage();
  const currentFlavor = getAppFlavor();

  const [currentRoute, setCurrentRoute] = useState<AppDestination>(() => {
    return currentFlavor === 'tabs' ? 'tabs' : 'tuner';
  });
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [a4Pitch, setA4Pitch] = useState(440);

  // Validate route according to active flavor
  useEffect(() => {
    const isTabRoute =
      currentRoute === 'tabs' ||
      currentRoute === 'guitartabedit' ||
      currentRoute === 'tuxguitar';

    if (currentFlavor === 'tabs') {
      if (!isTabRoute) {
        setCurrentRoute('tabs');
      }
    } else {
      // Standard flavor: hide tabs, guitartabedit, tuxguitar (and songsterr player)
      if (isTabRoute || currentRoute === 'player') {
        setCurrentRoute('tuner');
      }
    }
  }, [currentFlavor, currentRoute]);

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
        {currentFlavor !== 'tabs' && currentRoute === 'player' && (
          <div className="relative">
            <SongsterrPlayer
              onOpenStudioTools={() => setIsDrawerOpen(true)}
              onOpenTuxGuitar={() => setCurrentRoute('tuxguitar')}
            />
          </div>
        )}

        {(currentRoute === 'tuxguitar' || currentRoute === 'guitartabedit') && (
          <div className="pb-24 sm:pb-10">
            <TuxGuitarScreen />
          </div>
        )}

        {currentRoute === 'tabs' && (
          <div className="pb-24 sm:pb-10">
            <TabViewerScreen />
          </div>
        )}

        {currentFlavor !== 'tabs' && currentRoute === 'tuner' && (
          <div className="pb-24 sm:pb-10">
            <TunerScreen
              activeTuning={activeTuning}
              onTuningChange={handleTuningChange}
              a4Pitch={a4Pitch}
              onA4Change={handleA4Change}
            />
          </div>
        )}

        {currentFlavor !== 'tabs' && currentRoute === 'metronome' && (
          <div className="pb-24 sm:pb-10">
            <MetronomeScreen onGoToTrainer={() => setCurrentRoute('trainer')} />
          </div>
        )}

        {currentFlavor !== 'tabs' && currentRoute === 'drums' && (
          <div className="pb-24 sm:pb-10">
            <DrumsScreen />
          </div>
        )}

        {currentFlavor !== 'tabs' && currentRoute === 'trainer' && (
          <div className="pb-24 sm:pb-10">
            <AutoSpeedTrainerScreen />
          </div>
        )}

        {currentFlavor !== 'tabs' && currentRoute === 'fretboard' && (
          <div className="pb-24 sm:pb-10">
            <ChordScaleScreen
              activeTuning={activeTuning}
              onTuningChange={handleTuningChange}
            />
          </div>
        )}
        {currentFlavor !== 'tabs' && currentRoute === 'reverse_chord' && (
          <div className="pb-24 sm:pb-10">
            <ReverseChordFinderScreen
              activeTuning={activeTuning}
              onTuningChange={handleTuningChange}
            />
          </div>
        )}

        {currentFlavor !== 'tabs' && currentRoute === 'slowdowner' && (
          <div className="pb-24 sm:pb-10">
            <SlowDownerScreen />
          </div>
        )}

        {currentFlavor !== 'tabs' && currentRoute === 'recorder' && (
          <div className="pb-24 sm:pb-10">
            <RiffRecorderScreen activeTuning={activeTuning} />
          </div>
        )}

        {currentFlavor !== 'tabs' && currentRoute === 'tracker' && (
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

      {/* Floating Bottom Navigation Bar */}
      {currentRoute !== 'player' && (
        <div className="sm:hidden fixed bottom-0 left-0 right-0 z-30 bg-[#0C1018]/95 backdrop-blur-md border-t border-[#222B3D] py-2 px-3 flex items-center justify-around shadow-2xl">
          {currentFlavor === 'tabs' ? (
            <>
              {/* 3 Tab Sections Only in Tabs Flavor */}
              <button
                onClick={() => setCurrentRoute('tabs')}
                className="flex flex-col items-center py-1 px-2 rounded-xl transition-all cursor-pointer"
              >
                <Studio3DBadge
                  icon={Music}
                  accent={currentRoute === 'tabs' ? 'amber' : 'slate'}
                  size="sm"
                />
                <span
                  className={`text-[10px] font-bold mt-1 ${
                    currentRoute === 'tabs' ? 'text-amber-400' : 'text-zinc-400'
                  }`}
                >
                  {t('nav_tabs')}
                </span>
              </button>

              <button
                onClick={() => setCurrentRoute('guitartabedit')}
                className="flex flex-col items-center py-1 px-2 rounded-xl transition-all cursor-pointer"
              >
                <Studio3DBadge
                  icon={TuxGuitarIcon}
                  accent={currentRoute === 'guitartabedit' ? 'amber' : 'slate'}
                  size="sm"
                />
                <span
                  className={`text-[10px] font-bold mt-1 ${
                    currentRoute === 'guitartabedit' ? 'text-amber-400' : 'text-zinc-400'
                  }`}
                >
                  GuitarTabEdit
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
            </>
          ) : (
            <>
              {/* Main Flavor: Core Instruments (Tabs hidden) */}
              <button
                onClick={() => setCurrentRoute('tuner')}
                className="flex flex-col items-center py-1 px-2 rounded-xl transition-all cursor-pointer"
              >
                <Studio3DBadge
                  icon={Activity}
                  accent={currentRoute === 'tuner' ? 'teal' : 'slate'}
                  size="sm"
                />
                <span
                  className={`text-[10px] font-bold mt-1 ${
                    currentRoute === 'tuner' ? 'text-teal-400' : 'text-zinc-400'
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
                onClick={() => setCurrentRoute('drums')}
                className="flex flex-col items-center py-1 px-2 rounded-xl transition-all cursor-pointer"
              >
                <Studio3DBadge
                  icon={Drum}
                  accent={currentRoute === 'drums' ? 'ruby' : 'slate'}
                  size="sm"
                />
                <span
                  className={`text-[10px] font-bold mt-1 ${
                    currentRoute === 'drums' ? 'text-rose-400' : 'text-zinc-400'
                  }`}
                >
                  {t('nav_drums')}
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
            </>
          )}

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
