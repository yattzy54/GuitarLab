package app.tuxguitar.android.action.impl.storage

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.storage.TGStorageManager
import app.tuxguitar.util.TGContext

class TGSaveDocumentAction(context: TGContext) : TGActionBase(context, NAME) {
 override fun processAction(context: TGActionContext) { TGStorageManager.getInstance(getContext()).saveDocument() }
 companion object { const val NAME = "action.storage.save-document" }
}
