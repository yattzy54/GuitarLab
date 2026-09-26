package app.tuxguitar.android.storage.saf

import android.content.Intent
import app.tuxguitar.android.activity.TGActivityResultHandler
import app.tuxguitar.android.activity.TGActivityResultManager

abstract class TGSafBaseHandler(private val provider: TGSafProvider) : TGActivityResultHandler {
    val requestCode: Int = provider.getActivity().getResultManager().createRequestCode()

    init {
        provider.getActivity().getResultManager().addHandler(requestCode, this)
    }

    override fun onActivityResult(resultCode: Int, data: Intent?) {
        provider.getActivity().getResultManager().removeHandler(requestCode, this)
    }

    fun getProvider(): TGSafProvider = provider
}
