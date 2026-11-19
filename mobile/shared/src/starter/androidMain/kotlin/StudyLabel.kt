package workshop.studylab.shared

fun studyLabel(topic: String, minutes: Int): String {
    require(minutes >= 0) { "Minutes must be nonnegative." }
    val title = topic.trim().ifEmpty { "Study" }
    return "$title · $minutes min"
}
