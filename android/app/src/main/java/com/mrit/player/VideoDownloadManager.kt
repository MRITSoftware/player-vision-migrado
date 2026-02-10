package com.mrit.player

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
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

    fun preCachePlaylist(playlist: List<PlaylistItem>) {
        if (playlist.isEmpty()) return

        val cache = VideoCache.get(context)
        val upstream = DefaultHttpDataSource.Factory()
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstream)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        playlist.filter { it.type == ItemType.VIDEO }.forEach { item ->
            scope.launch {
                try {
                    val dataSpec = com.google.android.exoplayer2.upstream.DataSpec(
                        android.net.Uri.parse(item.url)
                    )

                    // CacheWriter vai ler do upstream e gravar no cache.
                    val writer = CacheWriter(
                        cacheDataSourceFactory.createDataSource(),
                        dataSpec,
                        null
                    ) { _, _, _ -> /* progresso opcional */ }

                    Log.d("VideoDownloadManager", "📥 Pré-cache de vídeo: ${item.url}")
                    writer.cache()
                    Log.d("VideoDownloadManager", "✅ Vídeo pré-cachado: ${item.url}")

                } catch (e: Exception) {
                    Log.e("VideoDownloadManager", "❌ Erro ao pré-cachar vídeo ${item.url}", e)
                }
            }
        }
    }
}

