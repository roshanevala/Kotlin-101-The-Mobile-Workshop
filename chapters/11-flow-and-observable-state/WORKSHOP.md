# Workshop 11 One screen, two streams

Bug report: “The search box has no observable loading state, and completing a study session does not update the progress beside the results.” Build a cold search stream, combine independent progress, and share one current screen value.

Read the [lesson](LESSON.md) first. Work in three sessions if needed: query replacement, combining, then shared ownership.

## Sync, run, and open your copy

Keep **`kotlin-labs`** open in Android Studio. After updating the course, use **Sync Project with Gradle Files** and wait for indexing. This module uses Kotlin **2.2.21**, Gradle **8.14.3**, JDK **17**, and coroutines **1.11.0**.

From its terminal:

```sh
./gradlew --console=plain :ch11-flow-and-observable-state:run
```

On Windows use `./gradlew.bat`. Run creates your editable file:

```text
ch11-flow-and-observable-state/src/main/kotlin/workshop/ch11/SearchFlow.kt
```

Edit **`src/main` only**. Existing answers are preserved and ignored by Git. Leave the [tracked starter](../../kotlin-labs/ch11-flow-and-observable-state/src/starter/kotlin/workshop/ch11/SearchFlow.kt) and fixed tests unchanged. See the [setup guide](../../kotlin-labs/README.md) if needed. Browser execution needs an editor with the coroutine library; the wrapper is the verified route.

The runnable starter prints:

```text
Search: []
Progress: 0/0
```

This is a successful run of deliberately unfinished functions. The finite demo checks the cold helpers; acceptance tests also exercise the long-lived holder. The source is fake and needs no network.

## Predict the stream and its owner

Read `TopicSource`, `SearchState`, `StudyProgress`, and `SearchScreen`. A search can be `Idle`, `Loading(query)`, `Results(query, topics)`, or `Failed(query, message)`. Progress is a supplied completed/total pair; this mission neither validates nor recalculates it.

Predict how many source calls two independent cold collections make. Then predict how many two subscribers to the same shared state make. What should happen when progress changes while results remain the same?

## Agree on the contracts

- `searchStates` is cold. Building it validates a nonnegative `debounceMillis` immediately, but performs no input collection or source work. Each collection owns its own query processing. Empty input emits nothing.
- Trim query text, then suppress adjacent equal normalized values. Preserve case and internal spaces. A repeat after a different query may run again; repeating an equal normalized query, even after failure, is not a retry.
- Every processed distinct query replaces the previous branch. Cancel and wait for that branch's cooperative cleanup before starting the replacement's quiet period. Keep the delay inside `flatMapLatest`. A blank query cancels old work and emits `Idle` without a delay or source call; it can still wait for cleanup.
- For a nonblank query, wait the configured quiet period, emit `Loading`, invoke the source, then emit `Results` with its exact topic order, repeats, and empty list if returned. Zero skips waiting. Finite input still lets its last branch finish the configured wait and search.
- Only the source's agreed `IllegalStateException` becomes `Failed`. Use `Search unavailable` only for a null message; preserve a blank message. Rethrow cancellation first. Query-input, collector, and unexpected source failures are not recoverable source content.
- A source-created cancellation may stop only its latest child branch. It must never become failure content. Cancelling collection or its owner stops ongoing work and waits for cleanup. Cancellation is cooperative; a value already delivered is not erased.
- Apply `buffer(0)` after the latest operator to avoid queued output capacity. This keeps coroutine concurrency; it does not make keyboard input and publication atomic.
- `screenStates` is cold and combines the latest search and progress values. Wait for both rather than inventing defaults. Changes from either contribute a new combined value; do not restart search when progress changes or promise every intermediate pairing.
- `StudySearch` keeps a private `MutableStateFlow("")` query. `submit` assigns the raw text. Expose `StateFlow<SearchScreen>` using the two helpers and `stateIn` with the **supplied scope**, `SharingStarted.Eagerly`, and initial `SearchScreen(Idle, progress.value)`. Do not create a detached scope or replace the owner's dispatcher/failure policy.
- The owner controls shutdown. The hot state remains readable after shutdown but receives no more updates. Query and screen StateFlows can conflate fast changes and suppress equal values. Observers are entitled to current state, not a log of every submission or brief `Loading` value. `submit` updates desired query state; it does not wait for processing or restart a stopped owner.

Inputs and returned topic lists are treated as stable read-only values. Do not mutate them after publication. There is no event bus, timeout, retry action, caching, sorting, or mobile lifecycle policy to add.

## Mission 1 Build a cold query pipeline

Add `@OptIn(ExperimentalCoroutinesApi::class)` on `searchStates`. Validate first, then build with `map`, `distinctUntilChanged`, and `flatMapLatest`. Return an inner `flow<SearchState>` for each query; explicit type arguments are available if Kotlin needs help finding a common sealed parent type.

Handle blank before delay. For nonblank queries, put delay and loading before source work. Calculate the final outcome in a `try` around the source call, with cancellation rethrown before recoverable failure. Emit that outcome outside the `try`. Finish with `buffer(0)`.

Do not use a terminal `collect` inside this builder or launch a separate coroutine per query yourself. The latest operator owns replacement. A `debounce` before that boundary would delay cancellation of an already running old search.

## Mission 2 Combine independent progress

Complete `screenStates` using `combine(search, progress)`. Build one `SearchScreen` from the current pair. Do not collect into a mutable list, use `zip`, call the source again, or manufacture an initial value here. The shared holder supplies its own initial value separately.

## Mission 3 Share one screen state

Wire the private query into `searchStates`, combine with supplied progress, then call `stateIn`. Keep the public type read-only. Complete `submit` by assigning `query.value`.

`Eagerly` means source work can start after submission without a screen subscriber. Multiple subscribers reuse this one pipeline. A collector of StateFlow never finishes normally; use `first { ... }` for a matching state, a bounded `take`, or a job cancelled by its owner. In console experiments, use a dedicated child `Job` and always `cancelAndJoin` it in `finally`, as the lesson shows.

After the repairs, `run` prints:

```text
Search: [Kotlin]
Progress: 2/5
```

Use the [progressive hints](HINTS.md) if needed.

## Check your work

```sh
./gradlew --console=plain :ch11-flow-and-observable-state:test
```

The **36 acceptance tests** check cold execution, query normalization and duplicates, quiet-period cancellation, ordered states and values, expected recovery and later queries, unexpected failures, cancellation and cleanup, context inheritance, combination, eager sharing, multiple subscribers, progress updates, blank submission, and owner shutdown. Some fail deliberately on the starter. The report is at `ch11-flow-and-observable-state/build/reports/tests/test/index.html`.

The tests assert complete emissions on controlled cold flows and eventual matching values on shared state. Signals control ordering; short real delays exercise the quiet period. A five-second guard detects hangs, not performance. Virtual-time test tooling is taught in Chapter 12. Do not use guessed sleeps to wait for StateFlow to update.

To restore the starter, move your answer outside its source folder, then run `:ch11-flow-and-observable-state:preparePractice`. `clean` preserves your answer and earlier chapters.

## Investigate the blank-filter bug

In a temporary answer, add `filter { it.isNotEmpty() }` after trimming, before the latest operator. The running-search blank-submission test should fail: clearing the field never reaches replacement, so the old request is not cancelled. Restore blank handling inside the latest branch.

Explain a second tempting mistake: why does `toList()` on the holder's StateFlow keep waiting? Why might `take(3)` also wait forever if you assumed every query emits idle, loading, and results to every subscriber?

## Explain and revisit

Explain in 60 seconds, or write: “What is cold, what is shared, and who cancels it? Why is progress combined after search? Which previous work does `collectLatest` cancel compared with `flatMapLatest`?” Include why repeating equal state is different from emitting two equal SharedFlow events.

Changed request: change the finite demo's query to `"  KMP  "` and progress to `StudyProgress(3, 5)`. Predict both output lines, run, and restore. Then use the lesson's SharedFlow program with `replay = 1` and print `replayCache` immediately after the first emission. What is retained without a subscriber? Do not confuse replay with exactly-once handling.

On another day, build a fresh small holder without the solution: submit a query, await a matching result with `first`, update progress, verify the source did not run again, and cancel its dedicated owner. Keep this recall experiment separate from the acceptance fixture.

Finish when tests pass and you can explain why observable state can skip intermediate values. Public sharing is optional.

**[Review the worked solution](SOLUTION.md)** · [Review the lesson](LESSON.md)
