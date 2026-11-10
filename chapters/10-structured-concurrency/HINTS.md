# Chapter 10 Hints

Try the [workshop](WORKSHOP.md), then reveal one level at a time.

<details>
<summary>Level 1 Trace the job and the editing path</summary>

Which job owns the old request? When is its cleanup complete? Can another method run while the first waits to join? A cancellation request should stop obsolete work rather than become a result that the publisher displays.

</details>

<details>
<summary>Level 2 Match a tool to each boundary</summary>

- Check current cancellation with `currentCoroutineContext().ensureActive()`.
- Rethrow `CancellationException` before translating `IllegalStateException`.
- `withTimeoutOrNull` identifies its own local timeout without catching unrelated cancellation.
- `cancelAndJoin` requests cancellation and waits for completion.
- A single `Mutex` protects `submit` and `stop` across the full state transition.
- Launch the request in the injected owner scope; keep its publication after an active-state check.

</details>

<details>
<summary>Level 3 Sketch the control flow</summary>

For bounded search, validate first, ensure the caller is active, and return `withTimeoutOrNull(timeoutMillis) { searchSafely(...) } ?: SearchOutcome.TimedOut(query)`.

Inside `submit`'s lock, join the cancelled previous job before assigning a newly launched job to `active`. Return that same handle. Inside `stop`'s lock, cancel and join, then set `active` to null. Do not clear or replace the reference while the transition is unprotected.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution after your attempt](SOLUTION.md)
