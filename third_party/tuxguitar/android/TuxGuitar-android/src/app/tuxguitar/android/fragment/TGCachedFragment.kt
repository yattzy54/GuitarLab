package app.tuxguitar.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class TGCachedFragment(private val layout: Int) : TGBaseFragment() {
    private var view: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        createdView: View?,
    ): View? {
        if (view == null) {
            view = inflater.inflate(layout, container, false)
            onPostInflateView()
        }
        return view
    }

    open fun onPostInflateView() {
    }

    open fun onShowView() {
    }

    open fun onHideView() {
    }
}
