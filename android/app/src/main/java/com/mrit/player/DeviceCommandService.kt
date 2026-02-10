package com.mrit.player

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray

data class DeviceCommand(
    val id: String,
    val command: String
)

/**
 * Lê e marca comandos remotos do dispositivo (tabela device_commands),
 * espelhando a lógica de verificarComandosDispositivo() do player.js.
 */
class DeviceCommandService(
    private val httpClient: OkHttpClient = OkHttpClient()
) {

    private val baseUrl = "$SUPABASE_URL/rest/v1"
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun getPendingCommands(deviceId: String): List<DeviceCommand> {
        if (deviceId.isBlank()) return emptyList()

        val url =
            "$baseUrl/device_commands?device_id=eq.$deviceId&executed=eq.false&select=id,command&order=created_at.asc&limit=10"

        val request = Request.Builder()
            .url(url)
            .header("apikey", SUPABASE_KEY)
            .header("Authorization", "Bearer $SUPABASE_KEY")
            .header("Accept", "application/json")
            .get()
            .build()

        httpClient.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) return emptyList()
            val body = resp.body?.string() ?: return emptyList()
            val arr = JSONArray(body)
            val result = mutableListOf<DeviceCommand>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val id = obj.opt("id")?.toString() ?: continue
                val cmd = obj.optString("command", "")
                if (cmd.isNotBlank()) {
                    result.add(DeviceCommand(id = id, command = cmd))
                }
            }
            return result
        }
    }

    fun markExecuted(id: String) {
        if (id.isBlank()) return
        val now = java.time.Instant.now().toString()
        val bodyJson = """{"executed": true, "executed_at": "$now"}"""
        val body = bodyJson.toRequestBody(jsonMediaType)

        val req = Request.Builder()
            .url("$baseUrl/device_commands?id=eq.$id")
            .header("apikey", SUPABASE_KEY)
            .header("Authorization", "Bearer $SUPABASE_KEY")
            .header("Prefer", "return=minimal")
            .patch(body)
            .build()

        runCatching {
            httpClient.newCall(req).execute().use { }
        }
    }
}

