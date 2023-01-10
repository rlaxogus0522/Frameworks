package co.framework.webview.base

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Environment
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import co.framework.webview.common.PermisionCheck
import co.framework.webview.common.Url
import co.framework.webview.common.Utils
import co.framework.webview.commonString.*
import co.framework.webview.dialog.DevelopPopup
import com.gun0912.tedpermission.PermissionListener
import kotlinx.android.synthetic.main.dialog_develop_popup.*
import java.io.File
import java.lang.IllegalArgumentException


//fun String.isEmptyReplace(replace: String): String {
//    return if (TextUtils.isEmpty(this))
//        replace
//    else
//        this
//}
@Suppress("UNCHECKED_CAST")
abstract class SettingBuilder<T : SettingBuilder<BaseWebviewSetting.Builder>>(private val context: Context) {
    private val TAG = SettingBuilder::class.simpleName
    private var userAgent : String? = null
    private var webview : WebView? = null
    private var appVersion : String? = null
    private var clRoot : ConstraintLayout? = null
    private var showDevelopDialog : Boolean = false
    var mChildWebView: WebView? = null
    lateinit var developPopup: DevelopPopup
    private var downloadReceiver: BroadcastReceiver? = null     //다운로드 완료 리시버


    /**
     * UserAgent 이름 셋팅
     */
    fun setWebView(webview : WebView) : T {
        this.webview = webview
        return this as T
    }

    /**
     * UserAgent 이름 셋팅
     */
    fun setUserAgent(agent : String) : T {
        this.userAgent = agent
        return this as T
    }

    /**
     * 앱 버전 셋팅, UserAgent 전송 시 함께 전송
     */
    fun setAppVersion(appVersion : String) : T {
        this.appVersion = appVersion
        return this as T
    }

    /**
     * 팝업화면을 위한 셋팅, webview의 부모 ConstraintLayout ID값
     */
    fun setConstraintLayoutRoot(clRoot : ConstraintLayout) : T {
        this.clRoot = clRoot
        return this as T
    }

    /**
     * 팝업화면이 아닌 외부로 이동되어야할 URL 추가 셋팅
     */
    fun setOutLinkUrl(array : ArrayList<String>) : T {
        Url.OUT_LINK_URLS.addAll(array)
        return this as T
    }

    /**
     * 스키마 동작 정의를 위한 스키마명 작성
     */
    fun setSchemeName(scheme : String) : T {
        SCHEME_INTENT = scheme
        return this as T
    }

    /**
     * OUT_LINK 스키마 변경
     */
    fun customOutLinkIntent(intent : String) : T {
        OUT_LINK = intent
        return this as T
    }

    /**
     * OUT_LINK 파라미터 변경
     */
    fun customOutLinkParameterIntent(intent : String) : T {
        OUT_LINK_QUERY_PARAMETER = intent
        return this as T
    }

    /**
     * SAVE_FILE 스키마 변경
     */
    fun customSaveFileIntent(intent : String) : T {
        SAVE_FILE = intent
        return this as T
    }

    /**
     * SAVE_FILE_ 파라미터 변경
     */
    fun customSaveFileParameterIntent(intent : String) : T {
        SAVE_FILE_QUERY_PARAMETER = intent
        return this as T
    }
    /**
     * SAVE_FILE_ 파라미터 변경
     */
    fun customSaveFileFolderName(intent : String) : T {
        FOLDER_NAME = intent
        return this as T
    }

    /**
     * setProvider
     */
    fun setProvider(intent : String) : T {
        PROVIDER = intent
        return this as T
    }



    /**
     * UPLOAD_FILE 스키마 변경
     */
    fun customUploadFileIntent(intent : String) : T {
        UPLOAD_FILE = intent
        return this as T
    }

    /**
     * MOVE_STORE 스키마 변경
     */
    fun customMoveStoreIntent(intent : String) : T {
        MOVE_STORE = intent
        return this as T
    }

    /**
     * DEVELOP 팝업 노출 여부
     */
    fun showDevelopDialog(data : Boolean) : T {
        showDevelopDialog = data
        return this as T
    }


    /**
     * DEVELOP URL 1
     */
    fun setDevelopDialogUrl1(url : String) : T {
        Url.OPERATION_URL = url
        return this as T
    }



    /**
     * DEVELOP URL 2
     */
    fun setDevelopDialogUrl2(url : String) : T {
        Url.DEVELOP_URL = url
        return this as T
    }



    /**
     * DEVELOP URL 3
     */
    fun setDevelopDialogUrl3(url : String) : T {
        Url.ETC_URL = url
        return this as T
    }



    /**
     * ACCESS URL
     */
    fun setMainUrl(url : String) : T {
        Url.ACCESS_URL = url
        return this as T
    }



    /**
     * DEVELOP 팝업
     */
    private fun DevelopDialog() {
            developPopup = DevelopPopup.createDialog(context, "개발 모드", false)
            developPopup.onRightClick("접속", View.OnClickListener {
                when {
                    developPopup.ck1.isChecked -> {
                        if (!TextUtils.isEmpty(developPopup.et_url.text.toString())) {
                            Url.ACCESS_URL = developPopup.et_url.text.toString()
                        }
                    }
                    developPopup.ck2.isChecked -> {
                        if (!TextUtils.isEmpty(developPopup.et_url2.text.toString())) {
                            Url.ACCESS_URL = developPopup.et_url2.text.toString()
                        }
                    }
                    developPopup.ck3.isChecked -> {
                        if (!TextUtils.isEmpty(developPopup.et_url3.text.toString())) {
                            Url.ACCESS_URL = developPopup.et_url3.text.toString()
                        }
                    }
                }

                Utils.setPrefString(context, "url1", developPopup.et_url.text.toString())
                Utils.setPrefString(context, "url2", developPopup.et_url2.text.toString())
                Utils.setPrefString(context, "url3", developPopup.et_url3.text.toString())

                showDevelopDialog(false)
                setWebViewSetting()

                developPopup.dismiss()

            })
            developPopup.show()
    }



    inner class InAppWebViewClient : WebViewClient() {
        override fun onReceivedSslError(
            view: WebView,
            handler: SslErrorHandler,
            error: SslError
        ) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("SSL 페이지 오류 입니다.\n에러코드" + error.primaryError)
            builder.setNegativeButton(
                "확인"
            ) { dialog: DialogInterface?, which: Int -> handler.cancel() }
            val dialog = builder.create()
            dialog.show()
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

            CookieManager.getInstance().flush()
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            var url = ""

            if (request == null) {
                return false
            } else {
                url = request.url.toString()
            }

            return customOverride(url)
        }
    }
    
    
    fun customOverride(url : String) : Boolean{

       return when {

            /**
             * 전화 걸기
             */
            url.startsWith("tel:") -> {
                val permission = Manifest.permission.CALL_PHONE

                Uri.parse(url).let {
                    PermisionCheck.setPermission(
                        object : PermissionListener {
                            override fun onPermissionGranted() {
                                if (ActivityCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CALL_PHONE
                                    ) != PackageManager.PERMISSION_DENIED
                                ) {
                                    val intent = Intent(Intent.ACTION_CALL, it)
                                    context.startActivity(intent)
                                    return
                                }
                            }

                            override fun onPermissionDenied(deniedPermissions: List<String>) {}
                        }, permission,
                        context
                    )
                }
                true
            }

            /**
             * 외부 URL 링크 이동
             */
            url.startsWith("$SCHEME_INTENT://${OUT_LINK}", true) -> {
                try {
                    if (url.contains("${OUT_LINK_QUERY_PARAMETER}=")) {
                        val tempUri = Uri.parse(url)
                        val outLinkUri =
                            Uri.parse(tempUri.getQueryParameter(OUT_LINK_QUERY_PARAMETER))
                        val intent = Intent(Intent.ACTION_VIEW, outLinkUri)
                        context.startActivity(intent)
                    }
                } catch (e: java.lang.Exception) {
                    Log.e(TAG, e.toString())
                }

                true
            }



            /**
             *  파일 저장 및 호출 리스너
             */
            url.startsWith("$SCHEME_INTENT://${SAVE_FILE}", true) -> {
                url.let{
                    PermisionCheck.setPermission(
                        object : PermissionListener {
                            override fun onPermissionGranted() {
                                //read, write 퍼미션 체크
                                try {
                                    if (it.contains("${SAVE_FILE_QUERY_PARAMETER}=")) {
                                        val tempUri = Uri.parse(it)
                                        var downloadUrl = ""
                                        if (!tempUri.getQueryParameter(SAVE_FILE_QUERY_PARAMETER).isNullOrEmpty()) {
                                            downloadUrl = tempUri.getQueryParameter(SAVE_FILE_QUERY_PARAMETER)!!
                                            onDownloadStart(downloadUrl)
                                        }
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onPermissionDenied(deniedPermissions: List<String>) {
                                (context as Activity).finish()
                            }
                        },
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        context
                    )
                }

                true
            }

            /**
             * 파일 멀티 파트 전송
             */
            url.startsWith("$SCHEME_INTENT://${UPLOAD_FILE}", true) -> {

                true
            }

            /**
             * 스토어 페이지 이동
             */
            url.startsWith("$SCHEME_INTENT://${MOVE_STORE}", true) -> {
                if (url.contains("${MOVE_STORE_QUERY_PARAMETER}=")) {
                    val tempUri = Uri.parse(url)
                    val packageName = tempUri.getQueryParameter(MOVE_STORE_QUERY_PARAMETER)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("market://details?id=$packageName")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                return true
            }


            else -> false
        }
    }

    /** 파일 다운로드 시작 */
    private fun onDownloadStart(url: String) {
        try {
            val mtype = MimeTypeMap.getSingleton()
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(url)
            val fileNameList = url.split("/").toTypedArray()
            val fileName: String

            fileName = try {
                fileNameList[fileNameList.size - 1]
            } catch (e: java.lang.Exception) {
                return
            }

            // MIME Type을 확장자를 통해 예측한다.
            val fileExtension =
                fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
            val mimeType = mtype.getMimeTypeFromExtension(fileExtension)
            // Download 디렉토리에 저장하도록 요청을 작성
            val request = DownloadManager.Request(downloadUri)
            request.setTitle(fileName)
            request.setDescription(url)
            request.setMimeType(mimeType)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "/$FOLDER_NAME/$fileName"
            )

            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/$FOLDER_NAME")
                    .absoluteFile
            path.mkdirs()
            if (isDownloadFileExists(path.absolutePath + "/" + fileName)) {
                deleteDownloadFile(path.absolutePath + "/" + fileName)
            }

            val completeFilter =
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            downloadReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    try {
                        context.unregisterReceiver(downloadReceiver)
                        if (!(context as Activity).isDestroyed) {
                            Toast.makeText(
                                context,
                                "$fileName\n다운로드 완료",
                                Toast.LENGTH_SHORT
                            ).show()
                            openFile(path.absolutePath, fileName)
                            if (mChildWebView != null) {
                                closeChildWebView()
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e(TAG, e.toString())
                    }
                }
            }
            context.registerReceiver(downloadReceiver, completeFilter)
            downloadManager.enqueue(request)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, e.toString())
            Toast.makeText(context, "다운로드 실패하였습니다.", Toast.LENGTH_SHORT).show()
            PermisionCheck.setPermission(object : PermissionListener {
                override fun onPermissionGranted() {}
                override fun onPermissionDenied(arrayList: List<String>) {}
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE, context)
        }
    }

    /** 다운로드 파일 열기 */
    private fun openFile(
        path: String,
        filename: String
    ) {
        try {
            val file = File("$path/$filename")
            val map = MimeTypeMap.getSingleton()
            //다운로드 리스너를 거쳐 들어오면 확장자가 ""로 떨어져 직접 확장자를 구하는 것으로 변경
//            val ext = MimeTypeMap.getFileExtensionFromUrl(file.name)
            val ext = file.toString().substring(file.toString().lastIndexOf(".") + 1).toLowerCase()
            var type = map.getMimeTypeFromExtension(ext)
            if (type == null) type = "*/*"
            val intent = Intent(Intent.ACTION_VIEW)
            //Uri data = Uri.fromFile(file);
            val data = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName
                    .toString() + PROVIDER,
                file
            )
            intent.setDataAndType(data, type)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, e.toString())
        }
    }

    /** 다운로드 파일이 존재 할 경우 */
    private fun isDownloadFileExists(pathName: String): Boolean {
        val folder1 = File(pathName)
        return folder1.exists()
    }

    /** 기존 다운로드 파일 삭제 */
    private fun deleteDownloadFile(pathName: String): Boolean {
        val folder1 = File(pathName)
        return folder1.delete()
    }




    //웹뷰에 사용하는 chromeClient 특정 팝업의 예외처리, Geolocation API, 파일 선택, 이미지 선택
    inner class InAppChromeClient : WebChromeClient() {
        override fun onGeolocationPermissionsShowPrompt(
            origin: String,
            callback: GeolocationPermissions.Callback
        ) { // Geolocation API 사용
            callback.invoke(origin, true, false)
        }

        override fun onCreateWindow(
            view: WebView,
            dialog: Boolean,
            userGesture: Boolean,
            resultMsg: Message
        ): Boolean {
            val href = view.handler.obtainMessage()
            val result = view.hitTestResult
            val type = result.type
            val url = result.extra
            if (checkOutLink(url)) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                context.startActivity(i)
                return false
            }
            mChildWebView = WebView(view.context)
            mChildWebView?.let {
                it.webViewClient = InAppWebViewClient()
                it.webChromeClient = object : WebChromeClient() {
                    override fun onCloseWindow(window: WebView) {
                        super.onCloseWindow(window)
                        closeChildWebView()
                    }
                }
                val settings: WebSettings = it.settings
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.pluginState = WebSettings.PluginState.ON
                settings.javaScriptCanOpenWindowsAutomatically = true


                it.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                clRoot!!.addView(it)
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = it
                resultMsg.sendToTarget()
            }

            return true
        }

    }

    fun closeChildWebView() {
        mChildWebView?.let {
            it.destroy()
//            cl_root.removeView(it)
            mChildWebView = null
        }
    }

    fun checkOutLink(url: String?): Boolean {
        if (url == null) return false
        for (i in Url.OUT_LINK_URLS.indices) {
            if (url.contains(Url.OUT_LINK_URLS[i])) return true
        }
        return false
    }


    protected fun setWebViewSetting() {

        when {
            webview == null -> {
                throw IllegalArgumentException("You must setWebView() on baseWebView")
            }
            userAgent == null -> {
                throw IllegalArgumentException("You must setUserAgent() on baseWebView")
            }
            clRoot == null -> {
                throw IllegalArgumentException("You must setClRoot() on baseWebView")
            }

            SCHEME_INTENT.isEmpty() -> {
                throw IllegalArgumentException("You must setSchemeName() on baseWebView")
            }

            else -> {

                if(showDevelopDialog){
                    DevelopDialog()
                    return
                }

                webview!!.webViewClient = InAppWebViewClient()
                webview!!.webChromeClient = InAppChromeClient()


                val settings: WebSettings = webview!!.settings
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.pluginState = WebSettings.PluginState.ON
                settings.setSupportMultipleWindows(true)
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.allowFileAccess = true
                settings.setGeolocationEnabled(true)
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                // 가로에 맞춰서 전체 화면 다 보이게 --cerick--
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setAppCacheEnabled(true)
                settings.userAgentString =
                    settings.userAgentString + " " + "${userAgent}-app-Agent : " + appVersion + " " + "${userAgent}-app-Platform : A " + Build.VERSION.RELEASE
                Log.d(TAG, settings.userAgentString)

                webview!!.loadUrl(Url.ACCESS_URL)
            }
        }


    }




}