package com.clawstack.carabaseadmin.data.network

import com.clawstack.carabaseadmin.data.security.SecureIdentityVault
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * CaraBaseClient — CaraBase Mobile
 * 
 * Constructs the Retrofit instance tied to the AuthInterceptor.
 * Maintains epistemic boundaries for network requests.
 * 
 * Maintained by CrustAgent©™
 */
object CaraBaseClient {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = false // Optimized for networking
    }

    fun createRetrofit(baseUrl: String, vault: SecureIdentityVault): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (android.util.Log.isLoggable("CaraBase", android.util.Log.DEBUG)) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }

        val authInterceptor = AuthInterceptor(vault)

        // Session Integrity Interceptor: Clear vault on 401 Unauthorized
        val integrityInterceptor = Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            
            if (response.code == 401) {
                // Invariant: Unauthorized access severs the local identity bridge
                vault.clearToken()
            } else if (response.code == 429) {
                // Invariant: Rate limiting is surfaced as a specific security event
                throw java.io.IOException("Rate limit exceeded. Please back off and retry later.")
            }
            response
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(integrityInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}
