package com.gabo.models.models.pagination

import com.google.gson.annotations.SerializedName

data class OffsetPagination(
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("limit")
    val limit: Int
) : Pagination {
    override val hasMoreItems: Boolean
        get() = offset + limit < total

    override fun nextPage(): Pagination {
        return this.copy(
            offset = offset + limit
        )
    }

    override fun toQueryMap(): Map<String, String> {
        return mapOf(
            "offset" to offset.toString(),
            "limit" to limit.toString()
        )
    }
}