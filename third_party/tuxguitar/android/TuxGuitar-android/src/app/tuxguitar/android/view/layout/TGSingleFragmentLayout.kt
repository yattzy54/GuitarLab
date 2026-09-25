package app.tuxguitar.android.view.layout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.drawer.TGDrawerViewBuilder
import app.tuxguitar.android.fragment.TGFragmentController
import app.tuxguitar.android.fragment.impl.TGMainFragmentController
import app.tuxguitar.util.TGContext

class TGSingleFragmentLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : DrawerLayout(context, attrs), TGDrawerViewBuilder {

    private val fragmentDrawerLayouts = HashMap<TGFragmentController<*>, Int>()

    override fun onFinishInflate() {
        super.onFinishInflate()
        createFragmentDrawerLayouts()
        findActivity().getDrawerManager().setDrawerBuilder(this)
    }

    override fun onOpenFragment(controller: TGFragmentController<*>, drawerView: ViewGroup) {
        val layoutId = fragmentDrawerLayouts[controller]
        if (layoutId != null) {
            findActivity().getThemedLayoutInflater().inflate(layoutId, drawerView)
        }
    }

    fun createFragmentDrawerLayouts() {
        val context: TGContext = findActivity().findContext()
        fragmentDrawerLayouts.clear()
        fragmentDrawerLayouts[TGMainFragmentController.getInstance(context)] = R.layout.view_main_drawer
    }

    fun findActivity(): TGActivity = TGActivity.requireCurrent()
}
