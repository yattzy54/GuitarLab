package app.tuxguitar.android.activity

fun interface TGActivityPermissionResultHandler {
    fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray)
}
