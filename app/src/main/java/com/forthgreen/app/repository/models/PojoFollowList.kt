package com.forthgreen.app.repository.models

/**
 * @author Nandita Gandhi
 * @since 22-04-2021
 */
data class PojoFollowList(
        val code: Int = 0,
        val data: List<UserProfile> = listOf(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0,
        val timestamp: String = "",
)