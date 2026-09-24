import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { SongTabScore, TabTrackInfo, AdvancedMeasure, StemDuration } from '../../types/tabPlayer';
import { TUX_PRESETS, BLANK_SCORE, SMOKE_ON_THE_WATER } from './tuxGuitarPresets';
import { TuxGuitarToolbar } from './TuxGuitarToolbar';
import { TuxGuitarStepEditor } from './TuxGuitarStepEditor';
import { TuxGuitarTrackTable } from './TuxGuitarTrackTable';
import { TuxGuitarFretboard } from './TuxGuitarFretboard';
import { TuxGuitarPiano } from './TuxGuitarPiano';
import { TuxGuitarAlphaTabCanvas } from './TuxGuitarAlphaTabCanvas';
import { TuxGuitarImportExportModal } from './TuxGuitarImportExportModal';
import { TuxGuitarSongInfoModal } from './TuxGuitarSongInfoModal';
import { playNotePreview } from './tuxAlphaTex';

const LOCAL_STORAGE_KEY = 'tuxguitar_studio_project_v1';

export const TuxGuitarScreen: React.FC = () => {
  // Load saved project or default to Smoke on the Water preset
  const [score, setScore] = useState<SongTabScore>(() => {
    try {
      const saved = localStorage.getItem(LOCAL_STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        if (parsed && parsed.tracks && parsed.tracks.length > 0) {
          return parsed;
        }
      }
    } catch (_) {}
    return SMOKE_ON_THE_WATER;
  });

  const [activeTrackId, setActiveTrackId] = useState<string>(() => {
    return score.tracks[0]?.id || 'sow_g1';
  });

  const [currentMeasureIndex, setCurrentMeasureIndex] = useState(0);
  const [currentBeatIndex, setCurrentBeatIndex] = useState(0);
  const [isPlaying, setIsPlaying] = useState(false);
  const [isLooping, setIsLooping] = useState(false);
  const [tempo, setTempo] = useState<number>(score.defaultTempo || 112);
  const [selectedDuration, setSelectedDuration] = useState<StemDuration>('q');

  // Toggleable panels
  const [showFretboard, setShowFretboard] = useState(true);
  const [showPiano, setShowPiano] = useState(false);
  const [showMixer, setShowMixer] = useState(true);

  // Modals
  const [isImportExportOpen, setIsImportExportOpen] = useState(false);
  const [isSongInfoOpen, setIsSongInfoOpen] = useState(false);

  // Auto-save to localStorage
  useEffect(() => {
    try {
      localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(score));
    } catch (_) {}
  }, [score]);

  // Keep activeTrack valid
  const activeTrack = useMemo(() => {
    return score.tracks.find((t) => t.id === activeTrackId) || score.tracks[0] || null;
  }, [score.tracks, activeTrackId]);

  useEffect(() => {
    if (activeTrack && activeTrack.id !== activeTrackId) {
      setActiveTrackId(activeTrack.id);
    }
  }, [activeTrack, activeTrackId]);

  // Global Play/Pause Keyboard Shortcut
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (['INPUT', 'TEXTAREA', 'SELECT'].includes((e.target as HTMLElement).tagName)) {
        return;
      }
      if (e.code === 'Space') {
        e.preventDefault();
        setIsPlaying((prev) => !prev);
      } else if (e.code === 'Home') {
        e.preventDefault();
        setIsPlaying(false);
        setCurrentMeasureIndex(0);
        setCurrentBeatIndex(0);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  // Update measures of current track
  const handleUpdateMeasures = (updatedMeasures: AdvancedMeasure[]) => {
    setScore((prev) => ({
      ...prev,
      tracks: prev.tracks.map((t) => {
        if (t.id !== activeTrackId) return t;
        return {
          ...t,
          measures: updatedMeasures,
        };
      }),
    }));
  };

  // Track operations
  const handleUpdateTrack = (updatedTrack: TabTrackInfo) => {
    setScore((prev) => ({
      ...prev,
      tracks: prev.tracks.map((t) => (t.id === updatedTrack.id ? updatedTrack : t)),
    }));
  };

  const handleAddTrack = (newTrack: TabTrackInfo) => {
    setScore((prev) => ({
      ...prev,
      tracks: [...prev.tracks, newTrack],
    }));
    setActiveTrackId(newTrack.id);
  };

  const handleDeleteTrack = (trackId: string) => {
    if (score.tracks.length <= 1) return;
    setScore((prev) => {
      const filtered = prev.tracks.filter((t) => t.id !== trackId);
      return { ...prev, tracks: filtered };
    });
    const remaining = score.tracks.filter((t) => t.id !== trackId);
    if (remaining.length > 0) {
      setActiveTrackId(remaining[0].id);
    }
  };

  const handleDuplicateTrack = (trackId: string) => {
    const trk = score.tracks.find((t) => t.id === trackId);
    if (!trk) return;

    const dupId = `trk_dup_${Date.now()}`;
    const duplicated: TabTrackInfo = {
      ...trk,
      id: dupId,
      name: `${trk.name} (Copy)`,
      measures: trk.measures.map((m) => ({
        ...m,
        beats: m.beats.map((b) => ({
          ...b,
          id: `b_dup_${Date.now()}_${b.id}`,
          notes: b.notes.map((n) => ({ ...n })),
        })),
      })),
    };

    handleAddTrack(duplicated);
  };

  // Preset loading
  const handleLoadPreset = (presetId: string) => {
    const preset = TUX_PRESETS.find((p) => p.id === presetId);
    if (preset) {
      setScore(JSON.parse(JSON.stringify(preset.score)));
      setTempo(preset.score.defaultTempo || 120);
      setActiveTrackId(preset.score.tracks[0]?.id || 'trk_1');
      setCurrentMeasureIndex(0);
      setCurrentBeatIndex(0);
      setIsPlaying(false);
    }
  };

  const handleNewProject = () => {
    setScore(JSON.parse(JSON.stringify(BLANK_SCORE)));
    setTempo(120);
    setActiveTrackId(BLANK_SCORE.tracks[0].id);
    setCurrentMeasureIndex(0);
    setCurrentBeatIndex(0);
    setIsPlaying(false);
  };

  // Notes active on current beat (for fretboard highlight)
  const currentMeasure = activeTrack?.measures?.[currentMeasureIndex];
  const currentBeat = currentMeasure?.beats?.[currentBeatIndex];
  const activeNotesInBeat = useMemo(() => {
    if (!currentBeat || !currentBeat.notes) return [];
    return currentBeat.notes.map((n) => ({
      stringIndex: n.stringIndex,
      fret: n.fret,
    }));
  }, [currentBeat]);

  // Handle note click on virtual fretboard
  const handleFretboardNoteClick = (stringIndex: number, fret: number) => {
    if (!activeTrack || !currentMeasure || !currentBeat) return;

    const existingNotes = currentBeat.notes || [];
    const alreadyThere = existingNotes.some(
      (n) => n.stringIndex === stringIndex && n.fret === fret
    );

    let nextNotes;
    if (alreadyThere) {
      // Toggle off
      nextNotes = existingNotes.filter((n) => n.stringIndex !== stringIndex);
    } else {
      // Replace note on string
      nextNotes = [
        ...existingNotes.filter((n) => n.stringIndex !== stringIndex),
        { stringIndex, fret },
      ];
    }

    const updatedMeasures = activeTrack.measures.map((m, mIdx) => {
      if (mIdx !== currentMeasureIndex) return m;
      return {
        ...m,
        beats: m.beats.map((b, bIdx) => {
          if (bIdx !== currentBeatIndex) return b;
          return {
            ...b,
            isRest: nextNotes.length === 0,
            notes: nextNotes,
          };
        }),
      };
    });

    handleUpdateMeasures(updatedMeasures);
  };

  // Handle piano key click
  const handlePianoKeyClick = (midiNote: number, noteName: string) => {
    if (!activeTrack) return;
    // Map midi note to closest fret on top strings
    const baseMidiHigh = 64; // E4
    const fret = Math.max(0, Math.min(24, midiNote - baseMidiHigh));
    handleFretboardNoteClick(0, fret);
  };

  return (
    <div className="min-h-screen bg-[#070A10] text-zinc-100 flex flex-col p-3 sm:p-5 max-w-7xl mx-auto space-y-4">
      {/* Top TuxGuitar Toolbar */}
      <TuxGuitarToolbar
        songTitle={score.title}
        songArtist={score.artist}
        isPlaying={isPlaying}
        isLooping={isLooping}
        tempo={tempo}
        selectedDuration={selectedDuration}
        showFretboard={showFretboard}
        showPiano={showPiano}
        showMixer={showMixer}
        onPlayToggle={() => setIsPlaying(!isPlaying)}
        onStop={() => {
          setIsPlaying(false);
          setCurrentMeasureIndex(0);
          setCurrentBeatIndex(0);
        }}
        onLoopToggle={() => setIsLooping(!isLooping)}
        onTempoChange={(t) => setTempo(t)}
        onDurationSelect={(d) => setSelectedDuration(d)}
        onToggleFretboard={() => setShowFretboard(!showFretboard)}
        onTogglePiano={() => setShowPiano(!showPiano)}
        onToggleMixer={() => setShowMixer(!showMixer)}
        onNewProject={handleNewProject}
        onLoadPreset={handleLoadPreset}
        onOpenImportExport={() => setIsImportExportOpen(true)}
        onOpenSongInfo={() => setIsSongInfoOpen(true)}
      />

      {/* Main Interactive Scoreboard (alphaTab) */}
      <TuxGuitarAlphaTabCanvas
        score={score}
        activeTrackId={activeTrackId}
        isPlaying={isPlaying}
        tempo={tempo}
        onPlayStateChanged={(pl) => setIsPlaying(pl)}
        onSelectMeasureBeat={(mIdx, bIdx) => {
          if (mIdx < (activeTrack?.measures?.length || 0)) {
            setCurrentMeasureIndex(mIdx);
            setCurrentBeatIndex(bIdx);
          }
        }}
      />

      {/* Interactive Step & Note Matrix Editor */}
      {activeTrack && (
        <TuxGuitarStepEditor
          track={activeTrack}
          currentMeasureIndex={currentMeasureIndex}
          currentBeatIndex={currentBeatIndex}
          onMeasureIndexChange={(mIdx) => setCurrentMeasureIndex(mIdx)}
          onBeatIndexChange={(bIdx) => setCurrentBeatIndex(bIdx)}
          onUpdateMeasures={handleUpdateMeasures}
        />
      )}

      {/* Virtual 24-Fret Fretboard */}
      {showFretboard && activeTrack && (
        <TuxGuitarFretboard
          tuningKey={activeTrack.tuningName.toLowerCase().replace(/ /g, '_')}
          tuningNotes={activeTrack.tuningNotes || ['E4', 'B3', 'G3', 'D3', 'A2', 'E2']}
          activeNotes={activeNotesInBeat}
          onNoteClick={handleFretboardNoteClick}
          instrumentName={activeTrack.name}
        />
      )}

      {/* Virtual Piano Roll */}
      {showPiano && (
        <TuxGuitarPiano onKeyClick={handlePianoKeyClick} />
      )}

      {/* Multi-Track Mixer Table */}
      {showMixer && (
        <TuxGuitarTrackTable
          tracks={score.tracks}
          activeTrackId={activeTrackId}
          onSelectTrack={(id) => setActiveTrackId(id)}
          onUpdateTrack={handleUpdateTrack}
          onAddTrack={handleAddTrack}
          onDeleteTrack={handleDeleteTrack}
          onDuplicateTrack={handleDuplicateTrack}
          measuresTemplate={activeTrack?.measures}
        />
      )}

      {/* Import / Export Dialog */}
      <TuxGuitarImportExportModal
        score={score}
        isOpen={isImportExportOpen}
        onClose={() => setIsImportExportOpen(false)}
        onImportScore={(newScore) => {
          setScore(newScore);
          setTempo(newScore.defaultTempo || 120);
          setActiveTrackId(newScore.tracks[0]?.id || 'trk_1');
          setCurrentMeasureIndex(0);
          setCurrentBeatIndex(0);
        }}
      />

      {/* Song Info Dialog */}
      <TuxGuitarSongInfoModal
        score={score}
        isOpen={isSongInfoOpen}
        onClose={() => setIsSongInfoOpen(false)}
        onSave={(updated) => {
          setScore((prev) => ({
            ...prev,
            ...updated,
          }));
          if (updated.defaultTempo) {
            setTempo(updated.defaultTempo);
          }
        }}
      />
    </div>
  );
};
