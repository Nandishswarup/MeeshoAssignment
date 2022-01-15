package com.example.interviewassignment.networking

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

class ApiInterfaceBuilder  {
    companion object
    {

        var baseUrl="";
        var apiInterFace: ApiInterace? = null
        var okHttpClient: OkHttpClient? = null

        fun getApiInterface(): ApiInterace?
        {
            if (apiInterFace == null) {

                //For printing API url and body in logcat
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

                okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(45,TimeUnit.SECONDS)
                    .build()


                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
                apiInterFace =retrofit.create(ApiInterace ::class.java)
            }

            return apiInterFace
        }

    }

}