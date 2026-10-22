package workshop.ch01

import kotlin.test.Test
import kotlin.test.assertEquals

// These same acceptance examples run against either the starter or the solution.
class TimerTest {
    @Test
    fun `zero minutes is an empty duration`() {
        assertEquals("0 h 0 min", formatStudyTime(0))
    }

    @Test
    fun `one minute is preserved`() {
        assertEquals("0 h 1 min", formatStudyTime(1))
    }

    @Test
    fun `just under one hour is preserved`() {
        assertEquals("0 h 59 min", formatStudyTime(59))
    }

    @Test
    fun `exactly one hour has no remaining minutes`() {
        assertEquals("1 h 0 min", formatStudyTime(60))
    }

    @Test
    fun `the reported 95 minute bug is fixed`() {
        assertEquals("1 h 35 min", formatStudyTime(95))
    }

    @Test
    fun `multiple hours preserve their remainder`() {
        assertEquals("2 h 5 min", formatStudyTime(125))
    }

    @Test
    fun `the largest nonnegative Int is supported`() {
        assertEquals("35791394 h 7 min", formatStudyTime(Int.MAX_VALUE))
    }
}
