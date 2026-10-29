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
    val seenIds = mutableSetOf<Int>()
    val unique = mutableListOf<StudySession>()
    for (session in sessions) {
        if (seenIds.add(session.id)) {
            unique.add(session)
        }
    }
    return unique
}

fun completedMinutesByTopic(sessions: List<StudySession>): Map<String, Long> {
    val totals = mutableMapOf<String, Long>()
    for (session in uniqueSessions(sessions)) {
        if (session.status == SessionStatus.COMPLETED) {
            totals[session.topic] = (totals[session.topic] ?: 0L) + session.minutes.toLong()
        }
    }
    return totals
}

fun indexSessionsById(sessions: List<StudySession>): Map<Int, StudySession> {
    val index = mutableMapOf<Int, StudySession>()
    for (session in uniqueSessions(sessions)) {
        index[session.id] = session
    }
    return index
}

fun completedTopics(sessions: List<StudySession>): List<String> {
    return completedMinutesByTopic(sessions).keys.sorted()
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
