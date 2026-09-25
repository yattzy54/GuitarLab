package app.tuxguitar.android.storage.saf

import android.content.Intent
import app.tuxguitar.android.activity.TGActivityResultHandler
import app.tuxguitar.android.activity.TGActivityResultManager

abstract class TGSafBaseHandler(val provider: TGSafProvider) : TGActivityResultHandler {
    val requestCode: Int = getResultManager().createRequestCode().also { getResultManager().addHandler(it, this) }

    override fun onActivityResult(resultCode: Int, data: Intent?) {
        getResultManager().removeHandler(requestCode, this)
    }

    fun getResultManager(): TGActivityResultManager = provider.getActivity().getResultManager()
}
