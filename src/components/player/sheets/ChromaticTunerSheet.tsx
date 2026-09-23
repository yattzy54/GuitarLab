import React, { useState, useEffect, useRef } from 'react';
import { ModalBottomSheet } from './ModalBottomSheet';
import { Mic, MicOff, Volume2, VolumeX, CheckCircle2 } from 'lucide-react';
import { YinPitchDetector, pitchFromFrequency } from '../../../audio/yinPitchDetector';
import { ensureAudioContextStarted } from '../../../audio/audioContext';
import { playGuitarPluck, playReferenceTone, stopReferenceTone } from '../../../audio/guitarSynth';
import { DetectedPitch } from '../../../types';
import { midiToHz } from '../../../data/defaultTunings';

interface ChromaticTunerSheetProps {
  isOpen: boolean;
  onClose: () => void;
  targetTuningName?: string;
  targetTuningNotes?: string[]; // e.g. ["D4", "A3", "F3", "C3", "G2", "C2"]
}

export const ChromaticTunerSheet: React.FC<ChromaticTunerSheetProps> = ({
  isOpen,
  onClose,
  targetTuningName = 'Standard E',
  targetTuningNotes = ['E4', 'B3', 'G3', 'D3', 'A2', 'E2'],
}) => {
  const [isListening, setIsListening] = useState(false);
  const [detectedPitch, setDetectedPitch] = useState<DetectedPitch | null>(null);
  const [playingNoteIndex, setPlayingNoteIndex] = useState<number | null>(null);

  const audioStreamRef = useRef<MediaStream | null>(null);
  const analyserRef = useRef<AnalyserNode | null>(null);
  const animFrameRef = useRef<number | null>(null);
  const detectorRef = useRef<YinPitchDetector | null>(null);

  const stopMic = () => {
    if (animFrameRef.current !== null) {
      cancelAnimationFrame(animFrameRef.current);
      animFrameRef.current = null;
    }
    if (audioStreamRef.current) {
      audioStreamRef.current.getTracks().forEach((t) => t.stop());
      audioStreamRef.current = null;
    }
    analyserRef.current = null;
    setIsListening(false);
  };

  const startMic = async () => {
    try {
      const audioCtx = await ensureAudioContextStarted();
      const stream = await navigator.mediaDevices.getUserMedia({
        audio: { echoCancellation: false, noiseSuppression: false, autoGainControl: false },
      });
      audioStreamRef.current = stream;

      const source = audioCtx.createMediaStreamSource(stream);
      const analyser = audioCtx.createAnalyser();
      analyser.fftSize = 2048;
      source.connect(analyser);
      analyserRef.current = analyser;

      detectorRef.current = new YinPitchDetector(audioCtx.sampleRate, analyser.fftSize, 0.12);
      const buffer = new Float32Array(analyser.fftSize);

      const loop = () => {
        if (!analyserRef.current || !detectorRef.current) return;
        analyserRef.current.getFloatTimeDomainData(buffer);

        let sum = 0;
        for (let i = 0; i < buffer.length; i++) sum += buffer[i] * buffer[i];
        const rms = Math.sqrt(sum / buffer.length);

        if (rms > 0.015) {
          const [freq, clarity] = detectorRef.current.detect(buffer);
          if (freq > 40 && freq < 1200 && clarity > 0.6) {
            const pitch = pitchFromFrequency(freq, 440, clarity);
            if (pitch) {
              setDetectedPitch(pitch);
            }
          }
        }
        animFrameRef.current = requestAnimationFrame(loop);
      };

      setIsListening(true);
      loop();
    } catch (err) {
      console.warn('Microphone permission or hardware error:', err);
      setIsListening(false);
    }
  };

  useEffect(() => {
    if (!isOpen) {
      stopMic();
      stopReferenceTone();
      setPlayingNoteIndex(null);
    }
    return () => {
      stopMic();
      stopReferenceTone();
    };
  }, [isOpen]);

  const handlePlayString = (idx: number, noteStr: string) => {
    if (playingNoteIndex === idx) {
      stopReferenceTone();
      setPlayingNoteIndex(null);
    } else {
      setPlayingNoteIndex(idx);
      // Rough midi calculation from string note name (e.g. "D4", "C2")
      const noteLetter = noteStr.slice(0, -1);
      const octave = parseInt(noteStr.slice(-1), 10) || 3;
      const noteMap: Record<string, number> = {
        C: 0, 'C#': 1, Db: 1, D: 2, 'D#': 3, Eb: 3, E: 4, F: 5,
        'F#': 6, Gb: 6, G: 7, 'G#': 8, Ab: 8, A: 9, 'A#': 10, Bb: 10, B: 11
      };
      const midi = (octave + 1) * 12 + (noteMap[noteLetter] ?? 0);
      const freq = midiToHz(midi);

      playGuitarPluck(freq, 2.5, 0.9);
      playReferenceTone(freq, 0.25);

      setTimeout(() => {
        setPlayingNoteIndex((prev) => (prev === idx ? null : prev));
        stopReferenceTone();
      }, 3000);
    }
  };

  const cents = detectedPitch ? Math.max(-50, Math.min(50, detectedPitch.cents)) : 0;
  const rotationDeg = (cents / 50) * 45;

  return (
    <ModalBottomSheet
      isOpen={isOpen}
      onClose={onClose}
      title="Хроматический тюнер"
      subtitle={`Текущий целевой строй трека: ${targetTuningName}`}
    >
      {/* Target Tuning Display Header */}
      <div className="bg-zinc-950/70 border border-zinc-800 rounded-2xl p-3 flex items-center justify-between">
        <div>
          <span className="text-[11px] font-semibold text-zinc-400 uppercase tracking-wider block">
            Целевой строй песни
          </span>
          <span className="text-sm font-bold text-amber-400 font-mono">
            {targetTuningName}
          </span>
        </div>
        <div className="text-xs font-mono text-zinc-300">
          {(targetTuningNotes || []).map((n) => n.replace(/[0-9]/g, '')).join(' · ')}
        </div>
      </div>

      {/* Cents Scale Visualizer (-50 to +50) */}
      <div className="bg-zinc-950 border border-zinc-800 rounded-2xl p-4 flex flex-col items-center justify-center relative overflow-hidden">
        {/* In-Tune Glow */}
        {detectedPitch?.inTune && (
          <div className="absolute inset-0 bg-emerald-500/10 backdrop-blur-3xl animate-pulse pointer-events-none" />
        )}

        {/* Gauge Arc */}
        <div className="relative w-64 h-36 flex items-center justify-center">
          <svg className="w-full h-full" viewBox="0 0 200 120">
            {/* Background Arch */}
            <path
              d="M 25 105 A 75 75 0 0 1 175 105"
              fill="none"
              stroke="#27272a"
              strokeWidth="9"
              strokeLinecap="round"
            />
            {/* Sweet Spot Center Arch */}
            <path
              d="M 94 30 A 75 75 0 0 1 106 30"
              fill="none"
              stroke={detectedPitch?.inTune ? '#10b981' : '#3f3f46'}
              strokeWidth="11"
              strokeLinecap="round"
            />

            {/* Ticks */}
            {[-50, -30, -15, 0, 15, 30, 50].map((c) => {
              const rad = ((c / 50) * 45 - 90) * (Math.PI / 180);
              const x1 = 100 + 70 * Math.cos(rad);
              const y1 = 105 + 70 * Math.sin(rad);
              const x2 = 100 + 80 * Math.cos(rad);
              const y2 = 105 + 80 * Math.sin(rad);
              return (
                <line
                  key={c}
                  x1={x1}
                  y1={y1}
                  x2={x2}
                  y2={y2}
                  stroke={c === 0 ? '#10b981' : '#52525b'}
                  strokeWidth={c === 0 ? '2.5' : '1.5'}
                />
              );
            })}

            {/* Needle */}
            <g
              transform={`rotate(${rotationDeg} 100 105)`}
              className="transition-transform duration-75 ease-out"
            >
              <line
                x1="100"
                y1="105"
                x2="100"
                y2="35"
                stroke={detectedPitch?.inTune ? '#10b981' : '#f59e0b'}
                strokeWidth="3"
                strokeLinecap="round"
              />
              <circle cx="100" cy="105" r="5" fill="#f59e0b" />
            </g>
          </svg>

          {/* Cents read-out badge */}
          <div className="absolute bottom-1">
            <span
              className={`text-xs font-mono font-bold px-2.5 py-0.5 rounded-full ${
                detectedPitch?.inTune
                  ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/40'
                  : 'bg-zinc-800 text-zinc-300'
              }`}
            >
              {detectedPitch ? `${detectedPitch.cents > 0 ? '+' : ''}${Math.round(detectedPitch.cents)} cents` : '0 cents'}
            </span>
          </div>
        </div>

        {/* Note Detected Big Typography */}
        <div className="text-center mt-2">
          <div className="flex items-baseline justify-center space-x-1">
            <span
              className={`text-5xl font-black tracking-tight ${
                detectedPitch?.inTune ? 'text-emerald-400' : detectedPitch ? 'text-white' : 'text-zinc-600'
              }`}
            >
              {detectedPitch ? detectedPitch.noteName : '—'}
            </span>
            <span className="text-xl font-bold text-zinc-400">
              {detectedPitch ? detectedPitch.octave : ''}
            </span>
          </div>

          <div className="text-xs font-mono text-zinc-400 mt-0.5">
            {detectedPitch ? `${detectedPitch.frequencyHz.toFixed(1)} Hz` : 'Включите микрофон или выберите струну'}
          </div>
        </div>

        {/* Mic Toggle Button */}
        <div className="mt-4">
          <button
            onClick={isListening ? stopMic : startMic}
            className={`px-5 py-2 rounded-xl text-xs font-bold flex items-center space-x-2 transition-all ${
              isListening
                ? 'bg-rose-600 text-white'
                : 'bg-amber-500 hover:bg-amber-400 text-zinc-950 shadow-md shadow-amber-500/20'
            }`}
          >
            {isListening ? (
              <>
                <MicOff className="w-3.5 h-3.5" />
                <span>Остановить микрофон</span>
              </>
            ) : (
              <>
                <Mic className="w-3.5 h-3.5" />
                <span>Включить микрофон</span>
              </>
            )}
          </button>
        </div>
      </div>

      {/* Target Strings Tone Generator */}
      <div className="space-y-2">
        <label className="text-xs font-semibold text-zinc-400 uppercase tracking-wider block">
          Настройка на слух (Струны {targetTuningName})
        </label>
        <div className="grid grid-cols-3 sm:grid-cols-6 gap-2">
          {(targetTuningNotes || []).map((note, idx) => {
            const isPlayingThis = playingNoteIndex === idx;
            return (
              <button
                key={idx}
                onClick={() => handlePlayString(idx, note)}
                className={`p-2.5 rounded-xl border flex flex-col items-center justify-center transition-all ${
                  isPlayingThis
                    ? 'bg-amber-500/20 border-amber-400 text-amber-300 scale-105 shadow-md shadow-amber-500/20'
                    : 'bg-zinc-800/70 border-zinc-700 text-zinc-200 hover:bg-zinc-800 hover:text-white'
                }`}
              >
                <span className="text-[10px] text-zinc-400 font-mono">Стр {idx + 1}</span>
                <span className="text-base font-bold text-white mt-0.5">{note}</span>
                <Volume2 className="w-3 h-3 text-amber-400 mt-1" />
              </button>
            );
          })}
        </div>
      </div>
    </ModalBottomSheet>
  );
};
