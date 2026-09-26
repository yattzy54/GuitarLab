package app.tuxguitar.android.action.installer

import app.tuxguitar.android.action.listener.cache.TGUpdateController
import app.tuxguitar.editor.undo.TGUndoableActionController

class TGActionConfig {
    var updateController: TGUpdateController? = null
    var undoableController: TGUndoableActionController? = null
    var lockableAction = false
    var disableOnPlaying = false
    var stopTransport = false
    var documentModifier = false
    var syncThread = false
}
