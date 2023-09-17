package com.framework.webview.base

import android.app.Activity
import android.content.Context

object BaseWebviewSetting {

    fun with(context: Context,act : Activity): Builder {
        return Builder(context,act)
    }

    class Builder(context: Context, act : Activity) : SettingBuilder<Builder>(context, act) {


        fun start() {
            setWebViewSetting()
        }
    }

}