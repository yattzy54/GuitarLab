package app.tuxguitar.android.view.channel

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView
import android.widget.RelativeLayout
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.android.view.util.TGProcess
import app.tuxguitar.android.view.util.TGSyncProcessLocked
import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.song.models.TGChannel
import app.tuxguitar.util.TGContext
import java.util.ArrayList

class TGChannelListView(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    val actionHandler: TGChannelActionHandler = TGChannelActionHandler(this)
    private var updateItemsProcess: TGProcess? = null

    init {
        this.createSyncProcesses()
    }

    override fun onFinishInflate() {
        this.fillListView()
        this.addListeners()
        this.updateItems()
    }

    fun fillListView() {
        val listView = this.findViewById<ListView>(R.id.channel_list_items)
        listView.adapter = TGChannelListAdapter(this)
    }

    fun addListeners() {
        TGEditorManager.getInstance(this.findContext()).addUpdateListener(TGChannelEventListener(this))
    }

    fun updateItems() {
        val channels = ArrayList<TGChannel>()
        val documentManager = TGDocumentManager.getInstance(this.findContext())
        if (documentManager.getSong() != null) {
            val it = documentManager.getSong().getChannels()
            while (it.hasNext()) {
                channels.add(it.next())
            }
        }
        this.refreshListView(channels)
    }

    fun refreshListView(channels: List<TGChannel>) {
        val listView = this.findViewById<ListView>(R.id.channel_list_items)

        val tgChannelListAdapter = listView.adapter as TGChannelListAdapter
        tgChannelListAdapter.setChannels(channels)
        tgChannelListAdapter.notifyDataSetChanged()
    }

    fun fireUpdateProcess() {
        this.updateItemsProcess!!.process()
    }

    fun findContext(): TGContext = TGApplicationUtil.findContext(this)

    fun findActivity(): TGActivity = this.context as TGActivity

    fun createSyncProcesses() {
        this.updateItemsProcess = TGSyncProcessLocked(this.findContext()) {
            this.updateItems()
        }
    }
}
