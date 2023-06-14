package com.forthgreen.app.repository.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class ShopResponse(

	@Json(name="code")
	val code: Int = 0,

	@Json(name="data")
	val data: List<Product>? = null,

	@Json(name="size")
	val size: Int? = null,

	@Json(name="limit")
	val limit: Int? = null,

	@Json(name="hasMore")
	val hasMore: Boolean? = null,

	@Json(name="format")
	val format: String? = null,

	@Json(name="page")
	val page: String? = null,

	@Json(name="message")
	val message: String? = null,

	@Json(name="timestamp")
	val timestamp: String? = null
)
@Parcelize
data class DataItem(

	@Json(name="brandName")
	val brandName: String? = null,

	@Json(name="images")
	val images: List<String?>? = null,

	@Json(name="subCategory")
	val subCategory: Int? = null,

	@Json(name="gender")
	val gender: Int? = null,

	@Json(name="topDate")
	val topDate: String? = null,

	/*@Json(name="priceDecimal")
	val priceDecimal: Any? = null,*/

	@Json(name="createdOn")
	val createdOn: String? = null,

	@Json(name="brandcoverImage")
	val brandcoverImage: String? = null,

	@Json(name="brandlogo")
	val brandlogo: String? = null,

	@Json(name="price")
	val price: String? = null,

	@Json(name="sortingDate")
	val sortingDate: String? = null,

	@Json(name="name")
	val name: String? = null,

	@Json(name="currency")
	val currency: String = "",

	@Json(name="_id")
	val id: String? = null,

	@Json(name="category")
	val category: Int? = null,

	@Json(name="isBookmark")
	val isBookmark: Boolean? = null
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