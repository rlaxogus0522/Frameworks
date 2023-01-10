package co.framework.webview.common


import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

object PermisionCheck {
    fun setPermission(
        listener: PermissionListener?,
        permissions: String?,
        context : Context
    ) {
        TedPermission.with(context)
            .setPermissionListener(listener)
            .setDeniedMessage("권한 요청을 취소하시면 진행을 할 수 없습니다.\\n[설정] > [권한]에서 권한을 허용하세요.")
            .setPermissions(permissions)
            .setGotoSettingButton(true)
            .setGotoSettingButtonText("설정화면으로 이동합니다.")
            .check()
    }


    fun IsPermissionGranted(per : String,context: Context): Boolean {
        val permission = per

        val wExternalPermission = ContextCompat.checkSelfPermission(context, permission)

        return wExternalPermission != PackageManager.PERMISSION_DENIED
    }
}