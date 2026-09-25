package app.tuxguitar.android.browser.assets

import app.tuxguitar.android.browser.model.TGBrowserElement
import java.util.Comparator

class TGAssetBrowserElementComparator : Comparator<TGBrowserElement> {
    override fun compare(element1: TGBrowserElement, element2: TGBrowserElement): Int {
        if (element1.isFolder() && !element2.isFolder()) return -1
        if (element2.isFolder() && !element1.isFolder()) return 1
        return element1.getName().compareTo(element2.getName())
    }
}
