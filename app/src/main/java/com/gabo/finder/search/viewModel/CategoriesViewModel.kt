package com.gabo.finder.search.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gabo.architecture.utils.launch
import com.gabo.finder.common.utils.toNetworkState
import com.gabo.finder.search.repository.CategoriesRepository
import com.gabo.models.models.search.SearchCategory
import com.gabo.network.transport.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repository: CategoriesRepository
) : ViewModel() {

    private val _categories = MutableLiveData<NetworkState<List<SearchCategory>>>()
    val categories: LiveData<NetworkState<List<SearchCategory>>> = _categories

    init {
        fetchCategories()
    }

    fun fetchCategories() = launch {
        _categories.postValue(NetworkState.Loading)
        _categories.postValue(
            repository.getCategories().toNetworkState()
        )
    }

}