package co.framework.app.base

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import co.framework.app.BuildConfig
import org.jetbrains.anko.toast
import timber.log.Timber

abstract class BaseActivity<T : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    ComponentActivity() {
    protected lateinit var binding: T
    private var waitTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        init()
    }

    abstract fun init()

    override fun onBackPressed() {
        if (System.currentTimeMillis() - waitTime >= 1500) {
            waitTime = System.currentTimeMillis()
            toast("뒤로가기 버튼을 한번 더 누르면 종료됩니다.")
        } else finish()
    }

}



