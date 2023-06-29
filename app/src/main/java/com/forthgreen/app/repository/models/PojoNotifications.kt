package com.forthgreen.app.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Nandita Gandhi
 * @since 24-04-2021
 */
data class PojoNotifications(
        val code: Int = 0,
        val data: List<Notification> = listOf(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0,
        val timestamp: String = "",
) {
    val hasUnreadNotifs: Boolean
        get() {
            val unreadNotifications = data.filter { notification ->
                !notification.seen
            }
            return unreadNotifications.isNotEmpty()
        }
}

data class Notification(
        val _id: String = "",
        val createdOn: String = "",
        val image: String = "",
        val message: String = "",
        val name: String = "",
        val ref: String = "",
        val userid: String = "",
        val refType: Int = 0,
        val seen: Boolean = false,
        val username: String = "",
) {
    val notificationText
        get() = "$name$message"
}

@Parcelize
data class PushNotificationPayload(
        val notificationId: String = "",
        val user: UserProfile = UserProfile(),
        val userRef: String = "",
) : Parcelable
