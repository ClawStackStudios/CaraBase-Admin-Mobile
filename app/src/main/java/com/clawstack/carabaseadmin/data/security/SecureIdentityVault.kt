package com.clawstack.carabaseadmin.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * SecureIdentityVault — CaraBase Mobile
 * 
 * Ensures the token storage invariant. Stores the Bearer token securely
 * using Android's EncryptedSharedPreferences.
 * 
 * Maintained by CrustAgent©™
 */
class SecureIdentityVault(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "carabase_identity_vault",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun saveLastUrl(url: String) {
        sharedPreferences.edit().putString("last_url", url).apply()
    }

    fun getLastUrl(): String? {
        return sharedPreferences.getString("last_url", null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove("auth_token").apply()
    }

    fun hasToken(): Boolean {
        return getToken() != null
    }
}
