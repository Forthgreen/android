package com.forthgreen.app.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Nandita Gandhi
 * @since 26-04-2021
 */
data class PojoCommentsList(
        val code: Int = 0,
        val data: List<Comment> = listOf(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0,
        val timestamp: String = "",
)

@Parcelize
data class Comment(
        val __v: Int = 0,
        val _id: String = "",
        val addedBy: UserProfile = UserProfile(),
        val comment: String = "",
        val createdOn: String = "",
        val isLike: Boolean = false,
        val likes: Int = 0,
        val reply: Int = 0,
        val postRef: String = "",
        val status: Boolean = false,
        val updatedOn: String = "",
        val userRef: String = "",
        val commentRef: String = "",
        val tags: List<Users> = listOf(),
) : Parcelable {
    val repliesText: String
        get() = "$reply Replies"

    val likesText: String
        get() = "$likes Likes"
}