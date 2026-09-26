package app.tuxguitar.android.properties

import android.content.Context

abstract class TGSharedPreferencesHandler(
    context: Context,
    module: String,
    resource: String,
) : TGDataStoreHandler(context, module, resource) {
    // Kept for binary/source compatibility with legacy plugins
}
