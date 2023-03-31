package com.forthgreen.app.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Nandita Gandhi
 * @since 21-04-2021
 */
data class PojoPostsList(
        val code: Int = 0,
        val data: List<HomeFeed> = listOf(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0,
        val timestamp: String = "",
)

@Parcelize
data class HomeFeed(
    val _id: String = "",
    val addedBy: UserProfile = UserProfile(),
    val bio: String = "",
    val comments: Int = 0,
    val createdOn: String = "",
    val email: String = "",
    val image: List<String> = listOf(),
    val isLike: Boolean = false,
    val likes: Int = 0,
    val name: String = "",
    val username: String = "",
    val text: String = "",
    val type: Int = 0,
    val status: Boolean = false,
    val updatedOn: String = "",
    var isFollowed: Boolean = false,
    val comment: PostComment = PostComment(),
    val priority: Boolean = false,
    val tags: List<Users> = listOf(),
    val whoLiked: List<UserProfile> = listOf(),
    val thumbnail: String = "",
    val time: String = "",
    val video: String = "",
    val videoHeight: Int = 0,
    val videoWidth: Int = 0,
) : Parcelable {
    val likesText: String
        get() = "$likes Likes"

    val commentsText: String
        get() = "$comments Comments"

    val usernameText: String
        get() = if (username.isNotEmpty()) {
            "@$username"
        } else {
            ""
        }

    val randomUserData: UserProfile
        get() = UserProfile(_id = _id, firstName = name, username = username)
}