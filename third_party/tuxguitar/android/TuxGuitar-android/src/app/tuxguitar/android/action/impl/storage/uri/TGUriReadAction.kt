package app.tuxguitar.android.action.impl.storage.uri

import android.app.Activity
import android.net.Uri
import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.util.TGStreamUtil
import app.tuxguitar.editor.action.file.TGReadSongAction
import app.tuxguitar.io.base.TGFileFormatUtils
import app.tuxguitar.util.TGContext

class TGUriReadAction(context: TGContext) : TGActionBase(context, NAME) {
 override fun processAction(context: TGActionContext) {
  try {
   val uri = context.getAttribute<Uri>(ATTRIBUTE_URI)
   val activity = context.getAttribute<Activity>(ATTRIBUTE_ACTIVITY)
   val inputStream = activity.contentResolver.openInputStream(uri)
   val bufferedStream = inputStream?.let { stream -> TGStreamUtil.getInputStream(stream).also { stream.close() } }
   if (bufferedStream != null) {
    context.setAttribute(TGReadSongAction.ATTRIBUTE_INPUT_STREAM, bufferedStream)
    context.setAttribute(TGReadSongAction.ATTRIBUTE_FORMAT_CODE, TGFileFormatUtils.getFileFormatCode(uri.lastPathSegment))
    TGActionManager.getInstance(getContext()).execute(TGReadSongAction.NAME, context)
   }
  } catch (throwable: Throwable) { throw TGActionException(throwable) }
 }
 companion object { const val NAME = "action.storage.uri.read-uri"; val ATTRIBUTE_ACTIVITY = TGActivity::class.java.name; val ATTRIBUTE_URI = Uri::class.java.name }
}
