package workshop.ch02

fun isValidDuration(minutes: Int): Boolean {
    return minutes in 1..120
}

fun sessionLabel(minutes: Int): String {
    return when (minutes) {
        in 1..24 -> "Quick"
        in 25..49 -> "Focus"
        in 50..120 -> "Deep"
        else -> "Invalid duration"
    }
}

fun buildStudyPlan(minutes: Int, rounds: Int = 1): String {
    if (!isValidDuration(minutes)) {
        return "Invalid duration"
    }
    if (rounds !in 1..4) {
        return "Invalid rounds"
    }

    val label = sessionLabel(minutes)
    var plan = ""
    for (round in 1..rounds) {
        if (round > 1) {
            plan += "\n"
        }
        plan += "Round $round: $label"
    }
    return plan
}

fun main() {
    println(buildStudyPlan(minutes = 30, rounds = 3))
}
