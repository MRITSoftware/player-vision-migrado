package com.mrit.player

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

data class ActiveDevice(
    val deviceId: String?,
    val localNome: String?
)

data class DisplayInfo(
    val codigoUnico: String,
    val isLocked: Boolean?,
    val codigoConteudoAtual: String?,
    val deviceId: String?,
    val deviceLastSeen: String?
)

/**
 * Serviço simplificado para validar código de tela e uso exclusivo,
 * inspirado em verificarCodigoSalvo() do player.js.
 */
class SupabaseDeviceService(
    private val httpClient: OkHttpClient = OkHttpClient()
) {

    private val baseUrl = "$SUPABASE_URL/rest/v1"
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun getDisplay(codigo: String): DisplayInfo? {
        if (codigo.isBlank()) return null

        return runCatching {
            val url =
                "$baseUrl/displays?codigo_unico=eq.$codigo&select=codigo_unico,is_locked,codigo_conteudoAtual,device_id,device_last_seen&limit=1"

            val request = Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer $SUPABASE_KEY")
                .header("Accept", "application/json")
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use null
                val body = response.body?.string() ?: return@use null
                val arr = JSONArray(body)
                if (arr.length() == 0) return@use null
                val obj = arr.getJSONObject(0)
                val codigoUnico = optNullableString(obj, "codigo_unico") ?: return@use null
                val isLocked = if (obj.isNull("is_locked")) null else obj.optBoolean("is_locked")
                val codigoConteudoAtual = optNullableString(obj, "codigo_conteudoAtual")
                val deviceId = optNullableString(obj, "device_id")
                val deviceLastSeen = optNullableString(obj, "device_last_seen")
                DisplayInfo(codigoUnico, isLocked, codigoConteudoAtual, deviceId, deviceLastSeen)
            }
        }.getOrNull()
    }

    fun getActiveDeviceForCodigo(codigo: String): ActiveDevice? {
        if (codigo.isBlank()) return null

        return runCatching {
            val url =
                "$baseUrl/dispositivos?codigo_display=eq.$codigo&is_ativo=eq.true&select=device_id,local_nome&limit=1"

            val request = Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer $SUPABASE_KEY")
                .header("Accept", "application/json")
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use null
                val body = response.body?.string() ?: return@use null
                val arr = JSONArray(body)
                if (arr.length() == 0) return@use null
                val obj = arr.getJSONObject(0)
                val devId = optNullableString(obj, "device_id")
                val localNome = optNullableString(obj, "local_nome")
                ActiveDevice(devId, localNome)
            }
        }.getOrNull()
    }

    /**
     * Atualiza o display e o dispositivo quando a tela é ativada,
     * marcando como "Em uso" e registrando device_id / last_seen.
     */
    fun marcarDisplayEmUso(codigo: String, deviceId: String) {
        if (codigo.isBlank()) return
        val now = java.time.Instant.now().toString()

        // Atualizar displays
        runCatching {
            val bodyJson = """
                {
                  "is_locked": true,
                  "status": "Em uso",
                  "device_id": "$deviceId",
                  "device_last_seen": "$now"
                }
            """.trimIndent()
            val body = bodyJson.toRequestBody(jsonMediaType)
            val req = Request.Builder()
                .url("$baseUrl/displays?codigo_unico=eq.$codigo")
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer $SUPABASE_KEY")
                .header("Prefer", "return=minimal")
                .patch(body)
                .build()
            httpClient.newCall(req).execute().use { /* ignore body */ }
        }

        // Upsert em dispositivos (garantir que este device esteja ativo)
        runCatching {
            val bodyJson = """
                {
                  "codigo_display": "$codigo",
                  "device_id": "$deviceId",
                  "is_ativo": true,
                  "last_seen": "$now"
                }
            """.trimIndent()
            val body = bodyJson.toRequestBody(jsonMediaType)
            val req = Request.Builder()
                .url("$baseUrl/dispositivos")
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer $SUPABASE_KEY")
                .header("Prefer", "resolution=merge-duplicates,return=minimal")
                .post(body)
                .build()
            httpClient.newCall(req).execute().use { /* ignore body */ }
        }
    }

    /**
     * Heartbeat periódico: atualiza last_seen e mantém is_ativo / status.
     */
    fun enviarHeartbeat(codigo: String, deviceId: String) {
        if (codigo.isBlank()) return
        val now = java.time.Instant.now().toString()

        runCatching {
            val bodyJson = """
                {
                  "status": "Em uso",
                  "device_id": "$deviceId",
                  "device_last_seen": "$now"
                }
            """.trimIndent()
            val body = bodyJson.toRequestBody(jsonMediaType)
            val req = Request.Builder()
                .url("$baseUrl/displays?codigo_unico=eq.$codigo")
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer $SUPABASE_KEY")
                .header("Prefer", "return=minimal")
                .patch(body)
                .build()
            httpClient.newCall(req).execute().use { }
        }

        runCatching {
            val bodyJson = """
                {
                  "is_ativo": true,
                  "last_seen": "$now"
                }
            """.trimIndent()
            val body = bodyJson.toRequestBody(jsonMediaType)
            val req = Request.Builder()
                .url("$baseUrl/dispositivos?device_id=eq.$deviceId")
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer $SUPABASE_KEY")
                .header("Prefer", "return=minimal")
                .patch(body)
                .build()
            httpClient.newCall(req).execute().use { }
        }
    }

    /**
     * Atualiza status do cache (campo cache em displays).
     */
    fun atualizarStatusCache(codigo: String, cached: Boolean) {
        if (codigo.isBlank()) return

        runCatching {
            val bodyJson = """{"cache": $cached}"""
            val body = bodyJson.toRequestBody(jsonMediaType)
            val req = Request.Builder()
                .url("$baseUrl/displays?codigo_unico=eq.$codigo")
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer $SUPABASE_KEY")
                .header("Prefer", "return=minimal")
                .patch(body)
                .build()
            httpClient.newCall(req).execute().use { }
        }
    }

    private fun optNullableString(obj: JSONObject, key: String): String? {
        if (!obj.has(key) || obj.isNull(key)) return null
        return obj.optString(key, "").ifBlank { null }
    }
}

