import React, { useState, useEffect } from 'react';
import {
  Flame,
  Clock,
  Award,
  Plus,
  Trash2,
  Calendar,
  Filter,
  CheckCircle2,
  TrendingUp,
} from 'lucide-react';
import { PracticeSession, PracticeStats } from '../../types';
import { loadSessions, saveSessions, calculatePracticeStats } from '../../data/storage';
import { Studio3DBadge, StudioCard, StudioPill } from '../common/Studio3DComponents';

const CATEGORIES = [
  'Technique',
  'Repertoire',
  'Theory & Scales',
  'Song Practice',
  'Improv',
  'Metronome Speed',
];

export const PracticeTrackerScreen: React.FC = () => {
  const [sessions, setSessions] = useState<PracticeSession[]>([]);
  const [stats, setStats] = useState<PracticeStats>({
    streakDays: 1,
    totalMinutes: 0,
    sessionsCount: 0,
  });
  const [selectedCategoryFilter, setSelectedCategoryFilter] = useState<string>('All');
  const [showLogModal, setShowLogModal] = useState(false);

  // Form State for new session
  const [newDuration, setNewDuration] = useState('30');
  const [newCategory, setNewCategory] = useState(CATEGORIES[0]);
  const [newNotes, setNewNotes] = useState('');

  useEffect(() => {
    const loaded = loadSessions();
    setSessions(loaded);
    setStats(calculatePracticeStats(loaded));
  }, []);

  const updateAndSave = (updated: PracticeSession[]) => {
    setSessions(updated);
    saveSessions(updated);
    setStats(calculatePracticeStats(updated));
  };

  const handleSaveSession = (e: React.FormEvent) => {
    e.preventDefault();
    const duration = parseInt(newDuration, 10);
    if (!duration || duration <= 0) return;

    const newSession: PracticeSession = {
      id: `session_${Date.now()}`,
      dateMillis: Date.now(),
      durationMinutes: duration,
      category: newCategory,
      notes: newNotes.trim(),
    };

    updateAndSave([newSession, ...sessions]);
    setShowLogModal(false);
    setNewNotes('');
  };

  const handleDeleteSession = (id: string) => {
    updateAndSave(sessions.filter((s) => s.id !== id));
  };

  const filteredSessions =
    selectedCategoryFilter === 'All'
      ? sessions
      : sessions.filter((s) => s.category === selectedCategoryFilter);

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
      {/* Top 3D Stats Dashboard Cards (Streak, Total Minutes, Sessions) */}
      <div className="grid grid-cols-3 gap-3">
        {/* Streak Card */}
        <StudioCard glow={stats.streakDays > 0 ? 'amber' : null}>
          <div className="p-4 flex flex-col items-center text-center">
            <Studio3DBadge icon={Flame} accent="amber" size="md" />
            <span className="text-2xl font-black text-amber-400 mt-2 font-mono">
              {stats.streakDays}
            </span>
            <span className="text-[11px] font-bold text-zinc-400 uppercase tracking-wider mt-0.5">
              Day Streak
            </span>
          </div>
        </StudioCard>

        {/* Total Time Card */}
        <StudioCard>
          <div className="p-4 flex flex-col items-center text-center">
            <Studio3DBadge icon={Clock} accent="teal" size="md" />
            <span className="text-2xl font-black text-teal-400 mt-2 font-mono">
              {stats.totalMinutes}m
            </span>
            <span className="text-[11px] font-bold text-zinc-400 uppercase tracking-wider mt-0.5">
              Total Practice
            </span>
          </div>
        </StudioCard>

        {/* Total Sessions Card */}
        <StudioCard>
          <div className="p-4 flex flex-col items-center text-center">
            <Studio3DBadge icon={Award} accent="green" size="md" />
            <span className="text-2xl font-black text-emerald-400 mt-2 font-mono">
              {stats.sessionsCount}
            </span>
            <span className="text-[11px] font-bold text-zinc-400 uppercase tracking-wider mt-0.5">
              Sessions
            </span>
          </div>
        </StudioCard>
      </div>

      {/* Log Practice Session Action Card */}
      <StudioCard>
        <div
          onClick={() => setShowLogModal(true)}
          className="p-4 sm:p-5 flex items-center justify-between cursor-pointer hover:bg-white/[0.02] transition-colors"
        >
          <div className="flex items-center space-x-3.5">
            <Studio3DBadge icon={Plus} accent="amber" size="md" />
            <div>
              <h3 className="font-bold text-white text-base">Log Practice Session</h3>
              <p className="text-xs text-zinc-400">Record drills, exercises, scales and songs</p>
            </div>
          </div>
          <StudioPill
            label="Log Now"
            selected={true}
            onClick={() => setShowLogModal(true)}
            accent="amber"
            size="sm"
          />
        </div>
      </StudioCard>

      {/* Sessions History Card */}
      <StudioCard>
        <div className="p-5 space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <h3 className="text-xs font-bold text-zinc-400 uppercase tracking-wider">
              Practice Sessions Log ({filteredSessions.length})
            </h3>

            {/* Category Filter Pills */}
            <div className="flex flex-wrap gap-1.5">
              {['All', ...CATEGORIES.slice(0, 3)].map((cat) => (
                <StudioPill
                  key={cat}
                  label={cat}
                  selected={selectedCategoryFilter === cat}
                  onClick={() => setSelectedCategoryFilter(cat)}
                  accent="teal"
                  size="sm"
                />
              ))}
            </div>
          </div>

          {filteredSessions.length === 0 ? (
            <div className="p-8 rounded-xl bg-[#141A26] border border-[#232D3F] text-center text-zinc-400 text-sm">
              No sessions logged yet. Tap "Log Practice Session" above to start your streak!
            </div>
          ) : (
            <div className="space-y-2.5">
              {filteredSessions.map((session) => (
                <div
                  key={session.id}
                  className="p-3.5 rounded-xl bg-[#151C2A] border border-[#242F44] hover:border-[#303E58] flex items-center justify-between gap-3 transition-colors"
                >
                  <div className="min-w-0">
                    <div className="flex items-center space-x-2">
                      <span className="px-2 py-0.5 rounded-md bg-amber-500/15 border border-amber-500/30 text-amber-300 font-bold text-xs">
                        {session.category}
                      </span>
                      <span className="text-xs font-mono font-bold text-teal-400">
                        {session.durationMinutes} mins
                      </span>
                    </div>
                    {session.notes && (
                      <p className="text-sm text-zinc-200 mt-1 line-clamp-2">{session.notes}</p>
                    )}
                    <span className="text-[11px] text-zinc-500 mt-1 block">
                      {formatDate(session.dateMillis)}
                    </span>
                  </div>

                  <button
                    onClick={() => handleDeleteSession(session.id)}
                    className="p-2 rounded-lg text-zinc-500 hover:text-rose-400 hover:bg-[#20293B] transition-colors cursor-pointer shrink-0"
                    title="Delete Session"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      </StudioCard>

      {/* Log Modal Dialog */}
      {showLogModal && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-[#131722] border border-[#253046] rounded-2xl max-w-md w-full p-6 shadow-2xl shadow-black/80 space-y-5">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-bold text-white">Log Practice Session</h3>
              <button
                onClick={() => setShowLogModal(false)}
                className="text-zinc-400 hover:text-white text-sm"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleSaveSession} className="space-y-4">
              {/* Category */}
              <div>
                <label className="text-[11px] font-bold text-zinc-400 uppercase tracking-wider block mb-2">
                  Category
                </label>
                <div className="flex flex-wrap gap-1.5">
                  {CATEGORIES.map((cat) => (
                    <StudioPill
                      key={cat}
                      label={cat}
                      selected={newCategory === cat}
                      onClick={() => setNewCategory(cat)}
                      accent="amber"
                      size="sm"
                    />
                  ))}
                </div>
              </div>

              {/* Duration with quick presets */}
              <div>
                <div className="flex justify-between items-center mb-1.5">
                  <label className="text-[11px] font-bold text-zinc-400 uppercase tracking-wider">
                    Duration (Minutes)
                  </label>
                  <div className="flex space-x-1">
                    {[15, 30, 45, 60].map((m) => (
                      <button
                        key={m}
                        type="button"
                        onClick={() => setNewDuration(m.toString())}
                        className="px-2 py-0.5 rounded bg-[#1C2538] hover:bg-[#243048] text-zinc-300 text-[11px] font-mono cursor-pointer"
                      >
                        {m}m
                      </button>
                    ))}
                  </div>
                </div>
                <input
                  type="number"
                  min={1}
                  max={600}
                  value={newDuration}
                  onChange={(e) => setNewDuration(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl bg-[#161D2B] border border-[#263146] text-white font-mono text-sm focus:outline-hidden focus:border-amber-500"
                  required
                />
              </div>

              {/* Session Notes */}
              <div>
                <label className="text-[11px] font-bold text-zinc-400 uppercase tracking-wider block mb-1.5">
                  Session Notes / Milestones
                </label>
                <textarea
                  rows={3}
                  value={newNotes}
                  onChange={(e) => setNewNotes(e.target.value)}
                  placeholder="e.g. Practiced Dorian modal runs at 130 BPM; nailed barre chord transition."
                  className="w-full px-3.5 py-2.5 rounded-xl bg-[#161D2B] border border-[#263146] text-white text-sm focus:outline-hidden focus:border-teal-500"
                />
              </div>

              <div className="flex justify-end space-x-2 pt-2 border-t border-[#222B3D]">
                <button
                  type="button"
                  onClick={() => setShowLogModal(false)}
                  className="px-4 py-2 rounded-xl text-xs font-semibold bg-[#1C2538] text-zinc-300 hover:bg-[#243048] cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl text-xs font-bold bg-gradient-to-b from-amber-400 to-amber-500 text-zinc-950 shadow-[0_2px_8px_rgba(245,158,11,0.4)] cursor-pointer"
                >
                  Save Session
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
