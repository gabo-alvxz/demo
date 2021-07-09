package com.gabo.network.utils

import com.gabo.models.models.pagination.Pagination

/***
 *  Generic pagination interface, to be used in the PaginatedAdapter.
 *  This represents the response of a paginated call , and can be extended in the future to support
 *  any other paginated response, not only for search results but also for comments, reviews, etc.
 *
 *  You can see OffsetPagination and SearchPage for an example of it's implementations.
 *  Once you have your own implementation of
 */
interface Page<T> {
    val pagination: Pagination
    val items: List<T>
    /**
     *  Append the next page at the end of the current one and returns
     *  the resulting Page<T>.
     *  The Pagination object of the returned Page should be the same Pagination of the
     *  appended page.
     *
     *  @param next the next page to be appended.
     *  @return a Page<T> with the items of both merged, with the next.items at the end of the
     *  list.
     */
    fun append(next : Page<T>) : Page<T>
}
