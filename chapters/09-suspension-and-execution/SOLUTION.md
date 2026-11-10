# Solution 09 Start independent work before waiting for its results

Try the [workshop](WORKSHOP.md) before comparing with the [reference implementation](../../kotlin-labs/ch09-suspension-and-execution/src/solution/kotlin/workshop/ch09/DashboardLoading.kt). Keep your answer in `src/main`; the reference compiles separately.

## A suspending function is sequential until we choose otherwise

`loadSequential` calls `fetchTopics`, then `fetchCompletedMinutes`, then constructs the dashboard. It does not create children or select another dispatcher. A topics failure exits before requesting minutes. A valid empty list or zero total is returned as supplied.

The `suspend` modifier enables suspending calls; it does not turn this straight-line function into independent requests or offload blocking work. The provided console bridge and current context determine execution.

## Own both result-producing children

`loadConcurrent` creates a `coroutineScope`, starts both `async` children, then constructs the dashboard from `topics.await()` and `minutes.await()`. Both handles exist before waiting for either value.

The local scope inherits the caller's dispatcher and waits for its body and children. It propagates failure rather than returning an empty success. It does not create a detached scope whose work could outlive the operation. The full rules for cancellation and failure recovery are next chapter's subject.

| Strategy | Request lifetimes in the supplied console fake | Returned payload |
| --- | --- | --- |
| Sequential | Topics finishes, then minutes begins; peak 1 | `[Kotlin, KMP]`, 75 |
| Concurrent | Both can wait before either finishes; peak 2 | `[Kotlin, KMP]`, 75 |

The peak is a count of active requests, not a thread count. This console comparison demonstrates overlapping waits. It does not measure elapsed time or establish a performance improvement for every source.

## Run in the supplied context and return to the caller

`loadOn` returns `withContext(dispatcher) { loadConcurrent(source) }`. The two requests inherit the updated dispatcher context inside the block. The caller continues in its original context after completion.

No fixed worker name or newly created physical thread is part of the contract. Default and IO can share JVM workers. The injected dispatcher makes the choice explicit and testable; `suspend` alone makes no such choice. An already suspending source does not automatically require IO.

## Bug Museum The `async` calls that never overlapped

**Symptom:** both payloads are correct, but the concurrent fixture still has peak 1. A gated request prevents the second from starting.

**Cause:** `.await()` was appended to the first `async` before the second child was created. The first request finishes while the second does not yet exist.

**Repair:** keep both deferred handles, then await their results. A local scope owns the complete operation.

**Useful check:** hold the first request at a signal and check that the second can start before release. Do not assert a particular millisecond speedup or child finish order. Correct data alone would miss serialization.

## Verify the reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch09-suspension-and-execution:runSolution :ch09-suspension-and-execution:checkSolution
```

Output:

```text
Sequential: [Kotlin, KMP], minutes=75, peak=1
Concurrent: [Kotlin, KMP], minutes=75, peak=2
```

All **26 tests** are shared: `test` checks your working copy and `checkSolution` checks the separate reference. Their controlled sources verify order, overlap, results, context inheritance, supplied dispatchers, and exceptions. The timeout is a deadlock guard rather than a timing assertion. Tests using Default or IO use safe source fixtures, not the console's confined mutable counter.

## Change request answers

With topics `[Flow, Kotlin, Flow]` and total `0L`, both loaders preserve the repeated Flow entry and return zero minutes. The delayed console fixture still produces peaks **1** and **2**. Returning zero does not mean the request should be skipped.

With `delay(0L)` instead, neither fake request suspends at that call. In the sample's inherited single-threaded context both peaks become **1**, even though the correct concurrent implementation still starts two children before awaiting results. Concurrent structure allows overlap when work waits; it does not manufacture waiting or guarantee parallelism. Restore the original fake before checking the documented baseline.

**[Next: Chapter 10 Structured concurrency](../10-structured-concurrency/LESSON.md)** introduces cancellation, timeouts, failure policies, and obsolete-search replacement.

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
