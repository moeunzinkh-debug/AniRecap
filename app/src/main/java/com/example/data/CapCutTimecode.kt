package com.example.data

/**
 * CapCut marker ranges arrive from two places: our local duration-based
 * generator ("00:00 - 00:15") and the model's JSON, which may use an en-dash
 * or an HH:MM:SS form ("0:01:30 – 0:02:10"). These helpers read both without
 * assuming an exact shape, so the monitor can seek to a cut's in-point.
 */
object CapCutTimecode {

    private val TimestampPattern = Regex("""(\d{1,2}):(\d{2})(?::(\d{2}))?""")

    /** Seconds of the range's in-point, or null when nothing parses. */
    fun startSeconds(timeRange: String): Int? =
        TimestampPattern.find(timeRange)?.toSeconds()

    /** Seconds of the range's out-point (looks after the dash). */
    fun endSeconds(timeRange: String): Int? {
        val parts = timeRange.split('-', '\u2013', '\u2014')
        if (parts.size < 2) return null
        return TimestampPattern.find(parts[1])?.toSeconds()
    }

    private fun MatchResult.toSeconds(): Int? {
        val first = groupValues.getOrNull(1)?.toIntOrNull() ?: return null
        val second = groupValues.getOrNull(2)?.toIntOrNull() ?: return null
        val third = groupValues.getOrNull(3)?.toIntOrNull()
        // Three groups means HH:MM:SS, two means MM:SS.
        return if (third != null) first * 3600 + second * 60 + third else first * 60 + second
    }
}
