package com.framework.webview.base

import android.content.Context

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