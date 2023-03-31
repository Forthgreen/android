package com.forthgreen.app.repository.models

/**
 * @author Nandita Gandhi
 * @since 01-05-2021
 */
data class PojoAddComment(
        val code: Int = 0,
        val data: Comment = Comment(),
        val format: String = "",
        val message: String = "",
        val timestamp: String = "",
)