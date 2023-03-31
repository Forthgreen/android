package com.forthgreen.app.repository.models

/**
 * @author ShrayChona@gmail.com
 * @since 22 May 2019
 */
data class PojoContactsList(
        val data: PojoContactData = PojoContactData(),
        val code: Int = 0,
        val error: String = "",
        val message: String = ""
)

data class PojoRegionMemberList(
        val data: List<FriendContact> = listOf(),
        val code: Int = 0,
        val error: String = "",
        val message: String = ""
)

data class PojoContactData(
        var existingUsersList: List<FriendContact> = listOf(),
        var uniqueUsersList: List<FriendContact> = listOf()
)

data class FriendContact(
        val _id: String = "",
        var name: String = "",
        var phoneNumber: String = "",
        var picture: String = "",
        var isOnAPP: Boolean = true,
        var isInvited: Boolean = false,
        var isSelected: Boolean = false
)