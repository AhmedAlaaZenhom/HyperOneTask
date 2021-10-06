package com.example.hyperonetask.ui.files_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hyperonetask.base.BaseViewModel
import com.example.hyperonetask.data.model.FileModel
import com.example.hyperonetask.data.repository.FilesRepo
import com.example.hyperonetask.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilesListViewModel @Inject constructor(
    private val filesRepo: FilesRepo,
): BaseViewModel() {

    private val _filesListLiveData = MutableLiveData<State<List<FileModel>>>()
    val filesListLiveData: LiveData<State<List<FileModel>>> = _filesListLiveData

    init {
        getListOfFiles()
    }

    fun getListOfFiles() = viewModelScope.launch {
        try {
            _filesListLiveData.value = State.Loading()
            _filesListLiveData.value = State.Success(filesRepo.getListOfFiles())
        }
        catch (e: Exception) {
            _filesListLiveData.value = State.Error()
            handleError(e)
        }
    }
}