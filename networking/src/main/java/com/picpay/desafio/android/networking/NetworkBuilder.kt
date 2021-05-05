package com.picpay.desafio.android.networking

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkBuilder {

    inline operator fun <reified T> invoke(url: String, okHttpClient: OkHttpClient) =
        with(Retrofit.Builder()) {
            addConverterFactory(GsonConverterFactory.create(Gson()))
            baseUrl(url)
            client(okHttpClient)
            build().create(T::class.java)
        }
}