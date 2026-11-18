# Workshop 18 The question that came back late

A learner chooses Swift, sees the prompt loading, and taps Cancel. The screen goes idle, then shows a load error for the request they just cancelled. Another learner reports that an old error replaced a newer question. Repair both behaviors and write a test that anyone can use to reproduce the second report.

Read the [lesson](LESSON.md) first. Follow [iOS setup](../../mobile/iosApp/README.md) to select and boot a simulator and set `study_simulator` to its UDID. Run these commands from `mobile/iosApp` in the same terminal.

```sh
python3 ios.py prepare
python3 ios.py run --device "$study_simulator"
```

Find **Review prompts** in the Study Lab app. Choose Swift and wait. The demo source deliberately fails the first completed Swift attempt on this screen. Tap Retry and wait for the Swift question. Now choose Kotlin and press Cancel while it is loading. Record what you see. The short local delay makes the race possible to explore by hand. The unit tests below let you control the order exactly.

Your two editable files are **`Practice/ReviewLoader.swift`** and **`Practice/ReviewRegressionTests.swift`**. Preparation creates missing files and preserves earlier answers. The screen, local source, and shared test helper are supplied.

## 1. Predict a request's lifetime

Open [ReviewScreen.swift](../../mobile/iosApp/Sources/ReviewScreen.swift) and [ReviewSource.swift](../../mobile/iosApp/Sources/ReviewSource.swift). Find the value that changes on every request, the modifier that owns the task, and the actor that returns the prompt. Follow one tap from the screen into `ReviewLoader.load(_:)` and back.

Sketch these four sequences. Write down the expected state at each comma, then check your predictions in the lesson.

- Start Kotlin, deliver a question.
- Start Swift, deliver a normal error, retry Swift, deliver a question.
- Start Kotlin, tap Cancel, deliver the original answer anyway.
- Start Swift A, start Swift B, deliver B's question, deliver A's error.

Run the practice unit suite to see which contracts the starter already meets.

```sh
python3 ios.py test-unit --device "$study_simulator"
```

The starter builds. Expect failed assertions around cancelled or superseded work, plus a deliberately unfinished learner test. An Xcode compilation or simulator setup error occurs before those test results and needs to be addressed separately.

## 2. Repair the loader

Work in `Practice/ReviewLoader.swift`. Keep `load(_:)` async and the loader isolated to the main actor. Use the supplied source through `ReviewSource`. The view already decides when to start and cancel its task.

- A call that is already cancelled on entry leaves the current state alone. A new active request enters `.loading(topic)`. Its success produces `.ready(question)` and an ordinary failure produces `.failed`.
- A later load supersedes the earlier one, even for two requests for the same topic. At every point where a suspended call can publish, consider who owns the state now.
- `cancel()` immediately returns the loader to `.idle` and invalidates that attempt. A late success or failure must leave it idle. A cancelled call must also respect any replacement's state.
- A current call that observes task cancellation returns to idle. A fresh retry can load successfully.

Run the unit suite again. Investigate each failure by predicting who owned the request *before* the source suspended and *when* it finished. The learner test remains unfinished until Step 3.

## 3. Write the regression yourself

In `Practice/ReviewRegressionTests.swift`, replace the starter's recorded issue with a test for **an old failure while a newer request for the same topic is still loading**. Use two distinct loads of `"Swift"` and identify which one you finish first. Let both tasks reach the source. Fail the older one while the newer one is still waiting. Assert what the learner sees at that moment. Then finish the newer request with a question of your choice and check the final state.

The fixed fixtures in [ReviewLoaderTests.swift](../../mobile/iosApp/Tests/ReviewLoaderTests.swift) let you arrange the order.

| Fixture | What your test controls |
| --- | --- |
| `ControlledReviewSource()` | A fresh source for this test. Inject it into a fresh loader. |
| `try await source.start("Swift", on: loader)` | Starts a load and waits until it reaches the source. Keep the returned pending request so you can finish it later. |
| `await request.succeed("…")` or `await request.fail()` | Delivers that request's result and waits for its load to finish. |
| `#expect(…)` | Checks a condition at the point you choose. |

Keep the test on `@MainActor` so it can use the loader. Finish **both** pending requests. Each test must own its fixture and work regardless of which other test runs first. To check your test's sensitivity, temporarily bypass only the stale-error guard in your loader, run the unit suite, and find your new test's failed expectation. Restore the guard and rerun.

```sh
python3 ios.py test-unit --device "$study_simulator"
python3 ios.py test --device "$study_simulator" --only-test StudyLabPracticeUITests/StudyLabUITests/testReviewFailureRetryAndReturnToSessions
```

Aim for all nine practice unit tests (eight supplied and your regression) and a passing review-screen UI check. The second command selects just that screen's test. When you're ready to check the earlier screens too, omit `--only-test` and its argument to run both chapters' checks. The [worked solution](SOLUTION.md) gives the commands for the independent reference.

## Part 4 checkpoint

Close the loader and explain how the screen, loader, and source divide responsibility. From memory, trace what happens if the first Swift request fails while the second is waiting. Name the single test observation that would expose the wrong answer.

Then return to the [Chapter 16 value and ARC examples](../16-swift-for-kotlin-developers/LESSON.md). Compare assigning a Swift struct containing only strings to assigning a Kotlin data-class instance. Predict what a copy containing a mutable class reference would share. Draw the strong references between a Swift class and its stored closure, then explain how ARC and Kotlin/JVM garbage collection differ when a cycle becomes unreachable. Use a small concrete example for each answer.

Return after a break and change the event order. What should happen if the old error arrives *after* the replacement has already succeeded? Write down the expected state, then try that sequence with a fresh controlled source.

[Progressive hints](HINTS.md) · [Worked solution](SOLUTION.md)
