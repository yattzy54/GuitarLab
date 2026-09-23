import React, { useState, useRef, useEffect } from 'react';
import {
  Play,
  Pause,
  Upload,
  Repeat,
  RotateCcw,
  Volume2,
  FastForward,
  Rewind,
  Music,
  Bookmark,
  Sliders,
} from 'lucide-react';
import { ensureAudioContextStarted } from '../../audio/audioContext';
import { Studio3DBadge, StudioCard, StudioPill } from '../common/Studio3DComponents';

const PRESET_TRACKS = [
  {
    name: 'Slow Blues in A Backing Track',
    url: 'https://cdn.freesound.org/previews/563/563870_9594498-lq.mp3',
  },
  {
    name: 'Acoustic Folk Rhythm',
    url: 'https://cdn.freesound.org/previews/415/415804_5121236-lq.mp3',
  },
];

export const SlowDownerScreen: React.FC = () => {
  const [trackTitle, setTrackTitle] = useState<string>('Slow Blues in A Backing Track');
  const [isPlaying, setIsPlaying] = useState(false);
  const [duration, setDuration] = useState(0);
  const [currentTime, setCurrentTime] = useState(0);
  const [speed, setSpeed] = useState(1.0);
  const [loopA, setLoopA] = useState<number | null>(null);
  const [loopB, setLoopB] = useState<number | null>(null);
  const [loopEnabled, setLoopEnabled] = useState(true);

  const audioRef = useRef<HTMLAudioElement | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    const audio = new Audio(PRESET_TRACKS[0].url);
    audio.crossOrigin = 'anonymous';
    audio.preservesPitch = true;
    (audio as unknown as { webkitPreservesPitch?: boolean }).webkitPreservesPitch = true;
    audio.playbackRate = speed;

    audio.onloadedmetadata = () => {
      setDuration(audio.duration || 0);
    };

    audio.ontimeupdate = () => {
      const cur = audio.currentTime;
      setCurrentTime(cur);

      if (loopEnabled && loopA !== null && loopB !== null && loopB > loopA) {
        if (cur >= loopB || cur < loopA) {
          audio.currentTime = loopA;
        }
      }
    };

    audio.onended = () => {
      setIsPlaying(false);
    };

    audioRef.current = audio;

    return () => {
      audio.pause();
      audio.src = '';
    };
  }, []);

  const togglePlay = async () => {
    await ensureAudioContextStarted();
    if (!audioRef.current) return;

    if (isPlaying) {
      audioRef.current.pause();
      setIsPlaying(false);
    } else {
      audioRef.current.play().catch(console.error);
      setIsPlaying(true);
    }
  };

  const handleSeek = (e: React.ChangeEvent<HTMLInputElement>) => {
    const time = parseFloat(e.target.value);
    if (audioRef.current) {
      audioRef.current.currentTime = time;
      setCurrentTime(time);
    }
  };

  const handleSpeedChange = (newSpeed: number) => {
    setSpeed(newSpeed);
    if (audioRef.current) {
      audioRef.current.playbackRate = newSpeed;
    }
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file && audioRef.current) {
      const objectUrl = URL.createObjectURL(file);
      audioRef.current.pause();
      audioRef.current.src = objectUrl;
      audioRef.current.load();
      setTrackTitle(file.name.replace(/\.[^/.]+$/, ''));
      setIsPlaying(false);
      setLoopA(null);
      setLoopB(null);
    }
  };

  const setMarkerA = () => {
    setLoopA(currentTime);
  };

  const setMarkerB = () => {
    if (loopA !== null && currentTime <= loopA) {
      setLoopB(loopA + 2);
    } else {
      setLoopB(currentTime);
    }
  };

  const clearLoop = () => {
    setLoopA(null);
    setLoopB(null);
  };

  const skipSeconds = (seconds: number) => {
    if (audioRef.current) {
      audioRef.current.currentTime = Math.max(
        0,
        Math.min(duration, audioRef.current.currentTime + seconds)
      );
    }
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    const ms = Math.floor((seconds % 1) * 10);
    return `${mins}:${secs.toString().padStart(2, '0')}.${ms}`;
  };

  return (
    <div className="max-w-xl mx-auto px-4 py-6 space-y-6">
      {/* Top Header Card */}
      <StudioCard>
        <div className="p-4 sm:p-5 flex items-center justify-between">
          <div className="flex items-center space-x-3.5">
            <Studio3DBadge icon={Music} accent="teal" size="lg" />
            <div>
              <h2 className="text-xl font-bold text-white tracking-tight">Audio Slow-Downer</h2>
              <p className="text-xs text-zinc-400">Pitch-Preserved Playback & A-B Looper</p>
            </div>
          </div>
          <button
            onClick={() => fileInputRef.current?.click()}
            className="px-3.5 py-2 rounded-xl bg-gradient-to-b from-[#232C3E] to-[#151C2A] border border-[#2F3C55] text-teal-400 hover:text-white font-bold text-xs flex items-center space-x-2 shadow-[0_2px_8px_rgba(0,0,0,0.4)] transition-all active:scale-95 cursor-pointer"
          >
            <Upload className="w-3.5 h-3.5" />
            <span>Import Track</span>
          </button>
          <input
            ref={fileInputRef}
            type="file"
            accept="audio/*"
            onChange={handleFileUpload}
            className="hidden"
          />
        </div>
      </StudioCard>

      {/* Main Player Card */}
      <StudioCard glow={isPlaying ? 'teal' : null}>
        <div className="p-6 sm:p-8 flex flex-col items-center">
          {/* Current track title */}
          <div className="w-full text-center mb-6">
            <h3 className="font-bold text-lg text-white truncate">{trackTitle}</h3>
            <span className="text-xs text-teal-400 font-semibold tracking-wide">
              {speed.toFixed(2)}x Speed · Pitch Locked
            </span>
          </div>

          {/* Time Displays */}
          <div className="w-full flex items-center justify-between font-mono text-xs text-zinc-400 mb-2">
            <span className="text-teal-400 font-bold text-sm">{formatTime(currentTime)}</span>
            <span>{formatTime(duration)}</span>
          </div>

          {/* Track Scrubber with A-B Marker Overlays */}
          <div className="relative w-full mb-8">
            <input
              type="range"
              min={0}
              max={duration || 1}
              step={0.1}
              value={currentTime}
              onChange={handleSeek}
              className="w-full h-2 bg-[#1C2538] rounded-lg appearance-none cursor-pointer accent-teal-400 border border-[#27344D]"
            />

            {/* Marker A Tag */}
            {loopA !== null && duration > 0 && (
              <div
                className="absolute top-4 -translate-x-1/2 flex flex-col items-center"
                style={{ left: `${(loopA / duration) * 100}%` }}
              >
                <div className="w-2.5 h-2.5 rounded-full bg-emerald-400 ring-2 ring-emerald-500/50" />
                <span className="text-[10px] font-mono text-emerald-400 font-bold mt-0.5">A</span>
              </div>
            )}

            {/* Marker B Tag */}
            {loopB !== null && duration > 0 && (
              <div
                className="absolute top-4 -translate-x-1/2 flex flex-col items-center"
                style={{ left: `${(loopB / duration) * 100}%` }}
              >
                <div className="w-2.5 h-2.5 rounded-full bg-rose-400 ring-2 ring-rose-500/50" />
                <span className="text-[10px] font-mono text-rose-400 font-bold mt-0.5">B</span>
              </div>
            )}
          </div>

          {/* Transport Controls: Skip -5s, Hero Play/Pause 3D Button, Skip +5s */}
          <div className="flex items-center space-x-6">
            <button
              onClick={() => skipSeconds(-5)}
              className="p-3.5 rounded-2xl bg-gradient-to-b from-[#232C3E] to-[#151C2A] border border-[#2F3C55] text-zinc-300 hover:text-white shadow-[0_4px_10px_rgba(0,0,0,0.4)] active:scale-95 transition-all cursor-pointer"
              title="Rewind 5s"
            >
              <Rewind className="w-5 h-5" />
            </button>

            {/* Big 3D Glowing Hero Button */}
            <button
              onClick={togglePlay}
              className={`w-20 h-20 rounded-full flex items-center justify-center transition-all transform active:scale-95 cursor-pointer border ${
                isPlaying
                  ? 'bg-gradient-to-b from-rose-500 via-red-600 to-red-700 text-white shadow-[0_6px_24px_rgba(239,68,68,0.5),inset_0_1px_1px_rgba(255,255,255,0.4)] border-rose-400/50 ring-4 ring-rose-500/20'
                  : 'bg-gradient-to-b from-cyan-400 via-teal-500 to-teal-600 text-zinc-950 shadow-[0_6px_24px_rgba(20,184,166,0.5),inset_0_1px_1px_rgba(255,255,255,0.4)] border-cyan-300/50 ring-4 ring-teal-400/20'
              }`}
            >
              {isPlaying ? (
                <Pause className="w-8 h-8 fill-current" />
              ) : (
                <Play className="w-8 h-8 fill-current ml-1" />
              )}
            </button>

            <button
              onClick={() => skipSeconds(5)}
              className="p-3.5 rounded-2xl bg-gradient-to-b from-[#232C3E] to-[#151C2A] border border-[#2F3C55] text-zinc-300 hover:text-white shadow-[0_4px_10px_rgba(0,0,0,0.4)] active:scale-95 transition-all cursor-pointer"
              title="Fast Forward 5s"
            >
              <FastForward className="w-5 h-5" />
            </button>
          </div>
        </div>
      </StudioCard>

      {/* Speed Controls Card */}
      <StudioCard>
        <div className="p-5 space-y-4">
          <div className="flex items-center justify-between">
            <label className="text-xs font-bold text-zinc-400 uppercase tracking-wider">
              Playback Speed
            </label>
            <span className="font-mono text-base font-black text-amber-400">
              {speed.toFixed(2)}x
            </span>
          </div>

          <div className="flex flex-wrap gap-2">
            {[0.5, 0.65, 0.75, 0.85, 1.0, 1.25].map((s) => (
              <StudioPill
                key={s}
                label={`${s.toFixed(2)}x`}
                selected={Math.abs(speed - s) < 0.02}
                onClick={() => handleSpeedChange(s)}
                accent="amber"
                size="sm"
                className="flex-1 min-w-[60px]"
              />
            ))}
          </div>

          <input
            type="range"
            min={0.25}
            max={1.5}
            step={0.05}
            value={speed}
            onChange={(e) => handleSpeedChange(parseFloat(e.target.value))}
            className="w-full h-1.5 bg-[#1C2538] rounded-lg appearance-none cursor-pointer accent-amber-400 border border-[#27344D]"
          />
        </div>
      </StudioCard>

      {/* A-B Looper Controls Card */}
      <StudioCard>
        <div className="p-5 space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <Repeat className="w-4 h-4 text-emerald-400" />
              <h3 className="text-xs font-bold text-zinc-400 uppercase tracking-wider">
                A-B Loop Practice
              </h3>
            </div>
            <StudioPill
              label={loopEnabled ? 'Loop Active' : 'Loop Off'}
              selected={loopEnabled}
              onClick={() => setLoopEnabled(!loopEnabled)}
              accent="green"
              size="sm"
            />
          </div>

          <div className="flex items-center justify-between text-xs text-zinc-300 bg-[#141A26] p-3 rounded-xl border border-[#232D3F] font-mono">
            <div>
              <span className="text-emerald-400 font-bold">Marker A:</span>{' '}
              {loopA !== null ? formatTime(loopA) : 'Not set'}
            </div>
            <div>
              <span className="text-rose-400 font-bold">Marker B:</span>{' '}
              {loopB !== null ? formatTime(loopB) : 'Not set'}
            </div>
          </div>

          <div className="flex items-center gap-2">
            <StudioPill
              label="Set Marker A"
              selected={false}
              onClick={setMarkerA}
              accent="green"
              size="sm"
              className="flex-1"
            />
            <StudioPill
              label="Set Marker B"
              selected={false}
              onClick={setMarkerB}
              accent="ruby"
              size="sm"
              className="flex-1"
            />
            <StudioPill
              label="Clear Loop"
              selected={false}
              onClick={clearLoop}
              accent="amber"
              size="sm"
            />
          </div>
        </div>
      </StudioCard>
    </div>
  );
};
