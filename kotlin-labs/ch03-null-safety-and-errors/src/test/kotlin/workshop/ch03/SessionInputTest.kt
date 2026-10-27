package workshop.ch03

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// These checks run against either the local working copy or the separate reference.
class SessionInputTest {
    @Test
    fun `missing topic uses the fallback`() {
        assertEquals("Untitled", topicOrDefault(null))
    }

    @Test
    fun `empty and whitespace topics use the fallback`() {
        assertEquals("Untitled", topicOrDefault(""))
        assertEquals("Untitled", topicOrDefault(" \t\n "))
    }

    @Test
    fun `topic trims its edges and preserves case`() {
        assertEquals("KMP", topicOrDefault(" \tKMP\n "))
    }

    @Test
    fun `topic keeps spaces inside the title`() {
        assertEquals("Kotlin  basics", topicOrDefault(" Kotlin  basics "))
    }

    @Test
    fun `the word null is ordinary topic text`() {
        assertEquals("null", topicOrDefault(" null "))
    }

    @Test
    fun `missing empty and whitespace minutes have no parsed value`() {
        assertNull(parseMinutes(null))
        assertNull(parseMinutes(""))
        assertNull(parseMinutes(" \t\n "))
    }

    @Test
    fun `minutes parse after trimming their edges`() {
        assertEquals(30, parseMinutes(" \t30\n "))
    }

    @Test
    fun `parsing preserves zero and negative numbers for validation`() {
        assertEquals(0, parseMinutes("0"))
        assertEquals(-5, parseMinutes("-5"))
    }

    @Test
    fun `an explicit plus sign is accepted`() {
        assertEquals(30, parseMinutes("+30"))
    }

    @Test
    fun `both Int endpoints can be parsed`() {
        assertEquals(Int.MIN_VALUE, parseMinutes("-2147483648"))
        assertEquals(Int.MAX_VALUE, parseMinutes("2147483647"))
    }

    @Test
    fun `values outside Int range have no parsed value`() {
        assertNull(parseMinutes("2147483648"))
        assertNull(parseMinutes("-2147483649"))
    }

    @Test
    fun `letters and units have no parsed value`() {
        assertNull(parseMinutes("banana"))
        assertNull(parseMinutes("30 min"))
    }

    @Test
    fun `fractional and scientific notation have no parsed value`() {
        assertNull(parseMinutes("4.5"))
        assertNull(parseMinutes("1e2"))
    }

    @Test
    fun `spaces and separators inside a number are rejected`() {
        assertNull(parseMinutes("3 0"))
        assertNull(parseMinutes("1_00"))
    }

    @Test
    fun `valid duration endpoints have no error`() {
        assertNull(durationError(1))
        assertNull(durationError(120))
    }

    @Test
    fun `an interior duration has no error`() {
        assertNull(durationError(30))
    }

    @Test
    fun `zero and negative durations have an error`() {
        assertEquals("Minutes must be 1..120", durationError(0))
        assertEquals("Minutes must be 1..120", durationError(Int.MIN_VALUE))
    }

    @Test
    fun `durations above the limit have an error`() {
        assertEquals("Minutes must be 1..120", durationError(121))
        assertEquals("Minutes must be 1..120", durationError(Int.MAX_VALUE))
    }

    @Test
    fun `a valid request uses both cleaned inputs`() {
        assertEquals("Study KMP for 45 min", describeSession(" KMP ", " 45 "))
    }

    @Test
    fun `a missing or blank topic uses the fallback in the description`() {
        assertEquals("Study Untitled for 30 min", describeSession(null, "30"))
        assertEquals("Study Untitled for 30 min", describeSession(" \t ", "30"))
    }

    @Test
    fun `missing minutes ask for a number`() {
        assertEquals("Enter whole minutes", describeSession("Kotlin", null))
        assertEquals("Enter whole minutes", describeSession(null, " \t "))
    }

    @Test
    fun `malformed minutes ask for a number`() {
        assertEquals("Enter whole minutes", describeSession("Kotlin", "banana"))
        assertEquals("Enter whole minutes", describeSession("Kotlin", "2.5"))
    }

    @Test
    fun `overflow asks for a number instead of inventing a duration`() {
        assertEquals("Enter whole minutes", describeSession("Kotlin", "2147483648"))
    }

    @Test
    fun `parsed but disallowed durations explain the range`() {
        assertEquals("Minutes must be 1..120", describeSession("Kotlin", "0"))
        assertEquals("Minutes must be 1..120", describeSession("Kotlin", "-1"))
        assertEquals("Minutes must be 1..120", describeSession("Kotlin", "121"))
        assertEquals("Minutes must be 1..120", describeSession("Kotlin", "2147483647"))
    }

    @Test
    fun `valid duration endpoints produce descriptions`() {
        assertEquals("Study Kotlin for 1 min", describeSession("Kotlin", "1"))
        assertEquals("Study Kotlin for 120 min", describeSession("Kotlin", "120"))
    }

    @Test
    fun `calls do not reuse a previous topic or duration`() {
        assertEquals("Study KMP for 45 min", describeSession("KMP", "45"))
        assertEquals("Enter whole minutes", describeSession(null, "banana"))
        assertEquals("Study Untitled for 1 min", describeSession(null, "1"))
        assertEquals("Study KMP for 45 min", describeSession("KMP", "45"))
    }
}
