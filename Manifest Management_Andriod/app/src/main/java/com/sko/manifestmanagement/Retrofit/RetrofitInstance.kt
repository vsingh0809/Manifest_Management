package com.sko.manifestmanagement.Retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    // Define your base URL
    private const val BASE_URL = "http://demo.skosystems.co/manifestportal/api/"
    //private const val BASE_URL = "http://10.0.2.2:5217/api/"

    private val loggingInterceptor=HttpLoggingInterceptor().apply {
        level= HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient =OkHttpClient.Builder().apply {
        addInterceptor(loggingInterceptor)
    }.build()

    // Lazy initialization for Retrofit instance
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}
