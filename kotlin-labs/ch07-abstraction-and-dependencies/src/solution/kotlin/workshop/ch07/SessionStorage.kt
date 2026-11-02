package workshop.ch07

// Inputs have already been validated. This exercise is synchronous and in memory only.
data class StudySession(
    val id: Int,
    val topic: String,
    val minutes: Int,
    val completed: Boolean = false
)

interface Reader<out T> {
    fun all(): List<T>
    fun findById(id: Int): T?
}

interface Writer<in T> {
    fun save(item: T)
}

interface Store<T> : Reader<T>, Writer<T>

class InMemoryStore<T>(private val idOf: (T) -> Int) : Store<T> {
    private val items = mutableMapOf<Int, T>()

    override fun all(): List<T> {
        return items.values.toList()
    }

    override fun findById(id: Int): T? {
        return items[id]
    }

    override fun save(item: T) {
        items[idOf(item)] = item
    }
}

class ReaderView<T>(source: Reader<T>) : Reader<T> by source

class StudyPlanner(private val store: Store<StudySession>) {
    fun schedule(session: StudySession) {
        store.save(session)
    }

    fun complete(id: Int): Boolean {
        val original = store.findById(id) ?: return false
        store.save(original.copy(completed = true))
        return true
    }
}

class ProgressReport(private val reader: Reader<StudySession>) {
    fun completedMinutes(): Long {
        return reader.all().filter { it.completed }.sumOf { it.minutes.toLong() }
    }
}

fun main() {
    val store = InMemoryStore<StudySession> { it.id }
    val planner = StudyPlanner(store)
    val reader = ReaderView(store)
    val report = ProgressReport(reader)
    planner.schedule(StudySession(7, "Kotlin", 30))
    planner.schedule(StudySession(2, "KMP", 45, completed = true))
    println("Complete 7: ${planner.complete(7)}")
    println("Stored topics: ${reader.all().map { it.topic }}")
    println("Completed minutes: ${report.completedMinutes()}")
}
