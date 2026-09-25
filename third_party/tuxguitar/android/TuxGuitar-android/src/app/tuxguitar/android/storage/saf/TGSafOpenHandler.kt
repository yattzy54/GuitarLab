package app.tuxguitar.android.storage.saf

import android.app.Activity
import android.content.Intent

class TGSafOpenHandler(provider: TGSafProvider) : TGSafBaseHandler(provider) {
    override fun onActivityResult(resultCode: Int, data: Intent?) {
        super.onActivityResult(resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data ?: return
            getProvider().getActionHandler().callReadUri(uri)
        }
    }
}
