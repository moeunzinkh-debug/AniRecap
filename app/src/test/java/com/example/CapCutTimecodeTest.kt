package com.example

import com.example.data.CapCutTimecode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The monitor's "tap a cut to jump" feature depends on these parses, and the
 * ranges come from two different producers (local generator + model JSON).
 */
class CapCutTimecodeTest {

    @Test
    fun parsesPlainRange() {
        assertEquals(0, CapCutTimecode.startSeconds("00:00 - 00:15"))
        assertEquals(15, CapCutTimecode.endSeconds("00:00 - 00:15"))
    }

    @Test
    fun parsesEnDashRange() {
        assertEquals(75, CapCutTimecode.startSeconds("01:15 – 02:00"))
        assertEquals(120, CapCutTimecode.endSeconds("01:15 – 02:00"))
    }

    @Test
    fun parsesHourMinuteSecondRange() {
        assertEquals(3750, CapCutTimecode.startSeconds("1:02:30 - 1:05:00"))
        assertEquals(3900, CapCutTimecode.endSeconds("1:02:30 - 1:05:00"))
    }

    @Test
    fun acceptsShortMinuteForm() {
        assertEquals(125, CapCutTimecode.startSeconds("2:05 - 3:10"))
    }

    @Test
    fun returnsNullWhenNothingIsParseable() {
        assertNull(CapCutTimecode.startSeconds("no timecode here"))
        assertNull(CapCutTimecode.endSeconds("00:00 with only one side"))
    }
}
