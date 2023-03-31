package com.forthgreen.app.repository.models

data class PojoRestaurantReviews(
        val code: Int = 0,
        val data: List<ReviewData> = listOf(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0,
        val timestamp: String = "",
)

data class ReviewData(
        val __v: Int = 0,
        val _id: String = "",
        val createdOn: String = "",
        val deleted: Boolean = false,
        val rating: Int = 0,
        val restaurantRef: String = "",
        val review: String = "",
        val title: String = "",
        val updatedOn: String = "",
        val userDetails: UserProfile = UserProfile(),
        val userRef: String = "",
)