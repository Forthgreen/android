package com.forthgreen.app.repository.models

import com.squareup.moshi.Json

data class PostDetailResponse(

	@Json(name="code")
	val code: Int = 0,

	@Json(name="data")
	val data: List<PostDetails> =  listOf(),

	@Json(name="format")
	val format: String? = null,

	@Json(name="message")
	val message: String? = null,

	@Json(name="timestamp")
	val timestamp: String? = null
)
