package com.forthgreen.app.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class PojoBrandList(
        val code: Int = 0,
        val data: List<Brand> = listOf(),
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val size: Int = 0,
)

data class PojoBrandDetail(
        val code: Int = 0,
        val data: Brand = Brand(),
        val message: String = "",
)

data class PojoProductDetail(
        val code: Int = 0,
        val data: Product = Product(),
        val message: String = "",
)

@Parcelize
data class Brand(
        val _id: String = "",
        val about: String = "",
        val brandName: String = "",
        val followers: Int = 0,
        val isFollowing: Boolean = false,
        val length: Int = 0,
        val limit: Int = 0,
        val page: Int = 0,
        val products: List<Product> = listOf(),
        val coverImage: String = "",
        var count: Int = 0,
        val logo: String = "",
        val website: String = "",
        val isBookmark: Boolean = false,
) : Parcelable

@Parcelize
data class Product(
        val _id: String = "",
        var brandRef: String = "",
        val deleted: Boolean = false,
        val info: String = "",
        val link: String = "",
        val name: String = "",
        val price: String = "",
        val brandName: String = "",
        val isFollowed: Boolean = false,
        var isBookmark: Boolean = false,
        val images: List<String> = listOf(),
        val blocked: Boolean = false,
        val averageRating: Double = 0.0,
        val coverImage: String = "",
        val length: Int = 0,
        val limit: Int = 0,
        val logo: String = "",
        val page: Int = 0,
        val ratingAndReview: List<RatingAndReview> = listOf(),
        val similarProducts: List<SimilarProduct> = listOf(),
        val totalReviews: Int = 0,
        val brandDetails: BrandDetails = BrandDetails(),
        val category: Int = 0,
        val keywords: List<String> = listOf(),
        val uploadedToProfile: Boolean = false,
        val createdOn: String = "",
        val currency: String = "",
        val subCategory: Int = 0,
        val gender: Int = 0,
        val __v: Int = 0,
        val updatedOn: String = "",

) : Parcelable {
    val productPriceText: String
        get() {
            return if (currency.isNotEmpty()) {
                "$currency$price"
            } else {
                "£$price"
            }
        }
}

@Parcelize
data class RatingAndReview(
        val _id: String = "",
        val fullName: String = "",
        val image: String = "",
        val rating: Float = 0F,
        val review: String = "",
        val title: String = "",
        val userRef: String = "",
) : Parcelable

@Parcelize
data class SimilarProduct(
        val _id: String = "",
        val brandName: String = "",
        val brandRef: String = "",
        val images: List<String> = listOf(),
        val info: String = "",
        val link: String = "",
        val name: String = "",
        val price: String = "",
        val currency: String = "",
        val isBookmark: Boolean = false,
        val category: Int = 0,
        val createdOn: String = "",
        val gender: Int = 0,
        val priceDecimal: Double = 0.0,
        val subCategory: Int = 0
) : Parcelable {
    val productPriceText: String
        get() {
            return if (currency.isNotEmpty()) {
                "$currency$price"
            } else {
                "£$price"
            }
        }
}

@Parcelize
data class BrandDetails(
        val _id: String = "",
        val about: String = "",
        val companyName: String = "",
        val coverImage: String = "",
        val email: String = "",
        val logo: String = "",
        val mobileCode: String = "",
        val mobileNumber: String = "",
        val name: String = "",
        val website: String = "",
) : Parcelable