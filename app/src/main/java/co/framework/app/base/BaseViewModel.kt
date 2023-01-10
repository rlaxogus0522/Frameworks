package co.framework.app.base

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.framework.app.utils.ScreenState
import co.framework.app.utils.SingleLiveEvent
import co.framework.domain.util.ErrorType
import co.framework.domain.util.RemoteErrorEmitter

abstract class BaseViewModel : ViewModel(), RemoteErrorEmitter{

    val mutableProgress = MutableLiveData<Int>(View.GONE)
    val mutableScreenState = SingleLiveEvent<ScreenState>()
    val mutableErrorMessage = SingleLiveEvent<String>()
    val mutableSuccessMessage = MutableLiveData<String>()
    val mutableErrorType = SingleLiveEvent<ErrorType>()


    override fun onError(errorType: ErrorType) {
        mutableErrorType.postValue(errorType)
    }

    override fun onError(msg: String) {
        mutableErrorMessage.postValue(msg)
    }

}