package com.gabo.finder.search

import androidx.lifecycle.SavedStateHandle
import com.gabo.architecture.utils.Resource
import com.gabo.finder.search.repository.SearchRepository
import com.gabo.finder.search.utils.SearchParameters
import com.gabo.finder.search.viewModel.SEARCH_PARAMETERS
import com.gabo.finder.search.viewModel.SearchViewModel
import com.gabo.finder.utils.CoroutinesTest
import com.gabo.finder.utils.getOrAwaitUntil
import com.gabo.finder.utils.getOrAwaitUntilState
import com.gabo.mocks.factories.SearchResultsFactory
import com.gabo.mocks.utils.FakeNetworkCall
import com.gabo.models.models.item.Item
import com.gabo.models.models.pagination.Pagination
import com.gabo.models.models.search.SearchPage
import com.gabo.network.api.SearchApi
import com.gabo.network.transport.NetworkException
import com.gabo.network.transport.NetworkState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class SearchViewModelTest : CoroutinesTest() {

    private fun makeRepoForItems(items: List<Item>): SearchRepository = mock {
        onBlocking { search(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()) }
            .thenReturn(
                Resource.Success(
                    SearchResultsFactory.fakeResults(items)
                )
            )
    }


    @Test
    fun testSearchHappyPath() = launchTest {
        val item = SearchResultsFactory.fakeItem("test item")
        val savedState: SavedStateHandle = mock()
        val viewModel = spy(
            SearchViewModel(
                savedState, makeRepoForItems(listOf(item))
            )
        )
        //Given any search with a successful response
        viewModel.search("test")
        val got = viewModel.results.getOrAwaitUntilState(NetworkState.Success::class)
                as NetworkState.Success

        //assert that :
        //it cleared the previous results
        verify(viewModel, times(1)).clear()

        // We got the correct results
        Assert.assertEquals(
            item,
            got.value.results.first()
        )

    }

    @Test
    fun testSearchForNetworkError() = launchTest {
        val exception = NetworkException()
        val savedState: SavedStateHandle = mock()
        val viewModel = SearchViewModel(
            repository = mock {
                onBlocking {
                    search(
                        anyOrNull(),
                        anyOrNull(),
                        anyOrNull(),
                        anyOrNull(),
                        anyOrNull()
                    )
                }
                    .thenReturn(Resource.Error(exception))
            },
            stateHandle = savedState
        )
        // Given any search with an  exception
        viewModel.search("test")
        //Assert that:
        //We got the correct error state
        val error = viewModel.results.getOrAwaitUntilState(NetworkState.Error::class)
                as NetworkState.Error
        Assert.assertEquals(exception, error.error)
        //No results were stored
        verify(savedState, never()).set(any(), any<SearchPage<Item>>())

    }

    @Test
    fun testRetrySearch() = launchTest {
        val item = SearchResultsFactory.fakeItem("test item")
        val savedState: SavedStateHandle = mock {
            on { get<SearchParameters>(eq(SEARCH_PARAMETERS)) }.thenReturn(
                SearchParameters(query = "retry")
            )
        }
        val repo = makeRepoForItems(listOf(item))
        val viewModel = spy(
            SearchViewModel(
                savedState, repo
            )
        )
        //Given a retry search
        viewModel.retrySearch()
        //Assert that:
        // the correct search was executed
        verify(repo, times(1)).search(
            eq("retry"),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
        //We got the correct result
        val got = viewModel.results.getOrAwaitUntilState(NetworkState.Success::class)
                as NetworkState.Success

        Assert.assertEquals(item, got.value.results.first())

    }

    @Test
    fun testUpdateSearchParameters() = launchTest {
        val savedState: SavedStateHandle = mock()
        val newParameters = SearchParameters(
            query = "test"
        )
        val viewModel = SearchViewModel(
            repository = mock(),
            stateHandle = savedState
        )
        //Given that we updated the search parameters
        viewModel.updateParameters(newParameters)
        //Assert that the correct event was fired
        viewModel.parametersChanged.getOrAwaitUntil {
            it.query == newParameters.query
        }
    }

    @Test
    fun testSearchLoadNextPage() = launchTest {
        val firstItem = SearchResultsFactory.fakeItem("first")
        val previous = SearchResultsFactory.fakeResults(listOf(firstItem))
        val pagination: Pagination = mock()

        val repo = makeRepoForItems(
            listOf(
                SearchResultsFactory.fakeItem("second")
            )
        )
        val viewModel = SearchViewModel(mock(), repo)
        viewModel.lastResults = previous
        //Given that I fetch the next page
        viewModel.loadNextPage(pagination)
        //Assert that:
        //The pagination was passed to the search call in the repo
        verify(repo, times(1)).search(
            anyOrNull(),
            anyOrNull(),
            eq(pagination),
            anyOrNull(),
            anyOrNull()
        )
        //We got the correct results properly appended
        val results = viewModel.results.getOrAwaitUntilState(NetworkState.Success::class)
            as NetworkState.Success
        Assert.assertEquals("first", results.value.results.first().title)
        Assert.assertEquals("second", results.value.results[1].title)
    }
}