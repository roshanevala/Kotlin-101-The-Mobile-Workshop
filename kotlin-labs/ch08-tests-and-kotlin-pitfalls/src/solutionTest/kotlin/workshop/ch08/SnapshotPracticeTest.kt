package workshop.ch08

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

// Worked regression examples, separate from the shared acceptance suite.
class SnapshotPracticeTest {
    @Test
    fun `equivalent session values need not be the same instance`() {
        val original = StudySession(7, "Kotlin", 30)
        val separate = original.copy()
        assertNotSame(original, separate)
        val actual = sameSessionValue(original, separate)
        assertTrue(actual)
    }

    @Test
    fun `editing a copied plan leaves the original session list unchanged`() {
        val session = StudySession(7, "Kotlin", 30)
        val original = StudyPlan("Week 1", mutableListOf(session))
        val edited = independentPlan(original)
        edited.sessions.clear()
        assertEquals(listOf(session), original.sessions)
    }

    @Test
    fun `history captures a plan before the caller edits its input`() {
        val session = StudySession(7, "Kotlin", 30)
        val original = StudyPlan("Week 1", mutableListOf(session))
        val history = PlanHistory()
        history.save(original)
        original.sessions.clear()
        assertEquals(listOf(session), history.snapshots().single().sessions)
    }

    @Test
    fun `editing a returned history plan leaves the next read unchanged`() {
        val session = StudySession(7, "Kotlin", 30)
        val history = PlanHistory()
        history.save(StudyPlan("Week 1", mutableListOf(session)))
        history.snapshots().single().sessions.clear()
        assertEquals(listOf(session), history.snapshots().single().sessions)
    }

    @Test
    fun `planned sessions contribute no completed minutes`() {
        val sessions = listOf(StudySession(7, "Kotlin", 30), StudySession(2, "KMP", 120))
        assertEquals(0L, completedMinutes(sessions))
    }

    @Test
    fun `completed total includes both valid duration endpoints`() {
        val sessions = listOf(StudySession(7, "Kotlin", 1, true), StudySession(2, "KMP", 120, true))
        assertEquals(121L, completedMinutes(sessions))
    }
}
