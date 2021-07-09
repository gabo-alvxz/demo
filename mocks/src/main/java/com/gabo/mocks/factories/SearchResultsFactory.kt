package com.gabo.mocks.factories

import com.gabo.models.models.item.Item
import com.gabo.models.models.search.SearchPage
import com.gabo.models.models.pagination.OffsetPagination
import java.util.UUID

object SearchResultsFactory {
    fun fakeResults(items : List<Item>) : SearchPage<Item> {
        return SearchPage(
            paging = OffsetPagination(
                offset = 0,
                total = items.size,
                limit = items.size
            ),
            results = items,
            sort = SearchPage.Sort(
                id = "relevance",
                name = "Relevance"
            ),
            availableSorts = emptyList(),
            filters = emptyList(),
            availableFilters = emptyList(),
            query = null
        )
    }

    fun fakeItem(name : String? = null) : Item {
        return Item(
            id = UUID.randomUUID().toString(),
            title = name ?: "Test Item",
            price = 1000.0,
            soldQuantity = 3,
            availableQuantity = 10,
            thumbnail="test",
            attributes = emptyList(),
            acceptsMercadoPago = true,
            condition = Item.Condition.NEW,
            installments = null,
            url = "https://www.mercadolibre.com",
            currencyId = "ARS",
            sellerId = UUID.randomUUID().toString()
        )
    }



}