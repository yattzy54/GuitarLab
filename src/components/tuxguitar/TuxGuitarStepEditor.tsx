import React, { useState, useEffect, useCallback } from 'react';
import { TabTrackInfo, AdvancedMeasure, AdvancedBeat, AdvancedNote, StemDuration } from '../../types/tabPlayer';
import {
  ChevronLeft,
  ChevronRight,
  Plus,
  Trash2,
  Copy,
  Repeat,
  Volume2,
  Delete,
  CornerDownLeft,
  Check,
} from 'lucide-react';
import { DURATION_MAP, DURATION_VALUES, playNotePreview } from './tuxAlphaTex';

interface TuxGuitarStepEditorProps {
  track: TabTrackInfo;
  currentMeasureIndex: number;
  currentBeatIndex: number;
  onMeasureIndexChange: (idx: number) => void;
  onBeatIndexChange: (idx: number) => void;
  onUpdateMeasures: (measures: AdvancedMeasure[]) => void;
}

export const TuxGuitarStepEditor: React.FC<TuxGuitarStepEditorProps> = ({
  track,
  currentMeasureIndex,
  currentBeatIndex,
  onMeasureIndexChange,
  onBeatIndexChange,
  onUpdateMeasures,
}) => {
  const measures = track.measures || [];
  const currentMeasure = measures[currentMeasureIndex] || measures[0];
  const beats = currentMeasure?.beats || [];
  const currentBeat = beats[currentBeatIndex] || beats[0];
  const stringsCount = track.tuningNotes?.length || 6;

  // Selected string index inside the beat (0 to stringsCount - 1)
  const [selectedStringIndex, setSelectedStringIndex] = useState(0);
  const [keypadInput, setKeypadInput] = useState('');

  // Clamp beat index when measure changes
  useEffect(() => {
    if (currentBeatIndex >= beats.length) {
      onBeatIndexChange(Math.max(0, beats.length - 1));
    }
  }, [currentMeasureIndex, beats.length]);

  // Find note on a particular string in the beat
  const getNoteOnString = (beat: AdvancedBeat, sIdx: number): AdvancedNote | undefined => {
    return beat.notes?.find((n) => n.stringIndex === sIdx);
  };

  // Update or insert note into the active beat
  const setNoteAt = useCallback(
    (sIdx: number, fret: number | null, effectUpdates?: Partial<AdvancedNote>) => {
      if (!currentMeasure || !currentBeat) return;

      const updatedMeasures = measures.map((m, mIdx) => {
        if (mIdx !== currentMeasureIndex) return m;

        const updatedBeats = m.beats.map((b, bIdx) => {
          if (bIdx !== currentBeatIndex) return b;

          const existingNotes = b.notes || [];
          let nextNotes: AdvancedNote[];

          if (fret === null) {
            // Remove note from string
            nextNotes = existingNotes.filter((n) => n.stringIndex !== sIdx);
          } else {
            // Replace or add note
            const filtered = existingNotes.filter((n) => n.stringIndex !== sIdx);
            const noteObj: AdvancedNote = {
              stringIndex: sIdx,
              fret,
              ...effectUpdates,
            };
            nextNotes = [...filtered, noteObj];
          }

          return {
            ...b,
            isRest: nextNotes.length === 0,
            notes: nextNotes,
          };
        });

        return { ...m, beats: updatedBeats };
      });

      onUpdateMeasures(updatedMeasures);

      if (fret !== null && fret >= 0) {
        playNotePreview(sIdx, fret, track.tuningName.toLowerCase().replace(/ /g, '_'), track.instrument);
      }
    },
    [measures, currentMeasureIndex, currentBeatIndex, currentMeasure, currentBeat, track, onUpdateMeasures]
  );

  // Keypad / direct fret change
  const handleFretChange = (fretVal: number) => {
    setNoteAt(selectedStringIndex, fretVal);
    setKeypadInput('');
  };

  // Keyboard navigation & number input
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // Don't intercept when user is typing in an input
      if (['INPUT', 'TEXTAREA', 'SELECT'].includes((e.target as HTMLElement).tagName)) {
        return;
      }

      // Arrows
      if (e.key === 'ArrowRight') {
        e.preventDefault();
        if (currentBeatIndex < beats.length - 1) {
          onBeatIndexChange(currentBeatIndex + 1);
        } else if (currentMeasureIndex < measures.length - 1) {
          onMeasureIndexChange(currentMeasureIndex + 1);
          onBeatIndexChange(0);
        }
      } else if (e.key === 'ArrowLeft') {
        e.preventDefault();
        if (currentBeatIndex > 0) {
          onBeatIndexChange(currentBeatIndex - 1);
        } else if (currentMeasureIndex > 0) {
          onMeasureIndexChange(currentMeasureIndex - 1);
          const prevM = measures[currentMeasureIndex - 1];
          onBeatIndexChange(Math.max(0, (prevM?.beats?.length || 1) - 1));
        }
      } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        setSelectedStringIndex((prev) => Math.max(0, prev - 1));
      } else if (e.key === 'ArrowDown') {
        e.preventDefault();
        setSelectedStringIndex((prev) => Math.min(stringsCount - 1, prev + 1));
      } else if (e.key === 'Delete' || e.key === 'Backspace') {
        e.preventDefault();
        setNoteAt(selectedStringIndex, null);
      } else if (/^[0-9]$/.test(e.key)) {
        e.preventDefault();
        const digit = parseInt(e.key, 10);
        const nextInput = keypadInput + digit;
        const val = parseInt(nextInput, 10);
        if (val <= 24) {
          handleFretChange(val);
          setKeypadInput(nextInput);
          setTimeout(() => setKeypadInput(''), 1200);
        } else {
          handleFretChange(digit);
          setKeypadInput(e.key);
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [
    currentBeatIndex,
    currentMeasureIndex,
    beats.length,
    measures.length,
    selectedStringIndex,
    stringsCount,
    keypadInput,
    setNoteAt,
  ]);

  // Measure operations: Add, Duplicate, Delete, Repeat
  const handleAddMeasure = () => {
    const newBarNum = measures.length + 1;
    const newBar: AdvancedMeasure = {
      number: newBarNum,
      timeSignature: currentMeasure?.timeSignature || [4, 4],
      tempoBpm: currentMeasure?.tempoBpm,
      beats: [
        { id: `b_${Date.now()}_1`, duration: 'q', durationValue: 1.0, isRest: true, notes: [] },
        { id: `b_${Date.now()}_2`, duration: 'q', durationValue: 1.0, isRest: true, notes: [] },
        { id: `b_${Date.now()}_3`, duration: 'q', durationValue: 1.0, isRest: true, notes: [] },
        { id: `b_${Date.now()}_4`, duration: 'q', durationValue: 1.0, isRest: true, notes: [] },
      ],
    };

    const next = [...measures, newBar];
    onUpdateMeasures(next);
    onMeasureIndexChange(next.length - 1);
    onBeatIndexChange(0);
  };

  const handleDuplicateMeasure = () => {
    if (!currentMeasure) return;
    const duplicated: AdvancedMeasure = {
      ...currentMeasure,
      number: measures.length + 1,
      beats: currentMeasure.beats.map((b, i) => ({
        ...b,
        id: `b_dup_${Date.now()}_${i}`,
        notes: b.notes.map((n) => ({ ...n })),
      })),
    };
    const next = [...measures, duplicated];
    onUpdateMeasures(next);
    onMeasureIndexChange(next.length - 1);
    onBeatIndexChange(0);
  };

  const handleDeleteMeasure = () => {
    if (measures.length <= 1) return;
    const next = measures.filter((_, idx) => idx !== currentMeasureIndex).map((m, idx) => ({
      ...m,
      number: idx + 1,
    }));
    onUpdateMeasures(next);
    onMeasureIndexChange(Math.max(0, currentMeasureIndex - 1));
    onBeatIndexChange(0);
  };

  const handleToggleRepeat = (type: 'start' | 'end') => {
    if (!currentMeasure) return;
    const updated = measures.map((m, idx) => {
      if (idx !== currentMeasureIndex) return m;
      return {
        ...m,
        repeatStart: type === 'start' ? !m.repeatStart : m.repeatStart,
        repeatEnd: type === 'end' ? !m.repeatEnd : m.repeatEnd,
      };
    });
    onUpdateMeasures(updated);
  };

  const handleAddBeat = (duration: StemDuration = 'q') => {
    if (!currentMeasure) return;
    const newBeat: AdvancedBeat = {
      id: `b_add_${Date.now()}`,
      duration,
      durationValue: DURATION_VALUES[duration] || 1.0,
      isRest: true,
      notes: [],
    };
    const updated = measures.map((m, idx) => {
      if (idx !== currentMeasureIndex) return m;
      return {
        ...m,
        beats: [...m.beats, newBeat],
      };
    });
    onUpdateMeasures(updated);
    onBeatIndexChange(currentMeasure.beats.length);
  };

  const handleDeleteBeat = () => {
    if (!currentMeasure || currentMeasure.beats.length <= 1) return;
    const updated = measures.map((m, idx) => {
      if (idx !== currentMeasureIndex) return m;
      return {
        ...m,
        beats: m.beats.filter((_, bIdx) => bIdx !== currentBeatIndex),
      };
    });
    onUpdateMeasures(updated);
    onBeatIndexChange(Math.max(0, currentBeatIndex - 1));
  };

  const activeNoteAtCurrent = currentBeat ? getNoteOnString(currentBeat, selectedStringIndex) : undefined;

  return (
    <div className="bg-[#0C1019] border border-[#222B3D] rounded-2xl p-4 shadow-xl space-y-4">
      {/* Bar / Measure Navigation Bar */}
      <div className="flex flex-wrap items-center justify-between gap-3 pb-3 border-b border-[#1E2638]">
        <div className="flex items-center space-x-2">
          <button
            onClick={() => onMeasureIndexChange(Math.max(0, currentMeasureIndex - 1))}
            disabled={currentMeasureIndex === 0}
            className="p-1.5 rounded-lg bg-[#141B28] hover:bg-[#1E273A] disabled:opacity-40 disabled:pointer-events-none text-zinc-300 hover:text-white border border-[#243044] transition-colors cursor-pointer"
            title="Previous Measure"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>

          <div className="px-3 py-1 rounded-xl bg-[#141B28] border border-[#27344D] text-xs font-black text-amber-400 font-mono flex items-center space-x-1.5">
            <span>BAR</span>
            <span className="text-white text-sm">{currentMeasureIndex + 1}</span>
            <span className="text-zinc-500">/ {measures.length}</span>
          </div>

          <button
            onClick={() => onMeasureIndexChange(Math.min(measures.length - 1, currentMeasureIndex + 1))}
            disabled={currentMeasureIndex >= measures.length - 1}
            className="p-1.5 rounded-lg bg-[#141B28] hover:bg-[#1E273A] disabled:opacity-40 disabled:pointer-events-none text-zinc-300 hover:text-white border border-[#243044] transition-colors cursor-pointer"
            title="Next Measure"
          >
            <ChevronRight className="w-4 h-4" />
          </button>

          {/* Time Signature */}
          <div className="px-2.5 py-1 rounded-lg bg-[#141B28] border border-[#222E42] text-[11px] font-mono text-zinc-300 font-bold">
            {currentMeasure?.timeSignature ? `${currentMeasure.timeSignature[0]}/${currentMeasure.timeSignature[1]}` : '4/4'}
          </div>

          {/* Repeat Start / End marks */}
          <button
            onClick={() => handleToggleRepeat('start')}
            className={`px-2 py-1 rounded-lg text-xs font-mono font-bold border transition-colors ${
              currentMeasure?.repeatStart
                ? 'bg-amber-500 text-zinc-950 border-amber-400'
                : 'bg-[#141B28] text-zinc-400 border-[#243044] hover:text-white'
            }`}
            title="Repeat Start (|:)"
          >
            |:
          </button>
          <button
            onClick={() => handleToggleRepeat('end')}
            className={`px-2 py-1 rounded-lg text-xs font-mono font-bold border transition-colors ${
              currentMeasure?.repeatEnd
                ? 'bg-amber-500 text-zinc-950 border-amber-400'
                : 'bg-[#141B28] text-zinc-400 border-[#243044] hover:text-white'
            }`}
            title="Repeat End (:|)"
          >
            :|
          </button>
        </div>

        {/* Measure management buttons */}
        <div className="flex items-center space-x-2">
          <button
            onClick={handleAddMeasure}
            className="px-2.5 py-1.5 rounded-xl bg-[#141B28] hover:bg-[#1E273A] border border-[#27344D] text-xs font-bold text-teal-300 flex items-center space-x-1 transition-colors cursor-pointer"
            title="Insert new measure at end"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Add Bar</span>
          </button>

          <button
            onClick={handleDuplicateMeasure}
            className="p-1.5 rounded-xl bg-[#141B28] hover:bg-[#1E273A] border border-[#27344D] text-xs text-zinc-300 hover:text-white transition-colors cursor-pointer"
            title="Duplicate current bar"
          >
            <Copy className="w-3.5 h-3.5" />
          </button>

          {measures.length > 1 && (
            <button
              onClick={handleDeleteMeasure}
              className="p-1.5 rounded-xl bg-[#141B28] hover:bg-rose-950/40 border border-[#27344D] hover:border-rose-700/60 text-xs text-zinc-400 hover:text-rose-300 transition-colors cursor-pointer"
              title="Delete current bar"
            >
              <Trash2 className="w-3.5 h-3.5" />
            </button>
          )}
        </div>
      </div>

      {/* Interactive Step Matrix Table */}
      <div className="overflow-x-auto">
        <div className="min-w-[500px] border border-[#1E2638] rounded-xl bg-[#090D15] p-3 space-y-2">
          {/* Header Row: Beat Numbers & Durations */}
          <div className="grid grid-cols-[80px_repeat(auto-fit,minmax(60px,1fr))] items-center gap-1.5 text-center text-xs font-mono font-bold text-zinc-400 border-b border-[#1E2638] pb-2">
            <div className="text-left text-[11px] text-zinc-500 uppercase">String</div>
            {beats.map((b, bIdx) => {
              const isSelectedBeat = bIdx === currentBeatIndex;
              return (
                <button
                  key={b.id || bIdx}
                  onClick={() => onBeatIndexChange(bIdx)}
                  className={`py-1 rounded-lg transition-all cursor-pointer border ${
                    isSelectedBeat
                      ? 'bg-amber-500/20 text-amber-300 border-amber-500/60 shadow-[0_0_8px_rgba(245,158,11,0.3)]'
                      : 'bg-[#111724] text-zinc-400 border-[#1E273A] hover:text-white hover:border-zinc-600'
                  }`}
                >
                  <div className="text-[10px]">Beat {bIdx + 1}</div>
                  <div className="text-[9px] text-zinc-500 uppercase">1/{DURATION_MAP[b.duration] || '4'}</div>
                </button>
              );
            })}
          </div>

          {/* Strings Matrix (0 = high string, stringsCount - 1 = lowest) */}
          {Array.from({ length: stringsCount }, (_, sIdx) => {
            const stringLabel = track.tuningNotes?.[sIdx] || `S${sIdx + 1}`;
            const isSelectedString = sIdx === selectedStringIndex;

            return (
              <div
                key={sIdx}
                className={`grid grid-cols-[80px_repeat(auto-fit,minmax(60px,1fr))] items-center gap-1.5 transition-colors ${
                  isSelectedString ? 'bg-[#141C2B]/80 rounded-lg p-0.5' : 'p-0.5'
                }`}
              >
                {/* String Label */}
                <button
                  onClick={() => setSelectedStringIndex(sIdx)}
                  className={`text-left px-2 py-1.5 rounded-lg text-xs font-mono font-black transition-colors flex items-center justify-between cursor-pointer ${
                    isSelectedString
                      ? 'bg-amber-400 text-zinc-950 shadow-md shadow-amber-400/20'
                      : 'text-zinc-300 hover:text-white hover:bg-[#141C2B]'
                  }`}
                >
                  <span>{stringLabel}</span>
                  <span className="text-[9px] text-zinc-500">#{sIdx + 1}</span>
                </button>

                {/* Beat Cells */}
                {beats.map((b, bIdx) => {
                  const note = getNoteOnString(b, sIdx);
                  const isCellActive = sIdx === selectedStringIndex && bIdx === currentBeatIndex;

                  return (
                    <button
                      key={b.id || bIdx}
                      onClick={() => {
                        setSelectedStringIndex(sIdx);
                        onBeatIndexChange(bIdx);
                        if (note) {
                          playNotePreview(
                            sIdx,
                            note.fret,
                            track.tuningName.toLowerCase().replace(/ /g, '_'),
                            track.instrument
                          );
                        }
                      }}
                      className={`h-9 rounded-lg font-mono font-black text-sm flex items-center justify-center transition-all cursor-pointer border ${
                        isCellActive
                          ? 'bg-amber-400/20 text-amber-300 border-amber-400 ring-2 ring-amber-400/60 shadow-[0_0_12px_rgba(245,158,11,0.4)]'
                          : note
                          ? 'bg-[#182335] text-amber-200 border-[#2B3952] hover:border-amber-400/50'
                          : 'bg-[#101520] text-zinc-600 border-[#1B2332] hover:border-zinc-600 hover:text-zinc-400'
                      }`}
                    >
                      {note ? (
                        <div className="flex items-center space-x-0.5">
                          <span>{note.fret}</span>
                          {note.vibrato && <span className="text-[9px] text-teal-400">~</span>}
                          {note.slide && <span className="text-[9px] text-indigo-400">/</span>}
                          {note.bend && <span className="text-[9px] text-rose-400">b</span>}
                          {note.deadNote && <span className="text-[9px] text-zinc-400">x</span>}
                        </div>
                      ) : (
                        <span className="text-zinc-700">·</span>
                      )}
                    </button>
                  );
                })}
              </div>
            );
          })}
        </div>
      </div>

      {/* Active Note Controls & Fret Number Pad */}
      <div className="bg-[#111723] border border-[#222E42] rounded-xl p-3 flex flex-wrap items-center justify-between gap-3">
        {/* Active note status */}
        <div className="flex items-center space-x-3 text-xs">
          <div className="text-zinc-400">
            Selected: <strong className="text-amber-400 font-mono">String {selectedStringIndex + 1}</strong> (
            {track.tuningNotes?.[selectedStringIndex] || 'E'}), <strong className="text-amber-400 font-mono">Beat {currentBeatIndex + 1}</strong>
          </div>
          {activeNoteAtCurrent ? (
            <div className="px-2.5 py-0.5 rounded-full bg-amber-400/20 border border-amber-400/40 text-amber-300 font-mono font-bold text-xs">
              Fret: {activeNoteAtCurrent.fret}
            </div>
          ) : (
            <div className="px-2 py-0.5 rounded-full bg-zinc-800 text-zinc-400 text-xs font-mono">
              Empty / Rest
            </div>
          )}
        </div>

        {/* Fret Buttons Pad (0 to 12 quick frets + Clear) */}
        <div className="flex items-center flex-wrap gap-1">
          {[0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 12, 14, 15, 17, 19, 21, 24].map((fret) => (
            <button
              key={fret}
              onClick={() => handleFretChange(fret)}
              className="w-7 h-7 rounded-lg bg-[#182335] hover:bg-amber-500 hover:text-zinc-950 text-zinc-200 border border-[#283852] font-mono text-xs font-bold transition-all cursor-pointer"
            >
              {fret}
            </button>
          ))}

          <button
            onClick={() => setNoteAt(selectedStringIndex, null)}
            className="px-2.5 h-7 rounded-lg bg-rose-500/20 hover:bg-rose-500 text-rose-300 hover:text-white border border-rose-500/30 text-xs font-bold transition-all flex items-center space-x-1 cursor-pointer"
            title="Clear note on this string"
          >
            <Delete className="w-3.5 h-3.5" />
            <span>Clear</span>
          </button>
        </div>

        {/* Articulations for active note */}
        <div className="flex items-center space-x-1.5 pt-1 sm:pt-0">
          <button
            onClick={() => {
              if (!activeNoteAtCurrent) return;
              setNoteAt(selectedStringIndex, activeNoteAtCurrent.fret, {
                vibrato: !activeNoteAtCurrent.vibrato,
              });
            }}
            disabled={!activeNoteAtCurrent}
            className={`px-2 py-1 rounded-md text-[11px] font-bold border transition-colors ${
              activeNoteAtCurrent?.vibrato
                ? 'bg-teal-500 text-zinc-950 border-teal-400'
                : 'bg-[#182335] text-zinc-400 border-[#283852] hover:text-white disabled:opacity-40'
            }`}
            title="Vibrato (~)"
          >
            Vibrato
          </button>

          <button
            onClick={() => {
              if (!activeNoteAtCurrent) return;
              setNoteAt(selectedStringIndex, activeNoteAtCurrent.fret, {
                slide: activeNoteAtCurrent.slide === 'up' ? 'none' : 'up',
              });
            }}
            disabled={!activeNoteAtCurrent}
            className={`px-2 py-1 rounded-md text-[11px] font-bold border transition-colors ${
              activeNoteAtCurrent?.slide === 'up'
                ? 'bg-indigo-500 text-white border-indigo-400'
                : 'bg-[#182335] text-zinc-400 border-[#283852] hover:text-white disabled:opacity-40'
            }`}
            title="Slide (/)"
          >
            Slide
          </button>

          <button
            onClick={() => {
              if (!activeNoteAtCurrent) return;
              setNoteAt(selectedStringIndex, activeNoteAtCurrent.fret, {
                bend: activeNoteAtCurrent.bend === 'full' ? 'none' : 'full',
              });
            }}
            disabled={!activeNoteAtCurrent}
            className={`px-2 py-1 rounded-md text-[11px] font-bold border transition-colors ${
              activeNoteAtCurrent?.bend === 'full'
                ? 'bg-rose-500 text-white border-rose-400'
                : 'bg-[#182335] text-zinc-400 border-[#283852] hover:text-white disabled:opacity-40'
            }`}
            title="Bend (b)"
          >
            Bend
          </button>
        </div>
      </div>
    </div>
  );
};
