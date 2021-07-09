package com.gabo.finder.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gabo.architecture.navigation.open
import com.gabo.architecture.ui.BaseFragment
import com.gabo.architecture.utils.hideKeyboard
import com.gabo.finder.common.adapter.BaseAdapter
import com.gabo.finder.databinding.SearchHomeFragmentBinding
import com.gabo.finder.search.ui.CategoryRow
import com.gabo.finder.search.utils.SearchParameters
import com.gabo.finder.search.viewModel.CategoriesViewModel
import com.gabo.finder.search.viewModel.SearchViewModel
import com.gabo.models.models.search.SearchCategory
import com.gabo.network.transport.NetworkState
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchHomeFragment : BaseFragment<SearchHomeFragmentBinding>() {

    val categoriesViewModel: CategoriesViewModel by viewModels()

    val searchViewModel : SearchViewModel by activityViewModels()

    val categoriesAdapter = BaseAdapter()

    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?
    ): SearchHomeFragmentBinding {
        return SearchHomeFragmentBinding.inflate(
            layoutInflater,
            viewGroup,
            false
        ).apply {
            val context = context ?: return@apply
            searchTagsRecyclerview.layoutManager = FlexboxLayoutManager(context)
            searchTagsRecyclerview.adapter = categoriesAdapter
            searchButton.setOnClickListener {
                startSearch()
            }

            searchInput.setOnEditorActionListener { _ , actionId, _ ->
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch()
                    true
                }else{
                    false
                }
            }
            searchInput.addTextChangedListener {
                footer.isVisible = !it.isNullOrEmpty()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesViewModel.categories.observe(viewLifecycleOwner, Observer {
            onCategories(it)
        })
    }

    private fun onCategories(categoriesState : NetworkState<List<SearchCategory>>){
        when(categoriesState){
            is NetworkState.Loading -> binding?.stateView?.showLoading()
            is NetworkState.Success -> renderCategories(categoriesState.value)
            is NetworkState.Error  -> binding?.stateView?.showError {
                categoriesViewModel.fetchCategories()
            }
        }
    }

    private fun startSearch() {
        val context = context ?: return
        val query = binding?.searchInput?.text?.toString()
        if(query.isNullOrEmpty()){
            return
        }
        binding?.searchInput?.hideKeyboard()

        searchViewModel.updateParameters(
            SearchParameters(query = query)
        )

        SearchHomeFragmentDirections
            .performSearch()
            .open(context)
    }


    private fun renderCategories(categories : List<SearchCategory>){
        binding?.stateView?.showContent()
        categoriesAdapter.addAll(
            categories.map {
                CategoryRow(it){ category ->
                    val context = context ?: return@CategoryRow
                    binding?.root?.hideKeyboard()

                    searchViewModel.updateParameters(
                        SearchParameters(category = it)
                    )

                    SearchHomeFragmentDirections
                        .performSearch()
                        .open(context)
                }
            }
        )
    }

}