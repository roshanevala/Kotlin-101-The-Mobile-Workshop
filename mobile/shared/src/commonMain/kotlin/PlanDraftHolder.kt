package workshop.studylab.shared

data class PlanDraftSnapshot(val topic: String = "", val minutesText: String = "")

class PlanDraftHolder {
    var snapshot = PlanDraftSnapshot()
        private set

    fun editTopic(text: String) {
        snapshot = snapshot.copy(topic = text)
    }

    fun editMinutes(text: String) {
        snapshot = snapshot.copy(minutesText = text)
    }

    fun clear() {
        snapshot = PlanDraftSnapshot()
    }
}
