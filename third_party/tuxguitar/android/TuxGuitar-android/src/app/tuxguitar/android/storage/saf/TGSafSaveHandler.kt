package app.tuxguitar.android.storage.saf

import android.app.Activity
import android.content.Intent
import app.tuxguitar.io.base.TGFileFormat

class TGSafSaveHandler(provider: TGSafProvider, private val fileFormat: TGFileFormat) : TGSafBaseHandler(provider) {
    override fun onActivityResult(resultCode: Int, data: Intent?) {
        super.onActivityResult(resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            provider.getActionHandler().callWriteUri(data.data!!, fileFormat)
        }
    }
}
