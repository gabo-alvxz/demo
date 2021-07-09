package com.gabo.finder.search.utils

import android.os.Parcelable
import com.gabo.network.models.Filter
import com.gabo.network.models.FilterValue
import com.gabo.models.models.search.SearchCategory
import com.gabo.models.models.search.SearchPage
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchParameters(
    val query : String? = null,
    val category : SearchCategory? = null,
    val filters : Map<Filter, FilterValue>? = null,
    val sort : SearchPage.Sort? = null,
    val availableFilters : List<Filter>? = null,
    val availableSorts : List<SearchPage.Sort>? = null
)  : Parcelable


fun SearchPage<*>.toSearchParameters() :  SearchParameters{
    return SearchParameters(
        query = this.query,
        filters = this.selectedFilters(),
        sort = this.sort,
        availableFilters = this.availableFilters,
        availableSorts = this.availableSorts
    )
}


fun SearchPage<*>.selectedFilters() :  Map<Filter, FilterValue>{
    val selected = mutableMapOf<Filter, FilterValue>()
    this.filters.filter {
        it.values.isNotEmpty()
    }.forEach { filter ->
        selected[filter] = filter.values.first()
    }
    return selected
}

fun Map<Filter, FilterValue>.getFilterValue(other : Filter) : FilterValue ?{
   return this.entries.firstOrNull {
        it.key.id == other.id
    }?.value
}