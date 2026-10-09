package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CacheStats
import com.example.data.formatBytes
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Pins the behaviour the old AppSettings.clearCache() got wrong: measure the
 * real bytes, delete only the *contents*, and never remove the cache directory
 * itself (the upload temp file is written into it by RecapGenerationService).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CacheStatsTest {

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun measureCountsRealFilesInCacheDir() = runBlocking<Unit> {
        val cache = context.cacheDir
        cache.mkdirs()
        File(cache, "alpha.bin").writeBytes(ByteArray(1024))
        val nested = File(cache, "nested").apply { mkdirs() }
        File(nested, "beta.bin").writeBytes(ByteArray(2048))

        val breakdown = CacheStats.measure(context)

        assertTrue(
            "expected the bytes just written to be measured, got ${breakdown.totalBytes}",
            breakdown.totalBytes >= 3072L
        )
        assertTrue("expected at least 2 files counted, got ${breakdown.fileCount}", breakdown.fileCount >= 2)
        assertFalse("a populated cache must not report empty", breakdown.isEmpty)

        File(cache, "alpha.bin").delete()
        File(nested, "beta.bin").delete()
        nested.delete()
    }

    @Test
    fun clearRemovesContentsButKeepsTheDirectory() = runBlocking<Unit> {
        val cache = context.cacheDir
        cache.mkdirs()
        val tempUpload = File(cache, "anirecap_upload_solo_leveling.mp4")
        tempUpload.writeBytes(ByteArray(4096))

        val result = CacheStats.clear(context)

        assertTrue("clear should not report failures, got ${result.entriesFailed}", result.success)
        assertTrue("should report the bytes actually freed, got ${result.bytesFreed}", result.bytesFreed >= 4096L)
        assertTrue(result.filesDeleted >= 1)
        assertFalse("the upload temp file must be gone", tempUpload.exists())
        assertTrue("cacheDir has to survive a clear", cache.exists() && cache.isDirectory)
        assertTrue(CacheStats.measure(context).isEmpty)
    }

    @Test
    fun clearingAnEmptyCacheReportsNothingRemoved() = runBlocking<Unit> {
        context.cacheDir.mkdirs()
        File(context.cacheDir, "leftover").let { leftover ->
            leftover.writeBytes(ByteArray(1))
            leftover.delete()
        }

        val first = CacheStats.clear(context)
        val second = CacheStats.clear(context)

        assertTrue("a second pass has nothing left to delete", second.filesDeleted == 0)
        assertTrue(second.success)
        assertEquals(0L, second.bytesFreed)
        assertTrue(first.entriesFailed == 0)
    }

    @Test
    fun formatBytesIsHumanReadable() {
        assertEquals("0 B", formatBytes(0L))
        assertEquals("0 B", formatBytes(-1L))
        assertEquals("512 B", formatBytes(512L))
        assertEquals("1.0 KB", formatBytes(1024L))
        assertEquals("1.5 MB", formatBytes((1.5 * 1024 * 1024).toLong()))
    }
}
