package app.tuxguitar.android.navigation

import app.tuxguitar.android.fragment.TGFragmentController

class TGNavigationFragment {
    var tagId: String? = null
    var controller: TGFragmentController<*>? = null

    override fun hashCode(): Int {
        tagId?.let { return "${javaClass.name}-${it.hashCode()}".hashCode() }
        controller?.let { return "${javaClass.name}-fr-${it.hashCode()}".hashCode() }
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean =
        other is TGNavigationFragment && hashCode() == other.hashCode()
}
