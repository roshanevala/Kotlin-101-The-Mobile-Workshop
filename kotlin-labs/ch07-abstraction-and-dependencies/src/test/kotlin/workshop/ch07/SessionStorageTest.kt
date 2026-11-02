package workshop.ch07

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Shared observable contracts for the working copy and the separate reference.
class SessionStorageTest {
    private val kotlin = StudySession(7, "Kotlin", 30)
    private val kmp = StudySession(2, "KMP", 45, completed = true)
    private data class Note(val key: Int, val text: String)

    // A controlled substitute: lookups and reads are independent supplied responses.
    private class FakeStore(
        var entries: List<StudySession> = emptyList(),
        var found: StudySession? = null
    ) : Store<StudySession> {
        val events = mutableListOf<String>()
        val saved = mutableListOf<StudySession>()
        override fun all(): List<StudySession> {
            events.add("all")
            return entries.toList()
        }
        override fun findById(id: Int): StudySession? {
            events.add("find:$id")
            return found
        }
        override fun save(item: StudySession) {
            events.add("save:${item.id}")
            saved.add(item)
        }
    }

    @Test fun `new store is empty`() {
        assertEquals(emptyList(), InMemoryStore<StudySession> { it.id }.all())
    }

    @Test fun `absent key returns null`() {
        assertNull(InMemoryStore<StudySession> { it.id }.findById(7))
    }

    @Test fun `store accepts a different model through its key function`() {
        val store = InMemoryStore<Note> { it.key }
        val note = Note(12, "Review variance")
        store.save(note)
        assertEquals(note, store.findById(12))
        assertEquals(listOf(note), store.all())
    }

    @Test fun `key function need not read an id property`() {
        val store = InMemoryStore<String> { it.length }
        store.save("Kotlin")
        store.save("Swift")
        assertEquals("Kotlin", store.findById(6))
        assertEquals("Swift", store.findById(5))
        assertNull(store.findById(0))
    }

    @Test fun `all keeps first insertion order rather than sorted keys`() {
        val store = InMemoryStore<StudySession> { it.id }
        store.save(kotlin)
        store.save(kmp)
        assertEquals(listOf(kotlin, kmp), store.all())
    }

    @Test fun `save replaces the value at a matching key`() {
        val store = InMemoryStore<StudySession> { it.id }
        val updated = kotlin.copy(topic = "Flow", minutes = 60, completed = true)
        store.save(kotlin)
        store.save(updated)
        assertEquals(updated, store.findById(7))
        assertEquals(listOf(updated), store.all())
    }

    @Test fun `replacement keeps the original key position`() {
        val store = InMemoryStore<StudySession> { it.id }
        store.save(kotlin)
        store.save(kmp)
        val updated = kotlin.copy(completed = true)
        store.save(updated)
        assertEquals(listOf(updated, kmp), store.all())
    }

    @Test fun `saving the same value twice keeps one record`() {
        val store = InMemoryStore<StudySession> { it.id }
        store.save(kotlin)
        store.save(kotlin)
        assertEquals(listOf(kotlin), store.all())
    }

    @Test fun `zero and negative keys are ordinary keys`() {
        val store = InMemoryStore<Note> { it.key }
        val zero = Note(0, "Zero")
        val negative = Note(Int.MIN_VALUE, "Negative")
        store.save(zero)
        store.save(negative)
        assertEquals(zero, store.findById(0))
        assertEquals(negative, store.findById(Int.MIN_VALUE))
        assertEquals(listOf(zero, negative), store.all())
    }

    @Test fun `matching session details do not collapse different IDs`() {
        val store = InMemoryStore<StudySession> { it.id }
        val other = kotlin.copy(id = 99)
        store.save(kotlin)
        store.save(other)
        assertEquals(listOf(kotlin, other), store.all())
    }

    @Test fun `all returns a snapshot before additions and replacements`() {
        val store = InMemoryStore<StudySession> { it.id }
        store.save(kotlin)
        val before = store.all()
        store.save(kmp)
        store.save(kotlin.copy(completed = true))
        assertEquals(listOf(kotlin), before)
        assertEquals(listOf(kotlin.copy(completed = true), kmp), store.all())
    }

    @Test fun `store instances keep independent contents`() {
        val first = InMemoryStore<StudySession> { it.id }
        val second = InMemoryStore<StudySession> { it.id }
        first.save(kotlin)
        second.save(kmp)
        assertEquals(listOf(kotlin), first.all())
        assertEquals(listOf(kmp), second.all())
    }

    @Test fun `reader view forwards all to its supplied reader`() {
        val fake = FakeStore(entries = listOf(kmp, kotlin))
        assertEquals(listOf(kmp, kotlin), ReaderView(fake).all())
        assertEquals(listOf("all"), fake.events)
    }

    @Test fun `reader view forwards lookup rather than deriving it from all`() {
        val fake = FakeStore(found = kmp)
        assertEquals(kmp, ReaderView(fake).findById(2))
        assertEquals(listOf("find:2"), fake.events)
    }

    @Test fun `reader view forwards missing keys unchanged`() {
        val fake = FakeStore()
        assertNull(ReaderView(fake).findById(-9))
        assertEquals(listOf("find:-9"), fake.events)
    }

    @Test fun `reader view reads fresh state on each invocation`() {
        val fake = FakeStore(entries = listOf(kotlin), found = kotlin)
        val view = ReaderView(fake)
        assertEquals(listOf(kotlin), view.all())
        assertEquals(kotlin, view.findById(7))
        fake.entries = listOf(kmp)
        fake.found = null
        assertEquals(listOf(kmp), view.all())
        assertNull(view.findById(7))
    }

    @Test fun `view is live while each store list is a snapshot`() {
        val store = InMemoryStore<StudySession> { it.id }
        store.save(kotlin)
        val view = ReaderView(store)
        val before = view.all()
        store.save(kmp)
        assertEquals(listOf(kotlin), before)
        assertEquals(listOf(kotlin, kmp), view.all())
    }

    @Test fun `schedule sends the unchanged session to the injected store`() {
        val fake = FakeStore()
        StudyPlanner(fake).schedule(kotlin)
        assertEquals(listOf(kotlin), fake.saved)
        assertEquals(listOf("save:7"), fake.events)
    }

    @Test fun `completion of missing key returns false without saving`() {
        val fake = FakeStore()
        assertFalse(StudyPlanner(fake).complete(99))
        assertEquals(emptyList(), fake.saved)
        assertEquals(listOf("find:99"), fake.events)
    }

    @Test fun `completion reads then saves a completed copy through the injected store`() {
        val fake = FakeStore(found = kotlin)
        assertTrue(StudyPlanner(fake).complete(7))
        assertEquals(listOf(kotlin.copy(completed = true)), fake.saved)
        assertEquals(listOf("find:7", "save:7"), fake.events)
        assertFalse(kotlin.completed)
    }

    @Test fun `completing an already completed record still succeeds and saves it`() {
        val fake = FakeStore(found = kmp)
        assertTrue(StudyPlanner(fake).complete(2))
        assertEquals(listOf(kmp), fake.saved)
    }

    @Test fun `completion asks the store for current values every time`() {
        val fake = FakeStore(found = kotlin)
        val planner = StudyPlanner(fake)
        assertTrue(planner.complete(7))
        fake.found = kotlin.copy(topic = "Flow", minutes = 60)
        assertTrue(planner.complete(7))
        assertEquals("Flow", fake.saved.last().topic)
        assertEquals(60, fake.saved.last().minutes)
        fake.found = null
        assertFalse(planner.complete(7))
        assertEquals(2, fake.saved.size)
    }

    @Test fun `planner and reader observe the same supplied in memory store`() {
        val store = InMemoryStore<StudySession> { it.id }
        val planner = StudyPlanner(store)
        planner.schedule(kotlin)
        planner.schedule(kmp)
        val before = store.all()
        assertTrue(planner.complete(7))
        assertEquals(listOf(kotlin.copy(completed = true), kmp), store.all())
        assertEquals(listOf(kotlin, kmp), before)
    }

    @Test fun `completion treats a negative id as a key rather than a position`() {
        val store = InMemoryStore<StudySession> { it.id }
        val session = kotlin.copy(id = -7)
        store.save(session)
        assertTrue(StudyPlanner(store).complete(-7))
        assertEquals(session.copy(completed = true), store.findById(-7))
    }

    @Test fun `report accepts a reader with no writing capability`() {
        class FixedReader : Reader<StudySession> {
            override fun all() = listOf(kmp)
            override fun findById(id: Int): StudySession? = null
        }
        assertEquals(45L, ProgressReport(FixedReader()).completedMinutes())
    }

    @Test fun `report returns zero for an empty source`() {
        assertEquals(0L, ProgressReport(FakeStore()).completedMinutes())
    }

    @Test fun `report ignores planned sessions and totals distinct completed sessions`() {
        val fake = FakeStore(entries = listOf(kotlin, kmp, StudySession(9, "Kotlin", 30, true)))
        assertEquals(75L, ProgressReport(fake).completedMinutes())
        assertEquals(emptyList(), fake.saved)
        assertEquals(listOf("all"), fake.events)
    }

    @Test fun `report includes both valid duration endpoints as Long values`() {
        val fake = FakeStore(entries = listOf(StudySession(1, "Kotlin", 1, true), StudySession(2, "Kotlin", 120, true)))
        assertEquals(121L, ProgressReport(fake).completedMinutes())
    }

    @Test fun `report reads again rather than caching a previous total`() {
        val fake = FakeStore(entries = listOf(kmp))
        val report = ProgressReport(fake)
        assertEquals(45L, report.completedMinutes())
        fake.entries = listOf(kotlin)
        assertEquals(0L, report.completedMinutes())
        fake.entries = emptyList()
        assertEquals(0L, report.completedMinutes())
        assertEquals(listOf("all", "all", "all"), fake.events)
    }

    @Test fun `report through a reader view reflects completion without changing input values`() {
        val store = InMemoryStore<StudySession> { it.id }
        store.save(kotlin)
        store.save(kmp)
        val report = ProgressReport(ReaderView(store))
        assertEquals(45L, report.completedMinutes())
        assertTrue(StudyPlanner(store).complete(7))
        assertEquals(75L, report.completedMinutes())
        assertFalse(kotlin.completed)
    }
}
