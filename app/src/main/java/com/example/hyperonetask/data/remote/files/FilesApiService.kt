package com.example.hyperonetask.data.remote.files

import com.example.hyperonetask.data.model.FileModel
import retrofit2.http.GET

interface FilesApiService {

    @GET("HyperoneWebservice/getListOfFilesResponse.json")
    suspend fun getListOfFiles(): List<FileModel>

}