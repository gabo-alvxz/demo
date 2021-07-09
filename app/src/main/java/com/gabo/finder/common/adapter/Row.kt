package com.gabo.finder.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

interface Row<B : ViewBinding> {
    fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup
    ) : B

    fun bind(binding : B)

}