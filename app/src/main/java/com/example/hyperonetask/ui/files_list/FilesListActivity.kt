package com.example.hyperonetask.ui.files_list

import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.hyperonetask.base.BaseActivity
import com.example.hyperonetask.data.model.FileStatus
import com.example.hyperonetask.databinding.ActivityFilesListBinding
import com.example.hyperonetask.utils.State
import com.example.hyperonetask.utils.showContent
import com.example.hyperonetask.utils.showError
import com.example.hyperonetask.utils.showLoading
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class FilesListActivity : BaseActivity<ActivityFilesListBinding, FilesListViewModel>() {

    override val viewModel: FilesListViewModel by viewModels()
    override val binding by viewBinding(ActivityFilesListBinding::inflate)

    private lateinit var adapter: FilesAdapter

    override fun onActivityCreated() {
        initViews()
        initObservers()
    }

    private fun initViews() {
        // RecyclerView
        adapter = FilesAdapter {
            when (it.status) {
                FileStatus.PENDING, FileStatus.DOWNLOADING -> viewModel.cancelDownload(it)
                FileStatus.ONLINE -> viewModel.downloadFile(
                    it, File(filesDir?.toString() + it.name)
                )
                else -> Unit
            }
        }
        binding.rvFiles.adapter = adapter

        // SearchBar
        binding.inputSearchFiles.addTextChangedListener {
            adapter.filter(it.toString())
        }
    }

    private fun initObservers() {
        viewModel.filesListLiveData.observe(this) {
            when (it) {
                is State.Loading -> binding.stateView.showLoading()
                is State.Success -> {
                    adapter.submit(it.data)
                    binding.stateView.showContent()
                }
                is State.Error -> binding.stateView.showError { viewModel.getListOfFiles() }
            }
        }

        viewModel.fileDownloadStatusLiveData.observe(this) {
            adapter.update(it)
        }
    }

}