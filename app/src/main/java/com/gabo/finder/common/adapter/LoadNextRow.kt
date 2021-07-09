package com.gabo.finder.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gabo.finder.databinding.RowLoadingBinding

class LoadNextRow : Row<RowLoadingBinding> {
    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup
    ): RowLoadingBinding {
        return RowLoadingBinding.inflate(layoutInflater,viewGroup, false)
    }

    override fun bind(binding: RowLoadingBinding) {}

}