package app.tuxguitar.android.browser

import app.tuxguitar.tools.browser.base.TGBrowserCallBack

class TGBrowserEmptyCallBack<T> : TGBrowserCallBack<T> {
    override fun onSuccess(successData: T) = Unit
    override fun handleError(throwable: Throwable) {
        throwable.printStackTrace()
    }
}
