import React, { useEffect, useRef, useState, useCallback } from 'react';
import * as alphaTab from '@coderline/alphatab';
import { SongTabScore } from '../../types/tabPlayer';
import { convertSongScoreToAlphaTex } from './tuxAlphaTex';
import { ZoomIn, ZoomOut, FileText, Music, CheckCircle2, Volume2, Sparkles } from 'lucide-react';

interface TuxGuitarAlphaTabCanvasProps {
  score: SongTabScore;
  activeTrackId: string;
  isPlaying: boolean;
  tempo: number;
  onPlayStateChanged: (playing: boolean) => void;
  onSelectMeasureBeat?: (measureIndex: number, beatIndex: number) => void;
  renderAllTracks?: boolean;
}

export const TuxGuitarAlphaTabCanvas: React.FC<TuxGuitarAlphaTabCanvasProps> = ({
  score,
  activeTrackId,
  isPlaying,
  tempo,
  onPlayStateChanged,
  onSelectMeasureBeat,
  renderAllTracks = false,
}) => {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const viewportRef = useRef<HTMLDivElement | null>(null);
  const apiRef = useRef<alphaTab.AlphaTabApi | null>(null);

  const [zoomScale, setZoomScale] = useState<number>(1.0);
  const [staveProfile, setStaveProfile] = useState<alphaTab.StaveProfile>(alphaTab.StaveProfile.Default);
  const [isScoreLoaded, setIsScoreLoaded] = useState(false);
  const [isSoundFontReady, setIsSoundFontReady] = useState(false);

  // Initialize alphaTab instance
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
      settings.display.scale = zoomScale;

      const Color = settings.display.resources.staffLineColor.constructor as any;
      settings.display.resources.staffLineColor = Color.fromJson('#374151');
      settings.display.resources.barSeparatorColor = Color.fromJson('#4B5563');
      settings.display.resources.barNumberColor = Color.fromJson('#F59E0B');
      settings.display.resources.mainGlyphColor = Color.fromJson('#F3F4F6');
      settings.display.resources.secondaryGlyphColor = Color.fromJson('#9CA3AF');
      settings.display.resources.scoreInfoColor = Color.fromJson('#F59E0B');

      const api = new alphaTab.AlphaTabApi(containerRef.current, settings);
      apiRef.current = api;

      api.scoreLoaded.on(() => {
        setIsScoreLoaded(true);
      });

      api.soundFontLoaded.on(() => {
        setIsSoundFontReady(true);
      });

      api.playerStateChanged.on((args) => {
        onPlayStateChanged(args.state === 1);
      });

      api.playerFinished.on(() => {
        onPlayStateChanged(false);
      });

      // Handle user clicking on beat/note in score
      api.beatMouseDown.on((beat) => {
        if (onSelectMeasureBeat && beat) {
          const mIdx = beat.voice.bar.index;
          const bIdx = beat.index;
          onSelectMeasureBeat(mIdx, bIdx);
        }
      });

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
    } catch (err) {
      console.error('Failed to init alphaTab viewer in TuxGuitar:', err);
    }
  }, []);

  // Re-generate AlphaTex on score, track or tempo change
  useEffect(() => {
    const api = apiRef.current;
    if (!api || !score) return;

    try {
      setIsScoreLoaded(false);
      const scoreWithTempo = { ...score, defaultTempo: tempo };
      const tex = convertSongScoreToAlphaTex(
        scoreWithTempo,
        !renderAllTracks,
        activeTrackId
      );
      api.tex(tex);
    } catch (err) {
      console.error('Failed to render AlphaTex score in TuxGuitar:', err);
    }
  }, [score, activeTrackId, tempo, renderAllTracks]);

  // Synchronize playback state
  useEffect(() => {
    const api = apiRef.current;
    if (!api || !isScoreLoaded) return;

    try {
      const playing = api.playerState === 1;
      if (isPlaying && !playing) {
        api.play();
      } else if (!isPlaying && playing) {
        api.pause();
      }
    } catch (_) {}
  }, [isPlaying, isScoreLoaded]);

  const handleToggleStave = () => {
    let next: alphaTab.StaveProfile;
    if (staveProfile === alphaTab.StaveProfile.Default) {
      next = alphaTab.StaveProfile.Tab;
    } else if (staveProfile === alphaTab.StaveProfile.Tab) {
      next = alphaTab.StaveProfile.Score;
    } else {
      next = alphaTab.StaveProfile.Default;
    }

    setStaveProfile(next);
    if (apiRef.current) {
      apiRef.current.settings.display.staveProfile = next;
      apiRef.current.updateSettings();
      apiRef.current.render();
    }
  };

  const handleZoom = (delta: number) => {
    const next = Math.max(0.65, Math.min(1.8, Math.round((zoomScale + delta) * 100) / 100));
    setZoomScale(next);
    if (apiRef.current) {
      apiRef.current.settings.display.scale = next;
      apiRef.current.updateSettings();
      apiRef.current.render();
    }
  };

  return (
    <div
      ref={viewportRef}
      className="w-full bg-[#0A0D15] border border-[#222B3D] rounded-2xl p-4 relative overflow-hidden shadow-2xl space-y-3"
    >
      {/* Top Canvas Bar */}
      <div className="flex flex-wrap items-center justify-between gap-3 pb-3 border-b border-[#1E2638]">
        <div className="flex items-center space-x-3">
          <div className="flex items-center space-x-2 text-xs font-bold text-amber-400">
            <Sparkles className="w-3.5 h-3.5 text-amber-400" />
            <span>Interactive Score & Tab View</span>
          </div>

          {isSoundFontReady ? (
            <div className="flex items-center space-x-1 text-[10px] text-emerald-400 bg-emerald-950/60 border border-emerald-800/50 px-2 py-0.5 rounded-full">
              <CheckCircle2 className="w-3 h-3" />
              <span>HQ Audio Synthesizer Ready</span>
            </div>
          ) : (
            <div className="text-[10px] text-zinc-500 animate-pulse">Loading soundfont...</div>
          )}
        </div>

        {/* View mode & Zoom controls */}
        <div className="flex items-center space-x-2">
          <button
            onClick={handleToggleStave}
            className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-[#121824] border border-[#243044] text-zinc-200 hover:text-white flex items-center space-x-1.5 transition-colors cursor-pointer"
          >
            {staveProfile === alphaTab.StaveProfile.Default ? (
              <>
                <Music className="w-3.5 h-3.5 text-amber-400" />
                <span>Score + TAB</span>
              </>
            ) : staveProfile === alphaTab.StaveProfile.Tab ? (
              <>
                <FileText className="w-3.5 h-3.5 text-amber-400" />
                <span>TAB Only</span>
              </>
            ) : (
              <>
                <Music className="w-3.5 h-3.5 text-amber-400" />
                <span>Score Only</span>
              </>
            )}
          </button>

          <div className="flex items-center bg-[#121824] border border-[#243044] rounded-lg overflow-hidden">
            <button
              onClick={() => handleZoom(-0.15)}
              className="px-2 py-1 text-zinc-400 hover:text-white transition-colors cursor-pointer"
              title="Zoom out"
            >
              <ZoomOut className="w-3.5 h-3.5" />
            </button>
            <span className="px-1 text-[10px] font-mono text-zinc-400 font-bold">
              {Math.round(zoomScale * 100)}%
            </span>
            <button
              onClick={() => handleZoom(0.15)}
              className="px-2 py-1 text-zinc-400 hover:text-white transition-colors cursor-pointer"
              title="Zoom in"
            >
              <ZoomIn className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>

      {/* alphaTab Render Host */}
      <div
        ref={containerRef}
        className="alphatab-surface w-full min-h-[320px] max-h-[500px] overflow-y-auto rounded-xl p-2 bg-[#090C14]"
      />
    </div>
  );
};
