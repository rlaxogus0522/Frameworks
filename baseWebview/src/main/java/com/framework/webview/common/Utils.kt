package com.framework.webview.common

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    val isDebug = true

    fun getVersionName(context: Context): String? {
        var pi: PackageInfo? = null
        try {
            pi = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("Utils", e.message.toString())
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



    fun setPrefString(context: Context, key: String, value: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getUrlPrefString(context: Context, key: String): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return when(key){
            "url1" -> {
                prefs.getString(key, Url.ACCESS_URL)
            }
            "url2" -> {
                prefs.getString(key, Url.DEVELOP_URL)
            }
            "url3" -> {
                prefs.getString(key, Url.STAGE_URL)
            }
            else -> {
                prefs.getString(key, Url.ACCESS_URL)
            }
        }
    }

    /** 임시 이미지파일 생성 */
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFileImageUpload(context: Context): String {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).absolutePath
    }

    fun uriToBitmap(context: Context, selectedImage: Uri): Bitmap? {
        val imageStream = context.contentResolver.openInputStream(selectedImage)
        return BitmapFactory.decodeStream(imageStream)
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // 이미지 사이즈를 체크할 원본 이미지 가로/세로 사이즈를 임시 변수에 대입.
        var originalWidth = options.outWidth
        var originalHeight = options.outHeight

        // 원본 이미지 비율인 1로 초기화
        var size = 1

        // 해상도가 깨지지 않을만한 요구되는 사이즈까지 2의 배수의 값으로 원본 이미지를 나눈다.
        while (reqWidth < originalWidth || reqHeight < originalHeight) {
            originalWidth /= 2
            originalHeight /= 2
            size *= 2
        }
        return size
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
        selectedImage.path?.let {
            val ei = ExifInterface(it)
            return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
                else -> img
            }
        }
        return img
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    @JvmStatic
    fun getRealPathFromURI(context: Context?, uri: Uri): String? { // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) { // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    (Environment.getExternalStorageDirectory().toString() + "/"
                            + split[1])
                } else {
                    val SDcardpath: String? = getRemovableSDCardPath(context)?.split("/Android")?.get(0)
                    SDcardpath + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id: String = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                    Uri.parse(checkUri("content://downloads/public_downloads")),
                    java.lang.Long.valueOf(id))
                return context?.let { getDataColumn(it, contentUri, null, null) }
            } else if (isMediaDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                var selectionArgs: Array<String?>? = null
                selectionArgs = arrayOf(split[1])
                return context?.let { getDataColumn(it, contentUri, selection, selectionArgs) }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) { // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else context?.let {
                getDataColumn(
                    it, uri, null, null)
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String?>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            uri?.let {
                cursor = context.contentResolver.query(
                    it, projection,
                    selection, selectionArgs, null
                )
                if (cursor != null && cursor?.moveToFirst() == true) {
                    val index: Int? = cursor?.getColumnIndexOrThrow(column)
                    index?.let {
                        return cursor?.getString(it)
                    }
                }
            }

        } finally {
            cursor?.close()
        }
        return null
    }

    private fun getRemovableSDCardPath(context: Context?): String? {
        if(context != null){
            val storages: Array<File?> = ContextCompat.getExternalFilesDirs(context, null)
            return if (storages.size > 1 && storages[0] != null && storages[1] != null) storages[1].toString() else ""
        }
        return ""
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri
            .authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri
            .authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri
            .authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri
            .authority
    }

    @JvmName("getRealPathFromURI1")
    fun getRealPathFromURI(context: Context, uri: Uri): String? {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split: Array<String?> = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    (Environment.getExternalStorageDirectory().toString() + "/"
                            + split[1])
                } else {
                    val SDcardpath = getRemovableSDCardPath(context)?.split("/Android".toRegex())?.toTypedArray()
                        ?.get(0)
                    SDcardpath + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse(checkUri("content://downloads/public_downloads")),
                    java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split: Array<String?> = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection,
                    selectionArgs)
            }
        } else if (uri != null) {
            if ("content".equals(uri.getScheme(), ignoreCase = true)) {
                // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.getLastPathSegment() else getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
                return uri.getPath()
            }
        }
        return null
    }

    /** 폴더 전체 삭제 */
    fun deleteDownloadFolder(pathName: String): Boolean {
        val file = File(pathName)
        try {
            val childFileList = file.listFiles()

            childFileList.forEach {
                if (it.isDirectory) {
                    deleteDownloadFolder(it.absolutePath)
                } else {
                    it.delete()
                }
            }
            file.delete()
            return true
        } catch (e: IOException) {
            return false
        } catch (e: Exception) {
            return false
        }
    }

    fun isNetworkConneted(context : Context): Boolean {
        try{
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

            return isConnected
        } catch ( e: NullPointerException ){
            Log.e( "",e.localizedMessage )
        } catch ( e: Exception ){
            Log.e( "",e.localizedMessage )
        }
        return false
    }

    fun checkUri(str : String) : String{
        val Filter = "[\\\\@#$%]"
        return str.replace(Filter.toRegex(),"")
    }
}