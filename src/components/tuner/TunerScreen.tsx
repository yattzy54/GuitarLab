import React, { useState, useEffect, useRef } from 'react';
import {
  Mic,
  MicOff,
  Volume2,
  VolumeX,
  Settings2,
  RefreshCw,
  Music2,
  Activity,
  CheckCircle2,
} from 'lucide-react';
import { Tuning, DetectedPitch, TuningNote } from '../../types';
import { getAllTunings } from '../../data/defaultTunings';
import { YinPitchDetector, pitchFromFrequency } from '../../audio/yinPitchDetector';
import { getAudioContext, ensureAudioContextStarted } from '../../audio/audioContext';
import { playGuitarPluck, playReferenceTone, stopReferenceTone } from '../../audio/guitarSynth';
import { Studio3DBadge, StudioCard, StudioPill } from '../common/Studio3DComponents';

interface TunerScreenProps {
  activeTuning: Tuning;
  onTuningChange: (tuning: Tuning) => void;
  a4Pitch: number;
  onA4Change: (a4: number) => void;
}

export const TunerScreen: React.FC<TunerScreenProps> = ({
  activeTuning,
  onTuningChange,
  a4Pitch,
  onA4Change,
}) => {
  const [isListening, setIsListening] = useState(false);
  const [micPermissionDenied, setMicPermissionDenied] = useState(false);
  const [detectedPitch, setDetectedPitch] = useState<DetectedPitch | null>(null);
  const [playingStringIndex, setPlayingStringIndex] = useState<number | null>(null);
  const [showSettings, setShowSettings] = useState(false);

  const audioStreamRef = useRef<MediaStream | null>(null);
  const analyserRef = useRef<AnalyserNode | null>(null);
  const animationFrameRef = useRef<number | null>(null);
  const detectorRef = useRef<YinPitchDetector | null>(null);
  const smoothedCentsRef = useRef(0);

  const tunings = getAllTunings(a4Pitch);

  const startTuning = async () => {
    try {
      const audioCtx = await ensureAudioContextStarted();
      const stream = await navigator.mediaDevices.getUserMedia({
        audio: {
          echoCancellation: false,
          autoGainControl: false,
          noiseSuppression: false,
        },
      });

      audioStreamRef.current = stream;
      setMicPermissionDenied(false);

      const source = audioCtx.createMediaStreamSource(stream);
      const analyser = audioCtx.createAnalyser();
      analyser.fftSize = 4096;
      source.connect(analyser);
      analyserRef.current = analyser;

      detectorRef.current = new YinPitchDetector(audioCtx.sampleRate, analyser.fftSize, 0.18);

      const buffer = new Float32Array(analyser.fftSize);
      const normBuffer = new Float32Array(analyser.fftSize);
      let lastPitch: DetectedPitch | null = null;
      let lastDetectTime = 0;

      const processAudio = () => {
        if (!analyserRef.current || !detectorRef.current) return;

        analyserRef.current.getFloatTimeDomainData(buffer);

        let sumSquares = 0;
        let maxAbs = 0;
        for (let i = 0; i < buffer.length; i++) {
          const s = buffer[i];
          sumSquares += s * s;
          const a = Math.abs(s);
          if (a > maxAbs) maxAbs = a;
        }
        const rms = Math.sqrt(sumSquares / buffer.length);
        const now = performance.now();

        // High-sensitivity gate for quiet electric guitars
        if (rms >= 0.0004 && maxAbs >= 0.0003) {
          // Adaptive gain boost up to 80x
          const gain = Math.min(80, Math.max(1, 0.75 / maxAbs));
          for (let i = 0; i < buffer.length; i++) {
            normBuffer[i] = Math.max(-1, Math.min(1, buffer[i] * gain));
          }

          const [frequencyHz, clarity] = detectorRef.current.detect(normBuffer);
          if (frequencyHz >= 30 && frequencyHz <= 1500 && clarity >= 0.42) {
            const pitch = pitchFromFrequency(frequencyHz, a4Pitch, clarity);
            if (pitch) {
              if (lastPitch && lastPitch.midiNote === pitch.midiNote) {
                smoothedCentsRef.current = smoothedCentsRef.current * 0.7 + pitch.cents * 0.3;
              } else {
                smoothedCentsRef.current = pitch.cents;
              }
              pitch.cents = smoothedCentsRef.current;
              lastPitch = pitch;
              lastDetectTime = now;
              setDetectedPitch(pitch);
            }
          } else if (now - lastDetectTime > 320) {
            setDetectedPitch(null);
          }
        } else if (now - lastDetectTime > 320) {
          setDetectedPitch(null);
        }

        animationFrameRef.current = requestAnimationFrame(processAudio);
      };

      setIsListening(true);
      processAudio();
    } catch (err) {
      console.error('Microphone access failed:', err);
      setMicPermissionDenied(true);
      setIsListening(false);
    }
  };

  const stopTuning = () => {
    if (animationFrameRef.current !== null) {
      cancelAnimationFrame(animationFrameRef.current);
      animationFrameRef.current = null;
    }
    if (audioStreamRef.current) {
      audioStreamRef.current.getTracks().forEach((track) => track.stop());
      audioStreamRef.current = null;
    }
    analyserRef.current = null;
    setIsListening(false);
  };

  useEffect(() => {
    return () => {
      stopTuning();
      stopReferenceTone();
    };
  }, []);

  const findClosestString = (pitch: DetectedPitch | null): TuningNote | null => {
    if (!pitch) return null;
    let closestNote: TuningNote | null = null;
    let minDiff = Number.MAX_VALUE;
    for (const note of activeTuning.notes) {
      const diff = Math.abs(note.targetFrequencyHz - pitch.frequencyHz);
      if (diff < minDiff) {
        minDiff = diff;
        closestNote = note;
      }
    }
    return closestNote;
  };

  const closestString = findClosestString(detectedPitch);

  const handlePlayString = (stringIndex: number, note: TuningNote) => {
    if (playingStringIndex === stringIndex) {
      stopReferenceTone();
      setPlayingStringIndex(null);
    } else {
      setPlayingStringIndex(stringIndex);
      playGuitarPluck(note.targetFrequencyHz, 3.0, 0.95);
      playReferenceTone(note.targetFrequencyHz, 0.3);
      setTimeout(() => {
        setPlayingStringIndex((prev) => (prev === stringIndex ? null : prev));
        stopReferenceTone();
      }, 3500);
    }
  };

  const cents = detectedPitch ? Math.max(-50, Math.min(50, detectedPitch.cents)) : 0;
  const isInTune = detectedPitch && Math.abs(cents) <= 3;
  const needleRotation = (cents / 50) * 45;

  return (
    <div className="max-w-xl mx-auto px-4 py-6 space-y-6">
      {/* Top Header with Tuning selector */}
      <StudioCard>
        <div className="p-4 sm:p-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="flex items-center space-x-3.5">
            <Studio3DBadge icon={Activity} accent="amber" size="lg" />
            <div>
              <h2 className="text-xl font-bold text-white tracking-tight">Chromatic Guitar Tuner</h2>
              <p className="text-xs text-zinc-400">YIN Pitch Engine · Precision ±1 Cent</p>
            </div>
          </div>

          <div className="flex items-center space-x-2 w-full sm:w-auto">
            <select
              value={activeTuning.id}
              onChange={(e) => {
                const selected = tunings.find((t) => t.id === e.target.value);
                if (selected) onTuningChange(selected);
              }}
              className="flex-1 sm:flex-none px-3.5 py-2 rounded-xl bg-[#161D2B] border border-[#27344D] text-zinc-100 text-xs sm:text-sm font-medium focus:outline-hidden focus:border-amber-500 transition-colors"
            >
              {tunings.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.name} ({t.category})
                </option>
              ))}
            </select>

            <button
              onClick={() => setShowSettings(!showSettings)}
              className={`p-2 rounded-xl border transition-colors cursor-pointer ${
                showSettings
                  ? 'bg-amber-500/20 text-amber-300 border-amber-500/40'
                  : 'bg-[#161D2B] text-zinc-400 border-[#27344D] hover:text-white'
              }`}
              title="Reference Pitch Settings"
            >
              <Settings2 className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Settings Bar */}
        {showSettings && (
          <div className="p-4 bg-[#0F131C] border-t border-[#222B3D] flex items-center justify-between text-xs">
            <span className="text-zinc-400">Reference Pitch A4:</span>
            <div className="flex items-center space-x-1.5">
              {[432, 440, 442, 444].map((freq) => (
                <StudioPill
                  key={freq}
                  label={`${freq} Hz`}
                  selected={a4Pitch === freq}
                  onClick={() => onA4Change(freq)}
                  accent="amber"
                  size="sm"
                />
              ))}
            </div>
          </div>
        )}
      </StudioCard>

      {/* Main Pitch Gauge Hero Card */}
      <StudioCard glow={isInTune ? 'green' : isListening ? 'amber' : null}>
        <div className="p-6 sm:p-8 flex flex-col items-center">
          {/* Target Note Display */}
          <div className="text-center mb-6">
            <div className="relative inline-flex items-center justify-center">
              <span
                className={`text-7xl font-black tracking-tight transition-colors ${
                  isInTune
                    ? 'text-emerald-400 drop-shadow-[0_0_20px_rgba(52,211,153,0.8)]'
                    : detectedPitch
                    ? 'text-white'
                    : 'text-zinc-500'
                }`}
              >
                {detectedPitch ? detectedPitch.noteName : closestString?.noteName || 'E'}
              </span>
              {detectedPitch && (
                <span className="text-xl font-bold text-amber-400 ml-1 mb-6">
                  {detectedPitch.octave}
                </span>
              )}
            </div>

            <div className="flex items-center justify-center gap-2 mt-1">
              <span className="text-xs font-mono text-zinc-400">
                {detectedPitch ? `${detectedPitch.frequencyHz.toFixed(1)} Hz` : 'Ready to listen'}
              </span>
              {isInTune && (
                <span className="px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-400 text-[10px] font-bold border border-emerald-500/40 flex items-center gap-1">
                  <CheckCircle2 className="w-3 h-3" /> PERFECT IN TUNE
                </span>
              )}
            </div>
          </div>

          {/* Cent Meter Arch & Needle */}
          <div className="relative w-full max-w-sm h-32 flex items-center justify-center overflow-hidden mb-6">
            {/* Cent Scale Ticks */}
            <div className="absolute inset-x-8 top-6 flex justify-between text-[10px] font-mono text-zinc-500">
              <span>-50</span>
              <span>-25</span>
              <span className="text-emerald-400 font-bold">0</span>
              <span>+25</span>
              <span>+50</span>
            </div>

            {/* Scale Gauge Track */}
            <div className="w-full h-3 bg-[#192233] rounded-full overflow-hidden border border-[#27344D] relative">
              {/* Perfect tune center notch */}
              <div className="absolute left-1/2 -translate-x-1/2 inset-y-0 w-3 bg-emerald-400/40 border-x border-emerald-400" />
            </div>

            {/* Glowing Indicator Needle */}
            <div
              className="absolute top-12 flex flex-col items-center transition-all duration-75 ease-out"
              style={{
                left: `${((cents + 50) / 100) * 100}%`,
                transform: 'translateX(-50%)',
              }}
            >
              <div
                className={`w-4 h-6 rounded-md shadow-lg ${
                  isInTune
                    ? 'bg-emerald-400 shadow-[0_0_12px_rgba(52,211,153,0.9)] ring-2 ring-white/60'
                    : Math.abs(cents) < 15
                    ? 'bg-amber-400 shadow-[0_0_10px_rgba(245,158,11,0.8)]'
                    : 'bg-rose-500 shadow-[0_0_8px_rgba(244,63,94,0.7)]'
                }`}
              />
              <span
                className={`text-[11px] font-mono font-bold mt-1 ${
                  isInTune ? 'text-emerald-400' : 'text-zinc-400'
                }`}
              >
                {cents > 0 ? `+${Math.round(cents)}` : Math.round(cents)}c
              </span>
            </div>
          </div>

          {/* Mic Toggle Button */}
          <button
            onClick={isListening ? stopTuning : startTuning}
            className={`px-6 py-3.5 rounded-2xl flex items-center space-x-2.5 font-bold text-xs sm:text-sm transition-all transform active:scale-95 cursor-pointer border ${
              isListening
                ? 'bg-gradient-to-b from-rose-500 via-red-600 to-red-700 text-white shadow-[0_4px_16px_rgba(239,68,68,0.4)] border-rose-400/50'
                : 'bg-gradient-to-b from-amber-400 via-amber-500 to-amber-600 text-zinc-950 shadow-[0_4px_16px_rgba(245,158,11,0.4)] border-amber-300/50'
            }`}
          >
            {isListening ? (
              <>
                <MicOff className="w-5 h-5" />
                <span>Pause Tuner</span>
              </>
            ) : (
              <>
                <Mic className="w-5 h-5" />
                <span>Start Tuner Mic</span>
              </>
            )}
          </button>

          {micPermissionDenied && (
            <div className="mt-4 p-3 rounded-xl bg-red-950/50 border border-red-500/50 text-red-200 text-xs text-center">
              Microphone access was denied. Please allow microphone permissions in your browser, or
              use the reference tone strings below.
            </div>
          )}
        </div>
      </StudioCard>

      {/* Interactive String Tone Generator (Tune By Ear) */}
      <StudioCard>
        <div className="p-5 space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <Volume2 className="w-4 h-4 text-amber-400" />
              <h3 className="text-xs font-bold text-zinc-400 uppercase tracking-wider">
                Reference Strings · Tune By Ear
              </h3>
            </div>
            {playingStringIndex !== null && (
              <button
                onClick={() => {
                  stopReferenceTone();
                  setPlayingStringIndex(null);
                }}
                className="text-xs px-2.5 py-1 rounded-lg bg-[#192233] text-zinc-300 hover:text-white flex items-center gap-1 cursor-pointer"
              >
                <VolumeX className="w-3.5 h-3.5" />
                <span>Mute</span>
              </button>
            )}
          </div>

          {/* Strings Grid */}
          <div className="grid grid-cols-3 sm:grid-cols-6 gap-2.5">
            {(activeTuning?.notes || []).map((note, index) => {
              const isPlaying = playingStringIndex === index;
              const isTarget = closestString?.stringNumber === note.stringNumber && detectedPitch;
              return (
                <button
                  key={note.stringNumber}
                  onClick={() => handlePlayString(index, note)}
                  className={`p-3.5 rounded-xl border flex flex-col items-center justify-center transition-all cursor-pointer ${
                    isPlaying
                      ? 'bg-amber-500/25 border-amber-400 text-amber-300 scale-105 shadow-md shadow-amber-500/20'
                      : isTarget
                      ? 'bg-[#1C2538] border-amber-500/60 text-white'
                      : 'bg-[#151C2A] border-[#253046] hover:bg-[#1E283D] text-zinc-300 hover:text-white'
                  }`}
                >
                  <div className="text-[11px] font-mono text-zinc-400">Str {note.stringNumber}</div>
                  <div className="text-xl font-black text-white mt-0.5">{note.noteName}</div>
                  <div className="text-[10px] text-zinc-400 mt-1">
                    {note.targetFrequencyHz.toFixed(1)} Hz
                  </div>
                  <div className="mt-2 w-full h-1 rounded-full bg-[#20293B] overflow-hidden">
                    <div
                      className={`h-full ${
                        isPlaying ? 'bg-amber-400 w-full animate-pulse' : 'bg-transparent w-0'
                      }`}
                    />
                  </div>
                </button>
              );
            })}
          </div>
        </div>
      </StudioCard>
    </div>
  );
};
