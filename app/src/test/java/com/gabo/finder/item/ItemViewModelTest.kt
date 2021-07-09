package com.gabo.finder.item

import com.gabo.architecture.utils.Resource
import com.gabo.finder.item.repository.ItemRepository
import com.gabo.finder.item.viewModel.ItemViewModel
import com.gabo.finder.user.repository.UserRepository
import com.gabo.finder.utils.CoroutinesTest
import com.gabo.finder.utils.getOrAwaitUntilState
import com.gabo.mocks.factories.SearchResultsFactory
import com.gabo.mocks.factories.UsersFactory
import com.gabo.network.transport.NetworkState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class ItemViewModelTest : CoroutinesTest(){
    @Test
    fun testFetchItem() = launchTest {
        val fakeItem = SearchResultsFactory.fakeItem("test item")
            .copy(
                sellerId = "test_seller"
            )
        val fakeUser =  UsersFactory.fakeUser("test user")
        val itemRepo : ItemRepository = mock {
            onBlocking { getItem(eq("test")) }.thenReturn(
                Resource.Success(fakeItem)
            )
            onBlocking { getItemDescription(eq("test")) }
                .thenReturn(Resource.Success("test description"))
        }
        val userRepo : UserRepository = mock {
            onBlocking { getUser(eq("test_seller")) }
                .thenReturn(Resource.Success(fakeUser))
        }

        val viewModel = ItemViewModel(itemRepo, userRepo)
        viewModel.fetchItem("test")

        val itemSuccess = viewModel.itemLiveEvent.getOrAwaitUntilState(NetworkState.Success::class)
            as NetworkState.Success

        val userSuccess = viewModel.sellerLiveEvent.getOrAwaitUntilState(NetworkState.Success::class)
            as NetworkState.Success

        val descriptionSuccess =  viewModel.descriptionLiveEvent.getOrAwaitUntilState(NetworkState.Success::class)
            as NetworkState.Success

        Assert.assertEquals(fakeItem,itemSuccess.value )
        Assert.assertEquals(fakeUser, userSuccess.value)
        Assert.assertEquals("test description", descriptionSuccess.value)
    }
}