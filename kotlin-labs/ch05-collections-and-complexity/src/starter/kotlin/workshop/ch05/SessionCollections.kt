package workshop.ch05

enum class SessionStatus {
    PLANNED,
    COMPLETED
}

data class StudySession(
    val id: Int,
    val topic: String,
    val minutes: Int,
    val status: SessionStatus = SessionStatus.PLANNED
)

fun uniqueSessions(sessions: List<StudySession>): List<StudySession> {
    // Mission 1: keep the first record for each ID, preserving encounter order.
    return sessions
}

fun completedMinutesByTopic(sessions: List<StudySession>): Map<String, Long> {
    // Mission 2: total completed minutes after applying the duplicate-ID rule.
    return emptyMap()
}

fun indexSessionsById(sessions: List<StudySession>): Map<Int, StudySession> {
    // Mission 3: build an ID lookup using the same first-record rule.
    return emptyMap()
}

fun completedTopics(sessions: List<StudySession>): List<String> {
    // Mission 4: return the completed report's distinct topic names in natural order.
    return emptyList()
}

fun main() {
    val sessions = listOf(
        StudySession(3, "Kotlin", 30, SessionStatus.COMPLETED),
        StudySession(1, "KMP", 45, SessionStatus.COMPLETED),
        StudySession(3, "Kotlin", 30, SessionStatus.COMPLETED),
        StudySession(2, "Kotlin", 20)
    )
    println("Completed topics: ${completedTopics(sessions)}")
}
