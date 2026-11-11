# Solution 10 Cancellation ends obsolete work, not the owner

Try the [workshop](WORKSHOP.md) before comparing with the [reference implementation](../../kotlin-labs/ch10-structured-concurrency/src/solution/kotlin/workshop/ch10/SearchLifecycle.kt). Keep your answer in `src/main`; the reference compiles separately.

## Preserve the failure contract at each boundary

`searchSafely` checks the current job before source work. It returns `Found` on success, rethrows `CancellationException`, and translates only `IllegalStateException` to `Failed`. A null error message uses the fallback; an empty message is preserved. Unexpected types still propagate.

Cancellation must be caught first because it is itself an `IllegalStateException`. Broadly converting exceptions, including an unexamined `runCatching` result, would violate the helper's promise even if a later publication guard happened to hide the symptom.

`searchWithin` validates its nonnegative timeout, checks cancellation, then uses `withTimeoutOrNull` around the safe helper. Its block returns a non-null outcome, so null identifies this wrapper's timeout. A source's nested timeout or owner cancellation is not replaced by timeout content. Checking cancellation before the zero-deadline path also prevents normal content from an already cancelled caller.

## Protect the complete replacement transition

`SearchSession.submit` locks the active-job transition, cancels and joins the old job, launches a replacement in the supplied scope, stores that handle, and returns it. Actual source execution and publication happen in the child. An active-state check precedes publication, but it is not atomic with cancellation racing an already executing callback. Joining waits for that callback as well, so old publication cannot occur after the replacement starts.

`stop` uses the same lock, cancels and joins the remembered job, then clears it. It stops this session's current work, not the entire owner. Calling it again is harmless; submitting later remains possible if the owner is active.

Joining is part of the transition. Releasing the lock before obsolete cleanup completes would let another method interleave and update the wrong active-job reference. Even on one thread, a join can suspend. The mutex protects this reference; it does not make the caller's arbitrary mutable dependencies safe.

The source must cooperate and finish its cleanup without reentering the session. This design does not forcibly interrupt blocking work. If the method's own caller is cancelled while waiting, the suspending lock or join can throw; it is not an unconditional shutdown primitive.

## Choose failure ownership deliberately

Search children belong to the provided scope, so owner cancellation reaches them. The session translates its agreed source failures but leaves unexpected exceptions to that owner's policy. It does not silently install supervision or detach jobs to avoid those failures.

Supervision is useful for independent optional work when each failure is handled. It does not by itself prevent old search results from arriving. The lesson's supervised peer waits for an explicitly released signal, while the failing async result is explicitly awaited and caught inside the supervisor block.

## Bug Museum The cancelled search that became an error message

**Symptom:** leaving a screen or replacing a search produces a failure result for work that should simply stop.

**Cause:** a broad catch converts `CancellationException` to normal content. A second bug may also publish without checking whether the current request is still active.

**Repair:** preserve cancellation through helper boundaries and keep publication in the owned, cancellable request. Join obsolete work before replacing it.

**Useful check:** cancel a waiting request, join it, and assert that nothing was published. Separately invoke the safe helper with a cancellation exception; it must throw rather than return `Failed`. Neither job status alone nor a successful latest-query test covers both contracts.

## Verify the reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch10-structured-concurrency:runSolution :ch10-structured-concurrency:checkSolution
```

Output:

```text
Old cancelled: true
Published queries: [Kotlin]
```

All **32 tests** are shared: `test` checks your working copy and `checkSolution` checks the separate reference. Controlled signals expose cleanup and state-transition order. A real deadline case verifies timeout outcome and cleanup; it does not assert a completion duration. Tests compare exception types and relevant messages rather than require object identity across coroutine boundaries.

## Change request answer

Submitting and joining `KMP` after Kotlin completes produces `[Kotlin, KMP]`. The obsolete `Ko` job remains cancelled and contributes nothing when its old signal is released. Cancellation prevents future publication; it does not erase a result that was already published before a later submission.

Stopping a waiting request cancels that request, waits for its cleanup, and leaves other sessions under the same owner alive. Cancelling the owner itself instead cancels its descendants. In the optional supervision variation, an uncaught exception escaping the supervisor block fails that block and cancels its remaining children.

**Next: [Chapter 11 Flow and observable state](../11-flow-and-observable-state/README.md)** — turn search and progress into shared screen state.

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
