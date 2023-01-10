package co.framework.security.base

import android.content.Context

object BaseSecuritySetting {

    fun with(context: Context): Builder {
        return Builder(context)
    }

    class Builder(context: Context) : SettingBuilder<Builder>(context) {


        fun start() {
            check()
        }
    }

}