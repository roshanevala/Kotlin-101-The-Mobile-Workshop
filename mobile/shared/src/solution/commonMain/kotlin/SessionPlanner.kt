package workshop.studylab.shared

class SessionPlanner(private val ids: SessionIdSource) {
    fun plan(topic: String?, minutes: Int): PlannedSession? {
        if (minutes !in 1..120) return null
        val title = topic?.trim().orEmpty().ifEmpty { "Untitled" }
        return PlannedSession(ids.nextId(), title, minutes)
    }
}
