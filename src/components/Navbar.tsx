import React from 'react';
import { Menu, Activity, Sparkles } from 'lucide-react';
import { AppDestination, NAV_ITEMS } from './NavigationDrawer';
import { Studio3DBadge } from './common/Studio3DComponents';

interface NavbarProps {
  currentRoute: AppDestination;
  onOpenDrawer: () => void;
  activeTuningName?: string;
}

export const Navbar: React.FC<NavbarProps> = ({
  currentRoute,
  onOpenDrawer,
  activeTuningName,
}) => {
  const currentItem = NAV_ITEMS.find((item) => item.id === currentRoute) || NAV_ITEMS[0];
  const Icon = currentItem.icon;

  return (
    <header className="sticky top-0 z-30 bg-[#0C1018]/90 backdrop-blur-md border-b border-[#222B3D] px-4 py-3">
      <div className="max-w-6xl mx-auto flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <button
            onClick={onOpenDrawer}
            className="p-2 -ml-1 rounded-xl text-zinc-300 hover:text-white hover:bg-[#182030] transition-colors cursor-pointer"
            aria-label="Open menu"
          >
            <Menu className="w-5 h-5 text-amber-400" />
          </button>

          <div className="flex items-center space-x-2.5">
            <Studio3DBadge icon={Icon} accent={currentItem.accent} size="sm" />
            <div>
              <h1 className="text-base font-bold text-white tracking-tight leading-none">
                {currentItem.title}
              </h1>
              <p className="text-[11px] text-zinc-400 hidden sm:block leading-none mt-1">
                {currentItem.subtitle}
              </p>
            </div>
          </div>
        </div>

        {/* Right Info badge */}
        <div className="flex items-center space-x-2">
          {activeTuningName && (
            <div className="px-3 py-1 rounded-full bg-[#151C2A] border border-[#263146] text-xs font-bold text-amber-400 flex items-center gap-1.5 shadow-sm">
              <span className="w-2 h-2 rounded-full bg-amber-400 shadow-[0_0_8px_rgba(245,158,11,0.8)] animate-pulse" />
              <span>{activeTuningName}</span>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
