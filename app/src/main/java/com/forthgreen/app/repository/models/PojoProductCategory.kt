package com.forthgreen.app.repository.models

data class PojoProductCategory(
    val code: Int = 0,
    val data: List<ProductCategory> = listOf(),
    val format: String = "",
    val message: String = "",
    val timestamp: String = ""
)

data class ProductCategory(
    val _id: Int = 0,
    val products: List<Product> = listOf()
)