package com.gabo.models.models.search

import android.os.Parcelable
import com.gabo.models.models.pagination.OffsetPagination
import com.gabo.models.models.pagination.Pagination
import com.gabo.network.models.Filter
import com.gabo.network.utils.Page
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/***
 *  Generic data class that represent a search. It  could be for Items but , as the app grows,
 *  support other type of data such as user reviews or sellers, provided that the search response is the same.
 *
 *  As it implements Page<T>  it can be used in PaginatedAdapter.
 */
data class SearchPage<T>(
    @SerializedName("query")
    val query : String?,
    @SerializedName("paging")
    val paging: OffsetPagination,
    @SerializedName("results")
    val results: List<T>,
    @SerializedName("sort")
    val sort: Sort,
    @SerializedName("available_sorts")
    val availableSorts: List<Sort>,
    @SerializedName("filters")
    val filters: List<Filter>,
    @SerializedName("available_filters")
    val availableFilters: List<Filter>

) : Page<T> {
    override val items: List<T>
        get() = results
    override val pagination: Pagination
        get() = paging

    @Parcelize
    data class Sort(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String
    ) : Parcelable

    override fun append(next: Page<T>): Page<T> {
        next as SearchPage<T>
        return this.copy(
            paging = next.paging,
            results = results.toMutableList()
                .apply { addAll(next.results) },
            sort = next.sort,
            availableSorts = next.availableSorts,
            filters = next.filters,
            availableFilters = next.availableFilters
        )
    }

    val hasMoreFilters : Boolean
       get() = this.availableFilters.isNotEmpty()

}


