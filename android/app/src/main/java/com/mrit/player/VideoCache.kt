package com.mrit.player

import android.content.Context
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

object VideoCache {

    private const val CACHE_FOLDER = "video_cache"
    private const val CACHE_SIZE_BYTES = 5L * 1024L * 1024L * 1024L // 5GB

    @Volatile
    private var simpleCache: SimpleCache? = null

    fun get(context: Context): SimpleCache {
        return simpleCache ?: synchronized(this) {
            simpleCache ?: run {
                val cacheDir = File(context.cacheDir, CACHE_FOLDER)
                val evictor = LeastRecentlyUsedCacheEvictor(CACHE_SIZE_BYTES)
                val databaseProvider = ExoDatabaseProvider(context)
                SimpleCache(cacheDir, evictor, databaseProvider).also {
                    simpleCache = it
                }
            }
        }
    }
}

