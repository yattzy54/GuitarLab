package app.tuxguitar.android.activity

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity

class TGActivityActionBarController(private val activity: TGActivity) {
    private val actionBarInternal: ActionBar?
        get() = (activity.requireActivity() as AppCompatActivity).supportActionBar

    fun getActionBar(): ActionBar? = actionBarInternal
    fun setHomeButtonEnabled(enabled: Boolean) { actionBarInternal?.setHomeButtonEnabled(enabled) }
    fun setDisplayHomeAsUpEnabled(enabled: Boolean) { actionBarInternal?.setDisplayHomeAsUpEnabled(enabled) }
    fun setDisplayShowTitleEnabled(enabled: Boolean) { actionBarInternal?.setDisplayShowTitleEnabled(enabled) }
    fun setDisplayShowHomeEnabled(enabled: Boolean) { actionBarInternal?.setDisplayShowHomeEnabled(enabled) }
    fun setDisplayUseLogoEnabled(enabled: Boolean) { actionBarInternal?.setDisplayUseLogoEnabled(enabled) }
    fun setLogo(resId: Int) { actionBarInternal?.setLogo(resId) }
    fun setTitle(resId: Int) { actionBarInternal?.setTitle(resId) }
    fun setTitle(title: CharSequence?) { actionBarInternal?.title = title }
}
