package com.gabo.finder.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gabo.finder.common.adapter.BaseAdapter
import com.gabo.finder.databinding.SearchFiltersDialogBinding
import com.gabo.finder.search.ui.SearchFilterRow
import com.gabo.finder.search.ui.SearchSortRow
import com.gabo.finder.search.utils.SearchParameters
import com.gabo.finder.search.utils.getFilterValue
import com.gabo.finder.search.viewModel.SearchViewModel
import com.gabo.network.models.Filter
import com.gabo.network.models.FilterValue
import com.gabo.models.models.search.SearchPage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFiltersDialog : BottomSheetDialogFragment() {

    private lateinit var binding: SearchFiltersDialogBinding

    private val searchViewModel: SearchViewModel by activityViewModels()

    private val args: SearchFiltersDialogArgs by navArgs()

    private val adapter = BaseAdapter()

    private val parameters: SearchParameters
        get() = args.searchParameters

    private lateinit var newParameters: SearchParameters

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return SearchFiltersDialogBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            binding = this
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            goBack.setOnClickListener {
                dismiss()
            }
            applyFilters.setOnClickListener {
                onApplyFilters()
            }
        }.root
    }

    private fun updateFilter(filter: Filter, value: FilterValue) {
        val filters = newParameters.filters?.toMutableMap() ?: mutableMapOf()
        filters[filter] = value
        newParameters = newParameters.copy(
            filters = filters
        )
    }

    private fun updateSort(sort: SearchPage.Sort) {
        newParameters = newParameters.copy(sort = sort)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newParameters = parameters
        addSorts()
        addFilters()
    }

    private fun addSorts() {
        parameters.availableSorts?.let { availableSorts ->
            val selected = parameters.sort
            val allSorts = mutableListOf<SearchPage.Sort>()
                .apply {
                    if (selected != null) {
                        add(selected)
                    }
                    addAll(availableSorts)
                }

            adapter.add(
                SearchSortRow(
                    availableSorts = allSorts,
                    onSortSelected = {
                        updateSort(it)
                    },
                    selectedSort = parameters.sort
                )
            )
        }
    }

    private fun addFilters() {
        parameters.availableFilters?.forEach { filter ->
            adapter.add(
                SearchFilterRow(
                    filter = filter,
                    onFilterSelection = { (filter, value) ->
                        updateFilter(filter, value)
                    },
                    currentlySelected = parameters.filters?.getFilterValue(filter)
                )
            )
        }
    }



    private fun onApplyFilters() {
        searchViewModel.updateParameters(newParameters)
        dismiss()
    }

}