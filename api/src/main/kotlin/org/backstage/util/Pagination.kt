package org.backstage.util

data class PageInfo(
    val pageIndex: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalItems: Long
)

data class PaginatedResponse<I>(
    val page: PageInfo,
    val items: List<I>
)
