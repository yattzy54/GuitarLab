package app.tuxguitar.android.fragment

import androidx.fragment.app.Fragment

interface TGFragmentController<T : Fragment> {
    fun getFragment(): T
}
