package com.example.hyperonetask.ui.files_list

import androidx.activity.viewModels
import com.example.hyperonetask.base.BaseActivity
import com.example.hyperonetask.databinding.ActivityFilesListBinding
import com.example.hyperonetask.utils.State
import com.example.hyperonetask.utils.showContent
import com.example.hyperonetask.utils.showError
import com.example.hyperonetask.utils.showLoading
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilesListActivity : BaseActivity<ActivityFilesListBinding, FilesListViewModel>() {

    override val viewModel: FilesListViewModel by viewModels()
    override val binding by viewBinding(ActivityFilesListBinding::inflate)


    override fun onActivityCreated() {
        initViews()
        initObservers()
    }

    private fun initViews() {

    }

    private fun initObservers() {
        viewModel.filesListLiveData.observe(this) {
            when (it) {
                is State.Loading -> binding.stateView.showLoading()
                is State.Success -> {
                    binding.stateView.showContent()
                }
                is State.Error -> binding.stateView.showError { viewModel.getListOfFiles() }
            }
        }
    }

}