package com.forthgreen.app.repository.models

data class PojoRestaurantDetails(
        val code: Int = 0,
        val data: Restaurant = Restaurant(),
        val format: String = "",
        val message: String = "",
        val timestamp: String = ""
)