package workshop.ch06

// Inputs are already validated. Each entry is a session; this mission does not deduplicate.
data class StudySession(val topic: String, val minutes: Int, val completed: Boolean)

fun allOf(
    first: (StudySession) -> Boolean,
    second: (StudySession) -> Boolean
): (StudySession) -> Boolean {
    // TODO Return a rule that checks both supplied rules, in order.
    return { false }
}

fun selectSessions(
    sessions: List<StudySession>,
    predicate: (StudySession) -> Boolean
): List<StudySession> {
    // TODO Select matching sessions without changing the input.
    return emptyList()
}

fun StudySession.label(): String {
    // TODO Include the topic and duration using this receiver.
    return topic
}

fun eagerPreview(
    sessions: List<StudySession>,
    limit: Int,
    predicate: (StudySession) -> Boolean,
    transform: (StudySession) -> String
): List<String> {
    // TODO Reject negative limits, then filter, map, and take on the list.
    return emptyList()
}

fun lazyPreview(
    sessions: List<StudySession>,
    limit: Int,
    predicate: (StudySession) -> Boolean,
    transform: (StudySession) -> String
): List<String> {
    // TODO Use a sequence, then materialize a list before returning.
    return emptyList()
}

fun main() {
    val sessions = listOf(
        StudySession("Android", 20, false),
        StudySession("Kotlin", 30, true),
        StudySession("KMP", 45, true),
        StudySession("Flow", 15, false)
    )
    val completed: (StudySession) -> Boolean = { it.completed }
    val longEnough: (StudySession) -> Boolean = { it.minutes >= 30 }
    val rule = allOf(completed, longEnough)
    println("Selected topics: ${selectSessions(sessions, rule).map { it.topic }}")

    // Counting is an experiment: production predicates should normally avoid side effects.
    var filterCalls = 0
    var mapCalls = 0
    val countedRule: (StudySession) -> Boolean = { session ->
        filterCalls += 1
        rule(session)
    }
    val countedLabel: (StudySession) -> String = { session ->
        mapCalls += 1
        session.label()
    }
    val eager = eagerPreview(sessions, 1, countedRule, countedLabel)
    println("Eager: $eager (filter=$filterCalls, map=$mapCalls)")
    filterCalls = 0
    mapCalls = 0
    val lazy = lazyPreview(sessions, 1, countedRule, countedLabel)
    println("Lazy: $lazy (filter=$filterCalls, map=$mapCalls)")
}
