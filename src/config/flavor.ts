export type AppFlavor = 'standard' | 'tabs';

const FLAVOR_STORAGE_KEY = 'guitarlab_flavor_mode';

/**
 * Gets the current app flavor:
 * - 'standard': Main flavor with standard guitar instruments (Tabs, GuitarTabEdit, and TabLab hidden)
 * - 'tabs': TabLab flavor showing ONLY the 3 tab tools (Tabs, GuitarTabEdit, and TabLab)
 */
export function getAppFlavor(): AppFlavor {
  if (typeof window !== 'undefined') {
    const urlParams = new URLSearchParams(window.location.search);
    const param = urlParams.get('flavor');
    if (param === 'tabs' || param === 'standard') {
      return param;
    }
    const saved = localStorage.getItem(FLAVOR_STORAGE_KEY);
    if (saved === 'tabs' || saved === 'standard') {
      return saved;
    }
  }
  return 'standard';
}

export function setAppFlavor(flavor: AppFlavor): void {
  if (typeof window !== 'undefined') {
    localStorage.setItem(FLAVOR_STORAGE_KEY, flavor);
    const url = new URL(window.location.href);
    url.searchParams.set('flavor', flavor);
    window.location.href = url.toString();
  }
}

export const TAB_EXCLUSIVE_ROUTES = ['tabs', 'guitartabedit', 'tuxguitar'] as const;
