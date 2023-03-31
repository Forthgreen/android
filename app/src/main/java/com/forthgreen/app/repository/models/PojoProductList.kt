package com.forthgreen.app.repository.models

data class PojoProductList(
        val code: Int = 0,
        val data: List<Product> = listOf(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0,
        val timestamp: String = "",
)