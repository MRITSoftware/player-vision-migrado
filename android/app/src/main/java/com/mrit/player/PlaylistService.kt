package com.mrit.player

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder

data class ApiPlaylistItem(
    val url: String?,
    val tipo: String?,
    val duration: Long?
)

class PlaylistService(
    private val httpClient: OkHttpClient = OkHttpClient()
) {

    /**
     * Carrega a playlist do Supabase usando a mesma lógica básica do player.js:
     * - Tabela playlists filtrando por codigo_unico
     * - Tabela playlist_itens filtrando por playlist_id = codigoConteudo
     *
     * Aqui usamos a REST API do Supabase diretamente.
     */
    fun carregarPlaylist(codigoConteudo: String): List<PlaylistItem> {
        if (codigoConteudo.isBlank()) return emptyList()

        val baseUrl = "https://base.muraltv.com.br/rest/v1"
        val encodedCodigo = URLEncoder.encode(codigoConteudo, "UTF-8")

        // 1) Tentar conteúdo único (igual ao fluxo do player.js em carregarConteudo)
        val conteudoUrl = "$baseUrl/conteudos?codigoAnuncio=eq.$encodedCodigo&select=*&limit=1"
        requestArray(conteudoUrl)?.let { arr ->
            if (arr.length() > 0) {
                val obj = arr.getJSONObject(0)
                val urlItem = obj.optString("url", null)
                if (!urlItem.isNullOrBlank()) {
                    val tipo = obj.optString("tipo", "Vídeo")
                    val isImage = tipo.lowercase().contains("imagem") ||
                        urlItem.matches(Regex(".*\\.(jpg|jpeg|png|webp|gif)(\\?.*)?$", RegexOption.IGNORE_CASE))
                    return listOf(
                        PlaylistItem(
                            url = urlItem,
                            type = if (isImage) ItemType.IMAGE else ItemType.VIDEO,
                            durationMs = if (isImage) 0L else null, // imagem única fica estática
                            fit = obj.optString("fit", null),
                            focus = obj.optString("focus", null),
                            urlPortrait = obj.optString("urlPortrait", null),
                            urlLandscape = obj.optString("urlLandscape", null)
                        )
                    )
                }
            }
        }

        // 2) Fallback para playlist_itens
        val playlistUrl = "$baseUrl/playlist_itens?playlist_id=eq.$encodedCodigo&order=ordem.asc"
        val json = requestArray(playlistUrl) ?: return emptyList()
        val result = mutableListOf<PlaylistItem>()

        for (i in 0 until json.length()) {
            val obj = json.getJSONObject(i)
            val urlItem = obj.optString("url", null)
            if (urlItem.isNullOrBlank()) continue

            val tipo = obj.optString("tipo", "Vídeo")
            val isVideo = tipo.lowercase().contains("video") ||
                urlItem.matches(Regex(".*\\.(mp4|webm|mkv|mov|avi|m4v|3gp|flv|wmv)(\\?.*)?$", RegexOption.IGNORE_CASE))

            val duration = if (tipo.lowercase() == "imagem") 15000L else null

            val fit = if (obj.has("fit") && !obj.isNull("fit")) obj.optString("fit", null) else null
            val focus = if (obj.has("focus") && !obj.isNull("focus")) obj.optString("focus", null) else null
            val urlPortrait = if (obj.has("urlPortrait") && !obj.isNull("urlPortrait")) obj.optString("urlPortrait", null) else null
            val urlLandscape = if (obj.has("urlLandscape") && !obj.isNull("urlLandscape")) obj.optString("urlLandscape", null) else null

            result.add(
                PlaylistItem(
                    url = urlItem,
                    type = if (isVideo) ItemType.VIDEO else ItemType.IMAGE,
                    durationMs = duration,
                    fit = fit,
                    focus = focus,
                    urlPortrait = urlPortrait,
                    urlLandscape = urlLandscape
                )
            )
        }
        return result
    }

    private fun requestArray(url: String): JSONArray? {
        val request = Request.Builder()
            .url(url)
            .header("apikey", SUPABASE_KEY)
            .header("Authorization", "Bearer $SUPABASE_KEY")
            .header("Accept", "application/json")
            .get()
            .build()

        return httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@use null
            val body = response.body?.string() ?: "[]"
            JSONArray(body)
        }
    }
}

