package co.framework.webview.base

import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import co.framework.webview.common.Url

object BaseWebviewSetting {

    fun with(context: Context): Builder {
        return Builder(context)
    }

    class Builder(context: Context) : SettingBuilder<Builder>(context) {


        fun start() {
            setWebViewSetting()
        }
    }

}