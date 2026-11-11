# Lesson 11 Flow and observable state

The study planner now has two things changing independently: search results and completed sessions. A single suspending call returns one answer. A stream can describe answers over time, and shared state can hold the screen's current answer.

Start after [Chapter 10](../10-structured-concurrency/LESSON.md). You should know lambdas, sealed results, cancellation, scopes, and injected dependencies. Split this lesson into cold streams and cancellation, then shared state. Each Kotlin block below is a complete program. Follow the [workshop setup](WORKSHOP.md#sync-run-and-open-your-copy). To run a block, keep your generated `SearchFlow.kt` safe outside its source folder, temporarily replace it with the block prefixed by `package workshop.ch11`, and run `:ch11-flow-and-observable-state:run`. Restore it before acceptance tests, which need the exercise declarations.

## A cold flow is a recipe

`Flow<T>` describes values of type `T` over time. The `flow` builder has a suspending block; `emit` sends a value downstream. A terminal operator such as `collect`, `first`, or `toList` starts collection. `map` changes each value; `filter` selects values, as with the collection operators from Chapter 06.

```kotlin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    var starts = 0
    val topics = flow {
        starts++
        emit(" Kotlin ")
        emit(" ")
        emit(" KMP ")
    }.map { it.trim() }.filter { it.isNotEmpty() }
    println("Before: $starts")
    topics.collect { println("First: $it") }
    topics.collect { println("Second: $it") }
    println("Starts: $starts")
}
```

Output:

```text
Before: 0
First: Kotlin
First: KMP
Second: Kotlin
Second: KMP
Starts: 2
```

Building this cold pipeline does no source work. Each collection executes the recipe again. A flow can finish, fail, or run until cancelled. `toList` needs completion; `first` returns after a matching value and stops its collection. [`flow`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/flow.html).

Normally, upstream operations and collection run sequentially in the collector's coroutine. Suspension still does not choose a thread. Do not put `emit` inside `withContext(Dispatchers.IO)` within a regular `flow`; that violates its emission context rule. `flowOn` changes upstream execution context when there is a reason to do so. Our already suspending fake source needs no dispatcher switch.

## Buffering permits overlap

A slow collector normally slows its producer. `buffer` introduces a coroutine boundary and a channel between them. With the default overflow policy, a full buffer suspends the producer; it does not discard values.

```kotlin
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    val secondSent = CompletableDeferred<Unit>()
    flow {
        emit(1)
        emit(2)
        secondSent.complete(Unit)
    }.buffer(1).collect { value ->
        if (value == 1) {
            secondSent.await()
            println("Producer sent second; collector still handling first")
        }
        println("Handled: $value")
    }
}
```

Output:

```text
Producer sent second; collector still handling first
Handled: 1
Handled: 2
```

The signal demonstrates overlap, not a speed measurement. Removing `buffer` makes this deliberately dependent example wait forever: the producer cannot reach its signal while handling `1` waits for it. Cancel the run if you try that variation. [`buffer`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/buffer.html).

`buffer(0)` retains the coroutine boundary with a rendezvous handoff and no queued element capacity. `conflate` instead lets a slow collector skip pending values in favor of the newest; it does not cancel work already handling an earlier value. Neither operator chooses a background thread. Buffer policy is a behavior choice, not an automatic performance fix.

## Latest means cancellation of a particular block

`collectLatest` cancels its previous **collector action** when the next value arrives. Here the old rendering action waits until cancelled; the next rendering action completes.

```kotlin
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    val started = CompletableDeferred<Unit>()
    flow {
        emit("old")
        started.await()
        emit("new")
    }.collectLatest { query ->
        println("Start: $query")
        if (query == "old") {
            try {
                started.complete(Unit)
                awaitCancellation()
            } finally {
                println("Stopped: $query")
            }
        }
        println("Finished: $query")
    }
}
```

Output:

```text
Start: old
Stopped: old
Start: new
Finished: new
```

This still needs cooperative cancellation. Blocking or CPU work without checks cannot be forcibly stopped. [`collectLatest`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/collect-latest.html).

| Operator | What happens to earlier work? |
| --- | --- |
| `map` | Transforms each value sequentially into one value |
| `mapLatest` | Cancels the previous transformation; produces one transformed value per completed action |
| `flatMapLatest` | Switches to a new inner flow and cancels the previous inner collection |
| `collectLatest` | Cancels the previous terminal collector action |

An inner flow lets one query produce several states: loading, then results or failure. Use `flatMapLatest` when replacement belongs to that query's whole lifetime. In coroutines 1.11.0 it requires `@OptIn(ExperimentalCoroutinesApi::class)`. The opt-in acknowledges that API status; it does not add a library.

## Put the quiet period inside the replaceable work

A debounce waits for a pause in changes. The library's `debounce` operator suppresses closely followed values and currently requires `FlowPreview`. However, placing `debounce` **before** `flatMapLatest` delays the next query reaching the cancellation boundary. An already running old search can finish during that pause.

Our mission chooses a stronger replacement rule: once a different normalized query reaches `flatMapLatest`, cancel the previous branch, wait for its cleanup, and then start the replacement's quiet period. A suspending `delay` inside the inner flow implements that rule. Another query can cancel the delay itself.

```kotlin
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    val oldStarted = CompletableDeferred<Unit>()
    val queries = flow {
        emit("old")
        oldStarted.await()
        emit("new")
    }
    val results = queries.flatMapLatest { query ->
        flow {
            delay(40L)
            if (query == "old") {
                try {
                    oldStarted.complete(Unit)
                    awaitCancellation()
                } finally {
                    println("Cleaned: old")
                }
            }
            emit(query)
        }
    }.buffer(0).toList()
    println("Results: $results")
}
```

Output:

```text
Cleaned: old
Results: [new]
```

[`flatMapLatest`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/flat-map-latest.html) has an output buffer by default; the following `buffer(0)` removes queued output capacity. It cannot retract a value already received. Replacement starts when the operator processes a value, not at a physical keyboard event. Cleanup and non-suspending actions already in progress can delay it.

For this mission, trim before `distinctUntilChanged`: `" Kotlin "` and `"Kotlin"` are the same adjacent query. That also means repeating a failed query does not retry it. A blank query must reach the latest boundary so it cancels old work, then emits idle without a quiet period. Filtering blank queries out first would leave the old search running. Idle can still wait for old cleanup.

A finite input's last branch finishes its delay and search. This differs from the library's [`debounce`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/debounce.html), which flushes the final pending value when its upstream completes. The lesson's timer values illustrate policy; they are not latency guarantees.

## Combine the latest values

Search and progress are independent streams. `combine` waits until both have a value, then builds output from their most recent values when either changes. It is not a history of every possible pairing, and it does not restart search merely because progress changed.

```kotlin
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    val firstSeen = CompletableDeferred<Unit>()
    val progress = flow {
        emit("2/5")
        firstSeen.await()
        emit("3/5")
    }
    combine(flowOf("Kotlin"), progress) { query, count -> "$query: $count" }
        .collect {
            println(it)
            firstSeen.complete(Unit)
        }
}
```

Output:

```text
Kotlin: 2/5
Kotlin: 3/5
```

A completed input can contribute its last value while another continues. With no value from an input there is no pair. Do not invent defaults inside the combining helper. [`combine`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/combine.html).

## StateFlow holds the current value

A cold flow is a recipe. A `StateFlow` is hot: it holds a current value independently of collectors. `MutableStateFlow` is the writable side; `asStateFlow` exposes a read-only view. `update` performs an atomic read-modify-write; its lambda should have no side effects because it may be evaluated again under contention.

```kotlin
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    val mutable = MutableStateFlow(0)
    val state = mutable.asStateFlow()
    val observed = async(start = CoroutineStart.UNDISPATCHED) { state.take(2).toList() }
    mutable.value = 0
    mutable.update { it + 1 }
    println("Observed: ${observed.await()}")
    println("Late reader: ${state.first()}")
}
```

Output:

```text
Observed: [0, 1]
Late reader: 1
```

Equal values are suppressed using `equals`. Slow collectors can skip quick updates and receive the newest value. Treat state as a snapshot, not an event log; replace immutable values rather than mutating a list held inside the same state object. Thread-safe state updates do not make the objects they contain thread-safe. `StateFlow` never completes normally: use `first`, `take`, or cancel a long-lived collector. [`StateFlow`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/).

## SharedFlow broadcasts with an explicit replay policy

`SharedFlow` is also hot but need not have an initial value or suppress equal emissions. Its replay and buffer settings define what subscribers can receive. The default mutable shared flow has neither replay nor extra capacity.

```kotlin
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    val events = MutableSharedFlow<String>()
    events.emit("Before subscriber")
    val received = async(start = CoroutineStart.UNDISPATCHED) { events.take(2).toList() }
    events.emit("Saved")
    events.emit("Saved")
    println("Received: ${received.await()}")
    println("Replay: ${events.replayCache}")
}
```

Output:

```text
Received: [Saved, Saved]
Replay: []
```

The first emission had no subscriber and was lost. Extra buffer capacity alone does not retain events for absent subscribers; replay does. A replayed event can be observed again after resubscription, so SharedFlow by itself does not promise durable or exactly-once delivery. Like StateFlow it never completes normally. [`SharedFlow`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-shared-flow/).

## Share one upstream with stateIn

`stateIn` collects one upstream pipeline in a supplied scope and exposes its latest value as state. `SharingStarted.Eagerly` starts it without waiting for subscribers. Its initial value is available immediately; subsequent values arrive asynchronously.

```kotlin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    val owner = Job(coroutineContext[Job])
    val scope = CoroutineScope(coroutineContext + owner)
    var starts = 0
    val state = flow {
        starts++
        emit(2)
        awaitCancellation()
    }.stateIn(scope, SharingStarted.Eagerly, 0)
    try {
        println("Initial: ${state.value}")
        println("First reader: ${state.first { it == 2 }}")
        println("Second reader: ${state.first()}")
        println("Upstream starts: $starts")
    } finally {
        owner.cancelAndJoin()
    }
}
```

Output:

```text
Initial: 0
First reader: 2
Second reader: 2
Upstream starts: 1
```

The local demo's single-threaded context keeps the initial print before the launched sharing coroutine runs. Do not generalize that scheduling order to every dispatcher. The owner must be cancelled: otherwise `runBlocking` would wait for its active child. The exposed value remains readable after the owner stops, but is no longer updated. [`stateIn`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/state-in.html).

`Lazily` waits for the first subscriber and then keeps sharing; `WhileSubscribed` ties upstream collection to subscriber presence, with configurable stopping/replay policies. We choose eager sharing explicitly for this console lab. Android lifecycle-aware collection and a screen's sharing policy come later.

## Recover at the right boundary

For this lab only, a source's `IllegalStateException` is a recoverable search failure. Rethrow `CancellationException` first. Calculate a result or failure around the source call, then emit that outcome **outside** the `try`. Never catch a failed downstream `emit` and attempt another emission: that violates Flow's exception transparency.

The [`catch` operator](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/catch.html) handles upstream failures, not downstream collector failures or cancellation used to stop collection. A recovery outside the entire query pipeline can emit a fallback and then finish; it does not automatically resume future query processing. Per-query recovery belongs inside the replaceable branch.

Each latest branch is a child coroutine. If the source itself throws cancellation, rethrowing cancels that branch without producing `Failed`; it need not cancel the outer flow. Cancelling the collector/owner instead cancels ongoing branches and waits for their cleanup. Unexpected failures escape a cold collection. After `stateIn`, they terminate its sharing coroutine according to the owner's policy; subscribers retain the last state rather than receiving that exception. Handle expected failures before sharing.

The mission combines these pieces: a cold query pipeline, independent progress, then one shared screen state. Hot query state may conflate rapid submissions; it represents the latest desired query, not every keystroke. Sharing may also skip brief loading states. No retry button, event delivery system, network, or mobile lifecycle is required here.

**[Build the search stream](WORKSHOP.md)** · [Back to the chapter](README.md)
