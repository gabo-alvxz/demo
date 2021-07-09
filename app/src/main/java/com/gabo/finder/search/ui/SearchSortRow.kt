package com.gabo.finder.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gabo.architecture.utils.context
import com.gabo.finder.R
import com.gabo.finder.common.adapter.Row
import com.gabo.finder.databinding.SearchFilterRowBinding
import com.gabo.models.models.search.SearchPage

class SearchSortRow(
    private val availableSorts : List<SearchPage.Sort>,
    private val onSortSelected: (SearchPage.Sort) -> Unit,
    private val selectedSort : SearchPage.Sort? = null
) : Row<SearchFilterRowBinding> {
    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup
    ): SearchFilterRowBinding {
        return SearchFilterRowBinding.inflate(layoutInflater, viewGroup, false)
    }

    override fun bind(binding: SearchFilterRowBinding) {
        binding.title.text = binding.context.getString(R.string.sort_by)
        binding.radioGroup.setOptions(
            options = availableSorts,
            textProducer =  { sort ->
                sort.name
            },
            onOptionSelected =  onSortSelected,
            initialSelection = { sort ->
                selectedSort?.id == sort.id
            }
        )
    }

}