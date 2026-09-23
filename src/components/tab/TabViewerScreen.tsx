import React, { useState, useEffect, useRef } from 'react';
import { Play, Pause, RotateCcw, FileText, Music, Copy, Check, Upload, Volume2 } from 'lucide-react';
import { TabScore, TabMeasure, TabBeat } from '../../types';
import { SAMPLE_TABS, parseAsciiTab } from '../../data/sampleTabs';
import { playGuitarPluck } from '../../audio/guitarSynth';
import { midiToHz } from '../../data/defaultTunings';
import { ensureAudioContextStarted } from '../../audio/audioContext';

// Standard 6 string base midi notes (high E to low E: 64, 59, 55, 50, 45, 40)
const STRING_BASE_MIDI = [64, 59, 55, 50, 45, 40];

export const TabViewerScreen: React.FC = () => {
  const [selectedScore, setSelectedScore] = useState<TabScore>(SAMPLE_TABS[0]);
  const [isPlaying, setIsPlaying] = useState(false);
  const [tempo, setTempo] = useState(selectedScore.tempo);
  const [currentMeasureIndex, setCurrentMeasureIndex] = useState(0);
  const [currentBeatIndex, setCurrentBeatIndex] = useState(0);
  const [showRawAscii, setShowRawAscii] = useState(false);
  const [copied, setCopied] = useState(false);
  const [customTabInput, setCustomTabInput] = useState('');
  const [isImportModalOpen, setIsImportModalOpen] = useState(false);

  const playbackTimerRef = useRef<number | null>(null);

  const track = selectedScore.tracks[0];
  const measures = track ? track.measures : [];

  // Stop playback on unmount or score change
  useEffect(() => {
    return () => {
      if (playbackTimerRef.current !== null) {
        clearInterval(playbackTimerRef.current);
      }
    };
  }, []);

  const stopPlayback = () => {
    if (playbackTimerRef.current !== null) {
      clearInterval(playbackTimerRef.current);
      playbackTimerRef.current = null;
    }
    setIsPlaying(false);
  };

  const startPlayback = async () => {
    await ensureAudioContextStarted();
    stopPlayback();
    setIsPlaying(true);

    let mIdx = currentMeasureIndex;
    let bIdx = currentBeatIndex;

    const playCurrentStep = () => {
      if (mIdx >= measures.length) {
        // Loop back to start
        mIdx = 0;
        bIdx = 0;
      }

      const measure = measures[mIdx];
      if (!measure || !measure.beats || measure.beats.length === 0) {
        mIdx++;
        bIdx = 0;
        return;
      }

      const beat = measure.beats[bIdx];
      if (beat && beat.notes) {
        // Play all notes in this beat
        for (const note of beat.notes) {
          if (note.fret >= 0) {
            const baseMidi = STRING_BASE_MIDI[note.stringIndex] || 40;
            const noteMidi = baseMidi + note.fret;
            const freq = midiToHz(noteMidi);
            playGuitarPluck(freq, 1.2, 0.85);
          }
        }
      }

      setCurrentMeasureIndex(mIdx);
      setCurrentBeatIndex(bIdx);

      bIdx++;
      if (bIdx >= measure.beats.length) {
        bIdx = 0;
        mIdx++;
      }
    };

    // 60000 / tempo = ms per quarter note; eighth note is half that
    const stepDurationMs = (60000 / tempo) * 0.5;
    playCurrentStep();
    playbackTimerRef.current = window.setInterval(playCurrentStep, stepDurationMs);
  };

  const handleTogglePlay = () => {
    if (isPlaying) {
      stopPlayback();
    } else {
      startPlayback();
    }
  };

  const handleReset = () => {
    stopPlayback();
    setCurrentMeasureIndex(0);
    setCurrentBeatIndex(0);
  };

  const handleCopyAscii = () => {
    if (selectedScore.rawAsciiContent) {
      navigator.clipboard.writeText(selectedScore.rawAsciiContent);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  const handleImportCustomTab = () => {
    if (customTabInput.trim()) {
      const parsed = parseAsciiTab(customTabInput, 'My Imported Riff');
      setSelectedScore(parsed);
      setTempo(parsed.tempo);
      setIsImportModalOpen(false);
      setCustomTabInput('');
      handleReset();
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-6 space-y-6">
      {/* Header & Preset Switcher */}
      <div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div className="flex items-center space-x-3">
          <div className="p-2.5 rounded-xl bg-amber-500/15 text-amber-400 border border-amber-500/30">
            <Music className="w-6 h-6" />
          </div>
          <div>
            <h2 className="text-xl font-bold text-white tracking-tight">{selectedScore.title}</h2>
            <p className="text-xs text-zinc-400">
              {selectedScore.artist} · 6-String Guitar · {tempo} BPM
            </p>
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          {SAMPLE_TABS.map((tab) => (
            <button
              key={tab.id}
              onClick={() => {
                stopPlayback();
                setSelectedScore(tab);
                setTempo(tab.tempo);
                setCurrentMeasureIndex(0);
                setCurrentBeatIndex(0);
              }}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold border transition-colors ${
                selectedScore.id === tab.id
                  ? 'bg-amber-500 text-zinc-950 border-amber-400'
                  : 'bg-zinc-800 border-zinc-700 text-zinc-300 hover:text-white'
              }`}
            >
              {tab.title.split(' ')[0]}
            </button>
          ))}

          <button
            onClick={() => setIsImportModalOpen(true)}
            className="px-3 py-1.5 rounded-xl text-xs font-semibold bg-zinc-800 hover:bg-zinc-700 border border-zinc-700 text-amber-400 flex items-center gap-1.5 transition-colors"
          >
            <Upload className="w-3.5 h-3.5" />
            <span>Paste Tab</span>
          </button>
        </div>
      </div>

      {/* Main Tab Player Canvas Card */}
      <div className="bg-zinc-900 border border-zinc-800 rounded-3xl p-5 sm:p-7 shadow-2xl space-y-6">
        {/* Controls Bar */}
        <div className="flex flex-wrap items-center justify-between gap-4 border-b border-zinc-800/80 pb-5">
          <div className="flex items-center space-x-3">
            <button
              onClick={handleTogglePlay}
              className={`px-5 py-2.5 rounded-xl font-bold text-sm flex items-center space-x-2 transition-all active:scale-95 ${
                isPlaying
                  ? 'bg-rose-600 hover:bg-rose-500 text-white'
                  : 'bg-amber-500 hover:bg-amber-400 text-zinc-950 shadow-md shadow-amber-500/20'
              }`}
            >
              {isPlaying ? <Pause className="w-4 h-4 fill-current" /> : <Play className="w-4 h-4 fill-current" />}
              <span>{isPlaying ? 'Pause' : 'Play Tab'}</span>
            </button>

            <button
              onClick={handleReset}
              className="p-2.5 rounded-xl bg-zinc-800 hover:bg-zinc-700 border border-zinc-700 text-zinc-300 transition-colors"
              title="Rewind to beginning"
            >
              <RotateCcw className="w-4 h-4" />
            </button>

            <button
              onClick={() => setShowRawAscii(!showRawAscii)}
              className={`px-3 py-2 rounded-xl text-xs font-semibold border flex items-center gap-1.5 transition-colors ${
                showRawAscii
                  ? 'bg-zinc-800 border-amber-500/40 text-amber-400'
                  : 'bg-zinc-800/50 border-zinc-700 text-zinc-400 hover:text-white'
              }`}
            >
              <FileText className="w-4 h-4" />
              <span>{showRawAscii ? 'Interactive View' : 'Raw Tab View'}</span>
            </button>
          </div>

          {/* Tempo adjustment */}
          <div className="flex items-center space-x-3">
            <span className="text-xs text-zinc-400 font-medium">Tempo:</span>
            <input
              type="range"
              min={40}
              max={220}
              value={tempo}
              onChange={(e) => setTempo(parseInt(e.target.value, 10))}
              className="w-28 sm:w-36 h-1.5 bg-zinc-800 rounded-lg appearance-none cursor-pointer accent-amber-400"
            />
            <span className="text-xs font-mono font-bold text-amber-400 w-16">{tempo} BPM</span>
          </div>
        </div>

        {/* Display: Interactive Tab vs Raw ASCII */}
        {!showRawAscii ? (
          <div className="space-y-6 overflow-x-auto pb-2">
            <div className="min-w-[640px] space-y-4">
              {measures.map((measure, mIdx) => {
                const isCurrentMeasure = isPlaying && currentMeasureIndex === mIdx;

                return (
                  <div
                    key={measure.number}
                    className={`rounded-2xl p-4 border transition-all ${
                      isCurrentMeasure
                        ? 'bg-zinc-800/80 border-amber-500/60 shadow-lg shadow-amber-500/5'
                        : 'bg-zinc-950/40 border-zinc-800/80'
                    }`}
                  >
                    <div className="flex justify-between items-center mb-3">
                      <span className="text-xs font-bold font-mono text-amber-400/90">
                        Measure {measure.number}
                      </span>
                      {isCurrentMeasure && (
                        <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-500/20 text-amber-300 animate-pulse">
                          PLAYING
                        </span>
                      )}
                    </div>

                    {/* 6 Tab Lines */}
                    <div className="space-y-2 font-mono text-sm relative py-2">
                      {['e', 'B', 'G', 'D', 'A', 'E'].map((stringLabel, sIdx) => (
                        <div key={stringLabel} className="flex items-center space-x-2">
                          <span className="w-5 text-right font-bold text-zinc-400 text-xs shrink-0">
                            {stringLabel}|
                          </span>
                          <div className="flex-1 flex items-center relative h-5">
                            {/* Horizontal string wire */}
                            <div className="absolute inset-x-0 top-1/2 -translate-y-1/2 h-[1px] bg-zinc-700" />

                            {/* Note columns */}
                            <div className="flex-1 flex justify-around relative z-10">
                              {measure.beats.map((beat, bIdx) => {
                                const note = beat.notes.find((n) => n.stringIndex === sIdx);
                                const isCurrentNote =
                                  isPlaying && currentMeasureIndex === mIdx && currentBeatIndex === bIdx;

                                return (
                                  <div
                                    key={bIdx}
                                    onClick={() => {
                                      if (note && note.fret >= 0) {
                                        const baseMidi = STRING_BASE_MIDI[sIdx];
                                        playGuitarPluck(midiToHz(baseMidi + note.fret), 1.5, 0.9);
                                      }
                                    }}
                                    className={`w-7 h-5 flex items-center justify-center rounded cursor-pointer transition-all ${
                                      isCurrentNote
                                        ? 'bg-amber-400 text-zinc-950 font-black scale-125 shadow-md shadow-amber-400/50'
                                        : note && note.fret >= 0
                                        ? 'bg-zinc-800 text-zinc-100 hover:bg-amber-500/20 hover:text-amber-300 font-bold'
                                        : 'text-zinc-600'
                                    }`}
                                  >
                                    {note && note.fret >= 0 ? note.fret : '-'}
                                  </div>
                                );
                              })}
                            </div>
                          </div>
                          <span className="text-zinc-600 text-xs shrink-0">|</span>
                        </div>
                      ))}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        ) : (
          /* Raw ASCII Tab View */
          <div className="space-y-3">
            <div className="flex justify-between items-center">
              <span className="text-xs text-zinc-400 font-mono">Standard ASCII Notation</span>
              <button
                onClick={handleCopyAscii}
                className="px-3 py-1.5 rounded-lg bg-zinc-800 hover:bg-zinc-700 text-zinc-300 text-xs flex items-center gap-1.5 transition-colors"
              >
                {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                <span>{copied ? 'Copied' : 'Copy ASCII Tab'}</span>
              </button>
            </div>
            <pre className="p-4 rounded-xl bg-zinc-950 border border-zinc-800 text-amber-300 font-mono text-xs sm:text-sm overflow-x-auto leading-relaxed">
              {selectedScore.rawAsciiContent || 'No raw ASCII content.'}
            </pre>
          </div>
        )}
      </div>

      {/* Import Modal */}
      {isImportModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-zinc-900 border border-zinc-800 rounded-3xl max-w-lg w-full p-6 space-y-4 shadow-2xl">
            <h3 className="text-lg font-bold text-white">Import Custom ASCII Guitar Tab</h3>
            <p className="text-xs text-zinc-400">
              Paste standard 6-string tab text with strings e, B, G, D, A, E and fret numbers:
            </p>

            <textarea
              rows={8}
              value={customTabInput}
              onChange={(e) => setCustomTabInput(e.target.value)}
              placeholder={`e|------------------|\nB|------------------|\nG|--0---2---3-------|\nD|--0---2---3-------|\nA|------------------|\nE|------------------|`}
              className="w-full p-3 rounded-xl bg-zinc-950 border border-zinc-700 text-amber-300 font-mono text-xs focus:outline-hidden focus:border-amber-500"
            />

            <div className="flex justify-end space-x-2 pt-2">
              <button
                onClick={() => setIsImportModalOpen(false)}
                className="px-4 py-2 rounded-xl text-xs font-semibold bg-zinc-800 text-zinc-300 hover:bg-zinc-700"
              >
                Cancel
              </button>
              <button
                onClick={handleImportCustomTab}
                className="px-4 py-2 rounded-xl text-xs font-bold bg-amber-500 hover:bg-amber-400 text-zinc-950"
              >
                Parse & Load Tab
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
