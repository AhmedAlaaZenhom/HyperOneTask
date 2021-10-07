package com.example.hyperonetask.ui.files_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hyperonetask.base.BaseViewModel
import com.example.hyperonetask.data.model.FileModel
import com.example.hyperonetask.data.repository.FilesRepo
import com.example.hyperonetask.utils.State
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import com.example.hyperonetask.R
import com.example.hyperonetask.data.model.FileStatus
import javax.inject.Inject

@HiltViewModel
class FilesListViewModel @Inject constructor(
    private val filesRepo: FilesRepo,
    private val fetch: Fetch
) : BaseViewModel() {

    // API Service

    private val _filesListLiveData = MutableLiveData<State<List<FileModel>>>()
    val filesListLiveData: LiveData<State<List<FileModel>>> = _filesListLiveData

    fun getListOfFiles() = viewModelScope.launch {
        try {
            _filesListLiveData.value = State.Loading()
            _filesListLiveData.value = State.Success(filesRepo.getListOfFiles())
        } catch (e: Exception) {
            _filesListLiveData.value = State.Error()
            handleError(e)
        }
    }

    // File Download Service

    private val downloadFileIdMap: HashMap<Int, Int> = hashMapOf()

    private val _fileDownloadStatusLiveData = MutableLiveData<FileModel>()
    val fileDownloadStatusLiveData: LiveData<FileModel> = _fileDownloadStatusLiveData

    fun downloadFile(fileModel: FileModel, destinationFile: File) {
        val downloadRequest = Request(fileModel.url ?: "", destinationFile.absolutePath)
        downloadRequest.priority = Priority.HIGH;
        downloadRequest.networkType = NetworkType.ALL

        downloadFileIdMap[downloadRequest.id] = fileModel.id!!

        fetch.enqueue(downloadRequest){
            if(it.throwable?.message.isNullOrEmpty())
                showErrorMsg(R.string.something_went_wrong)
            else
                showErrorMsg(it.throwable!!.message!!)

            _fileDownloadStatusLiveData.value =
                FileModel(id = fileModel.id, status = FileStatus.ONLINE)
        }
    }

    fun cancelDownload(fileModel: FileModel) {
        var requestId = 0
        downloadFileIdMap.filter { it.value == fileModel.id }.keys.let {
            if(it.isNotEmpty())
                requestId = it.first()
        }
        fetch.cancel(requestId){
            if(it.throwable?.message.isNullOrEmpty())
                showErrorMsg(R.string.something_went_wrong)
            else
                showErrorMsg(it.throwable!!.message!!)

            _fileDownloadStatusLiveData.value =
                FileModel(id = fileModel.id, status = FileStatus.ONLINE)
        }
    }

    private val fetchListener = object : FetchListener {
        override fun onAdded(download: Download) {}

        override fun onCancelled(download: Download) {
            _fileDownloadStatusLiveData.value =
                FileModel(id = downloadFileIdMap[download.id], status = FileStatus.ONLINE)
        }

        override fun onCompleted(download: Download) {
            _fileDownloadStatusLiveData.value =
                FileModel(id = downloadFileIdMap[download.id], status = FileStatus.DOWNLOADED)
        }

        override fun onDeleted(download: Download) {}

        override fun onDownloadBlockUpdated(
            download: Download,
            downloadBlock: DownloadBlock,
            totalBlocks: Int
        ) {

        }

        override fun onError(download: Download, error: Error, throwable: Throwable?) {
            if(error.throwable?.message.isNullOrEmpty())
                showErrorMsg(R.string.something_went_wrong)
            else
                showErrorMsg(error.throwable!!.message!!)

            _fileDownloadStatusLiveData.value =
                FileModel(id = downloadFileIdMap[download.id], status = FileStatus.ONLINE)
        }

        override fun onPaused(download: Download) {}

        override fun onProgress(
            download: Download,
            etaInMilliSeconds: Long,
            downloadedBytesPerSecond: Long
        ) {

        }

        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
            _fileDownloadStatusLiveData.value =
                FileModel(id = downloadFileIdMap[download.id], status = FileStatus.PENDING)
        }

        override fun onRemoved(download: Download) {}

        override fun onResumed(download: Download) {}

        override fun onStarted(
            download: Download,
            downloadBlocks: List<DownloadBlock>,
            totalBlocks: Int
        ) {
            _fileDownloadStatusLiveData.value =
                FileModel(id = downloadFileIdMap[download.id], status = FileStatus.DOWNLOADING)
        }

        override fun onWaitingNetwork(download: Download) {}
    }

    // View Model initiation & clearing

    init {
        getListOfFiles()
        fetch.addListener(fetchListener)
    }

    override fun onCleared() {
        fetch.cancelAll()
        // Just to be able to re-download items on next app start (no caching just for the sake of this task)
        fetch.deleteAll()
        fetch.removeListener(fetchListener)
        super.onCleared()
    }
}