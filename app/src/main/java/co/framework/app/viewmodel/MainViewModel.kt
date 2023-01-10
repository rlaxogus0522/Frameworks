package co.framework.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import co.framework.app.base.BaseViewModel
import co.framework.app.utils.ScreenState
import co.framework.app.utils.SingleLiveEvent
import co.framework.domain.model.TestResponse
import co.framework.domain.usecase.GetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUseCase: GetUseCase
) : BaseViewModel() {

    val eventRepo : LiveData<List<TestResponse>> get() = _eventRepo
    private val _eventRepo = SingleLiveEvent<List<TestResponse>>()

    fun getUser(owner : String) = viewModelScope.launch {
        val response = getUseCase.exTest(this@MainViewModel, owner)

        if(response == null) {
            mutableScreenState.postValue(ScreenState.ERROR)
        }else{
            mutableScreenState.postValue(ScreenState.RENDER)
            _eventRepo.postValue(response!!)
        }
    }

}