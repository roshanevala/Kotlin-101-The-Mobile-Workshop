# Chapter 12 Hints

Attempt the [workshop](WORKSHOP.md) before opening these. Keep code and tests in your generated working copies.

<details>
<summary>Hint 1 — What observation would prove the defect?</summary>

A final result does not tell you when attempts happened. What should the call list contain before a retry deadline, at that deadline before queued work runs, and afterward? Can your test distinguish a final failure from an extra final delay?

When replacing during backoff, record the query as well as the time. An extra old call must not be mistaken for the new query's legitimate retry.

</details>

<details>
<summary>Hint 2 — Control one clock and each lifetime</summary>

Use `StandardTestDispatcher(testScheduler)` for injected source work. Start queued children and collectors with `runCurrent`. Advance to just before a deadline, then to the exact deadline; execute that target with `runCurrent`.

Catch expected failures inside async children. Put long-lived query collectors in `backgroundScope`, or explicitly cancel and join them. Use explicit advancement for background timelines.

</details>

<details>
<summary>Hint 3 — Separate the two delays</summary>

For retries, try the source up to the positive attempt budget. Check active state before each call; rethrow cancellation before catching the agreed recoverable type. A non-final recoverable failure waits before the next attempt. The final failure is rethrown immediately.

For search, the latest branch contains quiet delay → loading → the whole retry helper → final outcome. Blank handling also belongs inside that branch. Combining a correct helper with a sequential query collector still leaves obsolete work running.

A useful replacement schedule is old failure at 100, new input at 150, new failure at 250, and new success at 450. There should be no old call at 300.

</details>

[Back to the workshop](WORKSHOP.md) · [Worked solution](SOLUTION.md)
