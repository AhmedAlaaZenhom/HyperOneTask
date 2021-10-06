package com.example.hyperonetask.utils

import android.widget.Button
import com.example.hyperonetask.R
import com.kennyc.view.MultiStateView

fun MultiStateView.showLoading() {
    viewState = MultiStateView.ViewState.LOADING
}

fun MultiStateView.showContent() {
    viewState = MultiStateView.ViewState.CONTENT
}

fun MultiStateView.showError(onRetry: ()-> Unit) {
    viewState = MultiStateView.ViewState.ERROR
    val view = getView(MultiStateView.ViewState.ERROR)
    val button: Button? = view?.findViewById(R.id.btn_retry)
    button?.setOnClickListener {
        onRetry.invoke()
    }
}

fun MultiStateView.init() {
    setViewForState(R.layout.layout_loading_state, MultiStateView.ViewState.LOADING)
    setViewForState(R.layout.layout_error_state, MultiStateView.ViewState.ERROR)
}