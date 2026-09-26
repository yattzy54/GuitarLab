package app.tuxguitar.android.fragment

interface TGFragmentController<T : TGScreen> {
    fun getFragment(): T
}
