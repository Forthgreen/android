package com.forthgreen.app.repository.models

/**
 * Created by Chetan Tuteja on 30-May-20.
 */
data class PojoBrandProductSearch(
        val code: Int = 0,
        val data: BrandProductSearch = BrandProductSearch(),
        val format: String = "",
        val hasMore: Boolean = false,
        val limit: Int = 0,
        val message: String = "",
        val page: Int = 0,
        val timestamp: String = ""
)

data class BrandProductSearch(
        val list: List<ProductSearch> = listOf(),
        val nextPageToken: String = ""
)

data class ProductSearch(
        val _id: String = "",
        val about: String = "",
        val brand: BrandSearch = BrandSearch(),
        val category: Int = 0,
        val coverImage: String = "",
        val images: List<String> = listOf(),
        val info: String = "",
        val isProduct: Boolean = false,
        val link: String = "",
        val logo: String = "",
        val name: String = "",
        val ownerName: String = "",
        val price: String = ""
)

data class BrandSearch(
        val _id: String = "",
        val coverImage: String = "",
        val logo: String = "",
        val name: String = "",
        val ownerName: String = ""
)