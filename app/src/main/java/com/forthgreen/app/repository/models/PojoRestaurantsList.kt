package com.forthgreen.app.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class PojoRestaurantsList(
        val code: Int = 0,
        val data: List<Restaurant> = listOf(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0,
        val timestamp: String = "",
)

@Parcelize
data class Restaurant(
        val __v: Int = 0,
        val _id: String = "",
        val about: String = "",
        val blocked: Boolean = false,
        val categories: List<Int> = listOf(),
        val createdOn: String = "",
        val deleted: Boolean = false,
        val images: List<String> = listOf(),
        val location: Location = Location(),
        val name: String = "",
        val phoneCode: String = "",
        val phoneNumber: String = "",
        val website: String = "",
        val portCode: String = "",
        val price: String = "",
        val distance: Double = 0.0,
        val isFollowed: Boolean = false,
        val ratings: Ratings = Ratings(),
        val placePicture: String = "",
        val thumbnail: String = "",
        val typeOfFood: String = "",
        val selfRating: SelfRating = SelfRating(),
        val updatedOn: String = "",
        val showPhoneNumber: Boolean = false,
        val isBookmark: Boolean = false,
) : Parcelable

@Parcelize
data class Location(
        val address: String = "",
        val coordinates: List<Double> = listOf(),
        val type: String = "",
) : Parcelable

@Parcelize
data class Ratings(
        val _id: String = "",
        val averageRating: Double = 0.0,
        val count: Int = 0,
) : Parcelable

@Parcelize
data class SelfRating(
        val __v: Int = 0,
        val _id: String = "",
        val createdOn: String = "",
        val deleted: Boolean = false,
        val rating: Int = 0,
        val restaurantRef: String = "",
        val review: String = "",
        val title: String = "",
        val updatedOn: String = "",
        val userRef: String = "",
) : Parcelable