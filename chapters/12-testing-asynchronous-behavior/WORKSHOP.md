# Workshop 12 Catch the retry that should never happen

Bug report: “A temporary search failure is never retried. The search also starts too early, and an old query can keep working after I type a replacement.” Write tests that expose these schedules, then repair the implementation.

Read the [lesson](LESSON.md) first. Work in three sessions if needed: retry timing, cancellation, then query replacement. This chapter asks you to write regression tests, not only make the provided suite pass.

## Sync, run, and open both copies

Keep **`kotlin-labs`** open in Android Studio. After updating the course, use **Sync Project with Gradle Files** and wait for indexing. This module uses Kotlin **2.2.21**, Gradle **8.14.3**, JDK **17**, core coroutines **1.11.0**, and matching **coroutines-test 1.11.0** in test dependencies only. The first test run may download that library.

From its terminal:

```sh
./gradlew --console=plain :ch12-testing-asynchronous-behavior:run
```

On Windows use `./gradlew.bat`. This creates two editable files:

```text
ch12-testing-asynchronous-behavior/src/main/kotlin/workshop/ch12/AsyncSearch.kt
ch12-testing-asynchronous-behavior/src/practiceTest/kotlin/workshop/ch12/AsyncPracticeTest.kt
```

Edit these two generated files only. Existing answers are preserved and ignored by Git. Leave the tracked [production starter](../../kotlin-labs/ch12-testing-asynchronous-behavior/src/starter/kotlin/workshop/ch12/AsyncSearch.kt), [test starter](../../kotlin-labs/ch12-testing-asynchronous-behavior/src/starterTest/kotlin/workshop/ch12/AsyncPracticeTest.kt), and fixed acceptance tests unchanged. See the [setup guide](../../kotlin-labs/README.md) if needed. The terminal wrapper is the verified route; browser execution would also need the test runner and libraries.

The deliberately broken starter prints:

```text
Loading: Kotlin
Failed: offline
Attempts: 1
```

The fake source fails once and then can succeed. The console uses zero delays and an explicit unconfined dispatcher for this tiny demonstration; your timing tests must inject `StandardTestDispatcher(testScheduler)`, not depend on that console scheduling choice.

## Predict the schedules

Read `searchWithRetry` and `searchStates`. Which duration belongs to a query's quiet period? Which duration separates retry attempts? Predict source-call times for three attempts with a 100 ms backoff, starting at virtual time 0. Should there be a delay after an exhausted final attempt?

Then predict: old query starts at 100 and fails; new query arrives at 150; quiet period is 100 and backoff is 200. Which old retry must disappear, and when may new start?

## Agree on the contracts

- `maxAttempts` counts **all attempts**, including the first. It must be positive. Retry delay and quiet period must be nonnegative. Validate arguments before work; `searchStates` validates at construction, even for empty/blank input. Zero backoff and zero quiet period are valid.
- The direct retry helper preserves its exact query and the source's topic order, repeats, and empty success. Run the source and backoff in the supplied dispatcher. Do not hardcode another dispatcher or create a detached scope.
- For this source agreement, only `IllegalStateException` is retryable. Rethrow `CancellationException` first, propagate unexpected types, and wait the fixed backoff only **between failed attempts**. On exhaustion, rethrow the last source failure without another wait. Check active state before each attempt, including when backoff is zero.
- Cancelling during source work or backoff stops later attempts and waits for cooperative cleanup. Do not turn cancellation into failure content. A non-cooperative source cannot be forcibly interrupted by these helpers.
- `searchStates` is cold: no work on construction, independent work per collection, and no invented states for empty input. Trim queries, then suppress adjacent normalized duplicates. Preserve case and internal spaces. Equal normalized input is not a new retry action.
- Every distinct normalized query replaces the entire old branch, including any source call or retry backoff. Cancel and wait for old cleanup before the new quiet period. A blank query cancels and emits `Idle` without a quiet period or source call; cleanup can still delay idle.
- A nonblank branch waits its quiet period, emits `Loading` **once per query**, and invokes the retry helper. Emit exact `Results` on success or `Failed` after agreed failure exhaustion. Use `Search unavailable` only for a null message; preserve an empty message. Keep final `emit` outside source recovery.
- Use `flatMapLatest` with `buffer(0)` as in Chapter 11. The delay belongs inside the latest branch, not before replacement. Its quiet period uses the collector's context; source work and backoff use the injected dispatcher. A source-created cancellation can end just its latest child without producing failure content.
- Values already delivered are not erased. Finite input still lets its final branch finish the quiet period and retries. Hot input can conflate updates; tests intentionally process the updates they are examining. There is no shared screen holder or mobile lifecycle to add here.

Treat input and output lists as stable read-only values. There is no exponential backoff, jitter, network, cache, or timeout policy to invent.

## Mission 1 Write retry regressions before repairing retries

The prepared `AsyncPracticeTest` has one blank-query example and six TODO prompts. Keep that example or replace it with an equivalent meaningful check. Add your own tests using `runTest(timeout = 5.seconds)`, the needed experimental opt-in, and one shared scheduler for injected dispatchers.

Write a fake that records calls and virtual times, fails twice, then succeeds. Start the helper in `async`, call `runCurrent`, and inspect calls just before and at each backoff boundary. Run your newly named test alone before changing production code:

```sh
./gradlew --console=plain :ch12-testing-asynchronous-behavior:test --tests 'workshop.ch12.AsyncPracticeTest.your test name'
```

Replace `your test name` with the actual function name. A “no matching tests” error is a naming problem, not the intended red regression. The expected red should be an assertion about calls, results, or timing. Catch an expected source failure inside an async child when necessary so it becomes test data instead of cancelling the parent.

Add an exhaustion test that checks the last failure, exact attempt budget, and absence of a final wait. Add cancellation during backoff: cancel the job, advance beyond all remaining retry boundaries, and assert no additional calls.

Now repair `searchWithRetry`. Validate, enter the injected context, check cancellation before each attempt, and catch only the agreed source failure. Retry within the budget and delay only before another attempt. Keep cancellation out of ordinary recovery.

## Mission 2 Catch early search and rapid replacement

Write a test that observes no loading state or source work before the quiet deadline. At the exact deadline, assert before `runCurrent`, then run current work and assert loading/results. Use a finite query flow for this boundary test.

Write a second test with changeable input. Start an old query, advance partway through its quiet period, submit a different query, and execute current work. Cross the old deadline, then the new deadline. Only the new query may reach the source. Use explicit time controls for a `backgroundScope` collector; do not assume `advanceUntilIdle` drives its entire future timeline.

Run each test red. Leave `searchStates` unchanged until Mission 3's integration regression has also run red; then repair query behavior once with all three observations available.

## Mission 3 Replace during retry backoff

Write a regression for the predicted schedule: old fails at 100, new arrives at 150, new first attempt runs at 250, and old's planned retry at 300 never happens. Let new fail once too, then succeed at 450. Check exact query/time pairs and states, including no old result or old failure display.

This test brings retry and replacement together. Making the retry helper pass alone does not establish that the enclosing query branch cancels it correctly. Run this test red while the sequential query starter is still unchanged (the retry helper is already repaired). Then replace the sequential collector with the latest inner-flow pattern from Chapter 11, add the quiet delay before loading inside each nonblank branch, and keep source recovery separate from downstream emission. Rerun all three query regressions green.

Use the [progressive hints](HINTS.md) if needed. Keep notes of the failure each regression caught; a final green suite alone does not establish that your tests detect the original defect.

## Check your implementation and tests

```sh
./gradlew --console=plain :ch12-testing-asynchronous-behavior:test
```

`test` runs **26 fixed acceptance checks plus your learner-authored tests**. The untouched test template contains one example, so it initially discovers **27 tests**. Your final count depends on the tests you write; there is no meaningful fixed “learner completed” count. The report is at `ch12-testing-asynchronous-behavior/build/reports/tests/test/index.html`.

The separate reference has **32 tests**: the same 26 checks plus six worked regressions. Fixed acceptance passes do not establish the quality of your own regressions. Review your red results, then demonstrate that your tests fail when a relevant defect is reintroduced in a temporary answer.

Corrected `run` output:

```text
Loading: Kotlin
Results: [Kotlin]
Attempts: 2
```

The test suite uses virtual deadlines and a real-time hang guard, without sleeps or elapsed-time performance assertions. Ordinary `run` still uses production coroutines; virtual time belongs to test sources.

To restore either template, move the corresponding answer outside its source folder. `:ch12-testing-asynchronous-behavior:preparePractice` prepares missing production and test files; `:ch12-testing-asynchronous-behavior:preparePracticeTests` prepares only missing tests. Existing files and `clean` preserve both answers.

## Investigate the boundary mistake

In the lesson's queued-work test, temporarily remove the `runCurrent()` immediately after reaching time 100 but keep the next `ready` assertion. Observe the failing expected/actual values. Restore that call. Explain why increasing the advance by one millisecond might hide the missing boundary execution instead of documenting it correctly.

Then consider a tempting test repair: call `pending.await()` before checking that a retry has not happened yet. Why can the test now observe a later virtual time than intended?

## Explain and revisit

Explain in 60 seconds, or write: “What does virtual time control, and what escapes it? Why must all test dispatchers share a scheduler? Why does a queued child need `runCurrent`? What does a passing controlled interleaving prove about a real data race?” Include test lifetime and source cancellation.

Changed request: increase the worked retry schedule's backoff from 100 to 250. Predict the three call times and total virtual time before updating assertions. Run that test without changing the production helper. Then make the first source attempt succeed: should any virtual backoff remain?

On another day, independently test a new query arriving **one virtual millisecond before** an old retry. Choose and document the ordering rather than relying on a simultaneous tie. Verify the new query can still retry and the cancelled old one cannot. At an exact tie, whichever runnable action the scheduler processes first can affect already completed work; do not claim a universal physical-time ordering.

Part 2 checkpoint: build and test a cancellable search pipeline without the reference, explain why `suspend` does not select a thread, and reproduce one regression before fixing it. Lesson delivery is not evidence that this independent checkpoint has been completed. Sharing your work publicly is optional.

**[Review implementation and regressions](SOLUTION.md)** · [Review the lesson](LESSON.md)
