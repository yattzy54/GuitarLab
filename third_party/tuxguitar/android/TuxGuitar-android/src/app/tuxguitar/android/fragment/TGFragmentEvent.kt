package app.tuxguitar.android.fragment

import app.tuxguitar.event.TGEvent

class TGFragmentEvent(screen: TGScreen, action: String) : TGEvent(EVENT_TYPE) {
    init {
        setAttribute(ATTRIBUTE_FRAGMENT, screen)
        setAttribute(ATTRIBUTE_ACTION, action)
    }

    companion object {
        const val EVENT_TYPE = "ui-fragment"
        const val ATTRIBUTE_FRAGMENT = "fragment"
        const val ATTRIBUTE_ACTION = "action"
        const val ACTION_CREATED = "onCreate"
        const val ACTION_VIEW_CREATED = "onCreateView"
        const val ACTION_OPTIONS_MENU_CREATED = "onCreateOptionsMenu"
    }
}
