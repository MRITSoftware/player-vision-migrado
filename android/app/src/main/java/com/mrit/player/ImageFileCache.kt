package com.mrit.player

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest

object ImageFileCache {
    private const val CACHE_DIR = "media_file_cache"

    fun getCachedFile(context: Context, url: String): File? {
        val file = buildFile(context, url)
        return if (file.exists() && file.length() > 0L) file else null
    }

    fun cacheImage(context: Context, httpClient: OkHttpClient, url: String) {
        cacheMedia(context, httpClient, url)
    }

    fun cacheMedia(context: Context, httpClient: OkHttpClient, url: String) {
        val target = buildFile(context, url)
        val tmp = File(target.parentFile, "${target.name}.tmp")

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        httpClient.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) return
            val bytes = resp.body?.bytes() ?: return
            target.parentFile?.mkdirs()
            tmp.writeBytes(bytes)
            if (!tmp.renameTo(target)) {
                tmp.delete()
            }
        }
    }

    private fun buildFile(context: Context, url: String): File {
        val folder = File(context.cacheDir, CACHE_DIR)
        val extension = extensionFor(url)
        return File(folder, "${sha256(url)}.$extension")
    }

    private fun extensionFor(url: String): String {
        val clean = url.substringBefore('?').substringBefore('#')
        val ext = clean.substringAfterLast('.', "").lowercase()
        return when (ext) {
            "jpg", "jpeg", "png", "webp", "gif",
            "mp4", "webm", "mkv", "mov", "avi", "m4v", "3gp", "flv", "wmv" -> ext
            else -> "img"
        }
    }

    private fun sha256(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
