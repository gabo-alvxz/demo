package com.gabo.finder.common.adapter

import com.gabo.models.models.pagination.Pagination
import com.gabo.network.transport.NetworkState
import com.gabo.network.utils.Page
import timber.log.Timber

/**
 *   Adapter to implement generic paginated responses in a infinite-scroll way.
 *
 */
class PaginatedAdapter<T>(
    private val loadNextPage: (Pagination?) -> Unit,
    private val mapRow: (T) -> Row<*>,
    private val onFirstLoading: (PaginatedAdapter<T>.() -> Unit)? = null,
    private val onLoadingError : (PaginatedAdapter<T>.()-> Unit)? = null,
    private val onPagesEmpty : (PaginatedAdapter<T>.() -> Unit)? = null,
    private val onPagesLoaded : (PaginatedAdapter<T>.() -> Unit)? = null,
    private val onStartOfPages: (PaginatedAdapter<T>.() -> Unit)? = null
) : BaseAdapter() {

    private var nextPage: Pagination? = null

    private var lastPage : Page<T>? = null

    var pages: NetworkState<Page<T>>? = null
        set(value) {
            field = value
            value ?: return clear()
            when(value) {
                is NetworkState.Loading -> onLoading()
                is NetworkState.Success ->{
                    lastPage = value.value
                    onPagesLoaded(value.value)
                }
                is NetworkState.Error -> onError()
            }
        }

    fun addPage(page: NetworkState<Page<T>>){
        when(page) {
            is NetworkState.Loading -> onLoading()
            is NetworkState.Error -> {
                Timber.e(page.error, "Page error!")
                onError()
            }
            is NetworkState.Success -> onSinglePageLoaded(page.value)
        }
    }

    private fun onSinglePageLoaded(page : Page<T>){
       lastPage = lastPage?.append(page) ?: page
       onPagesLoaded(lastPage!!)
    }


    private fun onPagesLoaded(page: Page<T>) {
        clear()
        addHeaders()
        addAll(page.items.map { mapRow(it) })
        if (page.pagination.hasMoreItems) {
            add(LoadNextRow())
            nextPage = page.pagination.nextPage()
        }
        if(lastPage?.items?.isNotEmpty() != true){
            onPagesEmpty?.invoke(this)
        }else{
            onPagesLoaded?.invoke(this)
        }
    }

    private fun onLoading() {
        if (isEmpty) {
            addHeaders()
            onFirstLoading?.invoke(this)
        }
    }

    private fun addHeaders(){
        onStartOfPages?.invoke(this)
    }

    private fun onError() {
        if(lastPage == null && onLoadingError != null){
            clear()
            addHeaders()
            onLoadingError.invoke(this)
        } else {
            addErrorRow()
        }
    }

    // We need to remove all the state rows, error and loading, and then add the error one
    private fun addErrorRow() {
        val filtered = rows.filter {
            it !is LoadNextRow && it !is ErrorRow
        }
        clear()
        addAll(filtered)
    }

    fun clearPages(){
        nextPage = null
        lastPage = null
        clear()
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val row = rows[position]
        if (row is LoadNextRow) {
            loadNextPage(nextPage)
        }
    }
}