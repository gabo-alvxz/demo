package com.gabo.finder.search

import com.gabo.architecture.utils.Resource
import com.gabo.finder.search.repository.SearchRepository
import com.gabo.finder.utils.CoroutinesTest
import com.gabo.finder.utils.assertInstance
import com.gabo.mocks.factories.SearchResultsFactory
import com.gabo.mocks.utils.FakeNetworkCall
import com.gabo.network.models.Filter
import com.gabo.network.transport.NetworkException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class SearchRepoTest : CoroutinesTest() {

    @Test
    fun testSearchQueryHappyPath() = launchTest {
        val results = SearchResultsFactory.fakeResults(listOf(SearchResultsFactory.fakeItem()))
        val repo = SearchRepository(
            api = mock {
                on { search(any(), eq("test"), isNull(), isNull(), any(), any()) }
                    .thenReturn(
                        FakeNetworkCall.Success(results)
                    )
            }, mock()
        )
        val got = repo.search("test")
        assertInstance(Resource.Success::class, got)
        Assert.assertEquals(results, (got as? Resource.Success)?.value)
    }

    @Test
    fun testSearchNetworkError() = launchTest {
        val e = NetworkException()
        val repo = SearchRepository(
            api = mock {
                on { search(any(), any(), anyOrNull(), anyOrNull(), any(), any()) }
                    .thenReturn(FakeNetworkCall.Error(e))
            },
            mock()
        )
        val got = repo.search("test")
        assertInstance(Resource.Error::class, got)
        Assert.assertEquals(e, (got as? Resource.Error)?.error)
    }

    @Test
    fun testFilterOutFiltersWithNullType() = launchTest {
        val fakeResponse = SearchResultsFactory.fakeResults(
            listOf(SearchResultsFactory.fakeItem())
        ).copy(
                availableFilters = listOf(
                    Filter(name = "test", id="123", type = null, values = emptyList())
                )
            )

        val repo  = SearchRepository(
            api = mock {
                on { search(any(), any(), anyOrNull(), anyOrNull(), any(), any()) }
                    .thenReturn(FakeNetworkCall.Success(fakeResponse))
            },
            mock()
        )
        val got = repo.search(query = "test")
        assertInstance(Resource.Success::class, got)
        val response  = (got as Resource.Success).value
        Assert.assertTrue("It filtered the wrong filter", response.availableFilters.isEmpty())
        Assert.assertTrue("It doesnt have available filters", !response.hasMoreFilters)

    }

}