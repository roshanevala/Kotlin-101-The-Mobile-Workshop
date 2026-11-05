package workshop.ch09

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

// Source results are already valid; topics keep their exact order and repeats.
data class Dashboard(val topics: List<String>, val completedMinutes: Long)

interface StudySource {
    suspend fun fetchTopics(): List<String>
    suspend fun fetchCompletedMinutes(): Long
}

suspend fun loadSequential(source: StudySource): Dashboard {
    // TODO Fetch topics, then completed minutes, and return both results.
    return Dashboard(emptyList(), 0L)
}

suspend fun loadConcurrent(source: StudySource): Dashboard {
    // TODO Start both independent requests in a local scope before awaiting either.
    return loadSequential(source)
}

suspend fun loadOn(source: StudySource, dispatcher: CoroutineDispatcher): Dashboard {
    // TODO Run the concurrent loader in the supplied dispatcher and return its result.
    return loadConcurrent(source)
}

// This counting demo is used only by main's inherited, single-threaded runBlocking context.
// It is not a thread-safe source for arbitrary dispatchers.
private class DemoSource : StudySource {
    private var active = 0
    var peakActive = 0
        private set

    private suspend fun pause(milliseconds: Long) {
        active += 1
        if (active > peakActive) peakActive = active
        try {
            delay(milliseconds)
        } finally {
            active -= 1
        }
    }

    override suspend fun fetchTopics(): List<String> {
        pause(20L)
        return listOf("Kotlin", "KMP")
    }

    override suspend fun fetchCompletedMinutes(): Long {
        pause(40L)
        return 75L
    }
}

fun main() = runBlocking {
    val firstSource = DemoSource()
    val sequential = loadSequential(firstSource)
    println("Sequential: ${sequential.topics}, minutes=${sequential.completedMinutes}, peak=${firstSource.peakActive}")
    val secondSource = DemoSource()
    val concurrent = loadConcurrent(secondSource)
    println("Concurrent: ${concurrent.topics}, minutes=${concurrent.completedMinutes}, peak=${secondSource.peakActive}")
}
