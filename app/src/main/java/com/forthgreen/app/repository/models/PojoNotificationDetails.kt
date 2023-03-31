package com.forthgreen.app.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Nandita Gandhi
 * @since 03-05-2021
 */
data class PojoNotificationDetails(
        val code: Int = 0,
        val data: List<PostDetails> = listOf(),
        val format: String = "",
        val message: String = "",
        val timestamp: String = "",
)

data class PostDetails(
        val _id: String = "",
        val posts: HomeFeed = HomeFeed(),
        val refType: Int = 0,
)

@Parcelize
data class PostComment(
        val _id: String = "",
        val addedBy: UserProfile = UserProfile(),
        val comment: String = "",
        val createdOn: String = "",
        val isLike: Boolean = false,
        val likes: Int = 0,
        val replies: Int = 0,
        val reply: Comment = Comment(),
        val status: Boolean = false,
        val tags: List<Users> = listOf(),
) : Parcelable {
    val repliesText: String
        get() = "$replies Replies"

    val likesText: String
        get() = "$likes Likes"

    val postCommentData: Comment
        get() = Comment(_id = _id, addedBy = addedBy, comment = comment, isLike = isLike,
                likes = likes, reply = replies)
}
