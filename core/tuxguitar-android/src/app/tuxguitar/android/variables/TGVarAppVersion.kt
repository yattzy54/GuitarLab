package app.tuxguitar.android.variables

import app.tuxguitar.util.TGVersion

class TGVarAppVersion {
    override fun toString(): String = TGVersion.CURRENT.version

    companion object {
        const val NAME = "appversion"
    }
}
