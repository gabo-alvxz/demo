package com.gabo.finder.item

import com.gabo.architecture.utils.Resource
import com.gabo.finder.item.repository.ItemRepository
import com.gabo.finder.utils.CoroutinesTest
import com.gabo.finder.utils.assertInstance
import com.gabo.mocks.factories.SearchResultsFactory
import com.gabo.mocks.utils.FakeNetworkCall
import com.gabo.models.models.item.Item
import com.gabo.network.api.ItemsApi
import com.gabo.network.transport.NetworkException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class ItemRepoTest : CoroutinesTest() {
    @Test
    fun testGetItemHappyPath() = launchTest {
        val item = SearchResultsFactory.fakeItem("test item")
        val api: ItemsApi = mock {
            on { getItem(eq("test")) }
                .thenReturn(
                    FakeNetworkCall.Success(item)
                )
        }
        val repo = ItemRepository(api)
        val got = repo.getItem("test")
        assertInstance(Resource.Success::class, got)
        Assert.assertEquals(
            item,
            (got as Resource.Success).value
        )
    }

    @Test
    fun testGetItemNetworkError() = launchTest {
        val exception = NetworkException()
        val api: ItemsApi = mock {
            on { getItem(eq("test")) }
                .thenReturn(
                    FakeNetworkCall.Error(exception)
                )
        }
        val repo = ItemRepository(api)
        val got = repo.getItem("test")
        assertInstance(Resource.Error::class, got)
        Assert.assertEquals(
            exception,
            (got as Resource.Error).error
        )
    }

    @Test
    fun testGetDescription() = launchTest {
        val api: ItemsApi = mock {
            on { getDescription(eq("test")) }
                .thenReturn(
                    FakeNetworkCall.Success(
                        Item.Description(
                            text = "test description"
                        )
                    )
                )
        }
        val repo = ItemRepository(api)
        val got = repo.getItemDescription("test")
        assertInstance(Resource.Success::class, got)
        Assert.assertEquals(
            "test description",
            (got as Resource.Success).value
        )

    }

}