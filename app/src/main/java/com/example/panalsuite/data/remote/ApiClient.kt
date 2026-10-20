package com.example.panalsuite.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Builds the Retrofit client once for the whole app. */
object ApiClient {

    /** 10.0.2.2 is the computer's localhost as seen from the Android emulator. */
    const val BASE_URL = "http://10.0.2.2:3000/"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    val api: PanalSuiteApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PanalSuiteApi::class.java)
    }
}
