package app.tuxguitar.android.fragment

abstract class TGCachedFragmentController<T : TGBaseFragment> : TGFragmentController<T> {
    @Volatile
    private var instance: T? = null

    abstract fun createNewInstance(): T

    fun findOrCreateInstance(): T {
        synchronized(TGCachedFragmentController::class.java) {
            if (instance == null) {
                instance = createNewInstance()
            }
            return instance!!
        }
    }

    fun attachInstance(instance: T) {
        synchronized(TGCachedFragmentController::class.java) {
            if (this.instance !== instance) {
                this.instance = instance
            }
        }
    }

    fun detachInstance(instance: T) {
        synchronized(TGCachedFragmentController::class.java) {
            if (this.instance === instance) {
                this.instance = null
            }
        }
    }

    override fun getFragment(): T = findOrCreateInstance()
}
