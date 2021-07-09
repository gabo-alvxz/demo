package com.gabo.finder.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gabo.finder.databinding.RowErrorBinding

class ErrorRow(
    val onClick : () -> Unit
) : Row<RowErrorBinding> {

    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup
    ): RowErrorBinding {
        return RowErrorBinding.inflate(layoutInflater, viewGroup, false)
    }

    override fun bind(binding: RowErrorBinding) {
    }
}