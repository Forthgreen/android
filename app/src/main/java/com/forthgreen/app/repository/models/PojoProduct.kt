package com.forthgreen.app.repository.models

/**
 * @author shraychona@gmail.com
 * @since 21 May 2020
 */

data class PojoProduct(
        val code: Int = 0,
        val data: List<Product> = listOf(),
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0
)