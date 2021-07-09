package com.gabo.finder.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.gabo.architecture.utils.context
import com.gabo.architecture.utils.formatCurrency
import com.gabo.architecture.utils.formatQuantity
import com.gabo.architecture.utils.loadUrl
import com.gabo.finder.R
import com.gabo.finder.common.adapter.Row
import com.gabo.finder.databinding.SearchResultRowBinding
import com.gabo.models.models.item.Item

class SearchResultRow(
    private val item : Item,
    private val onClick : (Item) -> Unit
) : Row<SearchResultRowBinding> {
    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup
    ): SearchResultRowBinding {
        return SearchResultRowBinding.inflate(
            layoutInflater,
            viewGroup,
            false
        )
    }

    override fun bind(binding: SearchResultRowBinding) {
        item.thumbnail?.let {
            binding.thumbnail.loadUrl(it)
        }
        binding.itemTitle.text = item.title
        binding.price.text = item.price.formatCurrency(item.currencyId)
        binding.availableQty.text = binding.context.getString(
            R.string.search_result_row_availability,
            item.availableQuantity.formatQuantity()
        )
        binding.newLabel.isVisible = item.condition == Item.Condition.NEW
        binding.usedLabel.isVisible = item.condition == Item.Condition.USED
        binding.root.setOnClickListener {
            onClick(item)
        }
    }
}