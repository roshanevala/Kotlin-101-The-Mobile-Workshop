package workshop.ch04

enum class SessionStatus {
    PLANNED,
    COMPLETED
}

data class StudySession(
    val topic: String,
    val minutes: Int,
    val status: SessionStatus = SessionStatus.PLANNED
)

sealed class SessionResult {
    data class Accepted(val session: StudySession) : SessionResult()
    data class Rejected(val message: String) : SessionResult()
}

fun createSession(rawTopic: String?, minutes: Int): SessionResult {
    if (minutes !in 1..120) {
        return SessionResult.Rejected("Minutes must be 1..120")
    }
    val trimmedTopic = rawTopic?.trim()
    val topic = if (trimmedTopic == null || trimmedTopic.isEmpty()) "Untitled" else trimmedTopic
    return SessionResult.Accepted(StudySession(topic = topic, minutes = minutes))
}

fun completeSession(session: StudySession): StudySession {
    return session.copy(status = SessionStatus.COMPLETED)
}

fun describeResult(result: SessionResult): String {
    return when (result) {
        is SessionResult.Accepted -> {
            val session = result.session
            val label = when (session.status) {
                SessionStatus.PLANNED -> "Planned"
                SessionStatus.COMPLETED -> "Completed"
            }
            "$label: ${session.topic} for ${session.minutes} min"
        }
        is SessionResult.Rejected -> result.message
    }
}

fun main() {
    println(describeResult(createSession(rawTopic = " KMP ", minutes = 45)))
}
