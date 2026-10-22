package workshop.ch01

// The function shell is supplied. Your mission is to fix the calculation inside.
fun formatStudyTime(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = 0 // The timer currently loses the remaining minutes.
    return "$hours h $minutes min"
}

fun main() {
    val totalMinutes = 95
    println(formatStudyTime(totalMinutes))
}
