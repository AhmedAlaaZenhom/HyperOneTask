package com.example.hyperonetask.data.model

import java.io.Serializable

data class FileModel(
    val id: String?,
    val type: String?,
    val url: String?,
    val name: String?,
) : Serializable