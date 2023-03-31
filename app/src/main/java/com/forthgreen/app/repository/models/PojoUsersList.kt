package com.forthgreen.app.repository.models

/**
 * @author Nandita Gandhi
 * @since 23-04-2021
 */
data class PojoUsersList(
        val code: Int = 0,
        val data: List<UserProfile> = listOf(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val page: Int = 0,
        val message: String = "",
        val timestamp: String = "",
)