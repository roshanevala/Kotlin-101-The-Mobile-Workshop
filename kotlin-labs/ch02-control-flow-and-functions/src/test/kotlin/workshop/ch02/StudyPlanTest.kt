package workshop.ch02

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Shared acceptance checks for either the learner's working copy or the reference.
class StudyPlanTest {
    @Test
    fun `duration includes both valid endpoints`() {
        assertTrue(isValidDuration(1))
        assertTrue(isValidDuration(120))
    }

    @Test
    fun `duration accepts an interior value`() {
        assertTrue(isValidDuration(30))
    }

    @Test
    fun `duration rejects zero and negatives`() {
        assertFalse(isValidDuration(0))
        assertFalse(isValidDuration(-1))
        assertFalse(isValidDuration(Int.MIN_VALUE))
    }

    @Test
    fun `duration rejects values above its limit`() {
        assertFalse(isValidDuration(121))
        assertFalse(isValidDuration(Int.MAX_VALUE))
    }

    @Test
    fun `quick label includes its lower and upper boundaries`() {
        assertEquals("Quick", sessionLabel(1))
        assertEquals("Quick", sessionLabel(24))
    }

    @Test
    fun `focus label includes its lower and upper boundaries`() {
        assertEquals("Focus", sessionLabel(25))
        assertEquals("Focus", sessionLabel(49))
    }

    @Test
    fun `deep label includes its lower and upper boundaries`() {
        assertEquals("Deep", sessionLabel(50))
        assertEquals("Deep", sessionLabel(120))
    }

    @Test
    fun `label rejects zero and negatives`() {
        assertEquals("Invalid duration", sessionLabel(0))
        assertEquals("Invalid duration", sessionLabel(Int.MIN_VALUE))
    }

    @Test
    fun `label rejects excessive durations`() {
        assertEquals("Invalid duration", sessionLabel(121))
        assertEquals("Invalid duration", sessionLabel(Int.MAX_VALUE))
    }

    @Test
    fun `default rounds produces one quick round`() {
        assertEquals("Round 1: Quick", buildStudyPlan(minutes = 1))
    }

    @Test
    fun `one explicit round has no extra newline`() {
        assertEquals("Round 1: Focus", buildStudyPlan(minutes = 25, rounds = 1))
    }

    @Test
    fun `three rounds have consecutive numbers and separators`() {
        assertEquals(
            "Round 1: Focus\nRound 2: Focus\nRound 3: Focus",
            buildStudyPlan(minutes = 30, rounds = 3),
        )
    }

    @Test
    fun `four rounds includes the final round`() {
        assertEquals(
            "Round 1: Deep\nRound 2: Deep\nRound 3: Deep\nRound 4: Deep",
            buildStudyPlan(minutes = 120, rounds = 4),
        )
    }

    @Test
    fun `plan rejects zero and negative durations`() {
        assertEquals("Invalid duration", buildStudyPlan(minutes = 0))
        assertEquals("Invalid duration", buildStudyPlan(minutes = Int.MIN_VALUE))
    }

    @Test
    fun `plan rejects excessive durations`() {
        assertEquals("Invalid duration", buildStudyPlan(minutes = 121))
        assertEquals("Invalid duration", buildStudyPlan(minutes = Int.MAX_VALUE))
    }

    @Test
    fun `plan rejects zero and negative rounds`() {
        assertEquals("Invalid rounds", buildStudyPlan(minutes = 30, rounds = 0))
        assertEquals("Invalid rounds", buildStudyPlan(minutes = 30, rounds = Int.MIN_VALUE))
    }

    @Test
    fun `plan rejects rounds above the limit`() {
        assertEquals("Invalid rounds", buildStudyPlan(minutes = 30, rounds = 5))
    }

    @Test
    fun `invalid duration takes priority when both inputs are invalid`() {
        assertEquals("Invalid duration", buildStudyPlan(minutes = 0, rounds = 0))
        assertEquals("Invalid duration", buildStudyPlan(minutes = 121, rounds = 5))
    }

    @Test
    fun `calls do not share accumulated text`() {
        val expected = "Round 1: Focus\nRound 2: Focus"
        assertEquals(expected, buildStudyPlan(minutes = 30, rounds = 2))
        assertEquals("Round 1: Quick", buildStudyPlan(minutes = 10))
        assertEquals(expected, buildStudyPlan(minutes = 30, rounds = 2))
    }
}
