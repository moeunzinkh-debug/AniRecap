package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** What one cache-cleanup pass actually did — reported verbatim in the UI. */
data class CacheCleanupResult(
    val bytesFreed: Long,
    val filesDeleted: Int,
    val entriesFailed: Int
) {
    val success: Boolean get() = entriesFailed == 0
}

/** Real on-disk cache usage, split so the UI can show where it lives. */
data class CacheBreakdown(
    val internalCacheBytes: Long = 0L,
    val codeCacheBytes: Long = 0L,
    val externalCacheBytes: Long = 0L,
    val fileCount: Int = 0
) {
    val totalBytes: Long get() = internalCacheBytes + codeCacheBytes + externalCacheBytes
    val isEmpty: Boolean get() = totalBytes <= 0L
}

/**
 * Measures and clears cache the way a real app should:
 *  - only the *contents* of each cache directory are removed and the directories
 *    themselves stay in place (deleting `cacheDir` breaks image loaders and the
 *    upload temp files written by [RecapGenerationService]),
 *  - every disk read/write runs on the IO dispatcher, never on the UI thread,
 *  - the caller gets the amount actually freed instead of a blind "success".
 */
object CacheStats {

    private fun cacheDirs(context: Context): List<File> =
        listOfNotNull(context.cacheDir, context.codeCacheDir, context.externalCacheDir)

    private fun File.sizeBytes(): Long =
        runCatching {
            if (isFile) length() else listFiles()?.sumOf { it.sizeBytes() } ?: 0L
        }.getOrDefault(0L)

    private fun File.fileCount(): Int =
        runCatching {
            if (isFile) 1 else listFiles()?.sumOf { it.fileCount() } ?: 0
        }.getOrDefault(0)

    suspend fun measure(context: Context): CacheBreakdown = withContext(Dispatchers.IO) {
        val internal = context.cacheDir
        val code = context.codeCacheDir
        val external = context.externalCacheDir
        val dirs = listOfNotNull(internal, code, external)

        CacheBreakdown(
            internalCacheBytes = internal.sizeBytes(),
            codeCacheBytes = code.sizeBytes(),
            externalCacheBytes = external?.sizeBytes() ?: 0L,
            fileCount = dirs.sumOf { it.fileCount() }
        )
    }

    suspend fun clear(context: Context): CacheCleanupResult = withContext(Dispatchers.IO) {
        var freed = 0L
        var deleted = 0
        var failed = 0

        cacheDirs(context).forEach { dir ->
            val children = dir.listFiles() ?: return@forEach
            children.forEach { child ->
                val size = child.sizeBytes()
                val count = child.fileCount()
                val removed = runCatching { child.deleteRecursively() }.getOrDefault(false)
                if (removed) {
                    freed += size
                    deleted += count
                } else {
                    failed += 1
                }
            }
        }

        CacheCleanupResult(bytesFreed = freed, filesDeleted = deleted, entriesFailed = failed)
    }
}

/** "1.2 MB" / "48 KB" / "0 B" — shared by the settings screens. */
fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 B"
    val kb = 1024.0
    if (bytes < kb.toLong()) return "$bytes B"
    val mb = kb * kb
    if (bytes < mb.toLong()) return "%.1f KB".format(bytes / kb)
    val gb = mb * kb
    if (bytes < gb.toLong()) return "%.1f MB".format(bytes / mb)
    return "%.2f GB".format(bytes / gb)
}
