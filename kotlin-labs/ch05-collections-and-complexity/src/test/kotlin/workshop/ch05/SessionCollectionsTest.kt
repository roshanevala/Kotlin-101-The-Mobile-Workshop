package workshop.ch05

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Shared contracts for the working copy and the separately compiled reference.
class SessionCollectionsTest {
    @Test
    fun `unique sessions handles an empty input`() {
        assertEquals(emptyList(), uniqueSessions(emptyList()))
    }

    @Test
    fun `unique sessions keeps encounter order rather than sorting IDs`() {
        val sessions = listOf(StudySession(9, "KMP", 30), StudySession(2, "Kotlin", 45))
        assertEquals(sessions, uniqueSessions(sessions))
    }

    @Test
    fun `unique sessions removes exact repeats`() {
        val session = StudySession(7, "Kotlin", 30)
        assertEquals(listOf(session), uniqueSessions(listOf(session, session, session)))
    }

    @Test
    fun `unique sessions keeps the first conflicting record for an ID`() {
        val first = StudySession(7, "Kotlin", 30)
        val later = StudySession(7, "KMP", 45, SessionStatus.COMPLETED)
        assertEquals(listOf(first), uniqueSessions(listOf(first, later)))
    }

    @Test
    fun `unique sessions detects nonadjacent duplicate IDs`() {
        val first = StudySession(7, "Kotlin", 30)
        val second = StudySession(2, "KMP", 45)
        val third = StudySession(9, "Flow", 20)
        assertEquals(listOf(first, second, third), uniqueSessions(listOf(first, second, first.copy(minutes = 60), third, second)))
    }

    @Test
    fun `different IDs remain separate even when other values match`() {
        val sessions = listOf(StudySession(1, "Kotlin", 30), StudySession(2, "Kotlin", 30))
        assertEquals(sessions, uniqueSessions(sessions))
    }

    @Test
    fun `zero and negative IDs are ordinary keys`() {
        val first = StudySession(0, "Kotlin", 30)
        val second = StudySession(Int.MIN_VALUE, "KMP", 45)
        assertEquals(listOf(first, second), uniqueSessions(listOf(first, second, first, second)))
    }

    @Test
    fun `deduplication does not change the supplied list`() {
        val session = StudySession(1, "Kotlin", 30)
        val input = mutableListOf(session, session, StudySession(2, "KMP", 45))
        val before = input.toList()
        uniqueSessions(input)
        assertEquals(before, input)
    }

    @Test
    fun `unique result is a snapshot rather than a view of the input`() {
        val first = StudySession(1, "Kotlin", 30)
        val input = mutableListOf(first)
        val result = uniqueSessions(input)
        input.add(StudySession(2, "KMP", 45))
        assertEquals(listOf(first), result)
    }

    @Test
    fun `completed totals handles an empty input`() {
        assertEquals(emptyMap(), completedMinutesByTopic(emptyList()))
    }

    @Test
    fun `planned-only topics do not appear in completed totals`() {
        assertEquals(emptyMap(), completedMinutesByTopic(listOf(StudySession(1, "Kotlin", 30))))
    }

    @Test
    fun `completed totals includes one completed session`() {
        val session = StudySession(1, "Kotlin", 30, SessionStatus.COMPLETED)
        assertEquals(mapOf("Kotlin" to 30L), completedMinutesByTopic(listOf(session)))
    }

    @Test
    fun `completed totals adds distinct sessions for the same topic`() {
        val sessions = listOf(
            StudySession(1, "Kotlin", 30, SessionStatus.COMPLETED),
            StudySession(2, "Kotlin", 45, SessionStatus.COMPLETED),
            StudySession(3, "Kotlin", 20)
        )
        assertEquals(mapOf("Kotlin" to 75L), completedMinutesByTopic(sessions))
    }

    @Test
    fun `completed totals keeps different topics separate`() {
        val sessions = listOf(
            StudySession(1, "Kotlin", 30, SessionStatus.COMPLETED),
            StudySession(2, "KMP", 45, SessionStatus.COMPLETED)
        )
        assertEquals(mapOf("Kotlin" to 30L, "KMP" to 45L), completedMinutesByTopic(sessions))
    }

    @Test
    fun `topic keys are case sensitive`() {
        val sessions = listOf(
            StudySession(1, "Kotlin", 30, SessionStatus.COMPLETED),
            StudySession(2, "kotlin", 45, SessionStatus.COMPLETED)
        )
        assertEquals(mapOf("Kotlin" to 30L, "kotlin" to 45L), completedMinutesByTopic(sessions))
    }

    @Test
    fun `planned first record wins over a later completed duplicate`() {
        val sessions = listOf(StudySession(1, "Kotlin", 30), StudySession(1, "Kotlin", 45, SessionStatus.COMPLETED))
        assertEquals(emptyMap(), completedMinutesByTopic(sessions))
    }

    @Test
    fun `completed first record is counted once despite conflicting duplicates`() {
        val first = StudySession(1, "Kotlin", 30, SessionStatus.COMPLETED)
        val sessions = listOf(first, first.copy(topic = "KMP", minutes = 60), first.copy(status = SessionStatus.PLANNED), first)
        assertEquals(mapOf("Kotlin" to 30L), completedMinutesByTopic(sessions))
    }

    @Test
    fun `completed totals includes both valid duration endpoints as Long values`() {
        val sessions = listOf(
            StudySession(1, "Kotlin", 1, SessionStatus.COMPLETED),
            StudySession(2, "Kotlin", 120, SessionStatus.COMPLETED)
        )
        assertEquals(mapOf("Kotlin" to 121L), completedMinutesByTopic(sessions))
    }

    @Test
    fun `completed totals calls do not retain a previous result`() {
        val first = listOf(StudySession(1, "Kotlin", 30, SessionStatus.COMPLETED))
        assertEquals(mapOf("Kotlin" to 30L), completedMinutesByTopic(first))
        assertEquals(emptyMap(), completedMinutesByTopic(emptyList()))
        assertEquals(mapOf("Kotlin" to 30L), completedMinutesByTopic(first))
    }

    @Test
    fun `index handles an empty input`() {
        assertEquals(emptyMap(), indexSessionsById(emptyList()))
    }

    @Test
    fun `index includes planned and completed sessions`() {
        val first = StudySession(1, "Kotlin", 30)
        val second = StudySession(2, "KMP", 45, SessionStatus.COMPLETED)
        assertEquals(mapOf(1 to first, 2 to second), indexSessionsById(listOf(first, second)))
    }

    @Test
    fun `missing ID lookup returns null`() {
        val index = indexSessionsById(listOf(StudySession(1, "Kotlin", 30)))
        assertNull(index[99])
    }

    @Test
    fun `index keeps the first conflicting record`() {
        val first = StudySession(7, "Kotlin", 30)
        val second = StudySession(7, "KMP", 45, SessionStatus.COMPLETED)
        assertEquals(mapOf(7 to first), indexSessionsById(listOf(first, second, first)))
    }

    @Test
    fun `index uses IDs rather than list positions`() {
        val first = StudySession(100, "Kotlin", 30)
        val second = StudySession(-7, "KMP", 45)
        val index = indexSessionsById(listOf(first, second))
        assertEquals(first, index[100])
        assertEquals(second, index[-7])
        assertNull(index[0])
        assertEquals(2, index.size)
    }

    @Test
    fun `index is a snapshot and does not change the input`() {
        val first = StudySession(1, "Kotlin", 30)
        val input = mutableListOf(first)
        val index = indexSessionsById(input)
        assertEquals(listOf(first), input)
        input.add(StudySession(2, "KMP", 45))
        assertEquals(mapOf(1 to first), index)
    }

    @Test
    fun `index calls do not retain earlier sessions`() {
        val first = StudySession(1, "Kotlin", 30)
        val second = StudySession(2, "KMP", 45)
        assertEquals(mapOf(1 to first), indexSessionsById(listOf(first)))
        assertEquals(mapOf(2 to second), indexSessionsById(listOf(second)))
        assertEquals(emptyMap(), indexSessionsById(emptyList()))
    }

    @Test
    fun `completed topics handles an empty input`() {
        assertEquals(emptyList(), completedTopics(emptyList()))
    }

    @Test
    fun `completed topics excludes topics that are only planned`() {
        assertEquals(emptyList(), completedTopics(listOf(StudySession(1, "Kotlin", 30))))
    }

    @Test
    fun `completed topics uses natural case-sensitive order`() {
        val sessions = listOf(
            StudySession(1, "android", 30, SessionStatus.COMPLETED),
            StudySession(2, "Kotlin", 30, SessionStatus.COMPLETED),
            StudySession(3, "Android", 30, SessionStatus.COMPLETED)
        )
        assertEquals(listOf("Android", "Kotlin", "android"), completedTopics(sessions))
    }

    @Test
    fun `completed topics lists each topic once`() {
        val sessions = listOf(
            StudySession(1, "Kotlin", 30, SessionStatus.COMPLETED),
            StudySession(2, "KMP", 30, SessionStatus.COMPLETED),
            StudySession(3, "Kotlin", 30, SessionStatus.COMPLETED)
        )
        assertEquals(listOf("KMP", "Kotlin"), completedTopics(sessions))
    }

    @Test
    fun `completed topics applies the first-record ID rule before choosing topics`() {
        val sessions = listOf(
            StudySession(1, "Kotlin", 30),
            StudySession(1, "Flow", 30, SessionStatus.COMPLETED),
            StudySession(2, "KMP", 30, SessionStatus.COMPLETED),
            StudySession(2, "Android", 30, SessionStatus.COMPLETED)
        )
        assertEquals(listOf("KMP"), completedTopics(sessions))
    }

    @Test
    fun `report helpers leave input order and values unchanged`() {
        val first = StudySession(9, "Kotlin", 30, SessionStatus.COMPLETED)
        val second = StudySession(2, "Android", 45, SessionStatus.COMPLETED)
        val input = mutableListOf(first, second, first)
        val before = input.toList()
        completedMinutesByTopic(input)
        indexSessionsById(input)
        completedTopics(input)
        assertEquals(before, input)
    }
}
