package workshop.studylab.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionPlannerTest {
    private class RecordingIds : SessionIdSource {
        var calls = 0
            private set

        override fun nextId(): String {
            calls++
            return "fixture-$calls"
        }
    }

    @Test
    fun acceptsBothDurationLimitsAndUsesOneSuppliedIdPerSession() {
        val ids = RecordingIds()
        val planner = SessionPlanner(ids)

        assertEquals(PlannedSession("fixture-1", "Kotlin", 1), planner.plan("Kotlin", 1))
        assertEquals(PlannedSession("fixture-2", "Kotlin", 120), planner.plan("Kotlin", 120))
        assertEquals(2, ids.calls)
    }

    @Test
    fun rejectsOutOfRangeMinutesBeforeRequestingAnId() {
        val ids = RecordingIds()
        val planner = SessionPlanner(ids)

        for (minutes in listOf(0, 121, -1, Int.MIN_VALUE, Int.MAX_VALUE)) {
            assertNull(planner.plan("Kotlin", minutes), "Reject $minutes minutes")
        }
        assertEquals(0, ids.calls, "Rejected sessions must leave the ID source untouched")
    }

    @Test
    fun trimsTopicEdgesAndKeepsInteriorSpaces() {
        val planner = SessionPlanner(RecordingIds())
        val session = assertNotNull(planner.plan("\t  Kotlin  flows \n", 25))

        assertEquals("Kotlin  flows", session.topic)
    }

    @Test
    fun usesUntitledForMissingOrBlankTopics() {
        val planner = SessionPlanner(RecordingIds())

        for (topic in listOf(null, "", " \t\n")) {
            val session = assertNotNull(planner.plan(topic, 25))
            assertEquals("Untitled", session.topic, "Fallback for topic '$topic'")
        }
    }

    @Test
    fun platformSourceSuppliesAUuidToAnAcceptedSession() {
        val planner = SessionPlanner(platformSessionIds())
        val session = assertNotNull(planner.plan("Kotlin", 25))
        val uuidShape = Regex(
            "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}",
            RegexOption.IGNORE_CASE
        )

        assertTrue(uuidShape.matches(session.id), "Expected a UUID from the platform source, got '${session.id}'")
    }
}
