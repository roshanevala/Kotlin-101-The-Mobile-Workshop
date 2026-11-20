package workshop.studylab.shared

import java.util.UUID

actual fun platformSessionIds(): SessionIdSource = object : SessionIdSource {
    override fun nextId(): String = UUID.randomUUID().toString()
}
