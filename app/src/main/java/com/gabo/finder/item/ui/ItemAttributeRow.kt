package com.gabo.finder.item.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gabo.finder.common.adapter.Row
import com.gabo.finder.databinding.ItemAttributeRowBinding
import com.gabo.models.models.item.Item

class ItemAttributeRow(val attribute: Item.Attribute) : Row<ItemAttributeRowBinding> {
    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup
    ): ItemAttributeRowBinding {
        return ItemAttributeRowBinding.inflate(
            layoutInflater,
            viewGroup,
            false
        )
    }

    override fun bind(binding: ItemAttributeRowBinding) {
        binding.attributeTitle.text = attribute.name
        binding.attributeValue.text = attribute.valueName
    }
}