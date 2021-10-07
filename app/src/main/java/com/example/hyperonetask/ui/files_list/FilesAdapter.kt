package com.example.hyperonetask.ui.files_list

import android.annotation.SuppressLint
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hyperonetask.data.model.FileModel
import com.example.hyperonetask.data.model.FileStatus
import com.example.hyperonetask.databinding.ItemFileBinding

@SuppressLint("SetTextI18n")
class FilesAdapter constructor(
    private val onSelect: (FileModel) -> Unit,
) : ListAdapter<FileModel, FilesAdapter.ItemViewHolder>(Differentiator) {

    private var unfilteredList: List<FileModel>? = null
    private var filteredList = mutableListOf<FileModel>()
    private var filterText: String = ""

    fun submit(list: List<FileModel>) {
        unfilteredList = list
        filter(filterText)
    }

    fun update(fileModel: FileModel) {
        unfilteredList?.first { model -> fileModel.id == model.id }?.let {
            it.status = fileModel.status
            it.downloadProgress = fileModel.downloadProgress
            it.downloadedBytesPerSecond = fileModel.downloadedBytesPerSecond
        }
        val index = filteredList.indexOf(fileModel)
        if (index != -1)
            notifyItemChanged(index)
    }

    fun filter(filterText: String?) {
        this.filterText = filterText ?: ""
        filter()
    }

    private fun filter() {
        if (unfilteredList == null)
            return

        filteredList = mutableListOf()

        if (filterText.isNotEmpty()) {
            filteredList.addAll(unfilteredList!!.filter {
                it.name?.lowercase()?.contains(filterText.toString().lowercase()) == true
            })
        } else {
            filteredList.addAll(unfilteredList!!)
        }

        submitList(filteredList)
    }

    class ItemViewHolder constructor(val binding: ItemFileBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val model = getItem(position)!!

        with(holder.binding) {
            // File Name
            tvName.text = model.name
            // File Status Text
            tvStatus.setText(model.status.labelResource)
            // File Status Icon, Progress, and download speed
            when (model.status) {
                FileStatus.ONLINE, FileStatus.PENDING, FileStatus.DOWNLOADED -> {
                    ivStatusTypes.visibility = View.VISIBLE
                    ivStatusTypes.setImageResource(model.status.iconResource)
                    gpDownloadingViews.visibility = View.GONE
                }
                FileStatus.DOWNLOADING -> {
                    ivStatusTypes.visibility = View.INVISIBLE
                    gpDownloadingViews.visibility = View.VISIBLE
                    ivStatusDownloading.setImageResource(model.status.iconResource)
                    tvDownloadPercentage.text = "${model.downloadProgress}%"
                    val readableBytes = Formatter.formatFileSize(tvDownloadSpeed.context, model.downloadedBytesPerSecond)
                    tvDownloadSpeed.text = "$readableBytes/s"
                }
            }
            // Click Listener
            root.setOnClickListener {
                onSelect.invoke(model)
            }
        }

    }

    object Differentiator : DiffUtil.ItemCallback<FileModel>() {

        override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
            return oldItem == newItem
        }
    }
}