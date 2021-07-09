package com.gabo.finder.search.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.gabo.architecture.livedata.SingleTimeEvent
import com.gabo.architecture.utils.launch
import com.gabo.finder.common.utils.toNetworkState
import com.gabo.finder.search.repository.SearchRepository
import com.gabo.finder.search.utils.SearchParameters
import com.gabo.finder.search.utils.toSearchParameters
import com.gabo.network.models.Filter
import com.gabo.network.models.FilterValue
import com.gabo.models.models.item.Item
import com.gabo.models.models.search.SearchCategory
import com.gabo.models.models.search.SearchPage
import com.gabo.models.models.pagination.Pagination
import com.gabo.network.transport.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val SEARCH_PARAMETERS = "SearchVM_parameters"

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: SearchRepository
) : ViewModel() {

    private val _results = MutableLiveData<NetworkState<SearchPage<Item>>>()
    val results: LiveData<NetworkState<SearchPage<Item>>>
        get() = _results

    var lastParameters : SearchParameters?
        set(value) =  stateHandle.set(SEARCH_PARAMETERS, value)
        get() = stateHandle.get(SEARCH_PARAMETERS)

    var lastResults: SearchPage<Item>? = null



    private val _parametersChanged = SingleTimeEvent<SearchParameters>()
    val parametersChanged
        get() = _parametersChanged

    fun updateParameters(parameters: SearchParameters) {
        lastParameters = parameters
        _parametersChanged.postValue(parameters)
    }



    fun search(
        parameters : SearchParameters
    ) = launch {
        clear()
        lastParameters = parameters
        _results.postValue(NetworkState.Loading)
        _results.postValue(
                repository.search(
                    query = parameters.query,
                    filters = parameters.filters,
                    sort = parameters.sort,
                    category = parameters.category
                ).alsoDo {
                    lastResults = it
                }.toNetworkState()
        )
    }

    fun retrySearch(){
        val parameters = lastParameters ?: SearchParameters()
        search(parameters)
    }

    fun search(
        query: String? = null,
        sort: SearchPage.Sort? = null,
        filters: Map<Filter, FilterValue>? = null,
        category: SearchCategory? = null,
    ) = launch {
        search(
            SearchParameters(
                query, category, filters, sort
            )
        )
    }

    fun loadNextPage(pagination: Pagination?)  = launch {
        val previousParams =  lastResults?.toSearchParameters() ?: return@launch
        val previousPage = lastResults
        _results.postValue(NetworkState.Loading)

        //Loads a new page
        val newPage = repository.search(
            query = previousParams.query,
            filters =previousParams.filters,
            sort = previousParams.sort,
            pagination = pagination
        ).map {
            (previousPage?.append(it) as? SearchPage<Item>) ?: it
        }.alsoDo {
            lastResults = it
        }.toNetworkState()
        _results.postValue(newPage)
    }

    fun clear(){
        lastResults = null
        lastParameters = null
    }


}