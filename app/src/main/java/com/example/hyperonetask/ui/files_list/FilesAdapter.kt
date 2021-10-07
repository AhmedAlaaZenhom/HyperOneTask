package com.example.hyperonetask.ui.files_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hyperonetask.R
import com.example.hyperonetask.data.model.FileModel
import com.example.hyperonetask.data.model.FileStatus
import com.example.hyperonetask.databinding.ItemFileBinding

class FilesAdapter constructor(
    private val onSelect: (FileModel)-> Unit,
): ListAdapter<FileModel, FilesAdapter.ItemViewHolder>(Differentiator) {

    private var unfilteredList: List<FileModel>? =null

    fun modifyList(list : List<FileModel>) {
        unfilteredList = list
        submitList(list)
    }

    fun filter(query: CharSequence?) {
        if(unfilteredList == null)
            return

        val list = mutableListOf<FileModel>()

        if(!query.isNullOrEmpty()) {
            list.addAll(unfilteredList!!.filter {
                it.name?.lowercase()?.contains(query.toString().lowercase()) == true })
        } else {
            list.addAll(unfilteredList!!)
        }

        submitList(list)
    }

    class ItemViewHolder constructor(val binding: ItemFileBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val model = getItem(position)!!

        with(holder.binding){
            // File Name
            tvName.text = model.name
            // File Status Text
            tvStatus.setText(model.status.labelResource)
            // File Status Icon & Progress Visibility
            when(model.status) {
                FileStatus.ONLINE, FileStatus.PENDING, FileStatus.DOWNLOADED -> {
                    ivStatusTypes.visibility = View.VISIBLE
                    ivStatusTypes.setImageResource(model.status.iconResource)
                    gpDownloadingViews.visibility = View.GONE
                }
                FileStatus.DOWNLOADING -> {
                    ivStatusTypes.visibility = View.INVISIBLE
                    gpDownloadingViews.visibility = View.VISIBLE
                    ivStatusDownloading.setImageResource(model.status.iconResource)
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