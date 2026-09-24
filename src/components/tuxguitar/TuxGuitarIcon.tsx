import React from 'react';

interface TuxGuitarIconProps {
  className?: string;
  size?: number;
}

export const TuxGuitarIcon: React.FC<TuxGuitarIconProps> = ({ className = 'w-6 h-6', size = 24 }) => {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 48 48"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={className}
    >
      <defs>
        <linearGradient id="tgPenguinBody" x1="12" y1="10" x2="36" y2="44" gradientUnits="userSpaceOnUse">
          <stop stopColor="#1E293B" />
          <stop offset="1" stopColor="#090D16" />
        </linearGradient>
        <linearGradient id="tgPenguinBelly" x1="20" y1="20" x2="28" y2="40" gradientUnits="userSpaceOnUse">
          <stop stopColor="#F8FAFC" />
          <stop offset="1" stopColor="#CBD5E1" />
        </linearGradient>
        <linearGradient id="tgGuitarBody" x1="28" y1="18" x2="44" y2="38" gradientUnits="userSpaceOnUse">
          <stop stopColor="#F59E0B" />
          <stop offset="0.7" stopColor="#D97706" />
          <stop offset="1" stopColor="#B45309" />
        </linearGradient>
      </defs>

      {/* Tux Penguin Body (Dark Slate) */}
      <ellipse cx="23" cy="27" rx="14" ry="17" fill="url(#tgPenguinBody)" stroke="#334155" strokeWidth="1.5" />

      {/* Penguin Belly (White/Silver) */}
      <ellipse cx="23" cy="30" rx="8" ry="12" fill="url(#tgPenguinBelly)" />

      {/* Penguin Wings */}
      <path d="M10 24 C7 28 8 36 12 39 C13 36 13 28 12 24 Z" fill="#0F172A" />
      <path d="M36 24 C39 28 38 36 34 39 C33 36 33 28 34 24 Z" fill="#0F172A" />

      {/* Penguin Feet (Orange) */}
      <ellipse cx="17" cy="43" rx="4" ry="2" fill="#F97316" />
      <ellipse cx="29" cy="43" rx="4" ry="2" fill="#F97316" />

      {/* Penguin Eyes */}
      <ellipse cx="20" cy="16" rx="2" ry="3" fill="#FFFFFF" />
      <circle cx="20.5" cy="16" r="1.2" fill="#0F172A" />
      <ellipse cx="26" cy="16" rx="2" ry="3" fill="#FFFFFF" />
      <circle cx="25.5" cy="16" r="1.2" fill="#0F172A" />

      {/* Penguin Beak (Yellow/Orange) */}
      <polygon points="23,17 20,21 26,21" fill="#FBBF24" stroke="#D97706" strokeWidth="0.5" />

      {/* Electric Guitar Across Body */}
      {/* Guitar Neck */}
      <line x1="16" y1="36" x2="38" y2="12" stroke="#475569" strokeWidth="3" strokeLinecap="round" />
      <line x1="16" y1="36" x2="38" y2="12" stroke="#F1F5F9" strokeWidth="0.8" strokeLinecap="round" />
      {/* Headstock */}
      <polygon points="38,12 43,7 41,5 36,10" fill="#334155" stroke="#F59E0B" strokeWidth="0.5" />
      {/* Guitar Body (Iconic Strat/SG double cutaway silhouette) */}
      <path
        d="M17 31 C14 28 11 31 10 35 C9 39 12 43 17 42 C22 41 24 37 22 33 C20 30 19 32 17 31 Z"
        fill="url(#tgGuitarBody)"
        stroke="#FCD34D"
        strokeWidth="1"
      />
      {/* Guitar Pickguard & Pickups */}
      <ellipse cx="16" cy="36" rx="2.5" ry="3.5" fill="#18181B" opacity="0.8" />
      <circle cx="16" cy="36" r="1" fill="#EF4444" />
    </svg>
  );
};
