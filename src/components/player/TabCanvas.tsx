import React, { useEffect, useRef } from 'react';
import { TabTrackInfo, AdvancedMeasure, AdvancedBeat, AdvancedNote } from '../../types/tabPlayer';

interface TabCanvasProps {
  track: TabTrackInfo;
  currentMeasureIndex: number;
  currentBeatIndex: number;
  isPlaying: boolean;
  onSelectPosition: (measureIdx: number, beatIdx: number) => void;
  loopRange?: [number, number] | null; // [measureStart, measureEnd]
}

export const TabCanvas: React.FC<TabCanvasProps> = ({
  track,
  currentMeasureIndex,
  currentBeatIndex,
  isPlaying,
  onSelectPosition,
  loopRange,
}) => {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const activeMeasureRef = useRef<HTMLDivElement | null>(null);

  const stringCount = track.tuningNotes.length || 6;
  // Labels for strings (highest pitch at index 0, lowest at stringCount - 1)
  const stringLabels =
    track.tuningNotes.length > 0
      ? track.tuningNotes.map((n) => n.replace(/[0-9]/g, ''))
      : ['e', 'B', 'G', 'D', 'A', 'E'];

  // Auto-scroll to keep active measure visible
  useEffect(() => {
    if (isPlaying && activeMeasureRef.current && containerRef.current) {
      activeMeasureRef.current.scrollIntoView({
        behavior: 'smooth',
        block: 'nearest',
        inline: 'center',
      });
    }
  }, [currentMeasureIndex, isPlaying]);

  return (
    <div
      ref={containerRef}
      className="flex-1 w-full overflow-y-auto overflow-x-hidden bg-[#0d0f12] text-zinc-100 select-none pb-36 pt-4 px-2 sm:px-6"
    >
      <div className="max-w-5xl mx-auto space-y-6">
        {track.measures.map((measure, mIdx) => {
          const isMeasureActive = isPlaying && currentMeasureIndex === mIdx;
          const isInLoop =
            loopRange && mIdx + 1 >= loopRange[0] && mIdx + 1 <= loopRange[1];

          return (
            <div
              key={measure.number}
              ref={isMeasureActive ? activeMeasureRef : null}
              className={`relative rounded-2xl border transition-all duration-150 ${
                isMeasureActive
                  ? 'bg-zinc-900/90 border-emerald-500 shadow-lg shadow-emerald-500/10 ring-2 ring-emerald-500/30'
                  : isInLoop
                  ? 'bg-zinc-900/50 border-amber-500/40'
                  : 'bg-zinc-950/60 border-zinc-800/80 hover:border-zinc-700/80'
              }`}
            >
              {/* Measure Top Bar (Number, Time Sig, Palm Mute Indicator, Loop Badge) */}
              <div className="flex items-center justify-between px-4 pt-3 pb-1 border-b border-zinc-800/50">
                <div className="flex items-center space-x-3">
                  <div className="flex items-baseline space-x-1.5">
                    <span className="text-xs font-mono font-bold text-zinc-500 uppercase">
                      Bar
                    </span>
                    <span
                      className={`text-sm font-mono font-extrabold ${
                        isMeasureActive ? 'text-emerald-400' : 'text-zinc-200'
                      }`}
                    >
                      {measure.number}
                    </span>
                  </div>

                  <span className="text-xs font-mono text-zinc-500 bg-zinc-800/80 px-2 py-0.5 rounded">
                    {measure.timeSignature[0]}/{measure.timeSignature[1]}
                  </span>

                  {measure.tempoBpm && (
                    <span className="text-xs font-mono text-zinc-400 hidden sm:inline">
                      ♩ = {measure.tempoBpm}
                    </span>
                  )}
                </div>

                {/* Palm Mute banner over measure */}
                {measure.palmMute && (
                  <div className="flex items-center space-x-1 text-xs font-mono font-bold text-amber-400/90 tracking-wider">
                    <span>{measure.palmMuteLabel || 'P.M. ----------------|'}</span>
                  </div>
                )}

                {/* Active or Loop Pill */}
                <div className="flex items-center space-x-2">
                  {isMeasureActive && (
                    <span className="flex items-center space-x-1.5 px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 animate-pulse">
                      <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
                      <span>PLAYING</span>
                    </span>
                  )}
                  {isInLoop && !isMeasureActive && (
                    <span className="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-amber-500/15 text-amber-300 border border-amber-500/30">
                      LOOP A-B
                    </span>
                  )}
                </div>
              </div>

              {/* Tab Notation Canvas Area */}
              <div className="p-4 overflow-x-auto">
                <div className="min-w-[500px] relative">
                  {/* Left String Tuning Labels */}
                  <div className="absolute left-0 top-0 bottom-10 w-9 flex flex-col justify-between py-1 border-r border-zinc-800 text-[11px] font-mono font-bold text-zinc-400 z-10 bg-[#0d0f12]/80 backdrop-blur-xs">
                    {stringLabels.map((lbl, sIdx) => (
                      <div
                        key={sIdx}
                        className="h-6 flex items-center justify-start pl-1 text-amber-400/80"
                      >
                        {lbl}
                      </div>
                    ))}
                  </div>

                  {/* String Horizontal Wire Grid */}
                  <div className="ml-10 relative">
                    <div className="space-y-0">
                      {Array.from({ length: stringCount }, (_, sIdx) => (
                        <div
                          key={sIdx}
                          className="h-6 flex items-center relative"
                        >
                          {/* Horizontal String Line */}
                          <div
                            className={`absolute inset-x-0 top-1/2 -translate-y-1/2 transition-colors ${
                              isMeasureActive ? 'bg-zinc-600' : 'bg-zinc-700/80'
                            }`}
                            style={{
                              height: `${Math.max(1, (stringCount - sIdx) * 0.4)}px`,
                            }}
                          />
                        </div>
                      ))}
                    </div>

                    {/* Beats & Notes Columns */}
                    <div className="absolute inset-0 flex justify-between items-stretch">
                      {measure.beats.map((beat, bIdx) => {
                        const isBeatActive = isMeasureActive && currentBeatIndex === bIdx;

                        return (
                          <div
                            key={beat.id}
                            onClick={() => onSelectPosition(mIdx, bIdx)}
                            className={`flex-1 relative cursor-pointer group flex flex-col justify-between py-0 transition-all rounded-lg ${
                              isBeatActive
                                ? 'bg-emerald-500/15 ring-2 ring-emerald-400 shadow-md shadow-emerald-500/20'
                                : 'hover:bg-zinc-800/40'
                            }`}
                          >
                            {/* Notes on each string for this beat */}
                            {Array.from({ length: stringCount }, (_, sIdx) => {
                              const note = beat.notes.find((n) => n.stringIndex === sIdx);

                              return (
                                <div
                                  key={sIdx}
                                  className="h-6 flex items-center justify-center relative z-20"
                                >
                                  {note && (
                                    <div
                                      className={`px-1.5 py-0.5 rounded font-mono font-bold text-xs sm:text-sm flex items-center justify-center transition-all ${
                                        isBeatActive
                                          ? 'bg-emerald-400 text-zinc-950 font-black scale-125 shadow-lg shadow-emerald-400/50'
                                          : 'bg-zinc-900 border border-zinc-700 text-white shadow-xs group-hover:border-zinc-500'
                                      }`}
                                    >
                                      {note.deadNote ? 'X' : note.fret}

                                      {/* Slide notation icon */}
                                      {note.slide === 'up' && (
                                        <span className="text-[10px] text-amber-400 ml-0.5 font-bold">
                                          /
                                        </span>
                                      )}
                                      {note.slide === 'down' && (
                                        <span className="text-[10px] text-amber-400 ml-0.5 font-bold">
                                          \
                                        </span>
                                      )}
                                      {note.bend && (
                                        <span className="text-[10px] text-cyan-400 ml-0.5 font-bold">
                                          b
                                        </span>
                                      )}
                                      {note.vibrato && (
                                        <span className="text-[10px] text-indigo-400 ml-0.5">
                                          ~
                                        </span>
                                      )}
                                    </div>
                                  )}
                                </div>
                              );
                            })}

                            {/* Graphical Rhythm Stem (Штигель и хвост длительности) under string grid */}
                            <div className="h-8 flex flex-col items-center justify-start pt-1.5 z-10">
                              {/* Stem line */}
                              <div
                                className={`w-[2px] h-3.5 transition-colors ${
                                  isBeatActive ? 'bg-emerald-400' : 'bg-zinc-500'
                                }`}
                              />
                              {/* Beam flags for eighth / sixteenth notes */}
                              {beat.duration === 'e' && (
                                <div
                                  className={`w-3 h-1 -mt-1 ml-2 rounded-xs ${
                                    isBeatActive ? 'bg-emerald-400' : 'bg-zinc-500'
                                  }`}
                                />
                              )}
                              {beat.duration === 's' && (
                                <div className="space-y-0.5 -mt-1 ml-2">
                                  <div
                                    className={`w-3 h-[2px] rounded-xs ${
                                      isBeatActive ? 'bg-emerald-400' : 'bg-zinc-500'
                                    }`}
                                  />
                                  <div
                                    className={`w-3 h-[2px] rounded-xs ${
                                      isBeatActive ? 'bg-emerald-400' : 'bg-zinc-500'
                                    }`}
                                  />
                                </div>
                              )}
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
