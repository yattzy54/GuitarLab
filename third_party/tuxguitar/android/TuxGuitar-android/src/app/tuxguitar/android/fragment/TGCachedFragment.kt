package app.tuxguitar.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class TGCachedFragment(private val layout: Int) : TGBaseFragment() {
    private var cachedView: View? = null

    override fun getView(): View? = cachedView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val createdView = super.onCreateView(inflater, container, savedInstanceState)
        onShowView()
        return createdView
    }

    override fun onDestroyView() {
        onHideView()
        super.onDestroyView()
    }

    override fun onPostCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
        createdView: View?
    ): View? {
        if (cachedView == null) {
            cachedView = inflater.inflate(layout, container, false)
            onPostInflateView()
        }
        return cachedView
    }

    open fun onPostInflateView() = Unit

    open fun onShowView() = Unit

    open fun onHideView() = Unit
}
