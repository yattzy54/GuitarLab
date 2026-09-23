import React, { useEffect, useRef, useState, useMemo } from 'react';
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

  // Mobile pinch-to-zoom & double-tap zoom state
  const [zoomScale, setZoomScale] = useState<number>(1.0);
  const lastTouchDistRef = useRef<number | null>(null);
  const lastTapTimeRef = useRef<number>(0);

  const isDrums = useMemo(() => {
    return (
      track.instrument?.toLowerCase().includes('drum') ||
      track.name?.toLowerCase().includes('drum')
    );
  }, [track.instrument, track.name]);

  const stringLabels = useMemo(() => {
    if (isDrums) return ['CC', 'HH', 'T1', 'SD', 'BD'];
    return track.tuningNotes.length > 0
      ? track.tuningNotes.map((n) => n.replace(/[0-9]/g, ''))
      : ['e', 'B', 'G', 'D', 'A', 'E'];
  }, [track.tuningNotes, isDrums]);

  const stringCount = isDrums ? 5 : stringLabels.length || 6;

  // Touch gesture handling: smooth pinch-to-zoom + double tap to reset
  const handleTouchStart = (e: React.TouchEvent) => {
    if (e.touches.length === 2) {
      const dx = e.touches[0].clientX - e.touches[1].clientX;
      const dy = e.touches[0].clientY - e.touches[1].clientY;
      lastTouchDistRef.current = Math.hypot(dx, dy);
    } else if (e.touches.length === 1) {
      const now = Date.now();
      if (now - lastTapTimeRef.current < 300) {
        // Double tap toggles between 1.0x and 1.35x zoom
        setZoomScale((prev) => (prev > 1.1 ? 1.0 : 1.35));
        lastTapTimeRef.current = 0;
      } else {
        lastTapTimeRef.current = now;
      }
    }
  };

  const handleTouchMove = (e: React.TouchEvent) => {
    if (e.touches.length === 2 && lastTouchDistRef.current !== null) {
      const dx = e.touches[0].clientX - e.touches[1].clientX;
      const dy = e.touches[0].clientY - e.touches[1].clientY;
      const dist = Math.hypot(dx, dy);
      const factor = dist / lastTouchDistRef.current;
      setZoomScale((prev) => Math.min(1.8, Math.max(0.85, prev * (1 + (factor - 1) * 0.5))));
      lastTouchDistRef.current = dist;
    }
  };

  const handleTouchEnd = () => {
    lastTouchDistRef.current = null;
  };

  // High-performance Auto-Scroll using RequestAnimationFrame & instant centering during playback
  useEffect(() => {
    if (isPlaying && activeMeasureRef.current && containerRef.current) {
      const container = containerRef.current;
      const activeEl = activeMeasureRef.current;
      
      const containerRect = container.getBoundingClientRect();
      const activeRect = activeEl.getBoundingClientRect();
      
      // Calculate relative center offset
      const targetScrollTop = container.scrollTop + (activeRect.top - containerRect.top) - (containerRect.height / 2) + (activeRect.height / 2);
      
      // Smooth animated scroll with requestAnimationFrame to prevent jank on mobile
      container.scrollTo({
        top: Math.max(0, targetScrollTop),
        behavior: 'smooth',
      });
    }
  }, [currentMeasureIndex, isPlaying]);

  return (
    <div
      ref={containerRef}
      onTouchStart={handleTouchStart}
      onTouchMove={handleTouchMove}
      onTouchEnd={handleTouchEnd}
      style={{
        WebkitOverflowScrolling: 'touch',
        touchAction: 'pan-x pan-y',
        transform: 'translateZ(0)', // Force hardware acceleration in Android WebView
      }}
      className="flex-1 w-full overflow-y-auto overflow-x-hidden bg-[#0a0c10] text-zinc-100 select-none pb-44 pt-2 px-1.5 sm:px-4"
    >
      {/* Zoom indicator & controls capsule for mobile quick view */}
      <div className="sticky top-1 z-30 flex justify-end px-2 mb-2 pointer-events-none">
        <div className="pointer-events-auto flex items-center space-x-1.5 bg-zinc-900/90 backdrop-blur-md border border-zinc-800/80 px-2.5 py-1 rounded-full text-[11px] font-mono shadow-lg text-zinc-400">
          <button
            onClick={() => setZoomScale((z) => Math.max(0.85, +(z - 0.15).toFixed(2)))}
            className="w-5 h-5 flex items-center justify-center rounded active:bg-zinc-800 text-zinc-300 font-bold"
          >
            -
          </button>
          <span className="w-9 text-center font-semibold text-zinc-200">{Math.round(zoomScale * 100)}%</span>
          <button
            onClick={() => setZoomScale((z) => Math.min(1.8, +(z + 0.15).toFixed(2)))}
            className="w-5 h-5 flex items-center justify-center rounded active:bg-zinc-800 text-zinc-300 font-bold"
          >
            +
          </button>
          {zoomScale !== 1.0 && (
            <button
              onClick={() => setZoomScale(1.0)}
              className="ml-1 text-[10px] text-amber-400 font-medium active:underline"
            >
              Reset
            </button>
          )}
        </div>
      </div>

      <div
        className="max-w-4xl mx-auto space-y-4 transition-transform duration-75 origin-top"
        style={{ transform: `scale(${zoomScale})` }}
      >
        {track.measures.map((measure, mIdx) => {
          const isMeasureActive = isPlaying && currentMeasureIndex === mIdx;
          const isInLoop =
            loopRange && mIdx + 1 >= loopRange[0] && mIdx + 1 <= loopRange[1];

          return (
            <div
              key={measure.number}
              ref={isMeasureActive ? activeMeasureRef : null}
              className={`relative rounded-2xl border transition-colors duration-150 ${
                isMeasureActive
                  ? 'bg-zinc-900/95 border-emerald-500 shadow-xl shadow-emerald-500/15 ring-2 ring-emerald-500/40'
                  : isInLoop
                  ? 'bg-zinc-950/90 border-amber-500/40'
                  : 'bg-zinc-950/70 border-zinc-800/70 active:border-zinc-700'
              }`}
            >
              {/* Measure Top Header */}
              <div className="flex items-center justify-between px-3 py-2 border-b border-zinc-800/60 bg-zinc-900/40 rounded-t-2xl">
                <div className="flex items-center space-x-2.5">
                  <div className="flex items-baseline space-x-1">
                    <span className="text-[10px] font-mono font-bold text-zinc-500 uppercase">
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
                  <span className="text-[11px] font-mono text-zinc-400 bg-zinc-800/90 px-1.5 py-0.5 rounded">
                    {measure.timeSignature[0]}/{measure.timeSignature[1]}
                  </span>
                  {measure.tempoBpm && (
                    <span className="text-[11px] font-mono text-zinc-400">
                      ♩={measure.tempoBpm}
                    </span>
                  )}
                </div>

                {/* Palm Mute label */}
                {measure.palmMute && (
                  <div className="px-2 py-0.5 rounded bg-amber-500/15 border border-amber-500/30 text-[10px] font-mono font-bold text-amber-400 tracking-wider">
                    {measure.palmMuteLabel || 'P.M.'}
                  </div>
                )}

                {/* Status Pill */}
                <div className="flex items-center space-x-1.5">
                  {isMeasureActive && (
                    <span className="flex items-center space-x-1 px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 animate-pulse">
                      <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
                      <span>PLAY</span>
                    </span>
                  )}
                  {isInLoop && !isMeasureActive && (
                    <span className="px-2 py-0.5 rounded-full text-[9px] font-semibold bg-amber-500/15 text-amber-300 border border-amber-500/30">
                      LOOP
                    </span>
                  )}
                </div>
              </div>

              {/* Tab Notation Touch Grid */}
              <div
                className="p-2 sm:p-3 overflow-x-auto scrollbar-none"
                style={{ WebkitOverflowScrolling: 'touch' }}
              >
                <div className="min-w-[460px] relative">
                  {/* Left String Tuning Labels or GP8 Percussion Clef */}
                  <div className="absolute left-0 top-0 bottom-8 w-8 flex flex-col justify-between py-1 border-r border-zinc-800 text-[11px] font-mono font-bold text-amber-400/90 z-10 bg-[#0a0c10]/90 backdrop-blur-xs">
                    {isDrums ? (
                      <div className="h-full flex items-center justify-center space-x-1">
                        <div className="w-1 h-20 bg-amber-400/90 rounded-xs" />
                        <div className="w-1 h-20 bg-amber-400/90 rounded-xs" />
                      </div>
                    ) : (
                      stringLabels.map((lbl, sIdx) => (
                        <div
                          key={sIdx}
                          className="h-7 flex items-center justify-start pl-1"
                        >
                          {lbl}
                        </div>
                      ))
                    )}
                  </div>

                  {/* Horizontal Wire Strings */}
                  <div className="ml-9 relative">
                    <div className="space-y-0">
                      {Array.from({ length: stringCount }, (_, sIdx) => (
                        <div
                          key={sIdx}
                          className="h-7 flex items-center relative"
                        >
                          <div
                            className={`absolute inset-x-0 top-1/2 -translate-y-1/2 transition-colors ${
                              isMeasureActive ? 'bg-zinc-500' : 'bg-zinc-700/80'
                            }`}
                            style={{
                              height: `${Math.max(1, (stringCount - sIdx) * 0.4)}px`,
                            }}
                          />
                        </div>
                      ))}
                    </div>

                    {/* Touch-Friendly Beat Columns */}
                    <div className="absolute inset-0 flex justify-between items-stretch">
                      {measure.beats.map((beat, bIdx) => {
                        const isBeatActive = isMeasureActive && currentBeatIndex === bIdx;
                        return (
                          <div
                            key={beat.id}
                            onClick={() => onSelectPosition(mIdx, bIdx)}
                            className={`flex-1 min-w-[36px] relative cursor-pointer flex flex-col justify-between py-0 rounded-lg active:scale-95 transition-transform ${
                              isBeatActive
                                ? 'bg-emerald-500/20 ring-2 ring-emerald-400 shadow-md shadow-emerald-500/30'
                                : 'hover:bg-zinc-800/30 active:bg-zinc-800/50'
                            }`}
                          >
                            {/* Notes on each string for this beat */}
                            {Array.from({ length: stringCount }, (_, sIdx) => {
                              const note = beat.notes.find((n) => n.stringIndex === sIdx);
                              return (
                                <div
                                  key={sIdx}
                                  className="h-7 flex items-center justify-center relative z-20"
                                >
                                  {note && (
                                    isDrums ? (
                                      /* CLASSIC GUITAR PRO 8 DRUM NOTATION */
                                      sIdx <= 1 ? (
                                        /* Cymbal / Hi-Hat Cross '×' notehead */
                                        <div
                                          className={`relative w-4 h-4 flex items-center justify-center transition-transform ${
                                            isBeatActive ? 'scale-125 text-emerald-400 font-black' : 'text-zinc-100'
                                          }`}
                                        >
                                          <div className="absolute w-3.5 h-0.5 bg-current rotate-45 rounded-full" />
                                          <div className="absolute w-3.5 h-0.5 bg-current -rotate-45 rounded-full" />
                                        </div>
                                      ) : (
                                        /* Snare / Tom / Kick solid oval notehead */
                                        <div
                                          className={`w-3.5 h-2.5 rounded-full border transition-all ${
                                            isBeatActive
                                              ? 'bg-emerald-400 border-emerald-300 scale-125 shadow-md shadow-emerald-400/50'
                                              : 'bg-zinc-100 border-zinc-300 shadow-xs'
                                          }`}
                                        />
                                      )
                                    ) : (
                                      <div
                                        className={`px-1.5 py-0.5 rounded font-mono font-bold text-xs flex items-center justify-center transition-all ${
                                          isBeatActive
                                            ? 'bg-emerald-400 text-zinc-950 font-black scale-110 shadow-lg shadow-emerald-400/50'
                                            : 'bg-zinc-900 border border-zinc-700 text-zinc-100 shadow-xs'
                                        }`}
                                      >
                                        {note.deadNote ? 'X' : note.fret}
                                        {note.slide === 'up' && (
                                          <span className="text-[9px] text-amber-400 ml-0.5">/</span>
                                        )}
                                        {note.slide === 'down' && (
                                          <span className="text-[9px] text-amber-400 ml-0.5">\</span>
                                        )}
                                        {note.bend && (
                                          <span className="text-[9px] text-cyan-400 ml-0.5">b</span>
                                        )}
                                        {note.vibrato && (
                                          <span className="text-[9px] text-indigo-400 ml-0.5">~</span>
                                        )}
                                      </div>
                                    )
                                  )}
                                </div>
                              );
                            })}

                            {/* Rhythm Stem under strings */}
                            <div className="h-7 flex flex-col items-center justify-start pt-1 z-10">
                              <div
                                className={`w-[2px] h-3.5 transition-colors ${
                                  isBeatActive ? 'bg-emerald-400' : 'bg-zinc-500'
                                }`}
                              />
                              {(beat.duration === 'e' || beat.duration === 's') && (
                                <div
                                  className={`w-2.5 h-[2px] mt-0.5 ${
                                    isBeatActive ? 'bg-emerald-400' : 'bg-zinc-400'
                                  }`}
                                />
                              )}
                              {beat.duration === 's' && (
                                <div
                                  className={`w-2.5 h-[2px] mt-0.5 ${
                                    isBeatActive ? 'bg-emerald-400' : 'bg-zinc-400'
                                  }`}
                                />
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
