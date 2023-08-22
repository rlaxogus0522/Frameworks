package co.framework.app.view

import android.os.Handler
import co.framework.app.base.BaseActivity
import co.framework.app.R
import co.framework.security.base.BaseSecuritySetting
import co.framework.app.databinding.ActivitySplashBinding
import co.framework.app.utils.startActivityWithFinish
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    var startTime: Long = 0

    override fun init() {
        binding.activity = this
        startTime = System.currentTimeMillis()
        checkSecurity()
    }

    fun checkSecurity(){
        BaseSecuritySetting.with(this)
            .checkForgery(true) // 해쉬값 검사를 통한 앱 위변조 검사
            .checkRooting(true) // 기기 루팅 상태 검사
            .start().let {
                goMain()
            }
    }


    private fun goMain() {
        val nowTime = System.currentTimeMillis()
        if (startTime < nowTime - 2000) {
            startActivityWithFinish(this, MainActivity::class.java)
        } else {
            Handler().postDelayed({
                startActivityWithFinish(this, MainActivity::class.java)
            }, 2000 - (nowTime - startTime))
        }
    }

}
