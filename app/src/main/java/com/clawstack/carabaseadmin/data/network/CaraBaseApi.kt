package com.clawstack.carabaseadmin.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query
import kotlinx.serialization.Serializable

/**
 * CaraBaseApi — CaraBase Mobile
 * 
 * The Retrofit API definition. Maps strictly to the CaraBase Backend Routes.
 * 
 * Maintained by CrustAgent©™
 */
interface CaraBaseApi {

    @POST("/api/admin/auth")
    suspend fun getAuthToken(@Body request: AdminAuthRequest): AuthTokenResponse

    @GET("/api/admin/stats")
    suspend fun getSystemStats(): TelemetryResponse

    @GET("/api/admin/system")
    suspend fun getAggregateSystemStats(): SystemAggregateResponse

    @GET("/api/admin/users")
    suspend fun getUsers(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): UsersResponse

    @DELETE("/api/admin/users/{uuid}")
    suspend fun deleteUser(@Path("uuid") uuid: String): GenericResponse

    @GET("/api/admin/audit")
    suspend fun getAuditLogs(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("event_type") eventType: String? = null,
        @Query("outcome") outcome: String? = null
    ): AuditResponse
}

@Serializable
data class AdminAuthRequest(val token: String)

@Serializable
data class AuthTokenResponse(
    val success: Boolean,
    val token: String? = null,
    val error: String? = null
)

@Serializable
data class GenericResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)

@Serializable
data class TelemetryResponse(
    val memory: MemoryStats,
    val uptime: Double
)

@Serializable
data class MemoryStats(
    val rss: Long,
    val heapTotal: Long,
    val heapUsed: Long,
    val external: Long
)

@Serializable
data class SystemAggregateResponse(
    val success: Boolean,
    val data: SystemStatsData? = null
)

@Serializable
data class SystemStatsData(
    val totalUsers: Int,
    val totalTables: Int,
    val totalPolicies: Int,
    val totalEndpoints: Int,
    val dbSize: Long,
    val uptime: Double,
    val lastAudit: String? = null
)

@Serializable
data class Pagination(
    val total: Int,
    val limit: Int,
    val offset: Int
)

@Serializable
data class UsersResponse(
    val success: Boolean,
    val data: List<UserMetadata> = emptyList(),
    val pagination: Pagination? = null,
    val error: String? = null
)

@Serializable
data class UserMetadata(
    val uuid: String,
    val username: String,
    val created_at: String,
    val active_keys: Int,
    val last_login: String? = null
)

@Serializable
data class AuditResponse(
    val success: Boolean,
    val data: List<AuditLog> = emptyList(),
    val pagination: Pagination? = null,
    val error: String? = null
)

@Serializable
data class AuditLog(
    val id: Long,
    val event_type: String,
    val actor: String? = null,
    val actor_type: String? = null,
    val action: String,
    val outcome: String,
    val resource: String? = null,
    val details: String? = null, // Backend sends JSON string
    val ip_address: String? = null,
    val user_agent: String? = null,
    val timestamp: String
)
