package co.framework.app.view


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import co.framework.app.R
import co.framework.app.base.BaseActivity
import co.framework.app.databinding.ActivityMainBinding
import co.framework.app.viewmodel.MainViewModel
import co.framework.domain.model.GitData
import com.framework.webview.base.BaseWebviewSetting
import com.framework.webview.common.Url.ACCESS_URL
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


/**
* Sample
*
* @author KimTaeHyun
* @since 2023/08/22
**/
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val TAG = "MainActivity"
    private val mainViewModel by viewModels<MainViewModel>()

    override fun init() {
        binding.activity = this
        observeViewModel() // observe 셋팅

        // WebView형 프로젝트 Default 셋팅
        val webviewSetting = BaseWebviewSetting.with(this, this)
            .setMainUrl(ACCESS_URL) // load url 셋팅
            .setWebView(binding.wvMain)// webview 셋팅
            .setConstraintLayoutRoot(binding.clRoot)
            .setUserAgent("BaseProject") // useragent 셋팅 (os정보/앱 버전정보 등 전송용)
            .setSchemeName("basepro") // web으로부터 데이터 수신용 스키마 설정
            .setProvider("$packageName.provider") // 파일 다운로드용 Provider 설정
            .customUrlScheme {
                val urlString = it.toString()
                return@customUrlScheme when {
                    // basepro://rate 로 들어올 시 평가 남기기 화면 이동
                    urlString.startsWith("rate") -> {
                        val app_package =
                            packageName //requesting app package name from Context or Activity object

                        try {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$app_package")
                                )
                            )
                        } catch (anfe: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$app_package")
                                )
                            )
                        }

                        true
                    }
                    else -> false
                }
            }
        webviewSetting.start()
    }


    fun clickGetGitDataBtn(view: View) {
        mainViewModel.getUser(binding.etName.text.toString())
    }

    private fun observeViewModel(){
        mainViewModel.eventRepo.observe(this) { list ->
            list.forEach {
                Timber.d(it.name)
            }
        }
    }

}