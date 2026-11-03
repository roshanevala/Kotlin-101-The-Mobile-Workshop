package workshop.ch08

// Inputs are already valid. Sessions are immutable values; a plan's list is editable.
data class StudySession(
    val id: Int,
    val topic: String,
    val minutes: Int,
    val completed: Boolean = false
)

data class StudyPlan(val title: String, val sessions: MutableList<StudySession>)

fun sameSessionValue(first: StudySession, second: StudySession): Boolean {
    // TODO A value comparison must work for separately created equivalent sessions.
    return first === second
}

fun independentPlan(plan: StudyPlan): StudyPlan {
    // TODO Editing either plan's list must not edit the other.
    return plan.copy()
}

class PlanHistory {
    private val entries = mutableListOf<StudyPlan>()

    fun save(plan: StudyPlan) {
        // TODO Capture the values at save time.
        entries.add(plan)
    }

    fun snapshots(): List<StudyPlan> {
        // TODO Prevent callers from editing stored history through returned plans.
        return entries
    }
}

fun completedMinutes(sessions: List<StudySession>): Long {
    // TODO Planned sessions contribute no completed minutes.
    return sessions.sumOf { it.minutes.toLong() }
}

fun main() {
    val session = StudySession(7, "Kotlin", 30, completed = true)
    val original = StudyPlan("Week 1", mutableListOf(session))
    val edited = independentPlan(original)
    edited.sessions.add(StudySession(2, "KMP", 45))
    println("Original topics: ${original.sessions.map { it.topic }}")
    val history = PlanHistory()
    history.save(original)
    original.sessions.clear()
    println("Saved topics: ${history.snapshots().first().sessions.map { it.topic }}")
    println("Equal sessions: ${sameSessionValue(session, session.copy())}")
    println("Completed minutes: ${completedMinutes(listOf(session, StudySession(2, "KMP", 45)))}")
}
