package com.forthgreen.app.repository.models

data class PojoMyReviews(
        val code: Int = 0,
        val data: List<Review> = listOf(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0
)

data class Review(
        val _id: String = "",
        val createdOn: String = "",
        val images: List<String> = listOf(),
        val name: String = "",
        val rating: Int = 0,
        val review: String = "",
        val title: String = ""
)