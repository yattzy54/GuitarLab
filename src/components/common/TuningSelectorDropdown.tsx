import React, { useState, useRef, useEffect } from 'react';
import { Tuning } from '../../types';
import { getAllTunings } from '../../data/defaultTunings';
import { Sliders, ChevronDown, Check, Music } from 'lucide-react';

interface TuningSelectorDropdownProps {
  activeTuning: Tuning;
  onTuningChange: (tuning: Tuning) => void;
  variant?: 'toolbar' | 'compact' | 'pill';
  a4Pitch?: number;
}

export const TuningSelectorDropdown: React.FC<TuningSelectorDropdownProps> = ({
  activeTuning,
  onTuningChange,
  variant = 'toolbar',
  a4Pitch = 440,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const dropdownRef = useRef<HTMLDivElement>(null);

  const allTunings = getAllTunings(a4Pitch);

  const categories = [
    { id: 'ALL', label: 'Все' },
    { id: 'Standard', label: 'Standard' },
    { id: 'Drop', label: 'Drop' },
    { id: 'Open', label: 'Open' },
    { id: 'Alternate', label: 'Alternate' },
    { id: '7-String', label: '7-Струн' },
    { id: '8-String', label: '8-Струн' },
  ];

  const filteredTunings =
    selectedCategory === 'ALL'
      ? allTunings
      : allTunings.filter((t) => t.category === selectedCategory);

  // Close dropdown on click outside or Escape
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setIsOpen(false);
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
      document.addEventListener('keydown', handleKeyDown);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen]);

  // Format notes: e.g. E · B · G · D · A · E
  const notesString = activeTuning.notes
    .map((n) => n.noteName.replace(/[0-9]/g, ''))
    .join('·');

  return (
    <div className="relative inline-block text-left" ref={dropdownRef}>
      {/* Trigger Button */}
      {variant === 'toolbar' && (
        <button
          type="button"
          onClick={() => setIsOpen(!isOpen)}
          className={`flex items-center space-x-2 px-3 py-1.5 rounded-full border text-xs font-bold transition-all cursor-pointer shadow-md ${
            isOpen
              ? 'bg-[#1e273a] border-amber-400 text-amber-300 shadow-[0_0_12px_rgba(245,158,11,0.25)]'
              : 'bg-[#151C2A] hover:bg-[#1A2335] border-[#2A3750] text-amber-400 hover:border-amber-500/50'
          }`}
          title="Сменить строй гитары"
        >
          <div className="flex items-center space-x-1.5">
            <span className="w-2 h-2 rounded-full bg-amber-400 shadow-[0_0_8px_rgba(245,158,11,0.8)] animate-pulse" />
            <span className="font-semibold text-zinc-300 hidden sm:inline text-[11px]">Строй:</span>
            <span className="text-amber-400 font-bold">{activeTuning.name}</span>
          </div>
          <span className="text-[10px] font-mono text-zinc-400 hidden md:inline px-1.5 py-0.5 rounded bg-black/40 border border-zinc-700/50">
            {notesString}
          </span>
          <ChevronDown
            className={`w-3.5 h-3.5 text-zinc-400 transition-transform duration-200 ${
              isOpen ? 'rotate-180 text-amber-400' : ''
            }`}
          />
        </button>
      )}

      {variant === 'compact' && (
        <button
          type="button"
          onClick={() => setIsOpen(!isOpen)}
          className="flex items-center space-x-1.5 px-2.5 py-1 rounded-lg bg-[#141A26] border border-[#27344D] text-xs font-semibold text-amber-400 hover:border-amber-400/60 transition-colors cursor-pointer"
        >
          <Music className="w-3.5 h-3.5 text-amber-400" />
          <span>{activeTuning.name}</span>
          <ChevronDown className="w-3 h-3 text-zinc-400" />
        </button>
      )}

      {variant === 'pill' && (
        <button
          type="button"
          onClick={() => setIsOpen(!isOpen)}
          className="flex items-center space-x-2 px-3 py-1.5 rounded-xl bg-[#18202E] border border-[#2A344A] text-xs font-semibold text-zinc-200 hover:text-white hover:border-amber-400/60 transition-colors cursor-pointer"
        >
          <Sliders className="w-3.5 h-3.5 text-amber-400" />
          <span>Строй: <strong className="text-amber-400">{activeTuning.name}</strong></span>
          <ChevronDown className="w-3.5 h-3.5 text-zinc-400" />
        </button>
      )}

      {/* Floating Dropdown Menu */}
      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 sm:w-88 rounded-2xl bg-[#0F141F] border border-[#2B3852] shadow-2xl shadow-black z-50 overflow-hidden text-zinc-100 animate-in fade-in zoom-in-95 duration-150">
          {/* Header */}
          <div className="p-3.5 bg-[#141A28] border-b border-[#242F44] flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <Sliders className="w-4 h-4 text-amber-400" />
              <span className="text-xs font-bold text-white uppercase tracking-wider">
                Выбор строя гитары
              </span>
            </div>
            <span className="text-[10px] font-mono text-zinc-400 bg-zinc-900 px-2 py-0.5 rounded-full border border-zinc-800">
              A4 = {a4Pitch} Hz
            </span>
          </div>

          {/* Category Filter Pills */}
          <div className="p-2.5 bg-[#101522] border-b border-[#20293D] flex items-center space-x-1 overflow-x-auto scrollbar-none">
            {categories.map((cat) => (
              <button
                key={cat.id}
                onClick={() => setSelectedCategory(cat.id)}
                className={`px-2.5 py-1 rounded-lg text-[11px] font-bold whitespace-nowrap transition-colors cursor-pointer ${
                  selectedCategory === cat.id
                    ? 'bg-amber-400 text-zinc-950 shadow-sm'
                    : 'bg-[#18202F] text-zinc-400 hover:text-zinc-200 hover:bg-[#202A3D]'
                }`}
              >
                {cat.label}
              </button>
            ))}
          </div>

          {/* Tunings List */}
          <div className="max-h-72 overflow-y-auto p-2 space-y-1">
            {filteredTunings.map((tuning) => {
              const isSelected = tuning.id === activeTuning.id;
              const tuningNotesStr = tuning.notes
                .map((n) => n.noteName.replace(/[0-9]/g, ''))
                .join(' · ');

              return (
                <button
                  key={tuning.id}
                  onClick={() => {
                    onTuningChange(tuning);
                    setIsOpen(false);
                  }}
                  className={`w-full text-left p-2.5 rounded-xl flex items-center justify-between transition-all cursor-pointer ${
                    isSelected
                      ? 'bg-[#1C2537] border border-amber-500/50 text-amber-300 shadow-sm'
                      : 'hover:bg-[#151D2A] text-zinc-300 hover:text-white border border-transparent'
                  }`}
                >
                  <div className="min-w-0 pr-2">
                    <div className="flex items-center space-x-2">
                      <span className="text-xs font-bold leading-tight">{tuning.name}</span>
                      <span className="text-[9px] font-semibold px-1.5 py-0.5 rounded bg-zinc-800/80 text-zinc-400 border border-zinc-700/50">
                        {tuning.stringCount} струн
                      </span>
                    </div>
                    <div className="text-[10px] font-mono text-zinc-400 mt-1 tracking-wider">
                      {tuningNotesStr}
                    </div>
                  </div>

                  {isSelected && (
                    <div className="w-5 h-5 rounded-full bg-amber-400 text-zinc-950 flex items-center justify-center shrink-0 shadow-sm">
                      <Check className="w-3.5 h-3.5 stroke-[3]" />
                    </div>
                  )}
                </button>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};
