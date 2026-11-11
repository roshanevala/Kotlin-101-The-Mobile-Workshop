# Chapter 11 Worked solution

Attempt the [workshop](WORKSHOP.md) before reading the [reference implementation](../../kotlin-labs/ch11-flow-and-observable-state/src/solution/kotlin/workshop/ch11/SearchFlow.kt).

## Replace the quiet period and request together

The helper validates the duration outside the cold recipe. Its source work remains inside the inner flow, so construction is inert and each collector gets independent work. Trimming precedes adjacent deduplication; case and internal spaces remain meaningful.

`flatMapLatest` replaces a whole query branch. The branch contains its delay, loading state, and source call. Replacement cancels and waits for previous cleanup before the next branch begins. Blank is a real replacement which emits idle without waiting a quiet period or calling the source. It still waits for old cleanup. `buffer(0)` removes queued output capacity, not the operator's coroutine boundary or values already delivered.

The implementation deliberately uses an inner delay rather than placing the library's `debounce` before replacement. Otherwise an old running search would remain alive until a new debounced value arrived. A finite query input waits for its final inner branch rather than flushing the delay early.

## Recover only the source agreement

Loading is emitted before the source call. The `try` produces either exact results or the agreed recoverable failure. Cancellation is rethrown first because it is an `IllegalStateException` subtype. Only null messages use the fallback; empty messages and empty successful lists remain valid values.

The final emission is outside recovery. Collector exceptions must escape rather than trigger another emission. Recovery happens per query so a recoverable failure does not end later query processing. Unexpected failures follow normal coroutine failure rules. A source-created cancellation can end just that child branch; cancelling collection or the owner stops the work it owns.

## Combine before sharing

`screenStates` uses `combine` without defaults: it needs a value from each input and uses their latest values. Progress updates do not touch query state or restart source work. Rapid combinations are not an event history.

`StudySearch` owns only private query state. One eager `stateIn` in the caller's scope shares the combined pipeline across subscribers. Its initial screen is idle with the supplied current progress. `submit` assigns raw query text; normalization remains in the reusable cold helper.

Equal normalized queries do not retry failures. Query StateFlow can skip rapid submissions, and screen StateFlow can skip brief loading states. That is the chosen current-state contract. An explicit retry action or durable event delivery would require a separate design.

The caller owns shutdown. Stopping its scope stops sharing and search cleanup; the final screen remains readable. Unexpected upstream failures terminate the sharing coroutine under the supplied owner policy rather than being thrown by state subscribers. This is why expected failures become states before sharing. Android collection lifecycle and subscriber-driven sharing policies come later.

## Run and check the reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch11-flow-and-observable-state:runSolution
./gradlew --console=plain :ch11-flow-and-observable-state:checkSolution
```

Expected console output:

```text
Search: [Kotlin]
Progress: 2/5
```

The **36 shared acceptance tests** apply to both implementations: `test` checks your working copy, while `checkSolution` checks the isolated reference. Fixtures use signals, bounded real quiet periods, and a deadlock guard. They assert exact cold emissions where controlled and eventual matching hot state rather than requiring every intermediate hot emission. Chapter 12 introduces virtual-time testing.

## Change request answer

The changed finite demo prints `Search: [KMP]` and `Progress: 3/5`. The query is trimmed, and progress is combined without recalculation.

With `MutableSharedFlow<String>(replay = 1)`, printing the replay cache immediately after the first emission gives `[Before subscriber]`. A new subscriber first receives that retained value. In the original `take(2)` example it then receives the first `Saved`, so its result becomes `[Before subscriber, Saved]`; the later `Saved` remains the newest replay value. Replay changes observation after subscription, not exactly-once processing.

**Next: Chapter 12 will introduce testing asynchronous behavior, including virtual time and controlled dispatchers. Its lesson is planned.**

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
