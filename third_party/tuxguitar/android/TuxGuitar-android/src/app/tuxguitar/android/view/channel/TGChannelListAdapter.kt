package app.tuxguitar.android.view.channel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SeekBar
import android.widget.TextView
import app.tuxguitar.android.R
import app.tuxguitar.android.view.util.TGProcess
import app.tuxguitar.android.view.util.TGSyncProcessLocked
import app.tuxguitar.song.models.TGChannel

class TGChannelListAdapter(private val channelList: TGChannelListView) : BaseAdapter() {

    private var channels: List<TGChannel>? = null
    private var notifyDataSetChangedLater: TGProcess? = null
    private var eventInProgress = false

    init {
        this.createSyncProcesses()
    }

    fun setChannels(channels: List<TGChannel>) {
        this.channels = channels
    }

    override fun getCount(): Int = if (this.channels != null) this.channels!!.size else 0

    override fun getItem(position: Int): Any? = if (this.channels != null && this.channels!!.size > position) this.channels!![position] else null

    override fun getItemId(position: Int): Long = position.toLong()

    fun getLayoutInflater(): LayoutInflater = this.channelList.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val channel = this.getItem(position) as TGChannel

        val view = convertView ?: this.getLayoutInflater().inflate(R.layout.view_channel_list_item, parent, false)
        view.tag = channel
        view.setOnClickListener(this.channelList.actionHandler.createEditChannelAction(channel))
        view.setOnLongClickListener(this.channelList.actionHandler.createChannelItemMenuAction(channel, view))

        val textViewName = view.findViewById<TextView>(R.id.channel_item_name)
        textViewName.text = channel.name

        val seekBarVolume = view.findViewById<SeekBar>(R.id.channel_item_volume_value)
        seekBarVolume.tag = channel
        seekBarVolume.progress = channel.volume.toInt()
        seekBarVolume.setOnSeekBarChangeListener(this.createVolumeChangeListener())

        return view
    }

    fun updateVolume(channel: TGChannel, volume: Short) {
        if (volume != channel.volume && volume >= 0 && volume <= 127) {
            this.channelList.actionHandler.createUpdateVolumeAction(channel, volume).process()
        }
    }

    private fun createVolumeChangeListener(): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                this@TGChannelListAdapter.eventInProgress = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                this@TGChannelListAdapter.eventInProgress = false
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress >= 0 && progress <= 127) {
                    this@TGChannelListAdapter.updateVolume(seekBar.tag as TGChannel, progress.toShort())
                }
            }
        }
    }

    override fun notifyDataSetChanged() {
        if (!this.eventInProgress) {
            super.notifyDataSetChanged()
        } else {
            this.notifyDataSetChangedLater?.process()
        }
    }

    fun createSyncProcesses() {
        this.notifyDataSetChangedLater = TGSyncProcessLocked(this.channelList.findContext()) {
            this.notifyDataSetChanged()
        }
    }
}
