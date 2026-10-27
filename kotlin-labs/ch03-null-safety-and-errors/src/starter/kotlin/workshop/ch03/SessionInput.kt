package workshop.ch03

// The form currently invents a valid session, even when the input is missing or invalid.
fun topicOrDefault(rawTopic: String?): String {
    // Mission 1: keep a cleaned topic or use the workshop's fallback.
    return "Kotlin"
}

fun parseMinutes(rawMinutes: String?): Int? {
    // Mission 2: parse the input without inventing a number or throwing for bad text.
    return 30
}

fun durationError(minutes: Int): String? {
    // Mission 3: return the specified error for a rejected duration.
    return null
}

fun describeSession(rawTopic: String?, rawMinutes: String?): String {
    // Mission 4: use the helpers to accept or explain the request.
    return "Study Kotlin for 30 min"
}

fun main() {
    println(describeSession(rawTopic = " Kotlin ", rawMinutes = "banana"))
}
