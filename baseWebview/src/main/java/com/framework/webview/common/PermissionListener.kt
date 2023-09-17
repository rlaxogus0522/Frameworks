package com.framework.webview.common

import java.io.IOException

interface PermissionListener {
    @Throws(IOException::class)
    fun onPermissionGranted()
    fun onPermissionDenied(deniedPermissions: List<String?>?)
}
