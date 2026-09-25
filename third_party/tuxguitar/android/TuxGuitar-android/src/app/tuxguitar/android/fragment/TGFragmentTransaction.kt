package app.tuxguitar.android.fragment

import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle

class TGFragmentTransaction(
    private val handle: FragmentTransaction,
    private val forceAllowingStateLoss: Boolean
) : FragmentTransaction() {
    constructor(fragmentManager: FragmentManager, forceAllowingStateLoss: Boolean) :
        this(fragmentManager.beginTransaction(), forceAllowingStateLoss)

    @NonNull
    override fun add(@NonNull fragment: Fragment, @Nullable tag: String?): FragmentTransaction =
        handle.add(fragment, tag)

    @NonNull
    override fun add(containerViewId: Int, @NonNull fragment: Fragment): FragmentTransaction =
        handle.add(containerViewId, fragment)

    @NonNull
    override fun add(
        containerViewId: Int,
        @NonNull fragment: Fragment,
        @Nullable tag: String?
    ): FragmentTransaction = handle.add(containerViewId, fragment, tag)

    @NonNull
    override fun replace(containerViewId: Int, @NonNull fragment: Fragment): FragmentTransaction =
        handle.replace(containerViewId, fragment)

    @NonNull
    override fun replace(
        containerViewId: Int,
        @NonNull fragment: Fragment,
        @Nullable tag: String?
    ): FragmentTransaction = handle.replace(containerViewId, fragment, tag)

    @NonNull
    override fun remove(@NonNull fragment: Fragment): FragmentTransaction = handle.remove(fragment)

    @NonNull
    override fun hide(@NonNull fragment: Fragment): FragmentTransaction = handle.hide(fragment)

    @NonNull
    override fun show(@NonNull fragment: Fragment): FragmentTransaction = handle.show(fragment)

    @NonNull
    override fun detach(@NonNull fragment: Fragment): FragmentTransaction = handle.detach(fragment)

    @NonNull
    override fun attach(@NonNull fragment: Fragment): FragmentTransaction = handle.attach(fragment)

    @NonNull
    override fun setPrimaryNavigationFragment(@Nullable fragment: Fragment?): FragmentTransaction =
        handle.setPrimaryNavigationFragment(fragment)

    @NonNull
    override fun setMaxLifecycle(
        @NonNull fragment: Fragment,
        @NonNull state: Lifecycle.State
    ): FragmentTransaction = handle.setMaxLifecycle(fragment, state)

    override fun isEmpty(): Boolean = handle.isEmpty()

    @NonNull
    override fun setCustomAnimations(enter: Int, exit: Int): FragmentTransaction =
        handle.setCustomAnimations(enter, exit)

    @NonNull
    override fun setCustomAnimations(
        enter: Int,
        exit: Int,
        popEnter: Int,
        popExit: Int
    ): FragmentTransaction = handle.setCustomAnimations(enter, exit, popEnter, popExit)

    @NonNull
    override fun addSharedElement(@NonNull sharedElement: View, @NonNull name: String): FragmentTransaction =
        handle.addSharedElement(sharedElement, name)

    @NonNull
    override fun setTransition(transition: Int): FragmentTransaction = handle.setTransition(transition)

    @NonNull
    override fun setTransitionStyle(styleRes: Int): FragmentTransaction = handle.setTransitionStyle(styleRes)

    @NonNull
    override fun addToBackStack(@Nullable name: String?): FragmentTransaction = handle.addToBackStack(name)

    override fun isAddToBackStackAllowed(): Boolean = handle.isAddToBackStackAllowed()

    @NonNull
    override fun disallowAddToBackStack(): FragmentTransaction = handle.disallowAddToBackStack()

    @NonNull
    override fun setBreadCrumbTitle(res: Int): FragmentTransaction = handle.setBreadCrumbTitle(res)

    @NonNull
    override fun setBreadCrumbTitle(@Nullable text: CharSequence?): FragmentTransaction =
        handle.setBreadCrumbTitle(text)

    @NonNull
    override fun setBreadCrumbShortTitle(res: Int): FragmentTransaction =
        handle.setBreadCrumbShortTitle(res)

    @NonNull
    override fun setBreadCrumbShortTitle(@Nullable text: CharSequence?): FragmentTransaction =
        handle.setBreadCrumbShortTitle(text)

    @NonNull
    override fun setReorderingAllowed(reorderingAllowed: Boolean): FragmentTransaction =
        handle.setReorderingAllowed(reorderingAllowed)

    @Deprecated("Deprecated in FragmentTransaction")
    @NonNull
    override fun setAllowOptimization(allowOptimization: Boolean): FragmentTransaction =
        handle.setAllowOptimization(allowOptimization)

    @NonNull
    override fun runOnCommit(@NonNull runnable: Runnable): FragmentTransaction = handle.runOnCommit(runnable)

    override fun commit(): Int =
        if (forceAllowingStateLoss) commitAllowingStateLoss() else handle.commit()

    override fun commitAllowingStateLoss(): Int = handle.commitAllowingStateLoss()

    override fun commitNow() {
        if (forceAllowingStateLoss) {
            commitAllowingStateLoss()
        } else {
            handle.commitNow()
        }
    }

    override fun commitNowAllowingStateLoss() {
        handle.commitNowAllowingStateLoss()
    }
}
