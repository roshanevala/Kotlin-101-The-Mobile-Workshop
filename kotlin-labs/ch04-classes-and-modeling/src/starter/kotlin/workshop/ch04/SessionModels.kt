package workshop.ch04

enum class SessionStatus {
    PLANNED,
    COMPLETED
}

// Mission 1: give sessions value equality and a generated copy operation.
class StudySession(
    val topic: String,
    val minutes: Int,
    val status: SessionStatus = SessionStatus.PLANNED
)

sealed class SessionResult {
    data class Accepted(val session: StudySession) : SessionResult()
    data class Rejected(val message: String) : SessionResult()
}

fun createSession(rawTopic: String?, minutes: Int): SessionResult {
    // Mission 2: reject invalid minutes and keep a cleaned topic or the fallback.
    return SessionResult.Accepted(StudySession("Kotlin", 30))
}

fun completeSession(session: StudySession): StudySession {
    // Mission 3: return an updated copy while preserving the supplied session.
    return session
}

fun describeResult(result: SessionResult): String {
    // Mission 4: describe the actual result and, when accepted, its status.
    return "Planned: Kotlin for 30 min"
}

fun main() {
    println(describeResult(createSession(rawTopic = " KMP ", minutes = 45)))
}
