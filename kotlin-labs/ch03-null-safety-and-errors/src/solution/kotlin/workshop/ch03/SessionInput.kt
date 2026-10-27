package workshop.ch03

fun topicOrDefault(rawTopic: String?): String {
    val topic = rawTopic?.trim() ?: return "Untitled"
    if (topic.isEmpty()) {
        return "Untitled"
    }
    return topic
}

fun parseMinutes(rawMinutes: String?): Int? {
    return rawMinutes?.trim()?.toIntOrNull()
}

fun durationError(minutes: Int): String? {
    if (minutes !in 1..120) {
        return "Minutes must be 1..120"
    }
    return null
}

fun describeSession(rawTopic: String?, rawMinutes: String?): String {
    val minutes = parseMinutes(rawMinutes) ?: return "Enter whole minutes"
    val error = durationError(minutes)
    if (error != null) {
        return error
    }
    val topic = topicOrDefault(rawTopic)
    return "Study $topic for $minutes min"
}

fun main() {
    println(describeSession(rawTopic = " Kotlin ", rawMinutes = "banana"))
}
