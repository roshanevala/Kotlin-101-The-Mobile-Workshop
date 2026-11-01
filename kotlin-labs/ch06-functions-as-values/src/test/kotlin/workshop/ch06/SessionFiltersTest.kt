package workshop.ch06

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

// The same behavioral contracts run against the working copy and the separate reference.
class SessionFiltersTest {
    private val planned = StudySession("Android", 20, false)
    private val kotlin = StudySession("Kotlin", 30, true)
    private val kmp = StudySession("KMP", 45, true)
    private val sessions = listOf(planned, kotlin, kmp, StudySession("Flow", 15, false))

    @Test fun `combined rule implements the full AND truth table`() {
        for (first in listOf(false, true)) {
            for (second in listOf(false, true)) {
                assertEquals(first && second, allOf({ first }, { second })(kotlin))
            }
        }
    }

    @Test fun `constructing a combined rule does not invoke either callback`() {
        var calls = 0
        allOf({ calls += 1; true }, { calls += 1; true })
        assertEquals(0, calls)
    }

    @Test fun `combined rule checks first before second on the supplied session`() {
        val seen = mutableListOf<String>()
        val rule = allOf({ seen.add("first:${it.topic}"); true }, { seen.add("second:${it.topic}"); true })
        assertEquals(true, rule(kmp))
        assertEquals(listOf("first:KMP", "second:KMP"), seen)
    }

    @Test fun `combined rule skips second after first rejects`() {
        var secondCalls = 0
        assertEquals(false, allOf({ false }, { secondCalls += 1; true })(kotlin))
        assertEquals(0, secondCalls)
    }

    @Test fun `combined rule reads captured state on each invocation`() {
        var minimum = 30
        val rule = allOf({ it.completed }, { it.minutes >= minimum })
        assertEquals(true, rule(kotlin))
        minimum = 31
        assertEquals(false, rule(kotlin))
    }

    @Test fun `select handles empty input without calling the predicate`() {
        assertEquals(emptyList(), selectSessions(emptyList()) { error("No elements") })
    }

    @Test fun `select keeps all matches in encounter order including repeats`() {
        val input = listOf(kmp, kotlin, kmp)
        assertEquals(input, selectSessions(input) { true })
    }

    @Test fun `select can reject every session`() {
        assertEquals(emptyList(), selectSessions(sessions) { false })
    }

    @Test fun `select uses a caller supplied rule rather than a hardcoded status`() {
        assertEquals(listOf(planned), selectSessions(sessions) { it.topic == "Android" })
    }

    @Test fun `select visits each entry once in order`() {
        val seen = mutableListOf<StudySession>()
        selectSessions(sessions) { seen.add(it); true }
        assertEquals(sessions, seen)
    }

    @Test fun `selection is a snapshot and preserves the input`() {
        val input = mutableListOf(kotlin, kmp)
        val result = selectSessions(input) { true }
        assertEquals(listOf(kotlin, kmp), input)
        input.clear()
        assertEquals(listOf(kotlin, kmp), result)
    }

    @Test fun `label includes exact topic and duration endpoints`() {
        assertEquals(" Kotlin : 1 min", StudySession(" Kotlin ", 1, false).label())
        assertEquals("KMP: 120 min", StudySession("KMP", 120, true).label())
    }

    @Test fun `label does not depend on completion status`() {
        assertEquals(kotlin.label(), kotlin.copy(completed = false).label())
    }

    @Test fun `both previews handle empty input`() {
        assertEquals(emptyList(), eagerPreview(emptyList(), 1, { error("empty") }, { error("empty") }))
        assertEquals(emptyList(), lazyPreview(emptyList(), 1, { error("empty") }, { error("empty") }))
    }

    @Test fun `both previews return the first matching transformed entries`() {
        assertEquals(listOf("KOTLIN"), eagerPreview(sessions, 1, { it.completed }, { it.topic.uppercase() }))
        assertEquals(listOf("KOTLIN"), lazyPreview(sessions, 1, { it.completed }, { it.topic.uppercase() }))
    }

    @Test fun `both previews keep repeats and order when limit exceeds matches`() {
        val input = listOf(kmp, kotlin, kmp)
        val expected = listOf("KMP", "Kotlin", "KMP")
        assertEquals(expected, eagerPreview(input, 99, { true }, { it.topic }))
        assertEquals(expected, lazyPreview(input, 99, { true }, { it.topic }))
    }

    @Test fun `both previews return empty when no session matches`() {
        assertEquals(emptyList(), eagerPreview(sessions, 2, { false }, { error("rejected") }))
        assertEquals(emptyList(), lazyPreview(sessions, 2, { false }, { error("rejected") }))
    }

    @Test fun `both previews reject negative limits before invoking callbacks`() {
        for (limit in listOf(-1, Int.MIN_VALUE)) {
            assertFailsWith<IllegalArgumentException> {
                eagerPreview(sessions, limit, { error("must validate first") }, { error("must validate first") })
            }
            assertFailsWith<IllegalArgumentException> {
                lazyPreview(sessions, limit, { error("must validate first") }, { error("must validate first") })
            }
        }
    }

    @Test fun `negative limit is invalid even for empty input`() {
        assertFailsWith<IllegalArgumentException> { eagerPreview(emptyList(), -1, { true }, { it.topic }) }
        assertFailsWith<IllegalArgumentException> { lazyPreview(emptyList(), -1, { true }, { it.topic }) }
    }

    @Test fun `eager preview finishes filtering before transforming all matches`() {
        val trace = mutableListOf<String>()
        val result = eagerPreview(sessions, 1,
            { trace.add("filter:${it.topic}"); it.completed },
            { trace.add("map:${it.topic}"); it.topic })
        assertEquals(listOf("Kotlin"), result)
        assertEquals(listOf("filter:Android", "filter:Kotlin", "filter:KMP", "filter:Flow", "map:Kotlin", "map:KMP"), trace)
    }

    @Test fun `lazy preview interleaves callbacks and stops at its limit`() {
        val trace = mutableListOf<String>()
        val result = lazyPreview(sessions, 2,
            { trace.add("filter:${it.topic}"); it.completed },
            { trace.add("map:${it.topic}"); it.topic })
        assertEquals(listOf("Kotlin", "KMP"), result)
        assertEquals(listOf("filter:Android", "filter:Kotlin", "map:Kotlin", "filter:KMP", "map:KMP"), trace)
    }

    @Test fun `zero eager limit still filters and maps but returns no entries`() {
        var filters = 0
        var maps = 0
        assertEquals(emptyList(), eagerPreview(sessions, 0, { filters += 1; it.completed }, { maps += 1; it.topic }))
        assertEquals(4, filters)
        assertEquals(2, maps)
    }

    @Test fun `zero lazy limit invokes neither callback`() {
        assertEquals(emptyList(), lazyPreview(sessions, 0, { error("zero") }, { error("zero") }))
    }

    @Test fun `lazy preview scans all input if there are too few matches`() {
        var filters = 0
        var maps = 0
        assertEquals(listOf("Kotlin", "KMP"), lazyPreview(sessions, 3,
            { filters += 1; it.completed }, { maps += 1; it.topic }))
        assertEquals(4, filters)
        assertEquals(2, maps)
    }

    @Test fun `both previews preserve input and materialize snapshots`() {
        val input = sessions.toMutableList()
        val eager = eagerPreview(input, 2, { it.completed }, { it.topic })
        val lazy = lazyPreview(input, 2, { it.completed }, { it.topic })
        assertEquals(sessions, input)
        input.clear()
        assertEquals(listOf("Kotlin", "KMP"), eager)
        assertEquals(eager, lazy)
    }

    @Test fun `separate calls use fresh rules without retaining prior results`() {
        assertEquals(listOf("Kotlin"), lazyPreview(sessions, 1, { it.completed }, { it.topic }))
        assertEquals(listOf("Android"), lazyPreview(sessions, 1, { !it.completed }, { it.topic }))
        assertEquals(listOf("Android"), eagerPreview(sessions, 1, { !it.completed }, { it.topic }))
    }
}
