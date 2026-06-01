package com.clawstack.carabaseadmin.data

import android.util.Log
import com.clawstack.carabaseadmin.data.network.AdminAuthRequest
import com.clawstack.carabaseadmin.data.network.CaraBaseApi
import com.clawstack.carabaseadmin.data.network.CaraBaseClient
import com.clawstack.carabaseadmin.data.network.SystemAggregateResponse
import com.clawstack.carabaseadmin.data.network.UsersResponse
import com.clawstack.carabaseadmin.data.network.AuditResponse
import com.clawstack.carabaseadmin.data.network.TelemetryResponse
import com.clawstack.carabaseadmin.data.security.SecureIdentityVault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

/**
 * CaraBaseSystemRepository — CaraBase Mobile
 * 
 * Central Repository managing epistemic bounds of API requests.
 * 
 * Maintained by CrustAgent©™
 */
class CaraBaseSystemRepository(private val vault: SecureIdentityVault) {
    private var baseUrl: String = ""
    private var retrofit: Retrofit? = null
    private var api: CaraBaseApi? = null

    init {
        val lastUrl = vault.getLastUrl()
        if (lastUrl != null) {
            setBaseUrl(lastUrl)
        }
    }

    fun setBaseUrl(url: String) {
        val cleanUrl = if (url.endsWith("/")) url else "$url/"
        // Only recreate Retrofit if the URL has actually changed
        if (baseUrl != cleanUrl) {
            baseUrl = cleanUrl
            try {
                retrofit = CaraBaseClient.createRetrofit(baseUrl, vault)
                api = retrofit?.create(CaraBaseApi::class.java)
            } catch (e: Exception) {
                Log.e("CaraBaseSystem", "Invalid Base URL: $baseUrl", e)
            }
        }
    }

    fun getLastUrl(): String? = vault.getLastUrl()

    suspend fun authenticate(adminToken: String): Result<Boolean> = withContext(Dispatchers.IO) {
        if (api == null) return@withContext Result.failure(Exception("Invalid Server URL Configuration"))

        try {
            val hashedToken = sha256(adminToken)
            // Mobile authenticates via POST /api/admin/auth to receive a stateless Admin Session token
            val response = api!!.getAuthToken(AdminAuthRequest(token = hashedToken))

            if (response.success && response.token != null) {
                // Save token and URL to SecureIdentityVault for future AuthInterceptor requests
                vault.saveToken(response.token)
                vault.saveLastUrl(baseUrl)
                Result.success(true)
            } else {
                Result.failure(Exception(response.error ?: "Authentication failed"))
            }
        } catch (e: Exception) {
            Log.e("CaraBaseSystem", "Authentication Exception", e)
            Result.failure(e)
        }
    }

    suspend fun getSystemStats(): Result<TelemetryResponse> = withContext(Dispatchers.IO) {
        val currentApi = api ?: return@withContext Result.failure(Exception("Invalid Server URL Configuration"))

        try {
            val stats = currentApi.getSystemStats()
            Result.success(stats)
        } catch (e: Exception) {
            Log.e("CaraBaseSystem", "Telemetry Fetch Failed", e)
            Result.failure(e)
        }
    }

    suspend fun getAggregateSystemStats(): Result<SystemAggregateResponse> = withContext(Dispatchers.IO) {
        val currentApi = api ?: return@withContext Result.failure(Exception("Invalid Server URL Configuration"))
        try {
            val stats = currentApi.getAggregateSystemStats()
            Result.success(stats)
        } catch (e: Exception) {
            Log.e("CaraBaseSystem", "Aggregate Fetch Failed", e)
            Result.failure(e)
        }
    }

    suspend fun getUsers(limit: Int = 50, offset: Int = 0): Result<UsersResponse> = withContext(Dispatchers.IO) {
        val currentApi = api ?: return@withContext Result.failure(Exception("Invalid Server URL Configuration"))
        try {
            val res = currentApi.getUsers(limit, offset)
            Result.success(res)
        } catch (e: Exception) {
            Log.e("CaraBaseSystem", "Users Fetch Failed", e)
            Result.failure(e)
        }
    }

    suspend fun deleteUser(uuid: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val currentApi = api ?: return@withContext Result.failure(Exception("Invalid Server URL Configuration"))
        try {
            val res = currentApi.deleteUser(uuid)
            if (res.success) {
                Result.success(true)
            } else {
                Result.failure(Exception(res.error ?: "Failed to delete user"))
            }
        } catch (e: Exception) {
            Log.e("CaraBaseSystem", "Delete User Failed", e)
            Result.failure(e)
        }
    }

    suspend fun getAuditLogs(limit: Int = 50, offset: Int = 0): Result<AuditResponse> = withContext(Dispatchers.IO) {
        val currentApi = api ?: return@withContext Result.failure(Exception("Invalid Server URL Configuration"))
        try {
            val res = currentApi.getAuditLogs(limit, offset)
            Result.success(res)
        } catch (e: Exception) {
            Log.e("CaraBaseSystem", "Audit Logs Fetch Failed", e)
            Result.failure(e)
        }
    }

    private fun sha256(input: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
