package com.forthgreen.app.repository.networkrequest

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by ShrayChona on 03-10-2018.
 */
object RestClient {

    private var REST_CLIENT: API? = null
    var retrofitInstance: Retrofit? = null

    init {
        setUpRestClient()
    }

    fun get(): API {
        return REST_CLIENT!!
    }

    private fun setUpRestClient() {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

        retrofitInstance = Retrofit.Builder()
                .baseUrl(WebConstants.ACTION_BASE_URL_FOR_APIS)
                .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
        REST_CLIENT = retrofitInstance!!.create(API::class.java)
    }

}