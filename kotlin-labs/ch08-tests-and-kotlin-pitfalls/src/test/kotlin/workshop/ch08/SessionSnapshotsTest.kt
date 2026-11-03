package workshop.ch08

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Fixed behavioral contracts shared by working and reference implementations.
class SessionSnapshotsTest {
    private val kotlin = StudySession(7, "Kotlin", 30, completed = true)
    private val kmp = StudySession(2, "KMP", 45)

    @Test fun `a value is equivalent to itself`() {
        assertTrue(sameSessionValue(kotlin, kotlin))
    }

    @Test fun `separate equal session instances have the same value`() {
        assertTrue(sameSessionValue(kotlin, kotlin.copy()))
    }

    @Test fun `different IDs are different values`() {
        assertFalse(sameSessionValue(kotlin, kotlin.copy(id = 2)))
    }

    @Test fun `different topics are different values`() {
        assertFalse(sameSessionValue(kotlin, kotlin.copy(topic = "kotlin")))
    }

    @Test fun `different minutes are different values`() {
        assertFalse(sameSessionValue(kotlin, kotlin.copy(minutes = 31)))
    }

    @Test fun `different completion states are different values`() {
        assertFalse(sameSessionValue(kotlin, kotlin.copy(completed = false)))
    }

    @Test fun `independent copy preserves title order and repeated entries`() {
        val original = StudyPlan(" Week 1 ", mutableListOf(kmp, kotlin, kmp))
        val copy = independentPlan(original)
        assertEquals(original, copy)
        assertEquals(" Week 1 ", copy.title)
        assertEquals(listOf(kmp, kotlin, kmp), original.sessions)
    }

    @Test fun `editing copy membership leaves original unchanged`() {
        val original = StudyPlan("Week 1", mutableListOf(kotlin, kmp))
        val copy = independentPlan(original)
        copy.sessions.removeAt(0)
        assertEquals(listOf(kotlin, kmp), original.sessions)
        assertEquals(listOf(kmp), copy.sessions)
    }

    @Test fun `editing original membership leaves copy unchanged`() {
        val original = StudyPlan("Week 1", mutableListOf(kotlin))
        val copy = independentPlan(original)
        original.sessions.add(kmp)
        assertEquals(listOf(kotlin), copy.sessions)
    }

    @Test fun `empty plan copy has an independent editable list`() {
        val original = StudyPlan("Empty", mutableListOf())
        val copy = independentPlan(original)
        copy.sessions.add(kotlin)
        assertEquals(emptyList(), original.sessions)
        assertEquals(listOf(kotlin), copy.sessions)
    }

    @Test fun `replacing a copied list entry does not replace the original entry`() {
        val original = StudyPlan("Week 1", mutableListOf(kotlin))
        val copy = independentPlan(original)
        copy.sessions[0] = kotlin.copy(completed = false)
        assertEquals(listOf(kotlin), original.sessions)
    }

    @Test fun `history starts empty`() {
        assertEquals(emptyList(), PlanHistory().snapshots())
    }

    @Test fun `saving captures values before source edits`() {
        val original = StudyPlan("Week 1", mutableListOf(kotlin))
        val history = PlanHistory()
        history.save(original)
        original.sessions.clear()
        assertEquals(listOf(StudyPlan("Week 1", mutableListOf(kotlin))), history.snapshots())
    }

    @Test fun `saving an empty plan captures its empty state`() {
        val original = StudyPlan("Empty", mutableListOf())
        val history = PlanHistory()
        history.save(original)
        original.sessions.add(kotlin)
        assertEquals(emptyList(), history.snapshots().single().sessions)
    }

    @Test fun `repeated saves append versions in save order`() {
        val original = StudyPlan("Week 1", mutableListOf(kotlin))
        val history = PlanHistory()
        history.save(original)
        original.sessions.add(kmp)
        history.save(original)
        original.sessions.clear()
        assertEquals(listOf(listOf(kotlin), listOf(kotlin, kmp)), history.snapshots().map { it.sessions })
    }

    @Test fun `edits through a returned plan do not alter saved history`() {
        val history = PlanHistory()
        history.save(StudyPlan("Week 1", mutableListOf(kotlin)))
        history.snapshots().single().sessions.clear()
        assertEquals(listOf(kotlin), history.snapshots().single().sessions)
    }

    @Test fun `two readers get independent editable plan lists`() {
        val history = PlanHistory()
        history.save(StudyPlan("Week 1", mutableListOf(kotlin)))
        val first = history.snapshots()
        val second = history.snapshots()
        first.single().sessions.add(kmp)
        assertEquals(listOf(kotlin), second.single().sessions)
    }

    @Test fun `previous outer result is a snapshot before later saves`() {
        val history = PlanHistory()
        history.save(StudyPlan("First", mutableListOf(kotlin)))
        val before = history.snapshots()
        history.save(StudyPlan("Second", mutableListOf(kmp)))
        assertEquals(listOf("First"), before.map { it.title })
        assertEquals(listOf("First", "Second"), history.snapshots().map { it.title })
    }

    @Test fun `two saved versions do not share editable lists`() {
        val original = StudyPlan("Week 1", mutableListOf(kotlin))
        val history = PlanHistory()
        history.save(original)
        history.save(original)
        val returned = history.snapshots()
        returned[0].sessions.clear()
        assertEquals(listOf(kotlin), returned[1].sessions)
    }

    @Test fun `history instances keep independent contents`() {
        val first = PlanHistory()
        val second = PlanHistory()
        first.save(StudyPlan("Week 1", mutableListOf(kotlin)))
        assertEquals(emptyList(), second.snapshots())
    }

    @Test fun `saving preserves repeated entries and exact input order`() {
        val plan = StudyPlan("Week 1", mutableListOf(kmp, kotlin, kmp))
        val history = PlanHistory()
        history.save(plan)
        assertEquals(listOf(kmp, kotlin, kmp), history.snapshots().single().sessions)
        assertEquals(listOf(kmp, kotlin, kmp), plan.sessions)
    }

    @Test fun `empty input has zero completed minutes`() {
        assertEquals(0L, completedMinutes(emptyList()))
    }

    @Test fun `planned only input has zero completed minutes`() {
        assertEquals(0L, completedMinutes(listOf(kmp, kotlin.copy(completed = false))))
    }

    @Test fun `mixed input totals only completed sessions without changing input`() {
        val sessions = mutableListOf(kmp, kotlin, StudySession(9, "Flow", 60, true))
        val before = sessions.toList()
        assertEquals(90L, completedMinutes(sessions))
        assertEquals(before, sessions)
    }

    @Test fun `valid duration endpoints both contribute Long minutes`() {
        val sessions = listOf(StudySession(1, "Kotlin", 1, true), StudySession(2, "KMP", 120, true))
        assertEquals(121L, completedMinutes(sessions))
    }

    @Test fun `totals count supplied entries including repeated IDs without retaining prior state`() {
        assertEquals(60L, completedMinutes(listOf(kotlin, kotlin)))
        assertEquals(0L, completedMinutes(emptyList()))
    }
}
