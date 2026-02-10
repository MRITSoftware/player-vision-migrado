package com.mrit.player

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

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

        // Endpoint REST do Supabase para playlist_itens
        // OBS: estamos assumindo a mesma base usada no player.js
        val baseUrl = "https://base.muraltv.com.br/rest/v1"

        // Busca itens da playlist pela coluna playlist_id (igual ao codigoConteudo)
        val url =
            "$baseUrl/playlist_itens?playlist_id=eq.$codigoConteudo&order=ordem.asc"

        val request = Request.Builder()
            .url(url)
            .header("apikey", SUPABASE_KEY)
            .header("Authorization", "Bearer $SUPABASE_KEY")
            .header("Accept", "application/json")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return emptyList()
            }

            val body = response.body?.string() ?: "[]"
            val json = JSONArray(body)
            val result = mutableListOf<PlaylistItem>()

            for (i in 0 until json.length()) {
                val obj = json.getJSONObject(i)
                val urlItem = obj.optString("url", null)
                if (urlItem.isNullOrBlank()) continue

                val tipo = obj.optString("tipo", "Vídeo")
                val isVideo = tipo.lowercase().contains("video") ||
                    urlItem.matches(Regex(".*\\.(mp4|webm|mkv|mov|avi|m4v|3gp|flv|wmv)(\\?.*)?$", RegexOption.IGNORE_CASE))

                val duration =
                    if (tipo.lowercase() == "imagem") 15000L else null

                // Campos opcionais usados no player.js
                val fit = if (obj.has("fit") && !obj.isNull("fit")) {
                    obj.optString("fit", null)
                } else null
                val focus = if (obj.has("focus") && !obj.isNull("focus")) {
                    obj.optString("focus", null)
                } else null
                val urlPortrait = if (obj.has("urlPortrait") && !obj.isNull("urlPortrait")) {
                    obj.optString("urlPortrait", null)
                } else null
                val urlLandscape = if (obj.has("urlLandscape") && !obj.isNull("urlLandscape")) {
                    obj.optString("urlLandscape", null)
                } else null

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
    }
}

