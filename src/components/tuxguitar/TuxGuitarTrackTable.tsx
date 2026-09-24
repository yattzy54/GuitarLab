import React, { useState } from 'react';
import { TabTrackInfo, AdvancedMeasure } from '../../types/tabPlayer';
import { Plus, Trash2, Copy, Volume2, Sliders, Music, Check, Eye } from 'lucide-react';
import { INSTRUMENT_PRESETS, TUNING_PRESETS } from './tuxAlphaTex';

interface TuxGuitarTrackTableProps {
  tracks: TabTrackInfo[];
  activeTrackId: string;
  onSelectTrack: (trackId: string) => void;
  onUpdateTrack: (updatedTrack: TabTrackInfo) => void;
  onAddTrack: (newTrack: TabTrackInfo) => void;
  onDeleteTrack: (trackId: string) => void;
  onDuplicateTrack: (trackId: string) => void;
  measuresTemplate?: AdvancedMeasure[];
}

export const TuxGuitarTrackTable: React.FC<TuxGuitarTrackTableProps> = ({
  tracks,
  activeTrackId,
  onSelectTrack,
  onUpdateTrack,
  onAddTrack,
  onDeleteTrack,
  onDuplicateTrack,
  measuresTemplate = [],
}) => {
  const [isAddMenuOpen, setIsAddMenuOpen] = useState(false);

  const handleCreateTrack = (type: 'dist_guitar' | 'acoustic_guitar' | 'bass' | 'drums') => {
    const id = `trk_${Date.now()}`;
    let name = 'Guitar';
    let inst = 'Distortion Guitar';
    let tuningKey = 'standard_e';

    if (type === 'acoustic_guitar') {
      name = 'Acoustic Guitar';
      inst = 'Acoustic Guitar (Steel)';
    } else if (type === 'bass') {
      name = 'Electric Bass';
      inst = 'Electric Bass (Finger)';
      tuningKey = 'bass_standard';
    } else if (type === 'drums') {
      name = 'Drum Kit';
      inst = 'Standard Drum Kit';
    }

    const tuning = TUNING_PRESETS[tuningKey] || TUNING_PRESETS['standard_e'];

    // Clone measures with rests
    const clonedMeasures: AdvancedMeasure[] = measuresTemplate.map((m) => ({
      number: m.number,
      timeSignature: m.timeSignature,
      tempoBpm: m.tempoBpm,
      beats: [
        { id: `b_${id}_${m.number}_1`, duration: 'w', durationValue: 4.0, isRest: true, notes: [] },
      ],
    }));

    const newTrack: TabTrackInfo = {
      id,
      name,
      instrument: inst,
      tuningName: tuning.label.split(' ')[0],
      tuningNotes: [...tuning.notes],
      volume: 0.9,
      isMuted: false,
      isSolo: false,
      measures: clonedMeasures.length > 0 ? clonedMeasures : [
        {
          number: 1,
          timeSignature: [4, 4],
          beats: [{ id: `b_${id}_1`, duration: 'w', durationValue: 4.0, isRest: true, notes: [] }],
        },
      ],
    };

    onAddTrack(newTrack);
    onSelectTrack(id);
    setIsAddMenuOpen(false);
  };

  return (
    <div className="bg-[#0C1019] border border-[#222B3D] rounded-2xl p-4 shadow-xl space-y-3">
      {/* Header */}
      <div className="flex items-center justify-between pb-3 border-b border-[#1E2638]">
        <div className="flex items-center space-x-2.5">
          <div className="p-1.5 rounded-lg bg-amber-500/20 text-amber-400 border border-amber-500/30">
            <Sliders className="w-4 h-4" />
          </div>
          <div>
            <h3 className="text-xs font-black text-white uppercase tracking-wider">
              TuxGuitar Multi-Track Mixer & Matrix
            </h3>
            <p className="text-[11px] text-zinc-400">
              {tracks.length} track{tracks.length !== 1 ? 's' : ''} in score · Solo, Mute, Tuning & Instrument assignment
            </p>
          </div>
        </div>

        <div className="relative">
          <button
            onClick={() => setIsAddMenuOpen(!isAddMenuOpen)}
            className="px-3 py-1.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-zinc-950 font-bold text-xs flex items-center space-x-1.5 transition-all shadow-md shadow-amber-500/20 cursor-pointer"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Add Track</span>
          </button>

          {isAddMenuOpen && (
            <div className="absolute right-0 mt-2 w-48 rounded-xl bg-[#141B28] border border-[#27344D] shadow-2xl p-1.5 z-40 space-y-1">
              <button
                onClick={() => handleCreateTrack('dist_guitar')}
                className="w-full text-left px-2.5 py-1.5 rounded-lg text-xs font-semibold text-zinc-200 hover:bg-[#1E273A] hover:text-white"
              >
                🎸 Distortion / Lead Guitar
              </button>
              <button
                onClick={() => handleCreateTrack('acoustic_guitar')}
                className="w-full text-left px-2.5 py-1.5 rounded-lg text-xs font-semibold text-zinc-200 hover:bg-[#1E273A] hover:text-white"
              >
                🪕 Acoustic Guitar (Steel)
              </button>
              <button
                onClick={() => handleCreateTrack('bass')}
                className="w-full text-left px-2.5 py-1.5 rounded-lg text-xs font-semibold text-zinc-200 hover:bg-[#1E273A] hover:text-white"
              >
                🎸 Electric Bass (4-Str)
              </button>
              <button
                onClick={() => handleCreateTrack('drums')}
                className="w-full text-left px-2.5 py-1.5 rounded-lg text-xs font-semibold text-zinc-200 hover:bg-[#1E273A] hover:text-white"
              >
                🥁 Drum Kit
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Tracks Table */}
      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs border-collapse">
          <thead>
            <tr className="border-b border-[#1E2638] text-[10px] uppercase font-bold text-zinc-500 tracking-wider">
              <th className="py-2 px-3 w-10">Active</th>
              <th className="py-2 px-3">Track Name</th>
              <th className="py-2 px-3">Instrument</th>
              <th className="py-2 px-3">Tuning</th>
              <th className="py-2 px-3 w-20 text-center">Controls</th>
              <th className="py-2 px-3 w-28">Volume</th>
              <th className="py-2 px-3 w-20 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#1A2234]">
            {tracks.map((track, idx) => {
              const isSelected = track.id === activeTrackId;

              return (
                <tr
                  key={track.id}
                  onClick={() => onSelectTrack(track.id)}
                  className={`transition-colors cursor-pointer ${
                    isSelected
                      ? 'bg-[#182234] border-l-4 border-amber-400 text-white'
                      : 'hover:bg-[#121824] text-zinc-300'
                  }`}
                >
                  {/* Select radio */}
                  <td className="py-2.5 px-3">
                    <div
                      className={`w-4 h-4 rounded-full border flex items-center justify-center ${
                        isSelected
                          ? 'border-amber-400 bg-amber-400 text-zinc-950 shadow-[0_0_8px_rgba(245,158,11,0.7)]'
                          : 'border-zinc-600 bg-zinc-800'
                      }`}
                    >
                      {isSelected && <div className="w-1.5 h-1.5 rounded-full bg-zinc-950" />}
                    </div>
                  </td>

                  {/* Name */}
                  <td className="py-2.5 px-3">
                    <input
                      type="text"
                      value={track.name}
                      onClick={(e) => e.stopPropagation()}
                      onChange={(e) => onUpdateTrack({ ...track, name: e.target.value })}
                      className="bg-transparent border-b border-transparent hover:border-zinc-600 focus:border-amber-400 focus:outline-hidden font-bold text-xs text-zinc-100 px-1 py-0.5 w-full max-w-[170px]"
                    />
                  </td>

                  {/* Instrument */}
                  <td className="py-2.5 px-3">
                    <select
                      value={track.instrument}
                      onClick={(e) => e.stopPropagation()}
                      onChange={(e) => onUpdateTrack({ ...track, instrument: e.target.value })}
                      className="bg-[#121824] border border-[#222E42] rounded-lg px-2 py-1 text-zinc-200 text-[11px] font-medium focus:border-amber-400 focus:outline-hidden"
                    >
                      {INSTRUMENT_PRESETS.map((p) => (
                        <option key={p.id} value={p.name}>
                          {p.name}
                        </option>
                      ))}
                    </select>
                  </td>

                  {/* Tuning */}
                  <td className="py-2.5 px-3 font-mono text-[11px] text-zinc-400">
                    <select
                      value={track.tuningName}
                      onClick={(e) => e.stopPropagation()}
                      onChange={(e) => {
                        const tKey = Object.keys(TUNING_PRESETS).find(
                          (k) => TUNING_PRESETS[k].label.split(' ')[0] === e.target.value
                        );
                        const preset = tKey ? TUNING_PRESETS[tKey] : null;
                        if (preset) {
                          onUpdateTrack({
                            ...track,
                            tuningName: preset.label.split(' ')[0],
                            tuningNotes: [...preset.notes],
                          });
                        }
                      }}
                      className="bg-[#121824] border border-[#222E42] rounded-lg px-2 py-1 text-zinc-200 text-[11px] font-medium focus:border-amber-400 focus:outline-hidden"
                    >
                      {Object.entries(TUNING_PRESETS).map(([k, v]) => {
                        const name = v.label.split(' ')[0];
                        return (
                          <option key={k} value={name}>
                            {v.label}
                          </option>
                        );
                      })}
                    </select>
                  </td>

                  {/* Solo & Mute */}
                  <td className="py-2.5 px-3 text-center">
                    <div className="flex items-center justify-center space-x-1" onClick={(e) => e.stopPropagation()}>
                      <button
                        onClick={() => onUpdateTrack({ ...track, isSolo: !track.isSolo })}
                        className={`w-6 h-6 rounded-md text-[10px] font-black transition-colors ${
                          track.isSolo
                            ? 'bg-amber-400 text-zinc-950 ring-1 ring-amber-300'
                            : 'bg-zinc-800 text-zinc-400 hover:text-white'
                        }`}
                        title="Solo this track"
                      >
                        S
                      </button>
                      <button
                        onClick={() => onUpdateTrack({ ...track, isMuted: !track.isMuted })}
                        className={`w-6 h-6 rounded-md text-[10px] font-black transition-colors ${
                          track.isMuted
                            ? 'bg-rose-500 text-white'
                            : 'bg-zinc-800 text-zinc-400 hover:text-white'
                        }`}
                        title="Mute this track"
                      >
                        M
                      </button>
                    </div>
                  </td>

                  {/* Volume Slider */}
                  <td className="py-2.5 px-3" onClick={(e) => e.stopPropagation()}>
                    <div className="flex items-center space-x-2">
                      <Volume2 className="w-3 h-3 text-zinc-500" />
                      <input
                        type="range"
                        min="0"
                        max="1"
                        step="0.05"
                        value={track.volume}
                        onChange={(e) =>
                          onUpdateTrack({ ...track, volume: parseFloat(e.target.value) })
                        }
                        className="w-16 h-1 bg-zinc-700 rounded-lg appearance-none cursor-pointer accent-amber-400"
                      />
                      <span className="text-[10px] font-mono text-zinc-400 w-7">
                        {Math.round(track.volume * 100)}%
                      </span>
                    </div>
                  </td>

                  {/* Duplicate / Delete actions */}
                  <td className="py-2.5 px-3 text-right" onClick={(e) => e.stopPropagation()}>
                    <div className="flex items-center justify-end space-x-1.5">
                      <button
                        onClick={() => onDuplicateTrack(track.id)}
                        className="p-1 rounded-lg text-zinc-400 hover:text-amber-400 hover:bg-[#1A2332] transition-colors"
                        title="Duplicate track"
                      >
                        <Copy className="w-3.5 h-3.5" />
                      </button>
                      {tracks.length > 1 && (
                        <button
                          onClick={() => onDeleteTrack(track.id)}
                          className="p-1 rounded-lg text-zinc-400 hover:text-rose-400 hover:bg-[#1A2332] transition-colors"
                          title="Delete track"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};
