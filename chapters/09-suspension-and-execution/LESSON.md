# Lesson 09 Suspension and execution

Your dashboard needs a topic list and a completed-minute total. It waits for one request before starting the other, although neither result depends on the other. Before changing the loader, distinguish three ideas: a computation that can pause, a scope that owns work, and a dispatcher that controls execution.

Read after [Chapter 08](../08-tests-and-kotlin-pitfalls/LESSON.md). Split this lesson into suspension and scopes, then results and dispatchers. Each Kotlin block is a complete console program. Local execution needs this chapter's coroutine dependency; follow the [setup steps](WORKSHOP.md#sync-run-and-open-your-copy). A browser editor must provide `kotlinx.coroutines`; do not treat a missing import as an exercise failure.

For a local experiment, keep your generated `DashboardLoading.kt` safe outside its source folder, temporarily replace its contents with one complete block, and use `:ch09-suspension-and-execution:run`. Each block intentionally omits a package, so prepend `package workshop.ch09` before the block's imports and declarations. Restore the original file afterward. The separate acceptance tests expect the original public declarations; restore them before running `test`.

## Allow suspension, then call functions in order

```kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

suspend fun readTopic(): String {
    delay(1L)
    return "Kotlin"
}

fun main() = runBlocking {
    println("Before request")
    val topic = readTopic()
    println(topic)
    println("After request")
}
```

Output:

```text
Before request
Kotlin
After request
```

`suspend` permits a function to suspend and call other suspending functions. It neither starts another coroutine nor chooses a background thread. Calls in one coroutine remain sequential. `delay` models waiting without holding the executing thread occupied for that wait. A suspending function need not actually suspend on every call. The language supplies `suspend`; the coroutine builders and `delay` come from the library. See [coroutine basics](https://kotlinlang.org/docs/coroutines-basics.html).

`runBlocking` bridges this console's ordinary `main` into suspending code. It blocks the invoking thread until its body and children finish, while its default event loop can execute coroutines on that thread. It is appropriate at this console boundary. Inside suspending application code, use suspending operations and scopes rather than nesting `runBlocking`. See [`runBlocking`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/run-blocking.html).

## Blocking occupies the thread; suspension lets it serve other work

```kotlin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun observe(blocking: Boolean): List<String> = coroutineScope {
    val events = mutableListOf<String>()
    val child = launch { events.add("child") }
    events.add("before")
    if (blocking) Thread.sleep(10L) else delay(10L)
    events.add("after")
    child.join()
    events.toList()
}

fun main() = runBlocking {
    println(observe(blocking = true))
    println(observe(blocking = false))
}
```

Output:

```text
[before, after, child]
[before, child, after]
```

Here the child inherits the single-threaded console context. `launch` starts a child coroutine and returns a `Job`, a handle for its lifetime. `join()` suspends until that child completes; it does not retrieve a result value. While `Thread.sleep` blocks this thread, the queued child cannot execute there. During positive `delay`, it can. The sleeps are a small JVM-only comparison, not the mission's implementation strategy.

Concurrency means work can make progress during overlapping lifetimes. Parallelism means execution happens at the same instant on multiple threads. This program demonstrates concurrency on one thread. Do not infer parallelism from two coroutines. See [threads and coroutine execution](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html).

## Suspension alone does not select another thread

```kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

suspend fun stillOn(caller: Thread): Boolean {
    delay(1L)
    return Thread.currentThread() === caller
}

fun main() = runBlocking {
    val caller = Thread.currentThread()
    println("Same caller thread: ${stillOn(caller)}")
}
```

Output:

```text
Same caller thread: true
```

This is a JVM observation of the inherited `runBlocking` context, not a promise for every suspending function. A different context or API may resume on another thread. Expensive CPU work or a blocking call inside a `suspend` function still needs an appropriate execution choice. Avoid treating the keyword itself as offloading.

## Let a scope own its children

```kotlin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun announce() = coroutineScope {
    launch {
        delay(1L)
        println("Child finished")
    }
    println("Scope body finished")
}

fun main() = runBlocking {
    announce()
    println("Function returned")
}
```

Output:

```text
Scope body finished
Child finished
Function returned
```

`coroutineScope` supplies a `CoroutineScope` receiver, so builders such as `launch` and `async` are available in its block. That receiver syntax builds on Chapter 06. It inherits the caller's context and suspends until the body and its children complete. No detached work is needed. If a child fails, this scope fails, cancels its other children, and rethrows to its caller. We will study cancellation and failure policies in Chapter 10. See [`coroutineScope`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html).

Use a local scope when several requests form one suspending operation. Do not create a global or unrelated scope to make the function return before its work is finished.

## Use `async` when a child produces a result

```kotlin
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

suspend fun combinedMinutes(): Int = coroutineScope {
    val first = async {
        delay(20L)
        30
    }
    val second = async {
        delay(40L)
        45
    }
    first.await() + second.await()
}

fun main() = runBlocking {
    println("Completed minutes: ${combinedMinutes()}")
}
```

Output:

```text
Completed minutes: 75
```

`async` creates a child and returns `Deferred<T>`, a handle for an eventual result of type `T`. `await()` obtains it, suspending if necessary. Create both children before waiting for either. Awaiting the first does not stop the second child from making progress. Independent requests can overlap; a request whose arguments depend on an earlier result must still wait. See [composing suspending functions](https://kotlinlang.org/docs/composing-suspending-functions.html).

## Find the accidental serialization

```kotlin
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

suspend fun serialized(): List<String> = coroutineScope {
    val events = mutableListOf<String>()
    val first = async {
        events.add("first:start")
        delay(1L)
        events.add("first:end")
        30
    }.await()
    val second = async {
        events.add("second:start")
        delay(1L)
        events.add("second:end")
        45
    }.await()
    println("Total: ${first + second}")
    events.toList()
}

fun main() = runBlocking {
    println(serialized())
}
```

Output:

```text
Total: 75
[first:start, first:end, second:start, second:end]
```

The result is correct, but the second child is created only after the first has finished. Merely seeing `async` in the file does not establish overlap. `launch` is also not a substitute for a value-returning request: its `Job` has no dashboard payload to await. Keep child result handles and combine their values inside their owning scope.

## Select execution with a dispatcher

```kotlin
import kotlin.coroutines.ContinuationInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main() = runBlocking {
    val caller = Thread.currentThread()
    val result = withContext(Dispatchers.Default) {
        val dispatcher = currentCoroutineContext()[ContinuationInterceptor]
        println("Uses Default: ${dispatcher === Dispatchers.Default}")
        6 * 7
    }
    println("Result: $result")
    println("Back on caller: ${Thread.currentThread() === caller}")
}
```

Output:

```text
Uses Default: true
Result: 42
Back on caller: true
```

A coroutine context holds execution and lifetime information. On this JVM example, indexing it with `ContinuationInterceptor` reads the dispatcher used here; you do not need to implement that interface. `withContext` updates the context for a block, waits for it, and returns its result. Afterward the caller continues in its original context. It does not launch detached work. See [`withContext`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/with-context.html).

`Dispatchers.Default` suits CPU work. On the JVM, `Dispatchers.IO` is designed for blocking I/O. Moving blocking work there does not turn that API into nonblocking suspension. A dispatcher selects execution resources, not necessarily a fresh physical thread; Default and IO can share workers. An already suspending source does not automatically need IO. See [dispatcher contexts](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html) and [`Dispatchers.IO`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-i-o.html).

The mission receives a dispatcher through a parameter, following Chapter 07's injection pattern. Use the supplied value rather than hardcoding Default or IO. Console setup does not supply an Android Main dispatcher; UI integration comes later.

## Observe overlap with controlled signals

```kotlin
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val firstStarted = CompletableDeferred<Unit>()
    val secondStarted = CompletableDeferred<Unit>()
    val release = CompletableDeferred<Unit>()
    val first = async {
        firstStarted.complete(Unit)
        release.await()
        "Kotlin"
    }
    val second = async {
        secondStarted.complete(Unit)
        release.await()
        "KMP"
    }
    firstStarted.await()
    secondStarted.await()
    println("Both waiting")
    release.complete(Unit)
    println(listOf(first.await(), second.await()))
}
```

Output:

```text
Both waiting
[Kotlin, KMP]
```

`CompletableDeferred<Unit>` is a one-shot signal here: `complete(Unit)` makes it ready, and `await()` suspends until ready. Both requests must start before the caller releases them. This observes overlap without guessing how many milliseconds a machine needs. The supplied tests use this idea; detailed asynchronous testing and virtual time arrive in Chapter 12.

The workshop's console fixture instead counts active requests around a small `delay`. Its `try/finally` decrements the count when leaving that block, including on an exception. `private set` lets the fixture update its peak while callers only read it. That counter fixture is confined to the console's inherited single-threaded context; it is not a thread-safe source for arbitrary dispatchers. The production loaders must also work with other supplied sources.

## Check your understanding

1. Why are two ordinary suspending calls sequential by default?
2. What can execute on the same thread while a coroutine is suspended? What changes during `Thread.sleep`?
3. How do the results of `launch` and `async` differ?
4. Why start both requests before awaiting either result?
5. Who owns the children in a `coroutineScope`, and when can the function return?
6. Why can a test prove overlapping requests without proving a speedup or parallel CPU execution?
7. Why does adding `suspend` not make a blocking function safe for a UI thread?

**[Continue to the workshop](WORKSHOP.md)** · [Chapter entry](README.md)
