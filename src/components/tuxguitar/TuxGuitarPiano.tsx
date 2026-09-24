import React from 'react';
import { midiToHz } from '../../data/defaultTunings';
import { playGuitarPluck } from '../../audio/guitarSynth';

interface TuxGuitarPianoProps {
  onKeyClick: (midiNote: number, noteName: string) => void;
  activeMidiNotes?: number[];
}

interface PianoKey {
  note: string;
  midi: number;
  isBlack: boolean;
  label: string;
}

// 2-Octave piano C3 (48) to B4 (71)
const PIANO_KEYS: PianoKey[] = [
  { note: 'C3', midi: 48, isBlack: false, label: 'C3' },
  { note: 'C#3', midi: 49, isBlack: true, label: 'C#' },
  { note: 'D3', midi: 50, isBlack: false, label: 'D3' },
  { note: 'D#3', midi: 51, isBlack: true, label: 'D#' },
  { note: 'E3', midi: 52, isBlack: false, label: 'E3' },
  { note: 'F3', midi: 53, isBlack: false, label: 'F3' },
  { note: 'F#3', midi: 54, isBlack: true, label: 'F#' },
  { note: 'G3', midi: 55, isBlack: false, label: 'G3' },
  { note: 'G#3', midi: 56, isBlack: true, label: 'G#' },
  { note: 'A3', midi: 57, isBlack: false, label: 'A3' },
  { note: 'A#3', midi: 58, isBlack: true, label: 'A#' },
  { note: 'B3', midi: 59, isBlack: false, label: 'B3' },
  { note: 'C4', midi: 60, isBlack: false, label: 'C4' },
  { note: 'C#4', midi: 61, isBlack: true, label: 'C#' },
  { note: 'D4', midi: 62, isBlack: false, label: 'D4' },
  { note: 'D#4', midi: 63, isBlack: true, label: 'D#' },
  { note: 'E4', midi: 64, isBlack: false, label: 'E4' },
  { note: 'F4', midi: 65, isBlack: false, label: 'F4' },
  { note: 'F#4', midi: 66, isBlack: true, label: 'F#' },
  { note: 'G4', midi: 67, isBlack: false, label: 'G4' },
  { note: 'G#4', midi: 68, isBlack: true, label: 'G#' },
  { note: 'A4', midi: 69, isBlack: false, label: 'A4' },
  { note: 'A#4', midi: 70, isBlack: true, label: 'A#' },
  { note: 'B4', midi: 71, isBlack: false, label: 'B4' },
];

export const TuxGuitarPiano: React.FC<TuxGuitarPianoProps> = ({ onKeyClick, activeMidiNotes = [] }) => {
  const handleKey = (key: PianoKey) => {
    const freq = midiToHz(key.midi);
    playGuitarPluck(freq, 1.0, 0.8);
    onKeyClick(key.midi, key.note);
  };

  const whiteKeys = PIANO_KEYS.filter((k) => !k.isBlack);

  return (
    <div className="bg-[#0C1019] border border-[#222B3D] rounded-2xl p-4 shadow-xl select-none">
      <div className="flex items-center justify-between pb-2 mb-3 border-b border-[#1E2638]">
        <div className="flex items-center space-x-2">
          <div className="w-2 h-2 rounded-full bg-teal-400" />
          <span className="text-xs font-bold text-teal-400 tracking-wider uppercase">
            TuxGuitar Piano Roll (C3 – B4)
          </span>
        </div>
        <span className="text-[11px] text-zinc-500 font-mono">Click keys to audition or insert note</span>
      </div>

      <div className="relative h-28 max-w-2xl mx-auto flex items-stretch">
        {/* White Keys */}
        {whiteKeys.map((wKey) => {
          const isActive = activeMidiNotes.includes(wKey.midi);
          return (
            <button
              key={wKey.midi}
              onClick={() => handleKey(wKey)}
              className={`flex-1 rounded-b-md border border-zinc-700 transition-colors flex flex-col justify-end items-center pb-2 cursor-pointer shadow-sm relative ${
                isActive
                  ? 'bg-amber-300 text-zinc-950 font-black ring-2 ring-amber-500'
                  : 'bg-zinc-200 hover:bg-zinc-100 text-zinc-800'
              }`}
            >
              <span className="text-[10px] font-mono font-bold">{wKey.label}</span>
            </button>
          );
        })}

        {/* Black Keys overlaid */}
        <div className="absolute inset-0 pointer-events-none flex">
          {/* We lay out spacing corresponding to standard 14 white keys */}
          {PIANO_KEYS.map((key, idx) => {
            if (!key.isBlack) return null;
            // Calculate proportional position
            // In 14 white keys, key index roughly maps
            const whiteIndexBefore = PIANO_KEYS.slice(0, idx).filter((k) => !k.isBlack).length - 1;
            const leftPercent = ((whiteIndexBefore + 0.65) / 14) * 100;
            const isActive = activeMidiNotes.includes(key.midi);

            return (
              <button
                key={key.midi}
                onClick={() => handleKey(key)}
                style={{ left: `${leftPercent}%`, width: '4.5%' }}
                className={`absolute top-0 h-16 pointer-events-auto rounded-b-md border border-zinc-900 transition-colors flex flex-col justify-end items-center pb-1 shadow-md z-10 cursor-pointer ${
                  isActive
                    ? 'bg-amber-500 text-zinc-950 font-black ring-2 ring-amber-300'
                    : 'bg-zinc-900 hover:bg-zinc-800 text-zinc-300'
                }`}
              >
                <span className="text-[8px] font-mono font-bold leading-none">{key.label}</span>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
};
