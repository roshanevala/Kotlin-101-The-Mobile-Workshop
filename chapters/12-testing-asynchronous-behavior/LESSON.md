# Lesson 12 Testing asynchronous behavior

“It passed after I added a sleep” is an uncertain search test. Was the request finished, or was the machine simply fast enough today? A test scheduler lets you stop before a retry deadline, inspect the state, then execute the deadline deliberately.

Start after [Chapter 11](../11-flow-and-observable-state/LESSON.md) and recall Chapter 08's arrange → act → assert and red-before-green practice. You should recognize cancellation, scopes, injected dependencies, and Flow. This lesson adds virtual time and explicit scheduling; it does not change production time or prove that code is safe on every real thread.

## Run examples as tests

Follow the [workshop setup](WORKSHOP.md#sync-run-and-open-both-copies). Coroutines **1.11.0** now has a matching **test-only** `kotlinx-coroutines-test` dependency. Production code still uses only core coroutines. Do not import test utilities into `src/main` or use them as an application runtime.

Each Kotlin block is a complete test class. To try one, keep your generated `src/practiceTest/kotlin/workshop/ch12/AsyncPracticeTest.kt` safe outside its source folder, temporarily replace it with the block prefixed by `package workshop.ch12`, and run from `kotlin-labs`:

```sh
./gradlew --console=plain :ch12-testing-asynchronous-behavior:test --tests 'workshop.ch12.LessonExampleTest'
```

Restore your test file before the mission. Each block has one test; all six should pass independently against the runnable starter because their examples do not depend on its unfinished functions. The test report is at `ch12-testing-asynchronous-behavior/build/reports/tests/test/index.html`. There is no console `main` in these examples.

## Virtual time belongs to a scheduler

`runTest` creates a test scope with a `TestCoroutineScheduler`. Delays on linked test dispatchers are scheduled in virtual milliseconds. When the test suspends waiting for work, the scheduler can advance automatically. Assertions can inspect its clock through `currentTime`.

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class LessonExampleTest {
    @Test fun `a suspending call advances virtual time`() = runTest(timeout = 5.seconds) {
        assertEquals(0L, currentTime)
        delay(5_000L)
        assertEquals(5_000L, currentTime)
    }
}
```

The clock moves by 5,000 virtual milliseconds without sleeping for that duration. This is not a performance benchmark. `timeout = 5.seconds` is a **real-time hang guard**, separate from the virtual clock. Do not wrap the test in `withTimeout(5_000L)` and expect the same meaning: that deadline runs on its coroutine context and can become virtual. [`runTest`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/run-test.html).

The test body and ordinary test-dispatcher children share a thread. Children need a chance to run. `StandardTestDispatcher` queues work rather than eagerly entering a new `launch`. Suspending through `join`/`await`, or explicitly controlling the scheduler, gives it that chance.

## Inspect before a boundary, then run the boundary

| Operation | What it does |
| --- | --- |
| `runCurrent()` | Runs queued work at the current virtual time without advancing the clock |
| `advanceTimeBy(n)` | Moves time forward and runs scheduled work strictly before the target instant |
| `advanceUntilIdle()` | Runs available scheduled work, advancing time as needed, until idle |
| `currentTime` | Reads the scheduler's virtual milliseconds |

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class LessonExampleTest {
    @Test fun `queued work and exact deadlines are separate`() = runTest(timeout = 5.seconds) {
        val steps = mutableListOf<String>()
        launch {
            steps.add("started")
            delay(100L)
            steps.add("ready")
            delay(50L)
            steps.add("finished")
        }
        assertEquals(emptyList(), steps)
        runCurrent()
        assertEquals(listOf("started"), steps)
        advanceTimeBy(99L); runCurrent()
        assertEquals(listOf("started"), steps)
        advanceTimeBy(1L)
        assertEquals(100L, currentTime)
        assertEquals(listOf("started"), steps)
        runCurrent()
        assertEquals(listOf("started", "ready"), steps)
        advanceUntilIdle()
        assertEquals(listOf("started", "ready", "finished"), steps)
        assertEquals(150L, currentTime)
    }
}
```

A check at exactly 100 sees `ready` only **after** running current work. Advancing the clock and executing its target instant are distinct operations. This boundary is documented by [`advanceTimeBy`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/advance-time-by.html).

Do not immediately call `await()` if your next assertion is meant to inspect the state before a deadline. Waiting can advance the scheduler until completion and hide early or late behavior. Likewise, `advanceUntilIdle` is useful for final results, but can skip the intermediate states a timing regression must inspect.

## Inject dispatchers that share one clock

A test scheduler controls only dispatchers linked to it. A hardcoded `Dispatchers.Default` or `IO` uses real scheduling; its delays are not skipped by the test clock. Pass dispatchers into code that selects execution context, then use `StandardTestDispatcher(testScheduler)` in tests.

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext

suspend fun loadLabel(dispatcher: CoroutineDispatcher): String = withContext(dispatcher) {
    delay(250L)
    "Ready"
}

@OptIn(ExperimentalCoroutinesApi::class)
class LessonExampleTest {
    @Test fun `injected dispatchers use the same scheduler`() = runTest(timeout = 5.seconds) {
        val first = StandardTestDispatcher(testScheduler, "first")
        val second = StandardTestDispatcher(testScheduler, "second")
        assertEquals("Ready", loadLabel(first))
        assertEquals("Ready", loadLabel(second))
        assertEquals(500L, currentTime)
    }
}
```

These are two dispatchers sharing one scheduler, not two real worker threads. Creating an unrelated `TestCoroutineScheduler` would create another clock; linked test work must use the same scheduler. [`StandardTestDispatcher`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-standard-test-dispatcher.html), [`TestCoroutineScheduler`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-coroutine-scheduler/).

The mission explicitly injects a dispatcher for source work and retry backoff. Its Flow quiet period stays in the collector's context. A suspending API does not inherently require a context switch; injection here makes a deliberate execution boundary testable. There is no Android `Main` dispatcher in this console lab, so global `setMain`/`resetMain` is unnecessary.

`UnconfinedTestDispatcher` enters top-level child work eagerly. That can help tests focused on values, but changes scheduling assumptions and does not supply general ordering guarantees. Keep the standard dispatcher for this mission's scheduling regressions rather than changing dispatcher to make an assertion pass. [`UnconfinedTestDispatcher`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-unconfined-test-dispatcher.html).

## Catch expected failures inside their child

An ordinary failed `async` child can cancel its parent before `await()` is caught. If failure is the expected result, assert it **inside** that child. The child can then return the caught exception as a successful test value.

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class LessonExampleTest {
    @Test fun `expected failure does not cancel the test parent`() = runTest(timeout = 5.seconds) {
        val pending = async {
            assertFailsWith<IllegalStateException> {
                delay(100L)
                throw IllegalStateException("offline")
            }
        }
        runCurrent()
        assertFalse(pending.isCompleted)
        advanceTimeBy(100L)
        assertFalse(pending.isCompleted)
        runCurrent()
        assertEquals("offline", pending.await().message)
    }
}
```

Direct suspending calls can be wrapped directly in `assertFailsWith`. Use `assertEquals` for values and relevant failure messages; do not require exception object identity across coroutine boundaries. Unhandled child exceptions remain test failures—virtual time does not suppress them.

## Schedule a replacement rather than guess a sleep

The next example controls the interleaving: old starts at 0, new replaces it at 100, and only new can emit at 300. The shared input represents the latest desired value, so we let queued work process each deliberate update with `runCurrent`.

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class LessonExampleTest {
    @Test fun `replacement cancels the obsolete delayed branch`() = runTest(timeout = 5.seconds) {
        val input = MutableStateFlow("old")
        val values = mutableListOf<String>()
        val stopped = mutableListOf<String>()
        val collector = backgroundScope.launch {
            input.flatMapLatest { value ->
                flow {
                    try { delay(200L); emit(value) }
                    finally { stopped.add(value) }
                }
            }.buffer(0).toList(values)
        }
        runCurrent(); advanceTimeBy(100L)
        input.value = "new"; runCurrent()
        assertEquals(listOf("old"), stopped)
        advanceTimeBy(199L); runCurrent()
        assertEquals(emptyList(), values)
        advanceTimeBy(1L)
        assertEquals(emptyList(), values)
        runCurrent()
        assertEquals(listOf("new"), values)
        collector.cancelAndJoin()
    }
}
```

`backgroundScope` is for work that should outlive the test's immediate operations; it is cancelled automatically at test end. We also cancel this collector explicitly because its lifetime is part of the example. `advanceUntilIdle` stops advancing once only background work remains, so drive this background timeline with explicit time advancement and current-work execution. [`backgroundScope`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-scope/background-scope.html).

A replay-zero SharedFlow input would need an active collector before emission; run queued collector startup first. These tests use StateFlow input and intentionally process each update. They do not promise to record every real keystroke or every possible concurrent interleaving.

## Current state is not every intermediate event

A StateFlow test that demands all rapid intermediate values can be testing the wrong contract. This standard-dispatcher example deliberately makes two updates before the collector resumes.

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class LessonExampleTest {
    @Test fun `a slow state observer can skip an intermediate value`() = runTest(timeout = 5.seconds) {
        val state = MutableStateFlow(0)
        val observed = mutableListOf<Int>()
        var cleaned = false
        val collector = backgroundScope.launch {
            try { state.toList(observed) } finally { cleaned = true }
        }
        runCurrent()
        state.value = 1
        state.value = 2
        runCurrent()
        assertEquals(listOf(0, 2), observed)
        assertEquals(2, state.value)
        collector.cancelAndJoin()
        assertTrue(cleaned)
    }
}
```

Use complete emission sequences for controlled cold streams when that is the contract. Use current/eventual state for hot screen holders. Advancing time cannot make a non-completing flow finish; `toList` on StateFlow needs a collector lifetime, `take`, or cancellation.

## Write a regression that can be wrong

For each mission behavior, write one test before changing that part of the starter. Earlier missions may already have repaired another part; follow the workshop's staged order. Run **only that named test**, read its expected/actual difference, and keep the failure as evidence. Then repair production code and rerun. An assertion-free test or one that only inspects the final result can miss a scheduling defect.

For retries, `maxAttempts` includes the first call. A source that fails twice with a 100 ms backoff should be called at virtual times 0, 100, and 200—not three calls at 0, and no extra wait after the final failure. Check just before and at the boundary, then execute its queued work. Retry only the agreed source failure, preserve cancellation, and check active state before each attempt: `delay(0)` does not suspend or check cancellation for you.

For replacement, arrange an old query in retry backoff, submit a new query, and cross the old deadline deliberately. Assert which query was called and which states were emitted. Do not test private job fields or count implementation operators. You are demonstrating an observable schedule that catches a known defect; single-threaded virtual scheduling is not proof against every real multicore data race.

Keep ordinary source agreement checks as well as timing regressions. A test that skips real time still needs exact values, meaningful boundary cases, cancellation cleanup, and a controlled lifetime. Uncooperative blocking code can outlast a test's attempted cancellation, so the hang guard is not a way to forcibly interrupt arbitrary work.

**[Write the search regressions](WORKSHOP.md)** · [Back to the chapter](README.md)
