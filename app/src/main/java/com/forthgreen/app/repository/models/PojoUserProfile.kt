package com.forthgreen.app.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ShrayChona on 04-12-2018.
 */
data class PojoUserProfile(
        val code: Int = 0,
        val data: LoginData = LoginData(),
        val message: String = "",
)

data class LoginData(
        val accessToken: String = "",
        val user: UserProfile = UserProfile(),
)

data class PojoUserUpdate(
        val code: Int = 0,
        val data: UserProfile = UserProfile(),
        val message: String = "",
)

@Parcelize
data class UserProfile(
    val __v: Int = 0,
    val _id: String = "",
    val blocked: Boolean = false,
    val isBlock: Boolean = false,
    val createdOn: String = "",
    val dateOfBirth: String = "",
    val deleted: Boolean = false,
    val device: String = "",
    val email: String = "",
    val emailToken: Long = 0L,
    val emailTokenDate: String = "",
    val fcmToken: String = "",
    val firstName: String = "",
    val bio: String = "",
    val username: String = "",
    val gender: Int = 0,
    val image: String = "",
    val isVerified: Boolean = false,
    val lastName: String = "",
    val isFollow: Boolean = false,
    val followers: Int = 0,
    val followings: Int = 0,
    val posts: List<HomeFeed> = listOf(),
    val isSenderBlock: List<SenderBlock> = listOf(),
    val isFollowing: Boolean = false,
    val dummyUser: Boolean = false,
    val socialIdentifier: Int = 0,
) : Parcelable {
    val usernameText: String
        get() = if (username.isNotEmpty()) {
            "@$username"
        } else {
            ""
        }
    val followersText: String
        get() = "$followers Followers"

    val followingText: String
        get() = "$followings Following"
}

data class Phone(
        val code: String = "",
        val number: Long = 0L,
        val isVerified: Boolean = false,
)

data class PojoDate(
        val day: Int = 0,
        val month: Int = 0,
        val year: Int = 0,
)

