import React from 'react';
import { LucideIcon } from 'lucide-react';

export type StudioAccent = 'amber' | 'teal' | 'green' | 'ruby' | 'purple' | 'slate';

interface Studio3DBadgeProps {
  icon: LucideIcon;
  accent?: StudioAccent;
  size?: 'sm' | 'md' | 'lg' | 'xl';
  className?: string;
  onClick?: () => void;
}

export const Studio3DBadge: React.FC<Studio3DBadgeProps> = ({
  icon: Icon,
  accent = 'amber',
  size = 'md',
  className = '',
  onClick,
}) => {
  const sizeClasses = {
    sm: 'w-8 h-8 rounded-lg p-1.5',
    md: 'w-10 h-10 rounded-xl p-2',
    lg: 'w-12 h-12 rounded-2xl p-2.5',
    xl: 'w-14 h-14 rounded-2xl p-3',
  }[size];

  const iconSizes = {
    sm: 'w-4 h-4',
    md: 'w-5 h-5',
    lg: 'w-6 h-6',
    xl: 'w-7 h-7',
  }[size];

  const accentStyles: Record<StudioAccent, string> = {
    amber:
      'bg-gradient-to-b from-amber-400 via-amber-500 to-amber-600 text-zinc-950 shadow-[0_4px_12px_rgba(245,158,11,0.35),inset_0_1px_1px_rgba(255,255,255,0.5),inset_0_-2px_2px_rgba(0,0,0,0.3)] border border-amber-300/40',
    teal:
      'bg-gradient-to-b from-cyan-400 via-teal-500 to-teal-600 text-zinc-950 shadow-[0_4px_12px_rgba(20,184,166,0.35),inset_0_1px_1px_rgba(255,255,255,0.5),inset_0_-2px_2px_rgba(0,0,0,0.3)] border border-cyan-300/40',
    green:
      'bg-gradient-to-b from-emerald-400 via-emerald-500 to-emerald-600 text-zinc-950 shadow-[0_4px_12px_rgba(16,185,129,0.35),inset_0_1px_1px_rgba(255,255,255,0.5),inset_0_-2px_2px_rgba(0,0,0,0.3)] border border-emerald-300/40',
    ruby:
      'bg-gradient-to-b from-rose-500 via-red-600 to-red-700 text-white shadow-[0_4px_12px_rgba(239,68,68,0.35),inset_0_1px_1px_rgba(255,255,255,0.5),inset_0_-2px_2px_rgba(0,0,0,0.4)] border border-rose-400/40',
    purple:
      'bg-gradient-to-b from-violet-400 via-purple-500 to-indigo-600 text-white shadow-[0_4px_12px_rgba(168,85,247,0.35),inset_0_1px_1px_rgba(255,255,255,0.5),inset_0_-2px_2px_rgba(0,0,0,0.3)] border border-purple-300/40',
    slate:
      'bg-gradient-to-b from-zinc-700 via-zinc-800 to-zinc-900 text-zinc-200 shadow-[0_4px_10px_rgba(0,0,0,0.5),inset_0_1px_1px_rgba(255,255,255,0.2),inset_0_-2px_2px_rgba(0,0,0,0.5)] border border-zinc-600/50',
  };

  const Component = onClick ? 'button' : 'div';

  return (
    <Component
      onClick={onClick}
      className={`inline-flex items-center justify-center relative transition-transform active:scale-95 cursor-default select-none ${sizeClasses} ${accentStyles[accent]} ${
        onClick ? 'cursor-pointer hover:brightness-110' : ''
      } ${className}`}
    >
      <Icon className={`${iconSizes} drop-shadow-sm`} />
    </Component>
  );
};

interface StudioCardProps {
  children: React.ReactNode;
  className?: string;
  glow?: 'amber' | 'teal' | 'green' | 'ruby' | null;
  onClick?: () => void;
}

export const StudioCard: React.FC<StudioCardProps> = ({
  children,
  className = '',
  glow = null,
  onClick,
}) => {
  const glowBorder = {
    amber: 'border-amber-500/50 shadow-[0_0_20px_rgba(245,158,11,0.15)]',
    teal: 'border-teal-400/50 shadow-[0_0_20px_rgba(45,212,191,0.15)]',
    green: 'border-emerald-400/50 shadow-[0_0_20px_rgba(52,211,153,0.15)]',
    ruby: 'border-rose-500/50 shadow-[0_0_20px_rgba(244,63,94,0.15)]',
  };

  const Component = onClick ? 'div' : 'div';

  return (
    <Component
      onClick={onClick}
      className={`bg-[#121620]/95 backdrop-blur-md border rounded-2xl shadow-xl shadow-black/40 relative overflow-hidden transition-all ${
        glow ? glowBorder[glow] : 'border-[#222B3D] hover:border-[#2F3C55]'
      } ${onClick ? 'cursor-pointer' : ''} ${className}`}
    >
      {/* Top Specular Sheen */}
      <div className="absolute top-0 left-0 right-0 h-px bg-gradient-to-r from-transparent via-white/15 to-transparent pointer-events-none" />
      {children}
    </Component>
  );
};

interface StudioPillProps {
  label: string;
  selected: boolean;
  onClick: () => void;
  icon?: LucideIcon;
  accent?: 'amber' | 'teal' | 'green' | 'ruby';
  size?: 'sm' | 'md';
  className?: string;
}

export const StudioPill: React.FC<StudioPillProps> = ({
  label,
  selected,
  onClick,
  icon: Icon,
  accent = 'amber',
  size = 'md',
  className = '',
}) => {
  const activeStyles = {
    amber:
      'bg-gradient-to-b from-amber-400 to-amber-500 text-zinc-950 font-bold border-amber-300/50 shadow-[0_2px_8px_rgba(245,158,11,0.35),inset_0_1px_1px_rgba(255,255,255,0.4)]',
    teal:
      'bg-gradient-to-b from-teal-400 to-cyan-500 text-zinc-950 font-bold border-teal-300/50 shadow-[0_2px_8px_rgba(20,184,166,0.35),inset_0_1px_1px_rgba(255,255,255,0.4)]',
    green:
      'bg-gradient-to-b from-emerald-400 to-green-500 text-zinc-950 font-bold border-emerald-300/50 shadow-[0_2px_8px_rgba(16,185,129,0.35),inset_0_1px_1px_rgba(255,255,255,0.4)]',
    ruby:
      'bg-gradient-to-b from-rose-500 to-red-600 text-white font-bold border-rose-300/50 shadow-[0_2px_8px_rgba(239,68,68,0.35),inset_0_1px_1px_rgba(255,255,255,0.4)]',
  }[accent];

  const inactiveStyles =
    'bg-[#19202E] text-zinc-300 hover:text-white hover:bg-[#20293B] border-[#2A344A] shadow-[inset_0_1px_1px_rgba(255,255,255,0.06),inset_0_-1px_1px_rgba(0,0,0,0.3)]';

  const py = size === 'sm' ? 'py-1 px-3 text-xs' : 'py-2 px-4 text-xs sm:text-sm';

  return (
    <button
      onClick={onClick}
      className={`inline-flex items-center justify-center gap-1.5 rounded-xl border transition-all active:scale-95 cursor-pointer font-medium select-none ${py} ${
        selected ? activeStyles : inactiveStyles
      } ${className}`}
    >
      {Icon && <Icon className={size === 'sm' ? 'w-3.5 h-3.5' : 'w-4 h-4'} />}
      <span>{label}</span>
    </button>
  );
};
