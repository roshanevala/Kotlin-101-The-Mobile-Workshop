package workshop.studylab.data

import java.io.IOException
import kotlinx.coroutines.delay
import workshop.studylab.StudySession

interface SessionsRepository {
    // Returns the current catalogue, or throws IOException for a recoverable load failure.
    suspend fun loadSessions(): List<StudySession>
}

// A deterministic teaching fixture. It makes no HTTP or database calls.
class DemoSessionsRepository(
    private val sessions: List<StudySession> = listOf(
        StudySession(1, "Kotlin", false), StudySession(2, "Android", false)
    ),
    private val failFirst: Boolean = true
) : SessionsRepository {
    private var attempts = 0

    override suspend fun loadSessions(): List<StudySession> {
        val attempt = ++attempts
        delay(400L)
        if (failFirst && attempt == 1) throw IOException("Demo catalogue unavailable")
        return sessions.toList()
    }
}
