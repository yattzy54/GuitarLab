package app.tuxguitar.android.storage

import app.tuxguitar.util.TGAbstractContext

interface TGStorageProvider {
    fun openDocument()
    fun saveDocument()
    fun saveDocumentAs()
    fun updateSession(source: TGAbstractContext)
}
