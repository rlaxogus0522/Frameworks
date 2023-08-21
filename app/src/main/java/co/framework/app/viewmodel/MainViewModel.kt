package co.framework.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import co.framework.app.base.BaseViewModel
import co.framework.app.utils.ScreenState
import co.framework.app.utils.SingleLiveEvent
import co.framework.domain.model.GitData
import co.framework.domain.usecase.GetGitUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUseCase: GetGitUserDataUseCase
) : BaseViewModel() {

    val eventRepo : LiveData<List<GitData>> get() = _eventRepo
    private val _eventRepo = SingleLiveEvent<List<GitData>>()

    fun getUser(owner : String) = viewModelScope.launch {
        val response = getUseCase.invoke(this@MainViewModel, owner)

        if(response == null) {
            mutableScreenState.postValue(ScreenState.ERROR)
        }else{
            mutableScreenState.postValue(ScreenState.RENDER)
            _eventRepo.postValue(response!!)
        }
    }

}