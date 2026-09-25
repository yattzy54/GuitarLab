package app.tuxguitar.android.view.dialog.fragment

import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.android.fragment.TGCachedFragmentController
import app.tuxguitar.android.fragment.TGFragmentController
import app.tuxguitar.android.view.dialog.TGDialogContext
import app.tuxguitar.android.view.dialog.TGDialogController
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.util.TGContext

abstract class TGModalFragmentController<T : TGModalFragment> :
    TGCachedFragmentController<T>(), TGDialogController {

    override fun showDialog(activity: TGActivity, dialogContext: TGDialogContext) {
        val context = TGApplicationUtil.findContext(activity)
        val processor = TGActionProcessor(context, TGOpenFragmentAction.NAME)
        processor.setAttribute(
            TGOpenFragmentAction.ATTRIBUTE_CONTROLLER,
            TGModalFragmentControllerWrapper(this, context, dialogContext)
        )
        processor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, activity)
        processor.process()
    }

    private class TGModalFragmentControllerWrapper<E : TGModalFragment>(
        private val target: TGModalFragmentController<E>,
        private val context: TGContext,
        private val dialogContext: TGDialogContext
    ) : TGFragmentController<E> {
        override fun getFragment(): E {
            val fragment = target.getFragment()
            context.setAttribute(fragment.getDialogContextKey(), dialogContext)
            return fragment
        }
    }
}
