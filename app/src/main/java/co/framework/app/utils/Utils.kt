package co.framework.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import co.framework.app.R
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

object Utils {
    const val BASE_URL = "https://api.github.com"

    fun getVersionName(context: Context): String? {
        var pi: PackageInfo? = null
        try {
            pi = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return pi?.versionName
    }



    fun setPermission(
        listener: PermissionListener?,
        permissions: String?,
        context : Context
    ) {
        TedPermission.with(context)
            .setPermissionListener(listener)
            .setDeniedMessage(R.string.permission_request)
            .setPermissions(permissions)
            .setGotoSettingButton(true)
            .setGotoSettingButtonText(R.string.setting_move)
            .check()
    }


    fun IsPermissionGranted(permission : String,context: Context): Boolean {

        val wExternal_Permission = ContextCompat.checkSelfPermission(context, permission)

        return wExternal_Permission != PackageManager.PERMISSION_DENIED
    }

    fun setPrefString(context: Context, key: String, value: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }
}