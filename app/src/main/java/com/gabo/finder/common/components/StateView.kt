package com.gabo.finder.common.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.gabo.finder.R
import com.gabo.finder.databinding.ComponentStateViewBinding

class StateView(
    context: Context,
    attributeSet: AttributeSet
) : FrameLayout(context, attributeSet) {

    private var showErrorImage = true
    private var showEmptyImage = true
    private var targetView : View? = null
    private val binding = ComponentStateViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        parseAttributes(attributeSet)
        this.isVisible = false
    }

    fun showLoading(){
        this.isVisible = true
        binding.emptyView.root.isVisible = false
        binding.errorView.root.isVisible = false
        binding.loadingView.root.isVisible = true
        targetView?.isVisible  = false
    }

    fun showEmpty(){
        this.isVisible = true
        binding.emptyView.root.isVisible = true
        binding.errorView.root.isVisible = false
        binding.loadingView.root.isVisible = false
        targetView?.isVisible  = false
        binding.emptyView.noResultsImagee.isVisible = showEmptyImage
    }

    fun showError(onRetry :  (()-> Unit)?){
        this.isVisible = true
        binding.emptyView.root.isVisible = false
        binding.errorView.root.isVisible = true
        binding.loadingView.root.isVisible = false
        targetView?.isVisible  = false
        binding.errorView.networkErrorImage.isVisible = showErrorImage
        binding.errorView.retryButton.isVisible = onRetry != null
        binding.errorView.retryButton.setOnClickListener {
            onRetry?.invoke()
        }
    }

    fun showContent(){
        this.isVisible = false
        binding.emptyView.root.isVisible = false
        binding.errorView.root.isVisible = false
        binding.loadingView.root.isVisible = false
        targetView?.isVisible  = true
    }

    private fun parseAttributes(attributeSet: AttributeSet){
        val a  = context.obtainStyledAttributes(
            attributeSet, R.styleable.StateView
        )
        showEmptyImage = a.getBoolean(R.styleable.StateView_showEmptyImage, true)
        showErrorImage = a.getBoolean(R.styleable.StateView_showErrorImage, true)
        a.getResourceId(R.styleable.StateView_targetView, -1).also {
            if(it != -1){
                targetView = findViewById(it)
            }
        }
        a.recycle()
    }
}