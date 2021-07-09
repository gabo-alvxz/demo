package com.gabo.finder.search

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gabo.architecture.navigation.open
import com.gabo.architecture.ui.BaseFragment
import com.gabo.architecture.ui.setOnCollapseListener
import com.gabo.architecture.utils.context
import com.gabo.architecture.utils.formatQuantity
import com.gabo.architecture.utils.hideKeyboard
import com.gabo.finder.R
import com.gabo.finder.common.adapter.PaginatedAdapter
import com.gabo.finder.databinding.SearchResultsFragmentBinding
import com.gabo.finder.search.ui.SearchResultRow
import com.gabo.finder.search.utils.SearchParameters
import com.gabo.finder.search.utils.toSearchParameters
import com.gabo.finder.search.viewModel.SearchViewModel
import com.gabo.models.models.item.Item
import com.gabo.models.models.search.SearchPage
import com.gabo.network.transport.NetworkState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultsFragment : BaseFragment<SearchResultsFragmentBinding>() {


    private var headerCollapsed = false

    private val searchViewModel: SearchViewModel by activityViewModels()


    private var displayedContent = false

    val adapter: PaginatedAdapter<Item> =  PaginatedAdapter(
            loadNextPage = { pagination ->
                searchViewModel.loadNextPage(pagination)
            },
            mapRow = { item ->
                SearchResultRow(item) {
                    SearchResultsFragmentDirections
                        .openItemFromSearch(item.id, item)
                        .open(context)
                }
            },
            onPagesEmpty = {
               binding?.stateView?.showEmpty()
            },
            onLoadingError = {
                binding?.stateView?.showError {
                    searchViewModel.retrySearch()
                }
            },
            onFirstLoading = {
                binding?.stateView?.showLoading()
                displayedContent = false
            },
            onPagesLoaded = {
                    binding?.stateView?.showContent()
            }
        )

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        menu.findItem(R.id.filter_menu).isVisible = headerCollapsed
                && searchViewModel.lastResults?.hasMoreFilters == true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.filter_menu) {
            openFiltersDialog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun onHeaderCollapseChanged(isCollapsed: Boolean) {
        val binding = binding ?: return
        headerCollapsed = isCollapsed
        binding.toolbarSearchView.isVisible = isCollapsed
        invalidateOptionsMenu()
        if(isCollapsed){
            setupActionBar()
        }
    }


    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?
    ): SearchResultsFragmentBinding {
        return SearchResultsFragmentBinding.inflate(
            layoutInflater,
            viewGroup,
            false
        ).apply {
            setToolbar(toolbar)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            appbar.setOnCollapseListener {
                onHeaderCollapseChanged(it)
            }
            appbar.setExpanded(!headerCollapsed)
            invalidateOptionsMenu()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel.results.observe(viewLifecycleOwner) {
            onSearchResults(it)
        }
        searchViewModel.parametersChanged.observe(viewLifecycleOwner) {
            onParametersChanged(it)
        }
    }


    private fun setupHeader(resultState : NetworkState<SearchPage<Item>>?) {
        val binding = binding ?: return
        val results =  (resultState as?  NetworkState.Success<SearchPage<Item>>)?.value

        binding.moreFilters.isVisible = results?.availableFilters?.isNotEmpty() == true
        val currentFilter = results?.filters
            ?.lastOrNull()
            ?.values?.firstOrNull()

        binding.selectedCategory.isVisible = currentFilter != null
        binding.selectedCategory.text = currentFilter?.name

        binding.moreFilters.setOnClickListener {
            openFiltersDialog()
        }

        binding.resultsCount.isVisible = results?.paging?.total != null
        binding.resultsCount.text = binding.context.getString(
            R.string.search_result_count,
            results?.paging?.total?.formatQuantity(
                resources.getInteger(R.integer.max_results_quantity_limit)
            )
        )
        binding.searchInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchQuery(binding.searchInput.text.toString())
                true
            } else false
        }
        val query = searchViewModel.lastParameters?.query ?: return
        binding.searchInput.setText(query)
        setupActionBar()
    }

    private fun setupActionBar() {
        val binding = binding ?: return
        binding.toolbarSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                onSearchQuery(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        binding.toolbarSearchView.setQuery(searchViewModel.lastParameters?.query, false)
    }


    private fun onSearchQuery(query: String) {
        val parameters = searchViewModel.lastResults?.toSearchParameters() ?: SearchParameters()
        searchViewModel.updateParameters(
            parameters.copy(query = query)
        )
    }

    private fun onParametersChanged(newParameters: SearchParameters) {
        val binding = binding ?: return
        adapter.clearPages()
        binding.root.hideKeyboard()
        binding.recyclerView.smoothScrollToPosition(0)
        binding.appbar.setExpanded(true)
        setupHeader(null)       //Clean up the search box from previous searches.
        searchViewModel.search(newParameters)
    }

    private fun openFiltersDialog() {
        val parameters = searchViewModel.lastResults?.toSearchParameters() ?: SearchParameters()
        SearchResultsFragmentDirections
            .openSearchFiltersDialog(parameters)
            .open(context)
    }

    private fun onSearchResults(state: NetworkState<SearchPage<Item>>) {
        if(!displayedContent) {
            setupHeader(state)
            displayedContent = true
        }
        adapter.pages = state
    }


}