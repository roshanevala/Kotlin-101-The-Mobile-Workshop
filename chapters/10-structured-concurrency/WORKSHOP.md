# Workshop 10 Stop the obsolete search

Bug report: “I searched for ‘Ko,’ then ‘Kotlin.’ The latest result appeared, then the old result replaced it. Cancelling sometimes displays an error instead.” Build a small search session with explicit request ownership and replacement rules.

Read the [lesson](LESSON.md) first. Work in three sessions if needed: failure contracts, local deadlines, then job replacement and shared state.

## Sync, run, and open your copy

Keep **`kotlin-labs`** open in Android Studio. Use **Sync Project with Gradle Files** after updating the course and wait for indexing. This independent module reuses coroutines **1.11.0**, Kotlin **2.2.21**, Gradle **8.14.3**, and JDK **17**.

From its terminal:

```sh
./gradlew --console=plain :ch10-structured-concurrency:run
```

On Windows use `./gradlew.bat`. This creates your editable file:

```text
ch10-structured-concurrency/src/main/kotlin/workshop/ch10/SearchLifecycle.kt
```

Edit **`src/main` only**. Existing answers are preserved and ignored by Git. The [tracked starter](../../kotlin-labs/ch10-structured-concurrency/src/starter/kotlin/workshop/ch10/SearchLifecycle.kt) is a template; leave it and the fixed tests unchanged. Use the [setup guide](../../kotlin-labs/README.md) if needed. Browser execution requires a Kotlin editor that includes the coroutine library; the wrapper is the verified path.

The runnable starter prints:

```text
Old cancelled: false
Published queries: [Kotlin, Ko]
```

Compilation succeeded; the behavior is deliberately wrong. The old source waits at a signal. `main` lets the new search publish, then releases the old source and waits for it. No guessed delay determines which response is late.

## Predict and inspect the owners

Read `SearchSession` and its injected `CoroutineScope`, `SearchSource`, timeout, and publisher. The scope owns launched search jobs. The session remembers its active request; it does not own the caller's entire scope. Its callback receives outcomes, while the sample records only successful query names for a compact demonstration.

Predict what should happen to the old request, its cleanup, the new request, and the owner when a new query arrives. A `Job` returned by `submit` is a lifetime handle. Use the publisher for the outcome; `join()` does not return it.

## Agree on the contracts

- Preserve query text and the source's topic order and repeats. An empty topic list is a valid `Found` outcome; do not add parsing, trimming, deduplication, or caching.
- For this lab's source agreement, `IllegalStateException` is a recoverable source failure. Return `Failed(query, message)`, using `Search unavailable` only when its message is null. Preserve an explicitly empty message. Other exception types propagate.
- `CancellationException` must propagate, including when a caller is already cancelled. Do not turn it into failure content or timeout content. It is a subtype of `IllegalStateException`, so exception ordering matters.
- `searchWithin` validates a nonnegative timeout before source work. Zero returns `TimedOut` without calling the source, provided its caller is active. Its own deadline returns `TimedOut`; cancellation from its caller or a separate nested operation propagates. An already cancelled caller receives no normal outcome.
- Construct a session with a nonnegative timeout and an owner whose lifetime the caller controls. Each request uses that supplied scope and source. Source work must cooperate with cancellation and finish its cleanup; a cancellation request cannot forcibly interrupt arbitrary work.
- A replacement cancels and joins the previous job before starting the next source. After replacement returns, the obsolete job has completed. A request cancelled while waiting must not publish failure or success afterward. A non-suspending publisher callback that already began may finish while the join waits; the old job cannot publish again after that transition completes. Previously published results are not erased.
- `submit` and `stop` protect the entire active-job transition with the same mutex, including suspension while joining. Concurrent method calls are serialized by lock acquisition; “latest” means the last submission processed under that lock, not an assumed wall-clock ordering among simultaneous callers.
- `stop` cancels and joins this session's active request, clears the reference, and leaves the owner and other sessions alive. It can be called repeatedly, and later submissions are allowed while the owner remains active. If the caller itself is cancelled, these suspending methods may throw.

This is a lifecycle lab, not a general reentrant task scheduler. Source cleanup and publication must not synchronously wait for this session to submit or stop itself. The mutex protects the active-job reference; callers remain responsible for source and publisher state in the supplied context. The sample confines its mutable list to one thread.

## Mission 1 Recover expected errors while preserving cancellation

Complete `searchSafely(source, query)`. Check that the current coroutine is active before calling the source. Return `Found` for successful values. Rethrow cancellation first, translate only the agreed recoverable failure, and let unexpected types propagate.

A broad `catch (Exception)` also catches cancellation. Merely checking a returned job's `isCancelled` would miss a cancelled coroutine that improperly publishes a failure value afterward. The tests check both outcome and publication behavior.

## Mission 2 Apply this operation's deadline

Complete `searchWithin(source, query, timeoutMillis)`. Validate the timeout, check caller cancellation, and wrap `searchSafely` with `withTimeoutOrNull`. Convert only that wrapper's null result to `TimedOut`.

The block returns a non-null outcome, so null cannot mean a legitimate source result here. Do not catch every timeout exception; an inner operation may have its own deadline. A local timeout should finish its source cleanup before returning. This is cooperative cancellation, not preemptive thread termination.

## Mission 3 Replace active work as one protected transition

Add a private `Mutex` to `SearchSession`. Inside `submit`'s `withLock`, cancel and join the remembered job, launch the new request in the supplied `scope`, remember it, and return its handle.

The child obtains the bounded outcome, checks its current job is still active, then publishes it. Keep actual request execution asynchronous in that child; the method waits for obsolete cleanup, not for the new search to finish. Do not create a global or unrelated scope, hardcode another dispatcher, or replace the owner's failure policy.

Both an immediate successful outcome and a current request's expected failure or local timeout are publishable. Cancellation is not a publishable error state. Unexpected exceptions still follow the supplied owner's normal failure rules; supervision is a separate policy choice taught in the lesson.

## Mission 4 Stop only this session's work

Protect `stop` with the same mutex. Cancel and join the active job, then clear the reference. Do not cancel the entire supplied scope. Keep the transition protected until cleanup finishes; unlocking before the join would let another call replace or clear the wrong job.

After the missions, `run` should print:

```text
Old cancelled: true
Published queries: [Kotlin]
```

The old request was already waiting when replacement began. Its later release cannot publish it. Use the [progressive hints](HINTS.md) if needed.

## Check your work

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch10-structured-concurrency:test
```

The **32 acceptance tests** cover exact outcomes, error contracts, cancellation before and during work, local and nested timeouts, replacement ordering, cleanup waiting, serialized transitions, stop/restart, owner cancellation, independent sessions, and supplied execution context. Some deliberately fail on the starter. The HTML report is at `ch10-structured-concurrency/build/reports/tests/test/index.html`.

Most fixtures use signals to control progress. One deadline test uses a real timer against a source that waits until cancelled. Tests assert outcomes and cleanup, not exact milliseconds. The five-second guard catches hung implementations; it is not a performance budget. Fixtures using `NonCancellable` release their cleanup signal in `finally`. Virtual-time test tooling remains Chapter 12 material.

To restore the starter, first move your working `SearchLifecycle.kt` somewhere safe outside its source folder. Run `:ch10-structured-concurrency:preparePractice` to make a fresh copy. `clean` keeps this answer and earlier working files.

## Investigate the cancellation-as-content bug

In a scratch example from the lesson, replace the ordered catches with a broad `Exception` catch that returns a display string. Invoke it with a source that throws `CancellationException`. Observe that the cancellation became normal content. Restore the correct catch policy before continuing. In the session, checking active state before publication adds another cooperation checkpoint, but does not excuse a helper that violates its own cancellation contract.

Explain why supervising every search would not solve stale publication: supervision isolates failures, while replacement still needs ownership and cancellation rules.

## Explain and revisit

Explain in 60 seconds, or write your answer: “Why does replacement cancel and join, rather than cancel alone? Why can a mutex matter even when callers share a thread? Which failures should reach the publisher?” Include the difference between a local timeout and owner cancellation.

Changed request: after the latest sample job finishes but before releasing the old source, submit and join a third query, `KMP`. Predict all published query names. Does cancelling obsolete work erase the already published Kotlin result? Add the call, run, then restore the sample.

On another day, call `stop` while a request waits at a signal. Check that it publishes nothing, its cleanup finishes, and another session under the same owner can still complete. For optional supervision practice, adapt the lesson's peer example so one optional request fails, the other succeeds, and then repeat with an uncaught exception escaping the supervisor block. Keep these experiments separate from the required starter repairs.

Finish when the tests pass and you can explain cancellation without calling it a displayable error. Public sharing is optional.

**[Review the worked solution](SOLUTION.md)** · [Review the lesson](LESSON.md)
