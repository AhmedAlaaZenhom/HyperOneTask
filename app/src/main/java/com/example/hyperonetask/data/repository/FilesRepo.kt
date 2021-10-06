package com.example.hyperonetask.data.repository

import com.example.hyperonetask.base.BaseRepository
import com.example.hyperonetask.data.model.FileModel
import com.example.hyperonetask.data.remote.files.FilesApiService
import javax.inject.Inject

class FilesRepo @Inject constructor(
    private val filesApi: FilesApiService,
) : BaseRepository() {

    suspend fun getListOfFiles(): List<FileModel> = execute {
        return@execute filesApi.getListOfFiles()
    }

}