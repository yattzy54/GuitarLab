package app.tuxguitar.android.action.installer

import app.tuxguitar.android.action.TGActionMap

class TGActionConfigMap {
    private val map = TGActionMap<TGActionConfig>()

    fun get(actionId: String): TGActionConfig? = map.get(actionId)
    fun set(actionId: String, config: TGActionConfig?) = map.set(actionId, config)
}
