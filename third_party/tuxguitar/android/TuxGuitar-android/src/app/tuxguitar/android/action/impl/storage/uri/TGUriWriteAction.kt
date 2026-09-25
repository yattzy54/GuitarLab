package app.tuxguitar.android.action.impl.storage.uri

import android.net.Uri
import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.editor.action.file.TGWriteSongAction
import app.tuxguitar.util.TGContext

class TGUriWriteAction(context: TGContext) : TGActionBase(context, NAME) {
 override fun processAction(context: TGActionContext) {
  try {
   val uri = context.getAttribute<Uri>(ATTRIBUTE_URI)
   val activity = context.getAttribute<TGActivity>(ATTRIBUTE_ACTIVITY)
   val outputStream = activity.requireContext().contentResolver.openOutputStream(uri)
   try { context.setAttribute(TGWriteSongAction.ATTRIBUTE_OUTPUT_STREAM, outputStream); TGActionManager.getInstance(getContext()).execute(TGWriteSongAction.NAME, context) }
   finally { outputStream?.close() }
  } catch (throwable: Throwable) { throw TGActionException(throwable) }
 }
 companion object { const val NAME = "action.storage.uri.write-uri"; @JvmField val ATTRIBUTE_ACTIVITY = TGActivity::class.java.name; @JvmField val ATTRIBUTE_URI = Uri::class.java.name }
}
