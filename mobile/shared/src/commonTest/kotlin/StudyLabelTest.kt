package workshop.studylab.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StudyLabelTest {
    @Test
    fun trimsTheTopicAndIncludesMinutes() {
        assertEquals("Kotlin · 25 min", studyLabel("  Kotlin ", 25))
    }

    @Test
    fun aBlankTopicAndZeroMinutesStillHaveALabel() {
        assertEquals("Study · 0 min", studyLabel("  ", 0))
    }

    @Test
    fun negativeMinutesAreRejected() {
        assertFailsWith<IllegalArgumentException> { studyLabel("Swift", -1) }
    }
}
