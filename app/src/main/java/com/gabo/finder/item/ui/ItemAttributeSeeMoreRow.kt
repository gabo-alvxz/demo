package com.gabo.finder.item.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gabo.architecture.utils.context
import com.gabo.finder.R
import com.gabo.finder.common.adapter.Row
import com.gabo.finder.databinding.ItemAttributeRowBinding
import com.gabo.finder.databinding.ItemAttributeSeeMoreRowBinding

class ItemAttributeSeeMoreRow(
    val count : Int,
    val onClick: () -> Unit
) : Row<ItemAttributeSeeMoreRowBinding> {
    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup
    ): ItemAttributeSeeMoreRowBinding {
        return ItemAttributeSeeMoreRowBinding.inflate(
            layoutInflater,
            viewGroup,
            false
        )
    }

    override fun bind(binding: ItemAttributeSeeMoreRowBinding) {
        binding.root.text = binding.context.getString(
            R.string.show_all_attributes,
            count
        )
        binding.root.setOnClickListener {
            onClick()
        }
    }
}