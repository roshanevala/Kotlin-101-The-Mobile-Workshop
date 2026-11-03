package workshop.ch08

import kotlin.test.Test
import kotlin.test.assertEquals

class SnapshotPracticeTest {
    @Test
    fun `empty input has no completed minutes`() {
        // Arrange: no sessions to count.
        val sessions = emptyList<StudySession>()
        // Act: ask for the observable result.
        val actual = completedMinutes(sessions)
        // Assert: expected value first, actual value second.
        assertEquals(0L, actual)
    }

    // TODO Add a test for equivalent session values held in different instances.
    // TODO Add a test that edits a copied plan without changing the original.
    // TODO Add tests for history after input edits and after returned-plan edits.
    // TODO Add a planned-only total test and a completed-duration boundary test.
    // Write one failing regression at a time before fixing the production code.
}
