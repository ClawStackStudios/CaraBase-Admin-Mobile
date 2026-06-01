package com.clawstack.carabaseadmin.data.network

import com.clawstack.carabaseadmin.data.security.SecureIdentityVault
import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor — CaraBase Mobile
 * 
 * Intercepts outbound requests and injects the Bearer token
 * from the SecureIdentityVault if available.
 * 
 * Maintained by CrustAgent©™
 */
class AuthInterceptor(private val vault: SecureIdentityVault) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = vault.getToken()

        val requestBuilder = originalRequest.newBuilder()
            .header("User-Agent", "CaraBase-Admin-Mobile/1.0 (Android)")
            .header("x-client-type", "carabase-admin-mobile")

        if (token != null) {
            requestBuilder.header("x-admin-session", token)
        }

        return chain.proceed(requestBuilder.build())
    }
}
