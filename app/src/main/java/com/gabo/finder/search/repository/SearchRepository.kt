package com.gabo.finder.search.repository

import com.gabo.architecture.utils.Resource
import com.gabo.architecture.utils.SettingsUtils
import com.gabo.finder.common.utils.toResource
import com.gabo.network.models.Filter
import com.gabo.network.models.FilterValue
import com.gabo.models.models.item.Item
import com.gabo.models.models.search.SearchCategory
import com.gabo.models.models.search.SearchPage
import com.gabo.network.models.toQueryMap
import com.gabo.network.api.SearchApi
import com.gabo.models.models.pagination.Pagination
import com.gabo.network.transport.NetworkException
import com.gabo.network.utils.NetworkConstants
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val api: SearchApi,
     settingsUtils: SettingsUtils
) {

    private val site  : String
            = settingsUtils.getSelectedSite() ?: NetworkConstants.DEFAULT_SITE_ID

    suspend fun search(
        query: String? = null,
        category: SearchCategory? = null,
        pagination: Pagination? = null,
        sort: SearchPage.Sort? = null,
        filters: Map<Filter, FilterValue>? = null
    ): Resource<SearchPage<Item>, NetworkException> {
        return api.search(
            site = site,
            query = query,
            category = category?.id,
            sort = sort?.id,
            pagination = pagination?.toQueryMap()?: emptyMap(),
            filters = filters?.toQueryMap()?: emptyMap()
        ).execute()
            .toResource()
            //Filter out every filter that we don't support or know how to handle.
            .map { searchPage ->
                searchPage.copy(
                    availableFilters = searchPage.availableFilters.filter {
                        it.type != null
                    },
                )
            }
    }


}