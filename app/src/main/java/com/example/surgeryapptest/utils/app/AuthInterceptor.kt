package com.example.surgeryapptest.utils.app

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor to add auth token to requests
 */
@Singleton
class AuthInterceptor @Inject constructor(@ApplicationContext private val context: Context) : Interceptor {

    private val sessionManager = SessionManager(context)
    private val dataStoreRepository = DataStoreRepository(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

//        val bearerToken = dataStoreRepository.readUserAccessToken.toString()
//        println("ACCESS TOKEN FROM DS: $bearerToken")
//        requestBuilder.addHeader("Authorization", "Bearer $bearerToken")

        return chain.proceed(requestBuilder.build())
    }
}