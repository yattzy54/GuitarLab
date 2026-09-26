package app.tuxguitar.android.browser.assets

import app.tuxguitar.android.browser.model.TGBrowserElement
import java.util.Comparator

class TGAssetBrowserElementComparator : Comparator<TGBrowserElement> {
    override fun compare(element1: TGBrowserElement, element2: TGBrowserElement): Int {
        if (element1.isFolder() && !element2.isFolder()) {
            return RESULT_LESS * DIRECTION_FOLDER
        }
        if (element2.isFolder() && !element1.isFolder()) {
            return RESULT_GREATER * DIRECTION_FOLDER
        }
        return DIRECTION * element1.getName().compareTo(element2.getName())
    }

    companion object {
        private const val RESULT_LESS = -1
        private const val RESULT_GREATER = 1
        private const val DIRECTION = 1
        private const val DIRECTION_FOLDER = 1
    }
}
