package workshop.ch02

// These functions compile, but the planner does not follow the workshop rules yet.
fun isValidDuration(minutes: Int): Boolean {
    // Mission 1: decide which durations the planner accepts.
    return true
}

fun sessionLabel(minutes: Int): String {
    // Mission 2: choose a label, including for invalid durations.
    return "Focus"
}

fun buildStudyPlan(minutes: Int, rounds: Int = 1): String {
    // Mission 3: validate both inputs and build the requested number of rounds.
    return "Round 1: ${sessionLabel(minutes)}"
}

fun main() {
    println(buildStudyPlan(minutes = 30, rounds = 3))
}
