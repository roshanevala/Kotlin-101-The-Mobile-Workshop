package workshop.ch04

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

// The same contracts check either the local working copy or the separate reference.
class SessionModelsTest {
    @Test
    fun `sessions with the same values are equal`() {
        assertEquals(StudySession("Kotlin", 30), StudySession("Kotlin", 30))
    }

    @Test
    fun `topic participates in equality`() {
        assertNotEquals(StudySession("Kotlin", 30), StudySession("KMP", 30))
    }

    @Test
    fun `minutes participate in equality`() {
        assertNotEquals(StudySession("Kotlin", 30), StudySession("Kotlin", 45))
    }

    @Test
    fun `status participates in equality`() {
        assertNotEquals(
            StudySession("Kotlin", 30),
            StudySession("Kotlin", 30, SessionStatus.COMPLETED)
        )
    }

    @Test
    fun `a session defaults to planned`() {
        assertEquals(SessionStatus.PLANNED, StudySession("Kotlin", 30).status)
    }

    @Test
    fun `creation keeps supplied values and trims topic edges`() {
        val result = assertIs<SessionResult.Accepted>(createSession(" \tKMP\n ", 45))
        assertEquals("KMP", result.session.topic)
        assertEquals(45, result.session.minutes)
    }

    @Test
    fun `missing topic gets the fallback`() {
        val result = assertIs<SessionResult.Accepted>(createSession(null, 30))
        assertEquals("Untitled", result.session.topic)
    }

    @Test
    fun `empty and whitespace topics get the fallback`() {
        assertEquals("Untitled", assertIs<SessionResult.Accepted>(createSession("", 30)).session.topic)
        assertEquals("Untitled", assertIs<SessionResult.Accepted>(createSession(" \t\n ", 30)).session.topic)
    }

    @Test
    fun `topic preserves case and spaces inside`() {
        val result = assertIs<SessionResult.Accepted>(createSession(" Kotlin  basics ", 30))
        assertEquals("Kotlin  basics", result.session.topic)
    }

    @Test
    fun `the word null remains a topic`() {
        val result = assertIs<SessionResult.Accepted>(createSession("null", 30))
        assertEquals("null", result.session.topic)
    }

    @Test
    fun `creation accepts the lower duration limit`() {
        val result = assertIs<SessionResult.Accepted>(createSession("Kotlin", 1))
        assertEquals(1, result.session.minutes)
    }

    @Test
    fun `creation accepts the upper duration limit`() {
        val result = assertIs<SessionResult.Accepted>(createSession("Kotlin", 120))
        assertEquals(120, result.session.minutes)
    }

    @Test
    fun `creation rejects zero and negative durations`() {
        assertEquals(SessionResult.Rejected("Minutes must be 1..120"), createSession("Kotlin", 0))
        assertEquals(SessionResult.Rejected("Minutes must be 1..120"), createSession(null, -1))
        assertEquals(SessionResult.Rejected("Minutes must be 1..120"), createSession("", Int.MIN_VALUE))
    }

    @Test
    fun `creation rejects durations above the limit`() {
        assertEquals(SessionResult.Rejected("Minutes must be 1..120"), createSession("Kotlin", 121))
        assertEquals(SessionResult.Rejected("Minutes must be 1..120"), createSession(null, Int.MAX_VALUE))
    }

    @Test
    fun `created sessions always start planned`() {
        assertEquals(SessionStatus.PLANNED, assertIs<SessionResult.Accepted>(createSession("KMP", 45)).session.status)
        assertEquals(SessionStatus.PLANNED, assertIs<SessionResult.Accepted>(createSession(null, 1)).session.status)
    }

    @Test
    fun `completion keeps topic and minutes and changes status`() {
        val completed = completeSession(StudySession("KMP", 45))
        assertEquals("KMP", completed.topic)
        assertEquals(45, completed.minutes)
        assertEquals(SessionStatus.COMPLETED, completed.status)
    }

    @Test
    fun `completion preserves the original session`() {
        val original = StudySession("Kotlin", 30)
        completeSession(original)
        assertEquals("Kotlin", original.topic)
        assertEquals(30, original.minutes)
        assertEquals(SessionStatus.PLANNED, original.status)
    }

    @Test
    fun `completion returns a separate session`() {
        val original = StudySession("Kotlin", 30)
        assertNotSame(original, completeSession(original))
    }

    @Test
    fun `completing an already completed session keeps its values in a new copy`() {
        val original = StudySession("Kotlin", 1, SessionStatus.COMPLETED)
        val completed = completeSession(original)
        assertEquals(original, completed)
        assertNotSame(original, completed)
    }

    @Test
    fun `completion does not reuse another session's values`() {
        val first = completeSession(StudySession("Kotlin", 1))
        val second = completeSession(StudySession("KMP", 120))
        assertEquals(StudySession("Kotlin", 1, SessionStatus.COMPLETED), first)
        assertEquals(StudySession("KMP", 120, SessionStatus.COMPLETED), second)
    }

    @Test
    fun `planned description uses the actual session`() {
        assertEquals("Planned: KMP for 45 min", describeResult(SessionResult.Accepted(StudySession("KMP", 45))))
    }

    @Test
    fun `completed description uses the actual status`() {
        val result = SessionResult.Accepted(StudySession("Kotlin", 30, SessionStatus.COMPLETED))
        assertEquals("Completed: Kotlin for 30 min", describeResult(result))
    }

    @Test
    fun `rejected description preserves any supplied message`() {
        assertEquals("Try another duration", describeResult(SessionResult.Rejected("Try another duration")))
        assertEquals("", describeResult(SessionResult.Rejected("")))
    }

    @Test
    fun `created descriptions handle fallback and duration endpoints`() {
        assertEquals("Planned: Untitled for 1 min", describeResult(createSession(null, 1)))
        assertEquals("Planned: Kotlin for 120 min", describeResult(createSession(" Kotlin ", 120)))
    }

    @Test
    fun `rejected creation remains rejected when described`() {
        assertEquals("Minutes must be 1..120", describeResult(createSession("KMP", 0)))
    }

    @Test
    fun `creation and description calls are independent`() {
        assertEquals("Planned: KMP for 45 min", describeResult(createSession(" KMP ", 45)))
        assertEquals("Minutes must be 1..120", describeResult(createSession(null, 121)))
        assertEquals("Planned: Untitled for 1 min", describeResult(createSession(" ", 1)))
        assertEquals("Planned: KMP for 45 min", describeResult(createSession(" KMP ", 45)))
    }
}
