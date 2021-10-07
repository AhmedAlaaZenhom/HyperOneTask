package com.example.hyperonetask.data.model

import com.example.hyperonetask.R
import java.io.Serializable

open class FileModel(
    var id: Int? = null,
    var type: String? = null,
    var url: String? = null,
    var name: String? = null,
    var status: FileStatus = FileStatus.ONLINE
) : Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FileModel
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + status.hashCode()
        return result
    }
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