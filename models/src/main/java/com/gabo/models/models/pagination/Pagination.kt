package com.gabo.models.models.pagination
/**
 *  Generic interface that represents a Pagination.
 *  This abstracts the pagination from the actual implementation, in this case , OffsetPaginaion.
 *  If the pagination changes,  this interface will remain being valid.
 *
 *  For example, we could easily implement the hash - scan
 */
interface Pagination {
    val hasMoreItems: Boolean
    fun nextPage(): Pagination
    fun toQueryMap(): Map<String, String>
}