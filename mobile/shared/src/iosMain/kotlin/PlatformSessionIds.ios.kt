package workshop.studylab.shared

import platform.Foundation.NSUUID

actual fun platformSessionIds(): SessionIdSource = object : SessionIdSource {
    override fun nextId(): String = NSUUID().UUIDString()
}
