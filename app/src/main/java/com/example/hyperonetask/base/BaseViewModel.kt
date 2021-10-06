package com.example.hyperonetask.base

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.example.hyperonetask.data.model.ErrorModel
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber


open class BaseViewModel: ViewModel() {

    private val _errorMsgLiveData = LiveEvent<String>()
    val errorMsgLiveData: LiveData<String> get() = _errorMsgLiveData

    private val _errorMsgResourceLiveData = LiveEvent<Int>()
    val errorMsgResourceLiveData: LiveData<Int> get() = _errorMsgResourceLiveData

    private val _successMsgLiveData = LiveEvent<String>()
    val successMsgLiveData: LiveData<String> get() = _successMsgLiveData

    private val _successMsgResourceLiveData = LiveEvent<Int>()
    val successMsgResourceLiveData: LiveData<Int> get() = _successMsgResourceLiveData

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean> get() = _loadingLiveData


    val handler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    fun safeLauncher(task: suspend CoroutineScope.() -> Unit) =
            viewModelScope.launch(context = handler, block = task)


    fun showLoading() {
        _loadingLiveData.value = true
    }

    fun hideLoading() {
        _loadingLiveData.value = false
    }

    fun showSuccessMsg(msg: String) {
        _successMsgLiveData.value = msg
    }

    fun showSuccessMsg(@StringRes id: Int) {
        _successMsgResourceLiveData.value = id
    }

    fun showErrorMsg(msg: String) {
        _errorMsgLiveData.value = msg
    }

    fun showErrorMsg(@StringRes id: Int) {
        _errorMsgResourceLiveData.value = id
    }

    fun handleError(t: Throwable?) {
        hideLoading()

        if(t is ErrorModel) {
            val msg = when(t) {
                is ErrorModel.Connection -> "Check your internet connection"
                is ErrorModel.Network -> t.serverMessage
                else -> {
                    Timber.e(t)
                    "Error occurred"
                }
            }

            msg?.let { showErrorMsg(it) }
        }
        else {
            showErrorMsg("Error occurred")
        }
    }

}