package app.tuxguitar.android.fragment.impl

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.tuxguitar.android.ui.state.editorViewModel
import androidx.compose.ui.Modifier
import app.tuxguitar.android.R
import app.tuxguitar.android.fragment.TGComposeCachedFragment
import app.tuxguitar.android.menu.controller.impl.fragment.TGChannelListMenu
import app.tuxguitar.android.view.channel.TGChannelActionHandler
import app.tuxguitar.android.view.channel.TGChannelEventListener
import app.tuxguitar.android.view.channel.TGChannelListItemContent
import app.tuxguitar.android.view.util.TGProcess
import app.tuxguitar.android.view.util.TGSyncProcessLocked
import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.song.models.TGChannel
import java.util.ArrayList

class TGChannelListFragment : TGComposeCachedFragment() {
    val actionHandler: TGChannelActionHandler = TGChannelActionHandler(this)
    private val eventListener = TGChannelEventListener(this)
    private val viewModel by lazy { editorViewModel { TGChannelListViewModel() } }
    private var updateItemsProcess: TGProcess? = null

    override fun onPostCreate() {
        attachInstance()
        createActionBar(true, false, R.string.channel_list)
        createSyncProcesses()
    }

    override fun onPostCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        TGChannelListMenu.getInstance(findContext()).inflate(menu, menuInflater)
    }

    override fun onShowView() {
        TGEditorManager.getInstance(findContext()).addUpdateListener(eventListener)
        updateItems()
    }

    override fun onHideView() {
        TGEditorManager.getInstance(findContext()).removeUpdateListener(eventListener)
    }

    fun attachInstance() {
        TGChannelListFragmentController.getInstance(findContext()).attachInstance(this)
    }

    fun createSyncProcesses() {
        updateItemsProcess = TGSyncProcessLocked(findContext()) { updateItems() }
    }

    fun fireUpdateProcess() {
        updateItemsProcess?.process()
    }

    fun updateItems() {
        val newChannels = ArrayList<TGChannel>()
        val documentManager = TGDocumentManager.getInstance(findContext())
        if (documentManager.getSong() != null) {
            val it = documentManager.getSong().getChannels()
            while (it.hasNext()) {
                newChannels.add(it.next())
            }
        }
        viewModel.updateChannels(newChannels, actionHandler::isRemovableChannel)
    }

    fun updateVolume(channel: TGChannel, volume: Short) {
        viewModel.changeVolume(channel, volume) {
            actionHandler.createUpdateVolumeAction(channel, volume).process()
        }
    }

    @Composable
    override fun FragmentContent() {
        val channels by viewModel.state.collectAsStateWithLifecycle()
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(channels, key = { it.channel.channelId }) { row ->
                val channel = row.channel
                TGChannelListItemContent(
                    name = row.name,
                    volume = row.volume.toInt(),
                    onVolumeChange = { volume -> updateVolume(channel, volume.toShort()) },
                    onClick = { actionHandler.createEditChannelAction(channel).process() },
                    isRemovable = row.removable,
                    onRemove = { actionHandler.removeChannel(channel) },
                )
            }
        }
    }
}
