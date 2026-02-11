package com.mrit.player

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import okhttp3.OkHttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Faz o pré-cache dos vídeos da playlist, semelhante ao forcarCacheDireto do player.js.
 * Usa o mesmo SimpleCache que o Player (VideoCache) para que o ExoPlayer reaproveite.
 */
class VideoDownloadManager(
    private val context: Context
) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val httpClient = OkHttpClient()

    fun preCachePlaylist(playlist: List<PlaylistItem>) {
        if (playlist.isEmpty()) return

        val cache = VideoCache.get(context)
        val upstream = DefaultHttpDataSource.Factory()
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstream)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val urls = linkedSetOf<String>()
        playlist.forEach { item ->
            listOf(item.url, item.urlPortrait, item.urlLandscape)
                .filterNotNull()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .forEach { urls.add(it) }
        }

        urls.forEach { mediaUrl ->
            scope.launch {
                try {
                    if (isFileMediaUrl(mediaUrl)) {
                        ImageFileCache.cacheMedia(context, httpClient, mediaUrl)
                        Log.d("VideoDownloadManager", "✅ Mídia pré-cachada em arquivo local: $mediaUrl")
                        return@launch
                    }

                    val dataSpec = com.google.android.exoplayer2.upstream.DataSpec(
                        android.net.Uri.parse(mediaUrl)
                    )

                    // CacheWriter vai ler do upstream e gravar no cache.
                    val writer = CacheWriter(
                        cacheDataSourceFactory.createDataSource(),
                        dataSpec,
                        null
                    ) { _, _, _ -> /* progresso opcional */ }

                    Log.d("VideoDownloadManager", "📥 Pré-cache de mídia: $mediaUrl")
                    writer.cache()
                    Log.d("VideoDownloadManager", "✅ Mídia pré-cachada: $mediaUrl")

                } catch (e: Exception) {
                    Log.e("VideoDownloadManager", "❌ Erro ao pré-cachar mídia $mediaUrl", e)
                }
            }
        }
    }

    private fun isFileMediaUrl(url: String): Boolean {
        return url.matches(
            Regex(
                ".*\\.(jpg|jpeg|png|webp|gif|mp4|webm|mkv|mov|avi|m4v|3gp|flv|wmv)(\\?.*)?$",
                RegexOption.IGNORE_CASE
            )
        )
    }
}

