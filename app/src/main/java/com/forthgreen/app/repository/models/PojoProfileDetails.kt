package com.forthgreen.app.repository.models

data class PojoProfileDetails(
        val code: Int = 0,
        val data: UserProfile = UserProfile(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val timestamp: String = "",
)