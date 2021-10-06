package com.example.hyperonetask.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.hyperonetask.R
import com.example.hyperonetask.common.LoadingDialog
import es.dmoral.toasty.Toasty

@SuppressLint("SourceLockedOrientationActivity")
abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : LocalizationActivity() {

    abstract val viewModel: VM
    abstract val binding: VB

    abstract fun onActivityCreated()


    private lateinit var loadingDialog: LoadingDialog
    var doubleBackToExitPressedOnce = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Force app in portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        initObservers()
        onActivityCreated()
    }

    private fun initObservers() {
        viewModel.errorMsgLiveData.observe(this) { showErrorMsg(it) }
        viewModel.errorMsgResourceLiveData.observe(this) { showErrorMsg(getString(it)) }

        viewModel.successMsgLiveData.observe(this) { showSuccessMsg(it) }
        viewModel.successMsgResourceLiveData.observe(this) { showSuccessMsg(getString(it)) }

        viewModel.loadingLiveData.observe(this) {
            if (it)
                showLoading()
            else
                hideLoading()
        }
    }

    fun showErrorMsg(msg: String) {
        Toasty.error(this, msg, Toasty.LENGTH_LONG).show()
    }

    fun showSuccessMsg(msg: String) {
        Toasty.success(this, msg, Toasty.LENGTH_LONG).show()
    }

    fun showLoading(msg: String?) {
        hideKeyboard()
        loadingDialog.show(msg)
    }

    fun showLoading() {
        showLoading(getString(R.string.loading))
    }

    fun hideLoading() {
        loadingDialog.dismiss()
    }

    protected open fun setTitleWithBack(title: String) {
        setTitle(title)
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    protected open fun showKeyboard(focusedView: View) {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(focusedView, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected open fun hideKeyboard() {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            var view = currentFocus
            if (view == null) {
                view = View(this)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            onBackPressed() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toasty.normal(this, getString(R.string.click_twice)).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
        crossinline bindingInflater: (LayoutInflater) -> T
    ) =
        lazy(LazyThreadSafetyMode.NONE) {
            bindingInflater.invoke(layoutInflater)
        }
}