package com.gabo.finder.categories

import com.gabo.architecture.utils.Resource
import com.gabo.models.models.search.SearchCategory
import com.gabo.network.transport.NetworkException
import com.gabo.finder.search.viewModel.CategoriesViewModel
import com.gabo.finder.utils.CoroutinesTest
import com.gabo.finder.utils.getOrAwaitUntilState
import com.gabo.network.transport.NetworkState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock


@ExperimentalCoroutinesApi
class CategoriesViewModelTest : CoroutinesTest() {
    @Test
    fun testGetCategoriesHappyPath() = launchTest {
        val categories = listOf(
            SearchCategory(id = "1", name = "test1"),
            SearchCategory(id = "2", name = "test2")
        )
        val viewModel = CategoriesViewModel(
            repository = mock {
                onBlocking { getCategories() }.thenReturn(Resource.Success(categories))
            }
        )
        viewModel.fetchCategories()
        val success = viewModel.categories
            .getOrAwaitUntilState(NetworkState.Success::class) as NetworkState.Success
        Assert.assertEquals(categories, success.value)
    }

    @Test
    fun testGetCategoriesNetworkError() = launchTest {
        val e = NetworkException()
        val viewModel = CategoriesViewModel(
            repository = mock {
                onBlocking { getCategories() }.thenReturn(Resource.Error(e))
            }
        )
        viewModel.fetchCategories()
        val error = viewModel.categories.getOrAwaitUntilState(NetworkState.Error::class)
                as NetworkState.Error
        Assert.assertEquals(e, error.error)

    }
}