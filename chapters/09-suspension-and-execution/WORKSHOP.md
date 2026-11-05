# Workshop 09 Two requests, one dashboard

Bug report: “The dashboard waits for topics before starting the completed-minute request. Adding `suspend` did not make them overlap.” Build a sequential baseline, then a scoped concurrent version and a dispatcher-aware entry point.

Read the [lesson](LESSON.md) first. Work in two sessions if needed: suspension and the baseline, then concurrent results and context selection.

## Sync, run, and open your copy

Keep **`kotlin-labs`** open in Android Studio. Use **Sync Project with Gradle Files** after updating the course and wait for indexing. Chapter 09 adds `kotlinx-coroutines-core` **1.11.0** to its own module. First use may download that dependency. Kotlin **2.2.21**, Gradle **8.14.3**, and JDK **17** remain the course toolchain.

From the workspace terminal:

```sh
./gradlew --console=plain :ch09-suspension-and-execution:run
```

On Windows use `./gradlew.bat`. The command prepares your editable file:

```text
ch09-suspension-and-execution/src/main/kotlin/workshop/ch09/DashboardLoading.kt
```

Edit **`src/main` only**. Existing work is preserved and ignored by Git. The [tracked starter](../../kotlin-labs/ch09-suspension-and-execution/src/starter/kotlin/workshop/ch09/DashboardLoading.kt) is a template, not the file to edit. Use the [setup guide](../../kotlin-labs/README.md) for Java and wrapper help. A plain browser Kotlin editor may lack the coroutine library; use the documented Gradle path for the mission.

The unfinished starter prints:

```text
Sequential: [], minutes=0, peak=0
Concurrent: [], minutes=0, peak=0
```

The program compiled, but its placeholder baseline ignores the source. Missing imports, dependency failures, or compilation errors are setup or code errors rather than this expected output.

## Predict and inspect the experiment

Read `StudySource` and `Dashboard`. Both source methods suspend and return values; neither request needs the other's result. `main` creates a fresh fake source for each comparison and uses its default single-threaded `runBlocking` context.

The supplied fake increments an active count, waits with `delay`, and decrements in `finally`. Its largest active count is the peak. Predict which peaks should differ once both loaders work. This is a count of overlapping request lifetimes, not a stopwatch or a count of CPU threads.

The fixture's mutable counters are confined to this console experiment. Do not pass that counting fixture to a multi-threaded dispatcher. The loader APIs work with any source that honors its calling context; the supplied tests use safe recording and signal-based sources for dispatcher checks.

## Agree on the contracts

- Return exactly the source's topics and completed-minute value. Keep topic order, exact text, and repeats. Empty topics and `0L` are valid; totals are nonnegative `Long` values and may exceed the `Int` range.
- Make one call to each source method on a successful load. Do not invent cached data, deduplicate, sort, or parse the source's results.
- `loadSequential` completes the topics request before beginning the minutes request. If topics fails, do not request minutes.
- `loadConcurrent` starts both independent requests within the current operation before awaiting their results. With controlled sources that wait, both must be able to start before either is released. Do not promise which finishes first.
- Return only after obtaining both successful results. Propagate source exceptions; do not replace a failure with a partial or empty dashboard. The local scope owns children, including on failure. Detailed failure and cancellation policy comes in Chapter 10.
- The two loaders inherit the caller's dispatcher. `loadOn` instead uses its supplied dispatcher for the concurrent operation, then resumes its caller in the original context. It must not hardcode a dispatcher or create a separate unowned scope.

## Mission 1 Establish the sequential baseline

Implement `loadSequential(source)` using ordinary suspending calls in the specified order. Store the topics and minutes results in locals, then construct the dashboard. `suspend` allows these calls; no child coroutine is needed for this baseline.

Run again. Once this helper works, the starter's `loadConcurrent` still delegates to it, so both peaks should be **1**. The results can be right while the execution strategy is still wrong.

## Mission 2 Give independent requests separate children

Implement `loadConcurrent` with a local `coroutineScope`. Start a value-producing `async` child for each source method, keep both handles, then await and combine their results inside that scope.

Do not append `.await()` immediately to the first `async` before creating the second. Do not use `launch` and shared result variables to simulate returned values. Keep the supplied source rather than constructing a concrete replacement.

After this mission, `run` should print:

```text
Sequential: [Kotlin, KMP], minutes=75, peak=1
Concurrent: [Kotlin, KMP], minutes=75, peak=2
```

Both requests run in the inherited console context. Their waiting overlaps on one thread; this is not proof of parallel CPU work or a measured speedup.

## Mission 3 Honor execution chosen by the caller

Implement `loadOn(source, dispatcher)` using `withContext` and the concurrent loader. Return its dashboard result. A new thread is not a required observable behavior; using the supplied dispatcher context is.

The console sample intentionally compares only the first two loaders. Tests check this third helper with safe sources and more than one supplied dispatcher. A hardcoded Default dispatcher would fail the IO case; ignoring the parameter would fail the context check.

If needed, open one level of the [hints](HINTS.md).

## Check your work

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch09-suspension-and-execution:test
```

The **26 tests** cover payloads, order, repeat calls, empty results, Long boundaries, overlapping gated requests, waiting for both results, inherited and supplied contexts, and error propagation. The fresh starter intentionally fails some. The report is at `ch09-suspension-and-execution/build/reports/tests/test/index.html`.

The provided gates use `CompletableDeferred<Unit>` signals from the lesson. The fixture releases work explicitly, so tests do not compare durations. A five-second timeout is a deadlock guard for an implementation that never starts a required request; it is not an allowed performance budget. Advanced asynchronous testing and virtual time remain Chapter 12 topics.

To restore the starter, first move your working `DashboardLoading.kt` somewhere safe outside its source folder. Run `:ch09-suspension-and-execution:preparePractice` to create a fresh copy. `clean` preserves your answer and earlier chapter work.

## Investigate the correct result with the wrong execution

In your working copy, temporarily await the first child immediately, before creating the second. Predict the payload and peak, then run. Restore the concurrent version afterward. Explain why value-only assertions would miss this bug and why the supplied gate check catches it.

Do not replace waiting with `Thread.sleep`, nest `runBlocking` in the loaders, or use a detached global scope. Those changes defeat the execution and lifetime behavior being taught.

## Explain and revisit

Explain in 60 seconds, or write your answer: “Why doesn't `suspend` select a thread? How do `async`, `await`, and `coroutineScope` make these two requests one owned operation?” Distinguish concurrency from parallelism.

Change the fake's payload to topics `[Flow, Kotlin, Flow]` and total `0L`. Predict both dashboards and peaks before running. Restore the sample afterward.

On a later day, temporarily change the fake's `delay(milliseconds)` to `delay(0L)`. In this inherited single-threaded console context, predict the peak of each loader. Does `async` promise overlap when neither request actually waits? Explain before running, then restore the original delay. Keep this experiment out of the acceptance contract for arbitrary sources.

Finish when all tests pass, you can trace both loading strategies, and you can explain the dispatcher parameter without relying on thread names. Public sharing is optional.

**[Review the worked solution](SOLUTION.md)** · [Review the lesson](LESSON.md)
