package app.tuxguitar.android.action.listener.gui
import app.tuxguitar.action.*
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.gui.*
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.util.TGContext
class TGExitConfirmInterceptor(private val context: TGContext, private val activity: TGActivity) : TGActionInterceptor {
 override fun intercept(id: String, c: TGActionContext): Boolean {
  if (id != TGExitAction.NAME || c.getAttribute<Boolean>(CONFIRMED) == true) return false
  val p=TGActionProcessor(context,TGOpenDialogAction.NAME)
  p.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER,TGConfirmDialogController())
  p.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY,activity)
  p.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE,activity.getString(R.string.global_exit_confirm_message))
  p.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE,Runnable { Thread { c.setAttribute(CONFIRMED,true);TGActionManager.getInstance(context).execute(id,c) }.start() })
  p.process(); return true
 }
 companion object { const val CONFIRMED="exitConfirmInterceptor_confirmed" }
}
