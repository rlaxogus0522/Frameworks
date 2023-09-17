package com.framework.webview.common


import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermisionCheck {

    fun setPermission(act: Activity, listener: PermissionListener, permissions: Array<String>, permissionCode : Int) {
        if(isPermissionGranted(act, permissions)){
            listener.onPermissionGranted()
        }else{
            ActivityCompat.requestPermissions(
                act,
                permissions,
                permissionCode
            )
        }
    }


    fun isPermissionGranted(context: Context, per: Array<String>): Boolean {
        for (permission in per) {
            val wExternal_Permission = ContextCompat.checkSelfPermission(context, permission)
            if(wExternal_Permission == PackageManager.PERMISSION_DENIED)
                return false
        }
        return true
    }
}