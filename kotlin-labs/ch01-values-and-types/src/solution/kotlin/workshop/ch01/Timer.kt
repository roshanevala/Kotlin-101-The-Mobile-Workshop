package workshop.ch01

fun formatStudyTime(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return "$hours h $minutes min"
}

fun main() {
    val totalMinutes = 95
    println(formatStudyTime(totalMinutes))
}
