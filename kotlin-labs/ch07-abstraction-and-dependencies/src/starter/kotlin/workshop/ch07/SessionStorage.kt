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
    // TODO Keep private storage keyed by the supplied idOf function.

    override fun all(): List<T> {
        // TODO Return a snapshot in first-insertion order.
        return emptyList()
    }

    override fun findById(id: Int): T? {
        // TODO Look up the key rather than a list position.
        return null
    }

    override fun save(item: T) {
        // TODO Insert or replace this key without moving an existing key.
    }
}

class ReaderView<T>(private val source: Reader<T>) : Reader<T> {
    // TODO Replace these placeholders with interface delegation using by.
    override fun all(): List<T> {
        return emptyList()
    }

    override fun findById(id: Int): T? {
        return null
    }
}

class StudyPlanner(private val store: Store<StudySession>) {
    fun schedule(session: StudySession) {
        // TODO Save to the supplied store.
    }

    fun complete(id: Int): Boolean {
        // TODO Find the latest record, save a completed copy when present, and report success.
        return false
    }
}

class ProgressReport(private val reader: Reader<StudySession>) {
    fun completedMinutes(): Long {
        // TODO Read current entries and total only completed minutes.
        return 0L
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
