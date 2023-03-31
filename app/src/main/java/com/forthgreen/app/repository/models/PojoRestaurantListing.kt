package com.forthgreen.app.repository.models

/**
 * Created by Chetan Tuteja on 23-Oct-20.
 */
data class PojoRestaurantListing(
        val code: Int = 0,
        val data: List<Restaurant> = listOf(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0,
        val timestamp: String = "",
)