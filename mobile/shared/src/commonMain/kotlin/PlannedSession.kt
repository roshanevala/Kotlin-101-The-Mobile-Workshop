package workshop.studylab.shared

data class PlannedSession(val id: String, val topic: String, val minutes: Int)

interface SessionIdSource {
    fun nextId(): String
}

expect fun platformSessionIds(): SessionIdSource
