package com.forthgreen.app.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PojoTag(
        val code: Int = 0,
        val data: List<Users> = listOf(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0,
        val timestamp: String = "",
) : Parcelable

@Parcelize
data class Users(
        val _id: String = "",
        val image: String = "",
        val name: String = "",
        val type: Int = 0,
) : Parcelable