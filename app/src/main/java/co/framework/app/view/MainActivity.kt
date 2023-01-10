package co.framework.app.view


import android.view.View
import androidx.activity.viewModels
import co.framework.app.R
import co.framework.app.base.BaseActivity
import co.framework.app.databinding.ActivityMainBinding
import co.framework.app.utils.ScreenState
import co.framework.app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.toast


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun init() {
        binding.activity = this
    }

    fun clickTestBtn(view : View){
        mainViewModel.getUser(binding.etName.text.toString())
    }

}