package app.tuxguitar.android.drawer

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.action.TGActionInterceptor
import app.tuxguitar.android.action.impl.gui.TGBackAction
import app.tuxguitar.util.TGException
import app.tuxguitar.util.TGSynchronizer

class TGDrawerActionInterceptor(private val drawerManager: TGDrawerManager) : TGActionInterceptor {
    override fun intercept(id: String, context: TGActionContext): Boolean {
        if (TGBackAction.NAME == id && drawerManager.isOpen()) {
            closeDrawerLater()
            return true
        }
        return false
    }

    fun closeDrawerLater() {
        TGSynchronizer.getInstance(drawerManager.findContext()).executeLater {
            drawerManager.closeDrawer()
        }
    }
}
