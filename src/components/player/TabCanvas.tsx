import React, { useEffect, useRef, useState, useCallback } from 'react';
import * as alphaTab from '@coderline/alphatab';
import { TabTrackInfo, SongTabScore } from '../../types/tabPlayer';
import { convertTrackToAlphaTex } from '../../audio/alphaTexConverter';
import { ZoomIn, ZoomOut, RotateCcw, FileText, Music, Sparkles, Volume2, CheckCircle2 } from 'lucide-react';

export interface TabCanvasProps {
  track: TabTrackInfo;
  song?: SongTabScore;
  currentMeasureIndex: number;
  currentBeatIndex: number;
  isPlaying: boolean;
  onSelectPosition?: (measureIdx: number, beatIdx: number) => void;
  loopRange?: [number, number] | null; // [measureStart, measureEnd]
  speedRatio?: number;
  isLoopActive?: boolean;
  countInEnabled?: boolean;
  metronomeClickEnabled?: boolean;
  semitones?: number;
  audioSource?: 'SYNTH' | 'ORIG';
  onTogglePlay?: () => void;
  onPlayStateChanged?: (playing: boolean) => void;
  onMeasureChanged?: (measureIndex: number) => void;
}

export const TabCanvas: React.FC<TabCanvasProps> = ({
  track,
  song,
  currentMeasureIndex,
  currentBeatIndex,
  isPlaying,
  onSelectPosition,
  loopRange,
  speedRatio = 1.0,
  isLoopActive = false,
  countInEnabled = false,
  metronomeClickEnabled = false,
  semitones = 0,
  audioSource = 'SYNTH',
  onTogglePlay,
  onPlayStateChanged,
  onMeasureChanged,
}) => {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const viewportRef = useRef<HTMLDivElement | null>(null);
  const apiRef = useRef<alphaTab.AlphaTabApi | null>(null);

  // Score display & layout states
  const [zoomScale, setZoomScale] = useState<number>(1.0);
  const [staveProfile, setStaveProfile] = useState<alphaTab.StaveProfile>(alphaTab.StaveProfile.Tab);
  const [layoutMode, setLayoutMode] = useState<alphaTab.LayoutMode>(alphaTab.LayoutMode.Page);

  // Status states
  const [isScoreLoaded, setIsScoreLoaded] = useState(false);
  const [isSoundFontReady, setIsSoundFontReady] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);

  // Initialize alphaTab engine
  useEffect(() => {
    if (!containerRef.current) return;

    try {
      const settings = new alphaTab.Settings();
      settings.core.fontDirectory = '/font/';
      settings.player.enablePlayer = true;
      settings.player.soundFont = '/soundfont/sonivox.sf2';
      settings.player.scrollElement = viewportRef.current || containerRef.current;
      settings.player.scrollMode = alphaTab.ScrollMode.Continuous;
      settings.display.staveProfile = staveProfile;
      settings.display.layoutMode = layoutMode;
      settings.display.scale = zoomScale;

      // Dark Studio Palette styling
      const Color = (settings.display.resources.staffLineColor.constructor as any);
      settings.display.resources.staffLineColor = Color.fromJson('#3f3f46'); // zinc-700
      settings.display.resources.barSeparatorColor = Color.fromJson('#52525b'); // zinc-600
      settings.display.resources.barNumberColor = Color.fromJson('#f59e0b'); // amber-500
      settings.display.resources.mainGlyphColor = Color.fromJson('#f4f4f5'); // zinc-100
      settings.display.resources.secondaryGlyphColor = Color.fromJson('#a1a1aa'); // zinc-400
      settings.display.resources.scoreInfoColor = Color.fromJson('#f59e0b'); // amber-500

      const api = new alphaTab.AlphaTabApi(containerRef.current, settings);
      apiRef.current = api;

      // Listeners
      api.scoreLoaded.on(() => {
        setIsScoreLoaded(true);
        setLoadError(null);
      });

      api.soundFontLoaded.on(() => {
        setIsSoundFontReady(true);
      });

      api.playerStateChanged.on((args) => {
        const playing = args.state === 1; // 1 = Playing
        if (onPlayStateChanged) {
          onPlayStateChanged(playing);
        }
      });

      api.playerPositionChanged.on((args) => {
        // Find active bar and beat
        if (api.score && args.currentTick !== undefined) {
          const bar = api.score.masterBars.find((mb) => {
            return args.currentTick >= mb.start && args.currentTick < (mb.start + mb.calculateDuration());
          });
          if (bar && onMeasureChanged) {
            onMeasureChanged(bar.index);
          }
        }
      });

      api.playerFinished.on(() => {
        if (onPlayStateChanged) {
          onPlayStateChanged(false);
        }
      });

      api.error.on((err) => {
        console.warn('alphaTab error event:', err);
      });

      // Handle ResizeObserver
      const resizeObserver = new ResizeObserver(() => {
        if (apiRef.current) {
          try {
            (apiRef.current as any).resizeRender?.() ?? (apiRef.current as any).triggerResize?.();
          } catch (_) {}
        }
      });

      if (viewportRef.current) {
        resizeObserver.observe(viewportRef.current);
      }

      return () => {
        resizeObserver.disconnect();
        api.destroy();
        apiRef.current = null;
      };
    } catch (e: any) {
      console.error('Failed to init alphaTab:', e);
      setLoadError(e?.message || 'Failed to initialize alphaTab engine');
    }
  }, []);

  // Load / update score AlphaTex whenever track or song changes
  useEffect(() => {
    const api = apiRef.current;
    if (!api || !track) return;

    try {
      setIsScoreLoaded(false);
      const songData: SongTabScore = song || {
        id: 'current_song',
        title: 'Guitar Tablature',
        artist: 'GuitarLab',
        revisionDate: '2026',
        defaultTempo: 120,
        tracks: [track],
      };

      const alphaTex = convertTrackToAlphaTex(songData, track);
      api.tex(alphaTex);
    } catch (err: any) {
      console.error('Error generating/loading AlphaTex:', err);
      setLoadError(err?.message || 'Failed to render tablature');
    }
  }, [track.id, song?.id]);

  // Synchronize Playback state
  useEffect(() => {
    const api = apiRef.current;
    if (!api || !isScoreLoaded) return;

    try {
      const isCurrentlyPlaying = api.playerState === 1; // 1 = Playing
      if (isPlaying && !isCurrentlyPlaying) {
        api.play();
      } else if (!isPlaying && isCurrentlyPlaying) {
        api.pause();
      }
    } catch (e) {
      console.warn('alphaTab play/pause error:', e);
    }
  }, [isPlaying, isScoreLoaded]);

  // Synchronize Playback Speed (Tempo modifier)
  useEffect(() => {
    const api = apiRef.current;
    if (!api) return;
    try {
      api.playbackSpeed = speedRatio;
    } catch (_) {}
  }, [speedRatio]);

  // Synchronize Looping
  useEffect(() => {
    const api = apiRef.current;
    if (!api) return;
    try {
      api.isLooping = isLoopActive;
    } catch (_) {}
  }, [isLoopActive]);

  // Synchronize Metronome Volume
  useEffect(() => {
    const api = apiRef.current;
    if (!api) return;
    try {
      api.metronomeVolume = metronomeClickEnabled ? 1.0 : 0.0;
    } catch (_) {}
  }, [metronomeClickEnabled]);

  // Synchronize Count-In Volume
  useEffect(() => {
    const api = apiRef.current;
    if (!api) return;
    try {
      api.countInVolume = countInEnabled ? 1.0 : 0.0;
    } catch (_) {}
  }, [countInEnabled]);

  // Synchronize Audio Source (SYNTH vs ORIG)
  useEffect(() => {
    const api = apiRef.current;
    if (!api) return;
    try {
      // If user chose original audio, mute the internal synthesizer so they hear only the master recording
      api.masterVolume = audioSource === 'ORIG' ? 0.0 : 1.0;
    } catch (_) {}
  }, [audioSource]);

  // Synchronize Transposition
  useEffect(() => {
    const api = apiRef.current;
    if (!api || !api.tracks || api.tracks.length === 0) return;
    try {
      api.changeTrackTranspositionPitch(api.tracks, semitones);
    } catch (_) {}
  }, [semitones]);

  // Zoom handlers
  const handleZoomIn = () => {
    const next = Math.min(1.8, Math.round((zoomScale + 0.15) * 100) / 100);
    setZoomScale(next);
    if (apiRef.current) {
      apiRef.current.settings.display.scale = next;
      apiRef.current.updateSettings();
      apiRef.current.render();
    }
  };

  const handleZoomOut = () => {
    const next = Math.max(0.65, Math.round((zoomScale - 0.15) * 100) / 100);
    setZoomScale(next);
    if (apiRef.current) {
      apiRef.current.settings.display.scale = next;
      apiRef.current.updateSettings();
      apiRef.current.render();
    }
  };

  const handleZoomReset = () => {
    setZoomScale(1.0);
    if (apiRef.current) {
      apiRef.current.settings.display.scale = 1.0;
      apiRef.current.updateSettings();
      apiRef.current.render();
    }
  };

  // Stave profile toggle (Tab only vs Score + Tab)
  const handleToggleStaveProfile = () => {
    const next =
      staveProfile === alphaTab.StaveProfile.Tab
        ? alphaTab.StaveProfile.Default
        : alphaTab.StaveProfile.Tab;
    setStaveProfile(next);
    if (apiRef.current) {
      apiRef.current.settings.display.staveProfile = next;
      apiRef.current.updateSettings();
      apiRef.current.render();
    }
  };

  // Layout mode toggle (Page vs Horizontal scroll)
  const handleToggleLayout = () => {
    const next =
      layoutMode === alphaTab.LayoutMode.Page
        ? alphaTab.LayoutMode.Horizontal
        : alphaTab.LayoutMode.Page;
    setLayoutMode(next);
    if (apiRef.current) {
      apiRef.current.settings.display.layoutMode = next;
      apiRef.current.updateSettings();
      apiRef.current.render();
    }
  };

  return (
    <div
      ref={viewportRef}
      className="flex-1 w-full overflow-y-auto overflow-x-hidden bg-[#090b10] relative flex flex-col items-center select-none"
    >
      {/* Top Floating Utility Bar */}
      <div className="sticky top-2 z-30 w-full max-w-4xl px-4 flex items-center justify-between pointer-events-none mb-1">
        {/* Left: Engine & SoundFont Badge */}
        <div className="flex items-center space-x-2 pointer-events-auto bg-zinc-950/85 backdrop-blur-md border border-zinc-800/80 px-2.5 py-1 rounded-full shadow-lg">
          <div className="flex items-center space-x-1.5 text-[11px] font-medium text-zinc-300">
            <span className="w-2 h-2 rounded-full bg-amber-400 animate-pulse" />
            <span className="font-bold text-amber-400">alphaTab</span>
            <span className="text-zinc-600">·</span>
            <span className="text-zinc-400">{track.name}</span>
          </div>
          {isSoundFontReady && (
            <div className="flex items-center space-x-1 text-[10px] text-emerald-400 bg-emerald-950/60 border border-emerald-800/60 px-2 py-0.5 rounded-full">
              <CheckCircle2 className="w-3 h-3" />
              <span>HQ Synth</span>
            </div>
          )}
        </div>

        {/* Right: Engraving Controls (Stave mode, Horizontal/Page, Zoom) */}
        <div className="flex items-center space-x-1.5 pointer-events-auto bg-zinc-950/85 backdrop-blur-md border border-zinc-800/80 p-1 rounded-xl shadow-lg">
          {/* Notation / Tab Profile toggle */}
          <button
            onClick={handleToggleStaveProfile}
            title={staveProfile === alphaTab.StaveProfile.Tab ? 'Switch to Score + Tab' : 'Switch to Tab only'}
            className="px-2.5 py-1 rounded-lg text-xs font-semibold text-zinc-300 hover:text-white bg-zinc-900 hover:bg-zinc-800 transition-colors border border-zinc-800 flex items-center space-x-1"
          >
            {staveProfile === alphaTab.StaveProfile.Tab ? (
              <>
                <FileText className="w-3 h-3 text-amber-400" />
                <span>TAB</span>
              </>
            ) : (
              <>
                <Music className="w-3 h-3 text-amber-400" />
                <span>SCORE+TAB</span>
              </>
            )}
          </button>

          {/* Page vs Horizontal Layout */}
          <button
            onClick={handleToggleLayout}
            title={layoutMode === alphaTab.LayoutMode.Page ? 'Continuous Horizontal Mode' : 'Standard Page Mode'}
            className="px-2.5 py-1 rounded-lg text-xs font-semibold text-zinc-300 hover:text-white bg-zinc-900 hover:bg-zinc-800 transition-colors border border-zinc-800"
          >
            {layoutMode === alphaTab.LayoutMode.Page ? 'Page' : 'Flow'}
          </button>

          {/* Zoom controls */}
          <div className="flex items-center bg-zinc-900 border border-zinc-800 rounded-lg overflow-hidden">
            <button
              onClick={handleZoomOut}
              title="Zoom Out"
              className="p-1 hover:bg-zinc-800 text-zinc-400 hover:text-zinc-100 transition-colors"
            >
              <ZoomOut className="w-3.5 h-3.5" />
            </button>
            <button
              onClick={handleZoomReset}
              title="Reset Zoom (100%)"
              className="px-1.5 text-[10px] font-mono text-zinc-400 hover:text-amber-400 transition-colors"
            >
              {Math.round(zoomScale * 100)}%
            </button>
            <button
              onClick={handleZoomIn}
              title="Zoom In"
              className="p-1 hover:bg-zinc-800 text-zinc-400 hover:text-zinc-100 transition-colors"
            >
              <ZoomIn className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>

      {/* Loading Skeleton */}
      {!isScoreLoaded && !loadError && (
        <div className="w-full max-w-4xl px-4 py-8 space-y-4">
          <div className="flex items-center justify-between">
            <div className="h-6 w-48 bg-zinc-800/60 rounded-md animate-pulse" />
            <div className="h-6 w-24 bg-zinc-800/60 rounded-md animate-pulse" />
          </div>
          <div className="space-y-6">
            {[1, 2, 3].map((idx) => (
              <div
                key={idx}
                className="h-28 w-full bg-zinc-900/60 border border-zinc-800/40 rounded-xl p-4 flex flex-col justify-between animate-pulse"
              >
                <div className="space-y-2">
                  <div className="h-1 w-full bg-zinc-800 rounded-full" />
                  <div className="h-1 w-full bg-zinc-800 rounded-full" />
                  <div className="h-1 w-full bg-zinc-800 rounded-full" />
                  <div className="h-1 w-full bg-zinc-800 rounded-full" />
                  <div className="h-1 w-full bg-zinc-800 rounded-full" />
                  <div className="h-1 w-full bg-zinc-800 rounded-full" />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Load Error Message */}
      {loadError && (
        <div className="w-full max-w-lg mx-auto my-12 p-6 bg-red-950/40 border border-red-800/60 rounded-2xl text-center space-y-3">
          <p className="text-sm font-semibold text-red-300">Ошибка отрисовки табулатуры</p>
          <p className="text-xs text-red-400/80 font-mono">{loadError}</p>
        </div>
      )}

      {/* alphaTab Rendering Host */}
      <div
        ref={containerRef}
        className={`alphatab-surface w-full max-w-4xl px-3 sm:px-6 pb-28 pt-2 transition-opacity duration-300 ${
          isScoreLoaded ? 'opacity-100' : 'opacity-0 h-0 overflow-hidden'
        }`}
      />
    </div>
  );
};
