package app.tuxguitar.android.view.keyboard

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import app.tuxguitar.android.R
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.caret.TGGoDownAction
import app.tuxguitar.android.action.impl.caret.TGGoLeftAction
import app.tuxguitar.android.action.impl.caret.TGGoRightAction
import app.tuxguitar.android.action.impl.caret.TGGoUpAction
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction
import app.tuxguitar.android.action.impl.view.TGShowSmartMenuAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.android.menu.controller.TGMenuController
import app.tuxguitar.android.menu.controller.impl.contextual.TGDurationMenu
import app.tuxguitar.editor.action.duration.TGDecrementDurationAction
import app.tuxguitar.editor.action.duration.TGIncrementDurationAction
import app.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction
import app.tuxguitar.editor.action.note.TGInsertRestBeatAction
import app.tuxguitar.editor.action.note.TGSetNoteFretNumberAction
import app.tuxguitar.util.TGContext

class TGTabKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    override fun onFinishInflate() {
        attachView()
        addListeners()
        super.onFinishInflate()
    }

    fun attachView() {
        TGTabKeyboardController.getInstance(TGApplicationUtil.findContext(this)).setView(this)
    }

    fun addListeners() {
        val context: TGContext = findContext()
        findViewById<View>(R.id.tab_kb_button_number_0).setOnClickListener(TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(0)))
        findViewById<View>(R.id.tab_kb_button_number_1).setOnClickListener(TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(1)))
        findViewById<View>(R.id.tab_kb_button_number_2).setOnClickListener(TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(2)))
        findViewById<View>(R.id.tab_kb_button_number_3).setOnClickListener(TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(3)))
        findViewById<View>(R.id.tab_kb_button_number_4).setOnClickListener(TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(4)))
        findViewById<View>(R.id.tab_kb_button_number_5).setOnClickListener(TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(5)))
        findViewById<View>(R.id.tab_kb_button_number_6).setOnClickListener(TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(6)))
        findViewById<View>(R.id.tab_kb_button_number_7).setOnClickListener(TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(7)))
        findViewById<View>(R.id.tab_kb_button_number_8).setOnClickListener(TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(8)))
        findViewById<View>(R.id.tab_kb_button_number_9).setOnClickListener(TGActionProcessorListener(context, TGSetNoteFretNumberAction.getActionName(9)))

        findViewById<View>(R.id.tab_kb_button_insert).setOnClickListener(TGActionProcessorListener(context, TGInsertRestBeatAction.NAME))
        findViewById<View>(R.id.tab_kb_button_delete).setOnClickListener(TGActionProcessorListener(context, TGDeleteNoteOrRestAction.NAME))

        findViewById<View>(R.id.tab_kb_button_up).setOnClickListener(TGActionProcessorListener(context, TGGoUpAction.NAME))
        findViewById<View>(R.id.tab_kb_button_down).setOnClickListener(TGActionProcessorListener(context, TGGoDownAction.NAME))
        findViewById<View>(R.id.tab_kb_button_left).setOnClickListener(TGActionProcessorListener(context, TGGoLeftAction.NAME))
        findViewById<View>(R.id.tab_kb_button_right).setOnClickListener(TGActionProcessorListener(context, TGGoRightAction.NAME))

        findViewById<View>(R.id.tab_kb_button_increment_duration).setOnClickListener(TGActionProcessorListener(context, TGIncrementDurationAction.NAME))
        findViewById<View>(R.id.tab_kb_button_decrement_duration).setOnClickListener(TGActionProcessorListener(context, TGDecrementDurationAction.NAME))
        findViewById<View>(R.id.tab_kb_button_set_duration).setOnClickListener(createContextMenuActionListener(TGDurationMenu(findActivity())))

        findViewById<View>(R.id.tab_kb_button_select).setOnClickListener(TGActionProcessorListener(context, TGShowSmartMenuAction.NAME))
    }

    fun createContextMenuActionListener(controller: TGMenuController): TGActionProcessorListener {
        val processor = TGActionProcessorListener(findContext(), TGOpenMenuAction.NAME)
        processor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller)
        processor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, findActivity())
        return processor
    }

    fun toggleVisibility() {
        clearAnimation()
        if (visibility == VISIBLE) {
            animate().setDuration(300).translationY(height.toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    clearAnimation()
                    visibility = GONE
                }
            })
        } else {
            visibility = VISIBLE
            animate().setDuration(300).translationY(0f).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    clearAnimation()
                }
            })
        }
    }

    private fun findActivity(): TGActivity = context as TGActivity

    private fun findContext(): TGContext = TGApplicationUtil.findContext(this)
}
