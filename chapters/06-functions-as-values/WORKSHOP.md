# Workshop 06 Pass the rule, preview the result

Bug report: “Every new report filter gets another copy of the loop. Our one-item preview also formats sessions it never displays.” Build composable rules and compare two explicit processing contracts.

Read the [lesson](LESSON.md) first. Plan two sessions if functions as values are new: selection and labels first, then previews and their counters.

## Sync, run, and open your copy

Keep **`kotlin-labs`** open in Android Studio. After updating the course, use **Sync Project with Gradle Files** and wait for indexing. From its terminal:

```sh
./gradlew --console=plain :ch06-functions-as-values:run
```

On Windows use `./gradlew.bat`. This creates the editable file:

```text
ch06-functions-as-values/src/main/kotlin/workshop/ch06/SessionFilters.kt
```

Edit **`src/main` only**. Existing work is preserved and stays out of normal Git commits. The [tracked starter](../../kotlin-labs/ch06-functions-as-values/src/starter/kotlin/workshop/ch06/SessionFilters.kt) supplies runnable placeholders; do not edit it. Use the [setup guide](../../kotlin-labs/README.md) for toolchain help. You can also paste the starter into [Kotlin Playground](https://play.kotlinlang.org/); wrapper tests require the local workspace.

An untouched starter prints:

```text
Selected topics: []
Eager: [] (filter=0, map=0)
Lazy: [] (filter=0, map=0)
```

This output means the program ran. It is the deliberate unfinished exercise, not a setup error.

## Predict and explore

Read `main` and predict which records are both completed and at least 30 minutes. Then trace the first matching record through `filter`, `map`, and `take(1)`. Which later records must the eager version process?

The two supplied callbacks increment counters before doing their work. `filter` counts calls to the combined predicate, not the individual rules inside it. After reading the lesson's scope-function table, identify why `also` would be suitable for logging a result but unsuitable for replacing `map`.

## Agree on the contracts

Inputs already contain valid topics and durations in `1..120`. A simple Boolean represents completion. Every list entry is a separate session for this mission; preserve repeats and exact topic text. Chapter 05's history-deduplication problem has already been handled upstream when needed. This module has no dependency on its code.

Keep input order and leave the input unchanged. Return materialized, read-only lists whose contents are independent of later source-list additions or removals. This does not promise deep immutability. Callbacks in the experiment only read session values and update their own counters; mutating the input during traversal is outside the contract.

## Mission 1 Compose two rules

Implement `allOf(first, second)`, which returns a `(StudySession) -> Boolean`.

Creating the combined rule must invoke neither callback. When the result is called, evaluate `first` on that supplied session. If false, return false without calling `second`; otherwise return `second`'s decision. Do this on each invocation, so a captured variable is read at invocation time. Do not cache a previous decision.

## Mission 2 Select and label

Implement `selectSessions(sessions, predicate)` with `filter`. Invoke the supplied predicate once per entry, in encounter order. Keep precisely the accepted entries, including repeats. An empty input invokes nothing.

Implement the extension `StudySession.label()`. It returns the exact topic followed by `: `, minutes, and ` min`. For example, a 30-minute Kotlin session gives `Kotlin: 30 min`. Completion does not change the label. Use receiver properties without changing the model.

## Mission 3 Keep the eager contract visible

Implement `eagerPreview(sessions, limit, predicate, transform)`.

Validate the limit before invoking any callback: a negative value throws `IllegalArgumentException`, even for empty input. On a valid limit, filter the whole list, map all accepted entries, then take the requested prefix. Use the supplied `transform`; do not hardcode `label()` inside the helper.

With limit zero, this eager experiment still filters every input and maps every match, then returns empty. This particular ordering makes the contrast observable. An oversized limit returns all matches. A mapper is never invoked for rejected entries.

## Mission 4 Stop a lazy preview early

Implement `lazyPreview` with the same parameters and negative-limit validation. Convert to a sequence, filter, map, take, then materialize a list with `toList()`.

Visit entries in order, mapping each match before inspecting the next entry. Stop after producing `limit` matches or exhausting the source. A zero limit calls neither callback. When there are fewer matches than requested, visit the entire input. Return the completed list rather than the sequence pipeline.

After all missions, `run` should print:

```text
Selected topics: [Kotlin, KMP]
Eager: [Kotlin: 30 min] (filter=4, map=2)
Lazy: [Kotlin: 30 min] (filter=2, map=1)
```

If stuck, reveal one level on the [hints page](HINTS.md).

## Check your work

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch06-functions-as-values:test
```

The 26 tests check decisions, order, captured state, labels, boundaries, callback traces, snapshots, and independent calls. A fresh starter intentionally fails some tests. Assertion failures mean the exercise compiled; compilation or dependency errors need fixing before interpreting the tests. Reports open at `ch06-functions-as-values/build/reports/tests/test/index.html`.

To start again, first move your working `SessionFilters.kt` somewhere safe outside its source folder. Run `:ch06-functions-as-values:preparePractice` to create a new copy from the starter. `clean` removes build output and keeps your working file.

## Investigate the scope-function bug

A teammate replaces `.map(transform)` with `.also { matches -> matches.map(transform) }` before calling `take`. Predict the result type. In a scratch copy, print the expression and explain why it keeps sessions rather than the strings produced inside the block. The fix must return the transformed list. Do not change the mission's public signatures to hide the type mismatch.

## Explain and revisit

Explain in 60 seconds, or write your answer: “Why did the lazy preview invoke fewer callbacks? Does that prove it is faster?” Include the zero-limit and too-few-matches cases.

Change the limit in `main` to `2`, then `0`, then `5`. Predict all counters before running each version. Restore it to `1` afterward. No acceptance-test edits are needed.

On a later day, create an `anyOf` rule in your working file: it accepts when either rule accepts and skips the second after the first returns true. Try all four Boolean combinations and a counter that checks short circuiting. Then write a one-line use of `let` that returns a preview's size and a use of `also` that logs the size while returning the preview. These are recall tasks, outside the required 26 tests.

You are done when the tests pass, you can trace both previews without running them, and you can explain the scope-function bug. Public sharing is optional.

**[Review the worked solution](SOLUTION.md)** · [Review the lesson](LESSON.md)
