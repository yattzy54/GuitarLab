import React, { useState } from 'react';
import { Grid, Search, Volume2, Sparkles, Trash2, Music } from 'lucide-react';
import { Tuning, FretboardMode, FretPosition } from '../../types';
import {
  NOTE_NAMES,
  CHORD_FORMULAS,
  SCALE_FORMULAS,
  getMidiNoteIndex,
  intervalName,
  reverseLookupChord,
} from '../../data/musicTheory';
import { playGuitarPluck } from '../../audio/guitarSynth';
import { midiToHz } from '../../data/defaultTunings';
import { ensureAudioContextStarted } from '../../audio/audioContext';
import { Studio3DBadge, StudioCard, StudioPill } from '../common/Studio3DComponents';

interface ChordScaleScreenProps {
  activeTuning: Tuning;
}

export const ChordScaleScreen: React.FC<ChordScaleScreenProps> = ({ activeTuning }) => {
  const [mode, setMode] = useState<FretboardMode>('CHORD_SCALE_FINDER');
  const [explorerType, setExplorerType] = useState<'CHORD' | 'SCALE'>('CHORD');
  const [rootNote, setRootNote] = useState('E');
  const [selectedChordIndex, setSelectedChordIndex] = useState(0); // Major
  const [selectedScaleIndex, setSelectedScaleIndex] = useState(0); // Minor Pentatonic
  const [pressedFrets, setPressedFrets] = useState<FretPosition[]>([]);

  const totalFrets = 15;
  const stringCount = activeTuning.stringCount;
  const tuningNotes = activeTuning.notes;

  const currentChord = CHORD_FORMULAS[selectedChordIndex];
  const currentScale = SCALE_FORMULAS[selectedScaleIndex];
  const rootPitchIndex = getMidiNoteIndex(rootNote);
  const targetIntervals =
    explorerType === 'CHORD' ? currentChord.intervals : currentScale.intervals;

  const handleFretClick = async (stringIndex: number, fret: number, midiNote: number) => {
    await ensureAudioContextStarted();
    playGuitarPluck(midiToHz(midiNote), 1.6, 0.85);

    if (mode === 'REVERSE_LOOKUP') {
      setPressedFrets((prev) => {
        const existing = prev.find((p) => p.stringIndex === stringIndex && p.fret === fret);
        if (existing) {
          return prev.filter((p) => !(p.stringIndex === stringIndex && p.fret === fret));
        } else {
          const filtered = prev.filter((p) => p.stringIndex !== stringIndex);
          return [...filtered, { stringIndex, fret, midiNote }];
        }
      });
    }
  };

  const detectedChords = reverseLookupChord(
    pressedFrets.map((p) => p.midiNote || 0).filter((n) => n > 0)
  );

  const handleStrum = async () => {
    await ensureAudioContextStarted();
    if (mode === 'REVERSE_LOOKUP') {
      const sorted = [...pressedFrets].sort((a, b) => b.stringIndex - a.stringIndex);
      sorted.forEach((p, idx) => {
        setTimeout(() => {
          if (p.midiNote) playGuitarPluck(midiToHz(p.midiNote), 2.0, 0.85);
        }, idx * 45);
      });
    } else {
      let delay = 0;
      for (let sIdx = stringCount - 1; sIdx >= 0; sIdx--) {
        const note = tuningNotes[sIdx];
        const baseMidi = note ? note.midiNote : 64 - sIdx * 5;
        let targetFret = -1;
        for (let f = 0; f <= 5; f++) {
          const noteMidi = baseMidi + f;
          const pitchClass = ((noteMidi % 12) + 12) % 12;
          const interval = (pitchClass - rootPitchIndex + 12) % 12;
          if (targetIntervals.includes(interval)) {
            targetFret = f;
            break;
          }
        }
        if (targetFret >= 0) {
          const pluckFret = targetFret;
          setTimeout(() => {
            playGuitarPluck(midiToHz(baseMidi + pluckFret), 2.2, 0.8);
          }, delay);
          delay += 45;
        }
      }
    }
  };

  const inlays = [3, 5, 7, 9, 12, 15];

  return (
    <div className="max-w-5xl mx-auto px-4 py-6 space-y-6">
      {/* Top Header Card */}
      <StudioCard>
        <div className="p-4 sm:p-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="flex items-center space-x-3.5">
            <Studio3DBadge icon={Grid} accent="amber" size="lg" />
            <div>
              <h2 className="text-xl font-bold text-white tracking-tight">Fretboard & Chords</h2>
              <p className="text-xs text-zinc-400">
                Interactive Ebony Neck · {activeTuning.name} Tuning
              </p>
            </div>
          </div>

          {/* Mode Switcher */}
          <div className="flex items-center space-x-2">
            <StudioPill
              label="Explorer"
              selected={mode === 'CHORD_SCALE_FINDER'}
              onClick={() => setMode('CHORD_SCALE_FINDER')}
              icon={Grid}
              accent="amber"
              size="sm"
            />
            <StudioPill
              label="Reverse Finder"
              selected={mode === 'REVERSE_LOOKUP'}
              onClick={() => setMode('REVERSE_LOOKUP')}
              icon={Search}
              accent="teal"
              size="sm"
            />
          </div>
        </div>
      </StudioCard>

      {/* Explorer Controls Card */}
      {mode === 'CHORD_SCALE_FINDER' ? (
        <StudioCard>
          <div className="p-5 space-y-4">
            {/* Root Note Picker */}
            <div>
              <label className="text-xs font-bold text-zinc-400 uppercase tracking-wider block mb-2.5">
                Root Note
              </label>
              <div className="flex flex-wrap gap-1.5">
                {NOTE_NAMES.map((note) => (
                  <button
                    key={note}
                    onClick={() => setRootNote(note)}
                    className={`w-9 h-9 rounded-xl font-bold text-xs sm:text-sm border transition-all cursor-pointer ${
                      rootNote === note
                        ? 'bg-gradient-to-b from-amber-400 to-amber-500 text-zinc-950 border-amber-300 shadow-[0_2px_8px_rgba(245,158,11,0.4)]'
                        : 'bg-[#18202E] border-[#2A344A] text-zinc-300 hover:text-white hover:bg-[#20293B]'
                    }`}
                  >
                    {note}
                  </button>
                ))}
              </div>
            </div>

            {/* Type Switcher: Chords vs Scales */}
            <div className="flex items-center space-x-2 pt-2 border-t border-[#222B3D]">
              <StudioPill
                label="Chords"
                selected={explorerType === 'CHORD'}
                onClick={() => setExplorerType('CHORD')}
                accent="amber"
                size="sm"
              />
              <StudioPill
                label="Scales"
                selected={explorerType === 'SCALE'}
                onClick={() => setExplorerType('SCALE')}
                accent="teal"
                size="sm"
              />
            </div>

            {/* Formula Choices */}
            <div className="flex flex-wrap gap-2 pt-1">
              {explorerType === 'CHORD'
                ? CHORD_FORMULAS.map((chord, idx) => (
                    <StudioPill
                      key={chord.name}
                      label={chord.name}
                      selected={selectedChordIndex === idx}
                      onClick={() => setSelectedChordIndex(idx)}
                      accent="amber"
                      size="sm"
                    />
                  ))
                : SCALE_FORMULAS.map((scale, idx) => (
                    <StudioPill
                      key={scale.name}
                      label={scale.name}
                      selected={selectedScaleIndex === idx}
                      onClick={() => setSelectedScaleIndex(idx)}
                      accent="teal"
                      size="sm"
                    />
                  ))}
            </div>

            {/* Formula Notes readout */}
            <div className="p-3 rounded-xl bg-[#141A26] border border-[#232D3F] flex items-center justify-between text-xs">
              <span className="text-zinc-400 font-medium">
                {rootNote}{' '}
                {explorerType === 'CHORD' ? currentChord.name : currentScale.name} tones:
              </span>
              <span className="text-amber-400 font-mono font-bold tracking-wide">
                {targetIntervals
                  .map((interval) => NOTE_NAMES[(rootPitchIndex + interval) % 12])
                  .join(' · ')}
              </span>
            </div>
          </div>
        </StudioCard>
      ) : (
        /* Reverse Lookup Instruction Card */
        <StudioCard glow="teal">
          <div className="p-4 sm:p-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
            <div>
              <h3 className="font-bold text-white text-sm">Tap frets to place fingers</h3>
              <p className="text-xs text-zinc-400">
                Click any string and fret position to detect chord identity in real time
              </p>
            </div>
            <div className="flex items-center space-x-2">
              {pressedFrets.length > 0 && (
                <button
                  onClick={() => setPressedFrets([])}
                  className="px-3 py-1.5 rounded-xl bg-rose-500/15 border border-rose-500/30 text-rose-400 text-xs font-semibold flex items-center space-x-1.5 hover:bg-rose-500/25 transition-colors cursor-pointer"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                  <span>Clear</span>
                </button>
              )}
            </div>
          </div>
        </StudioCard>
      )}

      {/* Main Realistic Ebony Fretboard Visualizer Card */}
      <StudioCard>
        <div className="p-4 sm:p-6 space-y-4">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-zinc-400 uppercase tracking-wider">
              {mode === 'CHORD_SCALE_FINDER'
                ? `${rootNote} ${
                    explorerType === 'CHORD' ? currentChord.name : currentScale.name
                  }`
                : detectedChords.length > 0
                ? `Detected: ${detectedChords.join(', ')}`
                : 'Touch any fret on the neck'}
            </span>

            {/* Strum button */}
            <button
              onClick={handleStrum}
              className="px-4 py-2 rounded-xl bg-gradient-to-b from-[#253046] to-[#151C2A] border border-[#2F3C55] text-amber-400 text-xs font-bold flex items-center space-x-1.5 shadow-[0_2px_8px_rgba(0,0,0,0.4)] active:scale-95 transition-all hover:border-amber-400/50 cursor-pointer"
            >
              <Volume2 className="w-3.5 h-3.5" />
              <span>Strum Notes</span>
            </button>
          </div>

          {/* Fretboard Canvas Container */}
          <div className="overflow-x-auto pb-4 pt-1">
            <div
              className="relative bg-gradient-to-b from-[#1A1F2C] to-[#10141D] border border-[#2B3549] rounded-2xl shadow-2xl shadow-black/80 select-none p-3"
              style={{ minWidth: '780px' }}
            >
              {/* Mother of pearl inlays */}
              <div className="absolute inset-x-0 top-1/2 -translate-y-1/2 flex pointer-events-none">
                {Array.from({ length: totalFrets + 1 }).map((_, fret) => (
                  <div key={fret} className="flex-1 flex items-center justify-center">
                    {inlays.includes(fret) && (
                      <div
                        className={`rounded-full bg-slate-300/40 shadow-[0_0_8px_rgba(255,255,255,0.25)] ${
                          fret === 12 ? 'w-2 h-4 space-y-1' : 'w-2.5 h-2.5'
                        }`}
                      />
                    )}
                  </div>
                ))}
              </div>

              {/* Fret Number Markers */}
              <div className="flex border-b border-[#252F43] pb-2 mb-1">
                {Array.from({ length: totalFrets + 1 }).map((_, fret) => (
                  <div
                    key={fret}
                    className="flex-1 text-center text-[10px] font-mono font-bold text-zinc-500"
                  >
                    {fret === 0 ? 'Nut' : fret}
                  </div>
                ))}
              </div>

              {/* Strings */}
              <div className="space-y-4 py-2">
                {tuningNotes.map((tuningNote, stringIdx) => {
                  const baseMidi = tuningNote.midiNote;
                  const stringGauge = Math.max(1, (stringCount - stringIdx) * 0.5 + 1);

                  return (
                    <div key={stringIdx} className="relative flex items-center">
                      {/* Guitar String Line */}
                      <div
                        className="absolute inset-x-0 bg-gradient-to-b from-zinc-300 to-zinc-500 shadow-[0_1px_2px_rgba(0,0,0,0.8)] z-0"
                        style={{ height: `${stringGauge}px` }}
                      />

                      {/* Frets along this string */}
                      <div className="flex w-full relative z-10">
                        {Array.from({ length: totalFrets + 1 }).map((_, fret) => {
                          const noteMidi = baseMidi + fret;
                          const pitchClass = ((noteMidi % 12) + 12) % 12;
                          const noteName = NOTE_NAMES[pitchClass];
                          const interval = (pitchClass - rootPitchIndex + 12) % 12;
                          const isTarget = targetIntervals.includes(interval);
                          const isRoot = isTarget && interval === 0;

                          const isFingerPressed = pressedFrets.some(
                            (p) => p.stringIndex === stringIdx && p.fret === fret
                          );

                          const shouldHighlight =
                            mode === 'CHORD_SCALE_FINDER' ? isTarget : isFingerPressed;

                          return (
                            <div
                              key={fret}
                              onClick={() => handleFretClick(stringIdx, fret, noteMidi)}
                              className={`flex-1 flex items-center justify-center h-8 transition-colors cursor-pointer group ${
                                fret === 0 ? 'border-r-4 border-zinc-200' : 'border-r border-[#2C374D]'
                              }`}
                            >
                              {shouldHighlight ? (
                                <div
                                  className={`w-6 h-6 rounded-full flex items-center justify-center font-black text-[10px] transition-all transform active:scale-90 shadow-md ${
                                    isRoot
                                      ? 'bg-gradient-to-b from-amber-400 to-amber-500 text-zinc-950 shadow-[0_0_12px_rgba(245,158,11,0.8)] ring-2 ring-white/60'
                                      : isFingerPressed
                                      ? 'bg-gradient-to-b from-teal-400 to-cyan-500 text-zinc-950 shadow-[0_0_12px_rgba(45,212,191,0.8)] ring-2 ring-white/60'
                                      : 'bg-gradient-to-b from-cyan-400 to-teal-500 text-zinc-950 shadow-[0_0_8px_rgba(45,212,191,0.6)]'
                                  }`}
                                >
                                  {noteName}
                                </div>
                              ) : (
                                <div className="w-5 h-5 rounded-full opacity-0 group-hover:opacity-100 bg-[#253046] flex items-center justify-center text-[9px] text-zinc-400">
                                  {noteName}
                                </div>
                              )}
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
      </StudioCard>
    </div>
  );
};
