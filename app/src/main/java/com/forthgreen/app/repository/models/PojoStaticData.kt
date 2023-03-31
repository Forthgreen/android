package com.forthgreen.app.repository.models

/**
 * Created by Chetan Tuteja on 06-Jun-20.
 */
data class PojoStaticData(
        val code: Int = 0,
        val data: StaticData = StaticData(),
        val format: String = "",
        val message: String = "",
        val timestamp: String = ""
)

data class StaticData(
        val __v: Int = 0,
        val _id: String = "",
        val aboutUs: String = "",
        val privacyPolicy: String = "",
        val termsAndCondition: String = ""
)