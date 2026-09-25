package app.tuxguitar.android.browser.model

import java.util.Comparator

class TGBrowserElementComparator : Comparator<TGBrowserElement> {
    override fun compare(first: TGBrowserElement, second: TGBrowserElement): Int {
        if (first.isFolder() && !second.isFolder()) return -1
        if (second.isFolder() && !first.isFolder()) return 1
        return first.getName().compareTo(second.getName())
    }
}
