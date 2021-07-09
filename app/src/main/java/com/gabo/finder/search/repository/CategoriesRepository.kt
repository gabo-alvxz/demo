package com.gabo.finder.search.repository

import com.gabo.architecture.utils.Resource
import com.gabo.architecture.utils.SettingsUtils
import com.gabo.finder.common.utils.toResource
import com.gabo.network.api.CategoriesApi
import com.gabo.models.models.search.SearchCategory
import com.gabo.network.transport.NetworkException
import com.gabo.network.utils.NetworkConstants
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val api : CategoriesApi,
    settingsUtils: SettingsUtils
) {
    private val site = settingsUtils.getSelectedSite() ?:  NetworkConstants.DEFAULT_SITE_ID
    suspend fun getCategories() : Resource<List<SearchCategory>, NetworkException>{
        return api.getCategories(site).execute().toResource()
    }
}