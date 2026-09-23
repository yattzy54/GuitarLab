import React, { useState, useEffect, useRef } from 'react';
import {
  Mic,
  Square,
  Play,
  Pause,
  Download,
  Trash2,
  Music2,
  Calendar,
  Volume2,
  Radio,
} from 'lucide-react';
import { RiffRecording, Tuning } from '../../types';
import { loadRiffs, saveRiffs } from '../../data/storage';
import { AudioRecorderEngine } from '../../audio/audioRecorder';
import { ensureAudioContextStarted } from '../../audio/audioContext';
import { Studio3DBadge, StudioCard, StudioPill } from '../common/Studio3DComponents';

interface RiffRecorderScreenProps {
  activeTuning: Tuning;
}

export const RiffRecorderScreen: React.FC<RiffRecorderScreenProps> = ({ activeTuning }) => {
  const [isRecording, setIsRecording] = useState(false);
  const [recordingDurationSec, setRecordingDurationSec] = useState(0);
  const [volumeLevel, setVolumeLevel] = useState(0);
  const [riffs, setRiffs] = useState<RiffRecording[]>([]);
  const [title, setTitle] = useState('');
  const [bpm, setBpm] = useState('120');
  const [playingRiffId, setPlayingRiffId] = useState<string | null>(null);

  const recorderRef = useRef<AudioRecorderEngine | null>(null);
  const timerRef = useRef<number | null>(null);
  const audioPlayerRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
    setRiffs(loadRiffs());
  }, []);

  const updateRiffs = (newRiffs: RiffRecording[]) => {
    setRiffs(newRiffs);
    saveRiffs(newRiffs);
  };

  const startRecording = async () => {
    try {
      await ensureAudioContextStarted();
      const recorder = new AudioRecorderEngine();
      recorderRef.current = recorder;
      setRecordingDurationSec(0);

      await recorder.startRecording((vol) => {
        setVolumeLevel(vol);
      });

      setIsRecording(true);
      timerRef.current = window.setInterval(() => {
        setRecordingDurationSec((prev) => prev + 1);
      }, 1000);
    } catch (err) {
      console.error('Failed to start recording:', err);
    }
  };

  const stopRecording = async () => {
    if (!recorderRef.current || !isRecording) return;

    if (timerRef.current !== null) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }

    const audioBlob = await recorderRef.current.stopRecording();
    const audioUrl = URL.createObjectURL(audioBlob);
    const durationMs = recordingDurationSec * 1000;
    setIsRecording(false);
    setVolumeLevel(0);

    const newRiff: RiffRecording = {
      id: `riff_${Date.now()}`,
      title: title.trim() || `Riff Memo #${riffs.length + 1}`,
      timestamp: Date.now(),
      durationMs,
      bpm: parseInt(bpm, 10) || 120,
      tuningName: activeTuning.name,
      audioBlobUrl: audioUrl,
    };

    updateRiffs([newRiff, ...riffs]);
    setTitle('');
  };

  const handlePlayRiff = (riff: RiffRecording) => {
    if (playingRiffId === riff.id) {
      audioPlayerRef.current?.pause();
      setPlayingRiffId(null);
    } else {
      if (audioPlayerRef.current) {
        audioPlayerRef.current.pause();
      }
      if (riff.audioBlobUrl) {
        const audio = new Audio(riff.audioBlobUrl);
        audio.onended = () => setPlayingRiffId(null);
        audio.play().catch(console.error);
        audioPlayerRef.current = audio;
        setPlayingRiffId(riff.id);
      }
    }
  };

  const handleDeleteRiff = (id: string) => {
    if (playingRiffId === id) {
      audioPlayerRef.current?.pause();
      setPlayingRiffId(null);
    }
    updateRiffs(riffs.filter((r) => r.id !== id));
  };

  const formatSeconds = (sec: number) => {
    const mins = Math.floor(sec / 60);
    const s = sec % 60;
    return `${mins}:${s.toString().padStart(2, '0')}`;
  };

  const formatDate = (timestamp: number) => {
    return new Date(timestamp).toLocaleDateString(undefined, {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <div className="max-w-xl mx-auto px-4 py-6 space-y-6">
      {/* Top Header Card */}
      <StudioCard>
        <div className="p-4 sm:p-5 flex items-center justify-between">
          <div className="flex items-center space-x-3.5">
            <Studio3DBadge icon={Radio} accent="ruby" size="lg" />
            <div>
              <h2 className="text-xl font-bold text-white tracking-tight">Riff Quick Recorder</h2>
              <p className="text-xs text-zinc-400">Microphone Memo & Guitar Idea Capturer</p>
            </div>
          </div>
          <div className="px-3 py-1 rounded-full bg-[#182030] border border-[#27344D] text-amber-400 font-bold text-xs">
            {activeTuning.name}
          </div>
        </div>
      </StudioCard>

      {/* Main Studio Cassette Deck Card */}
      <StudioCard glow={isRecording ? 'ruby' : null}>
        <div className="p-6 sm:p-8 flex flex-col items-center">
          {/* Inputs Row: Title + BPM */}
          <div className="w-full grid grid-cols-3 gap-3 mb-6">
            <div className="col-span-2">
              <label className="text-[11px] font-bold text-zinc-400 uppercase tracking-wider block mb-1.5">
                Riff Title
              </label>
              <input
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="e.g. Led Zep style intro"
                className="w-full px-3.5 py-2.5 rounded-xl bg-[#161D2B] border border-[#263146] text-zinc-100 text-xs sm:text-sm font-medium focus:outline-hidden focus:border-rose-500 transition-colors placeholder:text-zinc-500"
              />
            </div>
            <div>
              <label className="text-[11px] font-bold text-zinc-400 uppercase tracking-wider block mb-1.5">
                BPM
              </label>
              <input
                type="number"
                value={bpm}
                onChange={(e) => setBpm(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-xl bg-[#161D2B] border border-[#263146] text-amber-400 text-xs sm:text-sm font-mono font-bold focus:outline-hidden focus:border-amber-500 transition-colors"
              />
            </div>
          </div>

          {/* Recording Timer Display */}
          <div className="text-center mb-6">
            <span
              className={`text-6xl font-black font-mono tracking-tight transition-colors ${
                isRecording ? 'text-rose-500 drop-shadow-[0_0_15px_rgba(244,63,94,0.6)]' : 'text-zinc-100'
              }`}
            >
              {formatSeconds(recordingDurationSec)}
            </span>
            <span
              className={`block text-xs font-bold uppercase tracking-widest mt-1 ${
                isRecording ? 'text-rose-400 animate-pulse' : 'text-zinc-500'
              }`}
            >
              {isRecording ? 'RECORDING IN PROGRESS' : 'STANDBY · READY'}
            </span>
          </div>

          {/* Real-time sound level meter */}
          <div className="w-full max-w-xs h-2 bg-[#182030] rounded-full overflow-hidden border border-[#28354E] mb-8">
            <div
              className="bg-gradient-to-r from-emerald-400 via-amber-400 to-rose-500 h-full transition-all duration-75"
              style={{ width: `${Math.min(100, volumeLevel * 100)}%` }}
            />
          </div>

          {/* Master 3D Record / Stop Hero Button */}
          <button
            onClick={isRecording ? stopRecording : startRecording}
            className={`w-20 h-20 rounded-full flex items-center justify-center transition-all transform active:scale-95 cursor-pointer border ${
              isRecording
                ? 'bg-gradient-to-b from-rose-500 via-red-600 to-red-700 text-white shadow-[0_6px_24px_rgba(239,68,68,0.6),inset_0_1px_1px_rgba(255,255,255,0.4)] border-rose-300/50 ring-4 ring-rose-500/30 animate-pulse'
                : 'bg-gradient-to-b from-rose-600 to-red-700 text-white shadow-[0_6px_20px_rgba(225,29,72,0.4),inset_0_1px_1px_rgba(255,255,255,0.4)] border-rose-400/40 hover:brightness-110'
            }`}
          >
            {isRecording ? (
              <Square className="w-7 h-7 fill-current" />
            ) : (
              <Mic className="w-8 h-8" />
            )}
          </button>
        </div>
      </StudioCard>

      {/* Saved Riffs List */}
      <StudioCard>
        <div className="p-5 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-xs font-bold text-zinc-400 uppercase tracking-wider">
              Saved Riff Memos ({riffs.length})
            </h3>
          </div>

          {riffs.length === 0 ? (
            <div className="p-8 rounded-xl bg-[#141A26] border border-[#232D3F] text-center text-zinc-400 text-sm">
              No guitar riff recordings yet. Tap the record button above to capture ideas!
            </div>
          ) : (
            <div className="space-y-2.5">
              {riffs.map((riff) => {
                const isPlayingThis = playingRiffId === riff.id;
                return (
                  <div
                    key={riff.id}
                    className={`p-3.5 rounded-xl border flex items-center justify-between gap-3 transition-colors ${
                      isPlayingThis
                        ? 'bg-amber-500/10 border-amber-500/40'
                        : 'bg-[#151C2A] border-[#242F44] hover:border-[#303E58]'
                    }`}
                  >
                    <div className="flex items-center space-x-3 min-w-0">
                      <Studio3DBadge
                        icon={isPlayingThis ? Pause : Play}
                        accent={isPlayingThis ? 'amber' : 'slate'}
                        size="md"
                        onClick={() => handlePlayRiff(riff)}
                      />
                      <div className="min-w-0">
                        <h4 className="text-sm font-bold text-white truncate">{riff.title}</h4>
                        <div className="flex flex-wrap items-center gap-x-2.5 gap-y-0.5 text-xs text-zinc-400 font-mono mt-0.5">
                          <span className="text-teal-400 font-semibold">{riff.tuningName}</span>
                          <span>{riff.bpm} BPM</span>
                          <span>{formatSeconds(Math.round(riff.durationMs / 1000))}</span>
                          <span className="text-zinc-500">{formatDate(riff.timestamp)}</span>
                        </div>
                      </div>
                    </div>

                    <div className="flex items-center space-x-1 shrink-0">
                      {riff.audioBlobUrl && (
                        <a
                          href={riff.audioBlobUrl}
                          download={`${riff.title.replace(/\s+/g, '_')}.webm`}
                          className="p-2 rounded-lg text-zinc-400 hover:text-white hover:bg-[#20293B] transition-colors"
                          title="Download Audio"
                        >
                          <Download className="w-4 h-4" />
                        </a>
                      )}
                      <button
                        onClick={() => handleDeleteRiff(riff.id)}
                        className="p-2 rounded-lg text-zinc-500 hover:text-rose-400 hover:bg-[#20293B] transition-colors cursor-pointer"
                        title="Delete Riff"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </StudioCard>
    </div>
  );
};
