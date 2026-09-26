package app.tuxguitar.android.transport

import app.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.player.base.MidiPlayerException
import app.tuxguitar.player.impl.sequencer.MidiSequencerProviderImpl
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.error.TGErrorManager
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGTransportAdapter private constructor(private val context: TGContext) {
    fun initialize() {
        try {
            val midiPlayer = MidiPlayer.getInstance(context)
            midiPlayer.addListener(TGTransportListener(context))
            midiPlayer.addSequencerProvider(MidiSequencerProviderImpl(context), true)

            appendListeners()
            callLoadSettings()
        } catch (e: MidiPlayerException) {
            TGErrorManager.getInstance(context).handleError(e)
        }
    }

    fun callLoadSettings() {
        TGActionProcessor(context, TGTransportLoadSettingsAction.NAME).process()
    }

    fun appendListeners() {
        TGEditorManager.getInstance(context).addDestroyListener(TGTransportDestroyListener(this))
    }

    fun destroy() {
        MidiPlayer.getInstance(context).close()
    }

    fun playBeat(beat: TGBeat?) {
        TGEditorManager.getInstance(context).asyncRunLocked {
            val midiPlayer = MidiPlayer.getInstance(context)
            if (!midiPlayer.isRunning) {
                midiPlayer.playBeat(beat)
            }
        }
    }

    fun loadSettings() {
        val tgTransportProperties = TGTransportProperties(context)
        tgTransportProperties.load()

        val outputPortKey = tgTransportProperties.getMidiOutputPort()
        MidiPlayer.getInstance(context).openOutputPort(outputPortKey, outputPortKey == null)
    }

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGTransportAdapter {
            return TGSingletonUtil.getInstance(context, TGTransportAdapter::class.java.name, object : TGSingletonFactory<TGTransportAdapter> {
                override fun createInstance(context: TGContext): TGTransportAdapter = TGTransportAdapter(context)
            })
        }
    }
}
