package app.tuxguitar.android.drawer.main

import android.content.Context
import android.view.LayoutInflater
import android.widget.BaseAdapter

abstract class TGMainDrawerListAdapter(private val mainDrawer: TGMainDrawer) : BaseAdapter() {
    override fun getItemId(position: Int): Long = position.toLong()

    fun getLayoutInflater(): LayoutInflater =
        mainDrawer.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun getMainDrawer(): TGMainDrawer = mainDrawer

    open fun attachListeners() = Unit

    open fun detachListeners() = Unit
}
