package com.gabo.finder.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gabo.finder.common.adapter.Row
import com.gabo.finder.databinding.CategoryTagBinding
import com.gabo.models.models.search.SearchCategory

class CategoryRow(
    val category : SearchCategory,
    val onClick : (SearchCategory) -> Unit
) : Row<CategoryTagBinding> {

    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup
    ): CategoryTagBinding {
        return  CategoryTagBinding.inflate(
            layoutInflater,
            viewGroup,
            false
        )
    }

    override fun bind(binding: CategoryTagBinding) {
       binding.tag.text = category.name
       binding.tag.setOnClickListener {
           onClick(category)
       }
    }

}