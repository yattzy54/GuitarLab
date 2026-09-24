import React from 'react';
import { Volume2 } from 'lucide-react';
import { playNotePreview, TUNING_PRESETS } from './tuxAlphaTex';

interface TuxGuitarFretboardProps {
  tuningKey: string;
  tuningNotes: string[];
  activeNotes: { stringIndex: number; fret: number }[];
  onNoteClick: (stringIndex: number, fret: number) => void;
  instrumentName?: string;
}

const FRET_MARKERS = [3, 5, 7, 9, 12, 15, 17, 19, 21, 24];
const DOUBLE_MARKERS = [12, 24];

export const TuxGuitarFretboard: React.FC<TuxGuitarFretboardProps> = ({
  tuningKey,
  tuningNotes,
  activeNotes,
  onNoteClick,
  instrumentName = 'Guitar',
}) => {
  const stringsCount = tuningNotes.length > 0 ? tuningNotes.length : 6;
  const fretsCount = 24; // Full 24-fret professional fretboard

  // Check if string & fret is active
  const isFretActive = (sIdx: number, fIdx: number) => {
    return activeNotes.some((n) => n.stringIndex === sIdx && n.fret === fIdx);
  };

  const handleCellClick = (sIdx: number, fIdx: number) => {
    playNotePreview(sIdx, fIdx, tuningKey, instrumentName);
    onNoteClick(sIdx, fIdx);
  };

  return (
    <div className="bg-[#0C1019] border border-[#222B3D] rounded-2xl p-4 shadow-xl overflow-x-auto select-none">
      <div className="flex items-center justify-between pb-3 mb-2 border-b border-[#1E2638]">
        <div className="flex items-center space-x-2">
          <div className="w-2 h-2 rounded-full bg-amber-400 animate-pulse" />
          <span className="text-xs font-bold text-amber-400 tracking-wider uppercase">
            TuxGuitar Virtual Fretboard (24 Frets)
          </span>
          <span className="text-[11px] text-zinc-400 font-mono">
            [{instrumentName}] — Click fret to insert note into active beat
          </span>
        </div>
        <div className="text-[11px] text-zinc-400 flex items-center space-x-1.5">
          <Volume2 className="w-3.5 h-3.5 text-teal-400" />
          <span>Real-time Audio Preview</span>
        </div>
      </div>

      <div className="min-w-[920px]">
        {/* Fret number markers bar */}
        <div className="grid grid-cols-[60px_repeat(25,minmax(32px,1fr))] text-[10px] font-mono text-zinc-500 pb-1 text-center font-bold">
          <div>NUT</div>
          {Array.from({ length: 25 }, (_, i) => (
            <div
              key={i}
              className={`py-0.5 ${
                FRET_MARKERS.includes(i) ? 'text-amber-400 font-black' : 'text-zinc-600'
              }`}
            >
              {i === 0 ? '0' : i}
            </div>
          ))}
        </div>

        {/* Fretboard Strings Grid */}
        <div className="relative bg-[#161D2B] border-t-2 border-b-2 border-[#334155] rounded-lg shadow-inner py-1.5">
          {/* Position marker inlays */}
          <div className="absolute inset-0 pointer-events-none grid grid-cols-[60px_repeat(25,minmax(32px,1fr))] items-center">
            <div />
            {Array.from({ length: 25 }, (_, f) => {
              const isDouble = DOUBLE_MARKERS.includes(f);
              const isSingle = FRET_MARKERS.includes(f) && !isDouble;
              return (
                <div key={f} className="flex flex-col items-center justify-center space-y-3 h-full">
                  {isSingle && (
                    <div className="w-2 h-2 rounded-full bg-zinc-600/40 border border-zinc-500/50" />
                  )}
                  {isDouble && (
                    <div className="flex flex-col space-y-2">
                      <div className="w-1.5 h-1.5 rounded-full bg-amber-400/50" />
                      <div className="w-1.5 h-1.5 rounded-full bg-amber-400/50" />
                    </div>
                  )}
                </div>
              );
            })}
          </div>

          {/* Strings loop (top = string 0: high string, bottom = lowest string) */}
          {Array.from({ length: stringsCount }, (_, sIdx) => {
            const stringLabel = tuningNotes[sIdx] || `S${sIdx + 1}`;
            // Wire gauge thickness styling
            const wireThickness = Math.max(1, Math.round(1 + (sIdx / stringsCount) * 2.5));

            return (
              <div
                key={sIdx}
                className="grid grid-cols-[60px_repeat(25,minmax(32px,1fr))] items-center h-7 relative group"
              >
                {/* Nut / Open string label */}
                <button
                  onClick={() => handleCellClick(sIdx, 0)}
                  className={`h-6 mr-1.5 rounded-md px-1 text-[11px] font-mono font-bold flex items-center justify-between transition-all cursor-pointer border ${
                    isFretActive(sIdx, 0)
                      ? 'bg-amber-500 text-zinc-950 border-amber-300 shadow-[0_0_10px_rgba(245,158,11,0.6)]'
                      : 'bg-[#0E1420] text-zinc-300 border-[#243147] hover:border-amber-400/60 hover:text-white'
                  }`}
                  title={`Play open string ${stringLabel}`}
                >
                  <span>{stringLabel}</span>
                  <span className="text-[9px] text-zinc-500">0</span>
                </button>

                {/* Horizontal String Line */}
                <div
                  className="absolute left-[60px] right-0 pointer-events-none z-0 bg-gradient-to-r from-zinc-300 via-zinc-400 to-zinc-500"
                  style={{ height: `${wireThickness}px`, top: '50%', transform: 'translateY(-50%)' }}
                />

                {/* Frets 0 to 24 */}
                {Array.from({ length: 25 }, (_, fIdx) => {
                  const active = isFretActive(sIdx, fIdx);
                  return (
                    <div
                      key={fIdx}
                      className="h-full flex items-center justify-center relative border-r border-[#2C384E] z-10"
                    >
                      <button
                        onClick={() => handleCellClick(sIdx, fIdx)}
                        className={`w-6 h-5 rounded-full flex items-center justify-center text-[10px] font-mono font-black transition-all cursor-pointer ${
                          active
                            ? 'bg-amber-400 text-zinc-950 scale-110 shadow-[0_0_12px_rgba(245,158,11,0.8)] ring-2 ring-amber-300 ring-offset-1 ring-offset-[#161D2B]'
                            : 'opacity-0 group-hover:opacity-60 hover:!opacity-100 bg-[#253247]/90 text-zinc-200 border border-zinc-600 hover:border-amber-400 hover:bg-amber-500/30'
                        }`}
                        title={`String ${sIdx + 1} (${stringLabel}), Fret ${fIdx}`}
                      >
                        {fIdx}
                      </button>
                    </div>
                  );
                })}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
