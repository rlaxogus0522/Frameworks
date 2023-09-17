package com.framework.webview.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.*
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.framework.webview.BuildConfig
import com.framework.webview.common.PermisionCheck
import com.framework.webview.common.PermisionCheck.setPermission
import com.framework.webview.common.PermissionListener
import com.framework.webview.common.Url
import com.framework.webview.common.Utils
import com.framework.webview.commonString.*
import com.framework.webview.dialog.DevelopPopup
import kotlinx.android.synthetic.main.dialog_develop_popup.*
import java.io.File
import java.io.IOException
import java.net.URLDecoder
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.reflect.KFunction0

@Suppress("UNCHECKED_CAST")
abstract class SettingBuilder<T : SettingBuilder<BaseWebviewSetting.Builder>>(private val context: Context,private val act: Activity) {
    private val TAG = SettingBuilder::class.simpleName
    private var userAgent : String? = null
    private var webview : WebView? = null
    private var appVersion : String? = null
    private var clRoot : ConstraintLayout? = null
    private var showDevelopDialog : Boolean = false
    var mChildWebView: WebView? = null
    lateinit var developPopup: DevelopPopup
    private var downloadReceiver: BroadcastReceiver? = null     //다운로드 완료 리시버
    lateinit var Function: (Uri) -> Boolean
    lateinit var FunctionPageFinish: (WebView, String) -> Unit
    lateinit var FileFunction: (WebView?, ValueCallback<Array<Uri?>>?,WebChromeClient.FileChooserParams?) -> Boolean

    lateinit var FunctionStartDownLoad: (Boolean) -> Unit
    lateinit var FunctionSetProgress: (Int) -> Unit
    val PERMISSION_CODE_WRITE_EXTERNAL_STORAGE = 1001
    val REQUIRED_PERMISSIONS: Array<String> = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    var isPageFinish = false

    private val UPDATE_DOWNLOAD_PROGRESS = 1
    private var executor = Executors.newFixedThreadPool(1)
    private val mainHandler = Handler(Looper.getMainLooper()) { msg ->
        if (msg.what == UPDATE_DOWNLOAD_PROGRESS) {
            val downloadProgress = msg.arg1
            Log.d("LLLL", downloadProgress.toString());

            // Update your progress bar here.
//            progressBar.setProgress(downloadProgress)
        }
        true
    }


    /**
     * Custom URL 스키마 설정
     */
    fun customUrlScheme(Function : (Uri) -> Boolean) : T {
        this.Function = Function
        return this as T
    }

    /**
     * Custom URL 스키마 설정
     */
    fun customPageFinished(FunctionPageFinish : (WebView, String) -> Unit) : T {
        this.FunctionPageFinish = FunctionPageFinish
        return this as T
    }

    /**
     * 다운로드 시작
     */
    fun customDownLoadStart(FunctionStartDownLoad : (Boolean) -> Unit) : T {
        this.FunctionStartDownLoad = FunctionStartDownLoad
        return this as T
    }

    /**
     * 프로그래스 셋팅
     */
    fun customSetProgress(FunctionSetProgress : (Int) -> Unit) : T {
        this.FunctionSetProgress = FunctionSetProgress
        return this as T
    }

    /**
     * Custom URL 스키마 설정
     */
    fun customFileChooser(Function : (WebView?, ValueCallback<Array<Uri?>>?,WebChromeClient.FileChooserParams?) -> Boolean) : T {
        this.FileFunction = Function
        return this as T
    }

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
        SAVE_FILE1 = intent
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
        Url.STAGE_URL = url
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
            developPopup.onRightClick(developPopup,"접속", View.OnClickListener {
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

    /**
     * DEVELOP 팝업
     */
    fun DevelopDialog2(checkAppVersion: KFunction0<Unit>) {
        developPopup = DevelopPopup.createDialog(context, "개발 모드", false)
        developPopup.onRightClick(developPopup,"접속", View.OnClickListener {
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

            when {
                developPopup.ck_api_dev.isChecked -> {
                    Url.API_TARGET_PROD = false
                }

                developPopup.ck_api_prod.isChecked -> {
                    Url.API_TARGET_PROD = true
                }
            }

            Utils.setPrefString(context, "url1", developPopup.et_url.text.toString())
            Utils.setPrefString(context, "url2", developPopup.et_url2.text.toString())
            Utils.setPrefString(context, "url3", developPopup.et_url3.text.toString())

            showDevelopDialog(false)

            developPopup.dismiss()

            checkAppVersion()

        })
        developPopup.show()
    }



    inner class InAppWebViewClient : WebViewClient() {
        override fun onReceivedSslError(
            view: WebView,
            handler: SslErrorHandler,
            error: SslError
        ) {
            Log.d("TTT", error.toString())
            val builder = AlertDialog.Builder(context)
            builder.setMessage("SSL 페이지 오류 입니다.\n에러코드" + error.primaryError)
            builder.setNegativeButton(
                "확인"
            ) { dialog: DialogInterface?, which: Int -> handler.cancel() }
            val dialog = builder.create()
            if(!(context as Activity).isFinishing){
                dialog.show()
            }
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            Log.d("TTTT errorcode", error?.errorCode.toString())
        }



        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if(!isPageFinish) {
                FunctionPageFinish(view, url)
                isPageFinish = true
            }


            CookieManager.getInstance().flush()
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            var url =
            if (request == null) {
                return false
            } else {
                request.url
            }


            return customOverride(url)
        }

    }
    
    
    fun customOverride(url : Uri) : Boolean{

       return when {

           /**
            * 문자
            */
           url.toString().startsWith("sms:") -> {
               try {
                   val tmpUrl: String = url.toString().replace("sms", "smsto")
                   val phoneNum = Uri.parse(tmpUrl)
                   context.startActivity(Intent(Intent.ACTION_SENDTO, phoneNum).setData(phoneNum))
               } catch (e: Exception) {
                   Log.e(TAG, e.toString())
               }
               true
           }

            /**
             * 전화 걸기
             */
            url.toString().startsWith("tel:") -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url.toString()))
                context.startActivity(intent)
                true
            }

            /**
             * 외부 URL 링크 이동
             */
            url.toString().startsWith("$SCHEME_INTENT://${OUT_LINK}", true) -> {
                try {
                    if (url.toString().contains("${OUT_LINK_QUERY_PARAMETER}=")) {
                        val tempUri = url.toString().replace("$SCHEME_INTENT://${OUT_LINK}?${OUT_LINK_QUERY_PARAMETER}=","").toUri()
                        val send = URLDecoder.decode( tempUri.toString() , "UTF-8" )
                        val intent = Intent(Intent.ACTION_VIEW, send.toUri())
                        context.startActivity(intent)
                    }
                }  catch (e: java.lang.NullPointerException) {
                    Log.e(TAG, e.toString())
                }catch (e: java.lang.Exception) {
                    Log.e(TAG, e.toString())
                }

                true
            }



            /**
             *  파일 저장 및 호출 리스너
             */
            url.toString().startsWith("$SCHEME_INTENT://${SAVE_FILE1}", true) || url.toString().startsWith("$SCHEME_INTENT://${SAVE_FILE2}", true) -> {
                url.let{
                    setPermission(act = act,  listener = object : PermissionListener {
                        override fun onPermissionGranted() {
                            //read, write 퍼미션 체크
                            try {
                                if (it.toString().contains("${SAVE_FILE_QUERY_PARAMETER}=")) {
                                    onDownloadStart(it.getQueryParameter(SAVE_FILE_QUERY_PARAMETER)!!, true)
                                }
                            } catch (e: java.lang.NullPointerException) {
                                FunctionStartDownLoad(false)
                                Log.e("SetttingBuilder",e.message.toString())
                            } catch (e: java.lang.Exception) {
                                FunctionStartDownLoad(false)
                                Log.e("SetttingBuilder",e.message.toString())
                            }
                        }

                        override fun onPermissionDenied(deniedPermissions: List<String?>?) {
                            (context as Activity).finish()
                        }
                    }, REQUIRED_PERMISSIONS, PERMISSION_CODE_WRITE_EXTERNAL_STORAGE)
                }

                true
            }

            /**
             * 파일 멀티 파트 전송
             */
            url.toString().startsWith("$SCHEME_INTENT://${UPLOAD_FILE}", true) -> {

                true
            }

            /**
             * 스토어 페이지 이동
             */
            url.toString().startsWith("$SCHEME_INTENT://${MOVE_STORE}", true) -> {
                if (url.toString().contains("${MOVE_STORE_QUERY_PARAMETER}=")) {
                    val tempUri = url
                    val packageName = tempUri.getQueryParameter(MOVE_STORE_QUERY_PARAMETER)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(checkUri("market://details?id=$packageName"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                return true
            }


            else -> Function(url)
        }
    }


    fun checkUri(str : String) : String{
        val Filter = "[\\\\@#$%]"
        return str.replace(Filter.toRegex(),"")
    }

    /** 파일 다운로드 시작 */
    @SuppressLint("Range")
    private fun onDownloadStart(url: String, isOpen : Boolean) {
        try {
            val mtype = MimeTypeMap.getSingleton()
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = url.toUri()
            val fileNameList = url.split("/").toTypedArray()
            val fileName: String = try {
                URLDecoder.decode(fileNameList[fileNameList.size - 1], "EUC-KR")
            } catch (e: java.lang.NullPointerException) {
                return
            }catch (e: java.lang.Exception) {
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
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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
                        FunctionStartDownLoad(false)
                        if (!(context as Activity).isDestroyed) {
                            webview?.loadUrl("javascript:hideLoader()")
                            Toast.makeText(
                                context,
                                "$fileName\n다운로드 완료",
                                Toast.LENGTH_SHORT
                            ).show()
                            if(isOpen)
                                openFile(path.absolutePath, fileName)
                            if (mChildWebView != null) {
                                closeChildWebView()
                            }
                        }
                    } catch (e: java.lang.NullPointerException) {
                        FunctionStartDownLoad(false)
                        Log.e(TAG, e.toString())
                    }catch (e: java.lang.Exception) {
                        FunctionStartDownLoad(false)
                        Log.e(TAG, e.toString())
                    }
                }
            }
            context.registerReceiver(downloadReceiver, completeFilter)
            val downloadId = downloadManager.enqueue(request)
            getDownloadStatus(downloadId,downloadManager)
        } catch (e: java.lang.NullPointerException) {
            Log.e(TAG, e.toString())
            FunctionStartDownLoad(false)
            Toast.makeText(context, "다운로드 실패하였습니다.", Toast.LENGTH_SHORT).show()
            setPermission(act = act,  listener = object : PermissionListener {
                override fun onPermissionGranted() {}
                override fun onPermissionDenied(deniedPermissions: List<String?>?) {
                    TODO("Not yet implemented")
                }
            }, REQUIRED_PERMISSIONS, PERMISSION_CODE_WRITE_EXTERNAL_STORAGE)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, e.toString())
            FunctionStartDownLoad(false)
            Toast.makeText(context, "다운로드 실패하였습니다.", Toast.LENGTH_SHORT).show()
            setPermission(act = act,  listener = object : PermissionListener {
                override fun onPermissionGranted() {}
                override fun onPermissionDenied(deniedPermissions: List<String?>?) {
                    TODO("Not yet implemented")
                }
            }, REQUIRED_PERMISSIONS, PERMISSION_CODE_WRITE_EXTERNAL_STORAGE)
        }
    }


    @SuppressLint("Range")
    fun getDownloadStatus(DownloadManagerId: Long, downloadManager: DownloadManager){
        thread(start = true) {
            var downloading = true
            while (downloading){
                val query = DownloadManager.Query()
                query.setFilterById(DownloadManagerId)

                val cursor = downloadManager.query(query)
                if(cursor.moveToFirst()){
                    val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                    if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL){
                        downloading = false
                    }else if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED){
                        downloading = false
                    }

                    if(bytesTotal != 0){
                        val progress = ((bytesDownloaded * 100L)/bytesTotal).toInt()
                        FunctionSetProgress(progress)
                    }

                    cursor.close()
                }
            }
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
            val ext = MimeTypeMap.getFileExtensionFromUrl(file.name)
            var type = map.getMimeTypeFromExtension(ext)
            if (type == null) {
                type = "*/*"
                if (file.name.endsWith(".jpg")) {
                    type = map.getMimeTypeFromExtension("jpg")
                } else if (file.name.endsWith(".png")) {
                    type = map.getMimeTypeFromExtension("png")
                } else if (file.name.endsWith(".jpeg")) {
                    type = map.getMimeTypeFromExtension("jpeg")
                } else if (file.name.endsWith(".tiff")) {
                    type = map.getMimeTypeFromExtension("tiff")
                } else if (file.name.endsWith(".tif")) {
                    type = map.getMimeTypeFromExtension("tif")
                }
            }
            val intent = Intent(Intent.ACTION_VIEW)
            //			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            //Uri data = Uri.fromFile(file);
            val data = FileProvider.getUriForFile(
                context,
                PROVIDER,
                file
            )
            intent.setDataAndType(data, type)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if(newProgress == 100){
                try {
                    if(!isPageFinish){
                        FunctionPageFinish(view!!, view.url!!)
                        isPageFinish = true
                    }

                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        override fun onPermissionRequest(request: PermissionRequest?) {
            request?.grant(request.resources)
        }

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
                i.data = Uri.parse(url!!)
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

                    override fun onShowFileChooser(
                        webView: WebView?,
                        filePathCallback: ValueCallback<Array<Uri?>>?,
                        fileChooserParams: FileChooserParams?
                    ): Boolean {
                        return FileFunction(webView, filePathCallback, fileChooserParams)
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
                clRoot?.addView(it)
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = it
                resultMsg.sendToTarget()
            }

            return true
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri?>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            return FileFunction(webView, filePathCallback, fileChooserParams)
        }
    }




    fun closeChildWebView() {
        mChildWebView?.let {
            it.destroy()
            clRoot?.removeView(it)
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


    @SuppressLint("SetJavaScriptEnabled")
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

                if(BuildConfig.DEBUG)
                    WebView.setWebContentsDebuggingEnabled(true)




                webview?.webViewClient = InAppWebViewClient()
                webview?.webChromeClient = InAppChromeClient()


                val settings: WebSettings? = webview?.settings
                settings?.javaScriptEnabled = true
                settings?.setSupportZoom(true)
                settings?.setGeolocationEnabled(true)
                settings?.allowFileAccess = true
                settings?.allowFileAccessFromFileURLs = true
                settings?.allowUniversalAccessFromFileURLs = true
                settings?.useWideViewPort = true
                settings?.domStorageEnabled = true
                settings?.setSupportMultipleWindows(true)
                settings?.textZoom = 100
                settings?.userAgentString =
                    settings?.userAgentString + " " + "${userAgent}-app-Agent : " + appVersion + " " + "${userAgent}-app-Platform : A " + Build.VERSION.RELEASE + "/GA_Android"
                Log.d(TAG, settings?.userAgentString.toString())

//                settings?.cacheMode = WebSettings.LOAD_NO_CACHE
                settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                val cookieManager =
                    CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(webview, true)

                webview?.setLayerType(View.LAYER_TYPE_HARDWARE,null)
                webview?.setVerticalScrollbarOverlay(false)
                webview?.clearCache(true)
                webview?.clearHistory()

            }
        }


    }




}