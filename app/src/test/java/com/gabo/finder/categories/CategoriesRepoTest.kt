package com.gabo.finder.categories

import com.gabo.architecture.utils.Resource
import com.gabo.architecture.utils.SettingsUtils
import com.gabo.finder.search.repository.CategoriesRepository
import com.gabo.models.models.search.SearchCategory
import com.gabo.network.transport.NetworkException
import com.gabo.finder.utils.CoroutinesTest
import com.gabo.finder.utils.assertInstance
import com.gabo.mocks.utils.FakeNetworkCall
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.*


@ExperimentalCoroutinesApi
class CategoriesRepoTest : CoroutinesTest() {
    @Test
    fun testGetCategoriesHappyPathForDefaultSite() = launchTest {
        val categories = listOf(
            SearchCategory(id = "1", name = "test1"),
            SearchCategory(id = "2", name = "test2")
        )
        val settingsUtils : SettingsUtils = mock()
        val repo = CategoriesRepository(
            api = mock {
                on { getCategories(eq("MLA")) }
                    .thenReturn(FakeNetworkCall.Success(categories))
            },
            settingsUtils = settingsUtils
        )
        val result = repo.getCategories()
        verify(settingsUtils, times(1)).getSelectedSite()
        assertInstance(Resource.Success::class, result)
        Assert.assertEquals(categories, (result as? Resource.Success)?.value)
    }

    @Test
    fun testGetCategoriesHappyPathForOtherSite() = launchTest {
        val categories = listOf(
            SearchCategory(id = "1", name = "test1"),
            SearchCategory(id = "2", name = "test2")
        )
        val repo = CategoriesRepository(
            api = mock {
                on { getCategories(eq("TEST")) }
                    .thenReturn(FakeNetworkCall.Success(categories))
            },
            settingsUtils = mock {
                on { getSelectedSite() }.thenReturn("TEST")
            }
        )
        val result = repo.getCategories()
        Assert.assertEquals(categories, (result as? Resource.Success)?.value)
    }

    @Test
    fun testCategoriesNetworkError() = launchTest {
        val e = NetworkException()
        val repo = CategoriesRepository(
            api = mock {
                on { getCategories(any()) }.thenReturn(FakeNetworkCall.Error(e))
            },
            settingsUtils = mock()
        )
        val result = repo.getCategories()
        assertInstance(Resource.Error::class, result)
        Assert.assertEquals(e, (result as? Resource.Error)?.error)
    }
}