package com.mrit.player

import android.content.Context
import java.util.UUID

/**
 * Gera e mantém um deviceId persistente por dispositivo físico,
 * equivalente ao gerarDeviceId() do player.js, mas usando SharedPreferences.
 */
object DeviceIdProvider {

    private const val PREFS_NAME = "mrit_prefs"
    private const val KEY_DEVICE_ID = "device_id"

    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_DEVICE_ID, null)
        if (!existing.isNullOrBlank()) return existing

        val newId = "device_" + UUID.randomUUID().toString().replace("-", "")
        prefs.edit().putString(KEY_DEVICE_ID, newId).apply()
        return newId
    }
}

