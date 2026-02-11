package com.mrit.player

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Serviço responsável por ler e atualizar promoções no Supabase,
 * espelhando a lógica de verificarPromocao* do player.js.
 */
class PromotionService(
    private val httpClient: OkHttpClient = OkHttpClient()
) {

    private val baseUrl = "$SUPABASE_URL/rest/v1"

    fun getPromotionForCodigo(codigo: String): PromotionData? {
        if (codigo.isBlank()) return null

        // Buscar promo / id_promo no display
        val displayReq = Request.Builder()
            .url("$baseUrl/displays?codigo_unico=eq.$codigo&select=promo,id_promo&single")
            .header("apikey", SUPABASE_KEY)
            .header("Authorization", "Bearer $SUPABASE_KEY")
            .header("Accept", "application/json")
            .get()
            .build()

        val display = httpClient.newCall(displayReq).execute().use { resp ->
            if (!resp.isSuccessful) return null
            val body = resp.body?.string() ?: return null
            parseFirstObject(body) ?: return null
        }

        val promoAtiva = display.optBoolean("promo", false)
        val idPromo = display.opt("id_promo")?.toString()
        if (!promoAtiva || idPromo.isNullOrBlank()) return null

        return getPromotionById(idPromo)
    }

    fun getPromotionById(idPromo: String): PromotionData? {
        if (idPromo.isBlank()) return null

        val promoReq = Request.Builder()
            .url("$baseUrl/promo?id_promo=eq.$idPromo&select=*&single")
            .header("apikey", SUPABASE_KEY)
            .header("Authorization", "Bearer $SUPABASE_KEY")
            .header("Accept", "application/json")
            .get()
            .build()

        return httpClient.newCall(promoReq).execute().use { resp ->
            if (!resp.isSuccessful) return@use null
            val body = resp.body?.string() ?: return@use null
            val obj = parseFirstObject(body) ?: return@use null

            PromotionData(
                idPromo = obj.opt("id_promo")?.toString() ?: idPromo,
                imagemUrl = obj.optString("imagem_promo", null),
                texto = obj.optString("texto_promo", null),
                valorAntes = obj.opt("valor_antes")?.toString(),
                valorPromo = obj.opt("valor_promo")?.toString(),
                contador = obj.optInt("contador", 0)
            )
        }
    }

    /**
     * Ler apenas contador / textos atuais (similar a verificarContadorNoBanco).
     */
    fun getPromotionCounterAndTexts(idPromo: String): PromotionData? {
        if (idPromo.isBlank()) return null

        val promoReq = Request.Builder()
            .url("$baseUrl/promo?id_promo=eq.$idPromo&select=contador,texto_promo,valor_antes,valor_promo&single")
            .header("apikey", SUPABASE_KEY)
            .header("Authorization", "Bearer $SUPABASE_KEY")
            .header("Accept", "application/json")
            .get()
            .build()

        return httpClient.newCall(promoReq).execute().use { resp ->
            if (!resp.isSuccessful) return@use null
            val body = resp.body?.string() ?: return@use null
            val obj = parseFirstObject(body) ?: return@use null

            PromotionData(
                idPromo = idPromo,
                imagemUrl = null,
                texto = obj.optString("texto_promo", null),
                valorAntes = obj.opt("valor_antes")?.toString(),
                valorPromo = obj.opt("valor_promo")?.toString(),
                contador = obj.optInt("contador", 0)
            )
        }
    }

    /**
     * Desativar promoção: displays.promo=false, displays.id_promo=null e deletar linha em promo.
     */
    fun desativarPromocao(codigo: String, idPromo: String?) {
        if (codigo.isBlank()) return

        // Update displays
        runCatching {
            val body = """{"promo": false, "id_promo": null}"""
            val req = Request.Builder()
                .url("$baseUrl/displays?codigo_unico=eq.$codigo")
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer $SUPABASE_KEY")
                .header("Prefer", "return=minimal")
                .patch(body.toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()
            httpClient.newCall(req).execute().use { }
        }

        // Delete promo row
        if (!idPromo.isNullOrBlank()) {
            runCatching {
                val req = Request.Builder()
                    .url("$baseUrl/promo?id_promo=eq.$idPromo")
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer $SUPABASE_KEY")
                    .header("Prefer", "return=minimal")
                    .delete()
                    .build()
                httpClient.newCall(req).execute().use { }
            }
        }
    }

    /**
     * O PostgREST pode retornar objeto ({}) ou lista ([{}]) dependendo do endpoint/opções.
     * Aqui aceitamos ambos os formatos e sempre extraímos o primeiro objeto válido.
     */
    private fun parseFirstObject(body: String): JSONObject? {
        return when (val parsed = JSONTokener(body).nextValue()) {
            is JSONObject -> parsed
            is JSONArray -> {
                if (parsed.length() == 0) null else parsed.optJSONObject(0)
            }
            else -> null
        }
    }
}

