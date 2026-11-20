package workshop.studylab.shared

class SessionPlanner(private val ids: SessionIdSource) {
    fun plan(topic: String?, minutes: Int): PlannedSession? {
        return PlannedSession(ids.nextId(), topic.orEmpty(), minutes)
    }
}
