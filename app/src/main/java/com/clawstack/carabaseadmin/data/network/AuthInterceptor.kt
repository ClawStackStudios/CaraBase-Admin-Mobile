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

        if (token == null) {
            // Let the request proceed without auth, the server will reject if necessary
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest = originalRequest.newBuilder()
            .header("x-admin-session", token)
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
