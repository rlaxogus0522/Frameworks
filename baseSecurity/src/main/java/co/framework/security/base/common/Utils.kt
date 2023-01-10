package co.framework.security.base.common

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Utils {

    val isDebug = true

    fun getVersionName(context: Context): String? {
        var pi: PackageInfo? = null
        try {
            pi = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return pi?.versionName
    }

    val hash1 = "hash1"
    val hash2 = "hash2"
    val forgery = "Forgery"

    var binaryPaths = arrayOf(
        "/data/local/",
        "/data/local/bin/",
        "/data/local/xbin/",
        "/sbin/",
        "/su/bin/",
        "/system/bin/",
        "/system/bin/.ext/",
        "/system/bin/failsafe/",
        "/system/sd/xbin/",
        "/system/usr/we-need-root/",
        "/system/xbin/",
        "/system/app/Superuser.apk",
        "/cache",
        "/data",
        "/dev"
    )

    var rootString = arrayOf(
        "su",
        "busybox",
        "/system /xbin/which"
    )



    var isRooted = true


    fun getKey(context: Context): String? {
        try {
            val pm = context.packageManager
            val info =
                pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val digest = md.digest()
                val toRet = StringBuilder()
                for (i in digest.indices) {
                    if (i != 0) toRet.append(":")
                    val b: Int = digest[i].toInt() and 0xff
                    val hex = Integer.toHexString(b)
                    if (hex.length == 1) toRet.append("0")
                    toRet.append(hex)
                }
                return toRet.toString()
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("",e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("",e.toString())
        } catch (e: Exception) {
            Log.e("",e.toString())
        }
        return ""
    }
}