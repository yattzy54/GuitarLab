package app.tuxguitar.android.navigation

import app.tuxguitar.event.TGEvent

class TGNavigationEvent(
    loadedFragment: TGNavigationFragment,
    backFrom: TGNavigationFragment?,
) : TGEvent(EVENT_TYPE) {
    init {
        setAttribute(PROPERTY_LOADED_FRAGMENT, loadedFragment)
        setAttribute(PROPERTY_BACK_FROM, backFrom)
    }

    companion object {
        const val EVENT_TYPE = "ui-navigation"
        const val PROPERTY_LOADED_FRAGMENT = "loadedFragment"
        const val PROPERTY_BACK_FROM = "backFrom"
    }
}
