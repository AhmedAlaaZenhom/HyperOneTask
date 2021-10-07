package com.example.hyperonetask.data.model

import com.example.hyperonetask.R
import java.io.Serializable

open class FileModel(
): Serializable{
    var id: Int? = null
    var type: String? = null
    var url: String? = null
    var name: String? = null
    var status: FileStatus = FileStatus.ONLINE
}

enum class FileStatus {
    ONLINE,
    PENDING,
    DOWNLOADING,
    DOWNLOADED;

    val labelResource: Int
        get() {
            return when (this) {
                ONLINE -> R.string.online
                PENDING -> R.string.pending_download
                DOWNLOADING -> R.string.downloading
                DOWNLOADED -> R.string.downloaded
            }
        }

    val iconResource: Int
        get() {
            return when (this) {
                ONLINE -> R.drawable.ic_online
                PENDING -> R.drawable.ic_pending
                DOWNLOADING -> R.drawable.ic_downloading
                DOWNLOADED -> R.drawable.ic_downloaded
            }
        }
}