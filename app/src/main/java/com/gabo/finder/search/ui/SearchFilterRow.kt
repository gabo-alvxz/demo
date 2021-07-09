package com.gabo.finder.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gabo.finder.common.adapter.Row
import com.gabo.finder.databinding.SearchFilterRowBinding
import com.gabo.network.models.Filter
import com.gabo.network.models.FilterValue

class SearchFilterRow(
    private val filter : Filter,
    private val onFilterSelection :(Pair<Filter, FilterValue>) -> Unit,
    private val currentlySelected : FilterValue? = null
) : Row<SearchFilterRowBinding> {

    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup
    ): SearchFilterRowBinding {
        return SearchFilterRowBinding.inflate(
            layoutInflater,
            viewGroup,
            false
        )
    }

    override fun bind(binding: SearchFilterRowBinding) {
        binding.title.text =  filter.name
        binding.radioGroup.setOptions(
            options = filter.values,
            textProducer = { filterValue ->
                filterValue.name
            },
            onOptionSelected = { filterValue ->
                onFilterSelection(filter to filterValue)
            },
            initialSelection = {
                it.id == currentlySelected?.id
            }
        )
    }


}