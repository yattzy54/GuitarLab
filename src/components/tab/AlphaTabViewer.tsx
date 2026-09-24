import React, { useEffect, useRef, useState } from 'react';
import * as alphaTab from '@coderline/alphatab';
import { TabScore } from '../../types';
import { convertTabScoreToAlphaTex } from '../../audio/alphaTexConverter';
import { ZoomIn, ZoomOut, FileText, Music, CheckCircle2 } from 'lucide-react';

interface AlphaTabViewerProps {
  score: TabScore;
  isPlaying: boolean;
  tempo: number;
  onPlayStateChanged: (playing: boolean) => void;
}

export const AlphaTabViewer: React.FC<AlphaTabViewerProps> = ({
  score,
  isPlaying,
  tempo,
  onPlayStateChanged,
}) => {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const viewportRef = useRef<HTMLDivElement | null>(null);
  const apiRef = useRef<alphaTab.AlphaTabApi | null>(null);

  const [zoomScale, setZoomScale] = useState<number>(1.0);
  const [staveProfile, setStaveProfile] = useState<alphaTab.StaveProfile>(alphaTab.StaveProfile.Tab);
  const [isScoreLoaded, setIsScoreLoaded] = useState(false);
  const [isSoundFontReady, setIsSoundFontReady] = useState(false);

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

      const Color = (settings.display.resources.staffLineColor.constructor as any);
      settings.display.resources.staffLineColor = Color.fromJson('#3f3f46');
      settings.display.resources.barSeparatorColor = Color.fromJson('#52525b');
      settings.display.resources.barNumberColor = Color.fromJson('#f59e0b');
      settings.display.resources.mainGlyphColor = Color.fromJson('#f4f4f5');
      settings.display.resources.secondaryGlyphColor = Color.fromJson('#a1a1aa');
      settings.display.resources.scoreInfoColor = Color.fromJson('#f59e0b');

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
      console.error('Failed to init alphaTab viewer:', err);
    }
  }, []);

  // Update AlphaTex on score or tempo change
  useEffect(() => {
    const api = apiRef.current;
    if (!api || !score) return;

    try {
      setIsScoreLoaded(false);
      const scoreWithTempo = { ...score, tempo };
      const tex = convertTabScoreToAlphaTex(scoreWithTempo);
      api.tex(tex);
    } catch (err) {
      console.error('Failed to load AlphaTex score:', err);
    }
  }, [score.id, tempo]);

  // Synchronize isPlaying
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

  const handleZoom = (delta: number) => {
    const next = Math.max(0.7, Math.min(1.7, Math.round((zoomScale + delta) * 100) / 100));
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
      className="w-full bg-[#0a0c10] border border-zinc-800 rounded-2xl p-4 relative overflow-hidden"
    >
      {/* Floating Toolbar */}
      <div className="flex items-center justify-between mb-3 pb-2 border-b border-zinc-800/80">
        <div className="flex items-center space-x-2">
          <div className="flex items-center space-x-1.5 text-xs text-zinc-300">
            <span className="w-2 h-2 rounded-full bg-amber-400 animate-pulse" />
            <span className="font-bold text-amber-400">alphaTab Engine</span>
          </div>
          {isSoundFontReady && (
            <div className="flex items-center space-x-1 text-[10px] text-emerald-400 bg-emerald-950/60 border border-emerald-800/50 px-2 py-0.5 rounded-full">
              <CheckCircle2 className="w-3 h-3" />
              <span>HQ SoundFont</span>
            </div>
          )}
        </div>

        <div className="flex items-center space-x-2">
          <button
            onClick={handleToggleStave}
            className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-zinc-900 border border-zinc-800 text-zinc-300 hover:text-white flex items-center space-x-1"
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

          <div className="flex items-center bg-zinc-900 border border-zinc-800 rounded-lg overflow-hidden">
            <button
              onClick={() => handleZoom(-0.15)}
              className="p-1 text-zinc-400 hover:text-zinc-100"
            >
              <ZoomOut className="w-3.5 h-3.5" />
            </button>
            <span className="px-1.5 text-[10px] font-mono text-zinc-400">
              {Math.round(zoomScale * 100)}%
            </span>
            <button
              onClick={() => handleZoom(0.15)}
              className="p-1 text-zinc-400 hover:text-zinc-100"
            >
              <ZoomIn className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>

      {/* Render Host */}
      <div ref={containerRef} className="alphatab-surface w-full min-h-[300px]" />
    </div>
  );
};
