# Chapter 12 Worked solution

Attempt the [workshop](WORKSHOP.md) before opening the [production reference](../../kotlin-labs/ch12-testing-asynchronous-behavior/src/solution/kotlin/workshop/ch12/AsyncSearch.kt) and [six worked regressions](../../kotlin-labs/ch12-testing-asynchronous-behavior/src/solutionTest/kotlin/workshop/ch12/AsyncPracticeTest.kt). Compare both implementation and test reasoning.

## Retry within an explicit budget

`maxAttempts` includes the first source call. The helper validates settings before entering the supplied dispatcher. A bounded loop checks active state and attempts the source. A successful value returns immediately. Cancellation is rethrown first; unexpected exceptions escape. Only an agreed recoverable failure can reach another attempt.

The final recoverable failure is rethrown before delay, preserving that failure's message and avoiding an extra wait. Earlier recoverable failures delay before the next iteration. Checking cancellation explicitly is necessary even with zero backoff: `delay(0)` does not suspend. The final `error` after the loop is an unreachable guard for a validated positive budget: each final attempt must return or throw.

The direct helper preserves exact query text. The Flow wrapper owns trimming, adjacent deduplication, idle/loading/results/failure states, and query replacement. Source work and retry backoff use the injected dispatcher; the quiet period uses the collector's context.

## Replace the whole retrying query

The reference's latest branch includes the quiet period and entire retry operation. Replacement cancels obsolete source work or backoff and waits for cooperative cleanup before starting the next branch. Filtering blank input out or placing a debounce before replacement would hide a cancellation-triggering input.

Loading is emitted once per query, not on every retry. After exhaustion, the wrapper converts only the agreed failure, using a fallback only for a null message. Source recovery computes a value; the final emission is outside the `try`. Unexpected/input/collector failures are not display content. Source-created cancellation can stop just its latest child; cancelling the collector stops owned branches.

The final `buffer(0)` removes queued output capacity without making input and publication atomic. Already delivered values remain delivered. The module is independent; it does not import or alter Chapter 11's implementation.

## What the six regressions demonstrate

| Regression | Evidence |
| --- | --- |
| Two retry boundaries | Calls occur at 0, 100, and 200; none arrive before executing each exact target |
| Exhaustion | Two failures finish at 100 with the second message and no final wait |
| Cancellation in backoff | Cancelling after the first call prevents all later retries |
| Quiet boundary | Neither loading nor source work occurs before the configured deadline is run |
| Rapid replacement | Input at 50 resets the quiet deadline to 150; the original deadline produces no call |
| Replacement during backoff | Calls are old at 100, new at 250, and new at 450; old never retries at 300 |

The failure-as-data async test catches the expected exception **inside** its child. The two-boundary test converts premature exhaustion into an empty diagnostic result so its call/value assertions can identify the starter defect without cancelling the parent. The cancellation test's diagnostic catch lets the broken starter finish so an explicit active-job assertion identifies its missing backoff; cancellation itself is always rethrown.

Background collectors have controlled lifetimes. Tests drive their timelines explicitly and let `backgroundScope` cancel them afterward. Every injected test dispatcher shares the test's scheduler. There are no wall-clock sleeps, and the five-second real-time guard is not a virtual deadline or performance target.

These tests check selected interleavings and values. They do not establish correctness for every real parallel race. Fixed acceptance passes also do not prove a learner's test suite is meaningful: reintroduce a targeted defect in a temporary answer and confirm a corresponding regression fails.

## Run the separate reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch12-testing-asynchronous-behavior:runSolution
./gradlew --console=plain :ch12-testing-asynchronous-behavior:checkSolution
```

Expected console output:

```text
Loading: Kotlin
Results: [Kotlin]
Attempts: 2
```

`checkSolution` runs **32 tests**: 26 shared acceptance checks plus six worked regressions. `test` runs your working implementation with the shared checks and your own test file. The untouched template contributes one test, so its initial count is 27. Neither reference task compiles your answers.

## Change request answer

With a 250 ms backoff, the three call times become `[0, 250, 500]`. Update both before-boundary increments from 99 to 249, the target list from 100/200 to 250/500, and the boundary-to-attempt calculation accordingly; preserve the one-millisecond step and `runCurrent` at each target. Total virtual time is 500.

If the first source call succeeds, the call list is `[0]` and backoff adds zero virtual time. Test that as a separate success scenario rather than keeping assertions expecting three calls. An independently delayed successful source still contributes its own delay.

**Next: [Chapter 13 Android runtime and lifecycle](../13-android-runtime-and-lifecycle/README.md)** — open the first mobile workspace and repair a disappearing draft.

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
