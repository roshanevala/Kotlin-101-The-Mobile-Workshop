# Workshop 04 Give each session its own plan

Bug report: “I asked for KMP for 45 minutes and got Kotlin for 30. Completing the session still leaves it planned. Two sessions with identical details compare as different.” Repair the model and the functions that use it.

Read the [lesson](LESSON.md) first. This mission stays in plain Kotlin; the model and its rules are preparation for our Android app.

## Run and find your working copy

Keep **`kotlin-labs`** open in Android Studio. Click **Sync Project with Gradle Files** after updating the course and wait for sync to finish. In its terminal, run:

```sh
./gradlew --console=plain :ch04-classes-and-modeling:run
```

On Windows, use `./gradlew.bat`. The command creates your editable file:

```text
ch04-classes-and-modeling/src/main/kotlin/workshop/ch04/SessionModels.kt
```

Edit that file under `src/main`. Later runs preserve existing work. This working copy stays out of normal Git commits, and your Chapters 01–03 answers stay separate. For IDE execution, open this working file after sync; the tracked template is not a runnable source folder.

The published question lives in [src/starter](../../kotlin-labs/ch04-classes-and-modeling/src/starter/kotlin/workshop/ch04/SessionModels.kt). For a browser attempt, paste that template into [Kotlin Playground](https://play.kotlinlang.org/). See the [one-time setup guide](../../kotlin-labs/README.md) if needed.

A fresh copy prints:

```text
Planned: Kotlin for 30 min
```

Find the request in `main`. Predict the intended output, then inspect which functions currently ignore their arguments. The starter compiles and runs; its wrong answers are the exercise.

## Mission 1 Make a session a value

The supplied `StudySession` has three read-only properties in its primary constructor:

| Property | Type | Meaning |
| --- | --- | --- |
| `topic` | `String` | The topic to study |
| `minutes` | `Int` | The session duration |
| `status` | `SessionStatus` | `PLANNED` by default, or `COMPLETED` |

Make it a **data class**. Keep all three constructor properties as `val`, and keep the default status. Sessions with the same topic, minutes, and status should compare equal. A difference in any of those values should make them unequal.

The enum and sealed result cases are supplied so the unfinished program can compile. Read their declarations: `Accepted` contains a session, while `Rejected` contains a message. Keep those names and shapes for the tests.

## Mission 2 Create or reject a session

Implement `createSession(rawTopic: String?, minutes: Int): SessionResult`.

1. Reject minutes outside **1..120**, including zero and negative values. Return `SessionResult.Rejected` carrying exactly `Minutes must be 1..120`.
2. For an allowed duration, trim the topic's edges. A missing, empty, or all-whitespace topic becomes `Untitled`. Preserve case and spaces inside other text.
3. Return `SessionResult.Accepted` carrying a session with that topic, the supplied minutes, and `PLANNED` status.

Examples:

| Topic | Minutes | Required case and contents |
| --- | --- | --- |
| `" KMP "` | `45` | Accepted: topic `KMP`, minutes `45`, status `PLANNED` |
| `null` | `1` | Accepted: topic `Untitled`, minutes `1`, status `PLANNED` |
| `"   "` | `120` | Accepted: topic `Untitled`, minutes `120`, status `PLANNED` |
| `"null"` | `30` | Accepted: topic `null`, minutes `30`, status `PLANNED` |
| Any topic | `0` or `121` | Rejected: `Minutes must be 1..120` |

Minutes arrive as an `Int` here; text parsing was Chapter 03's job. Return a typed rejection for an invalid duration rather than throwing or inventing a valid session. The public model constructor itself does not enforce this rule; this function is the validation boundary.

## Mission 3 Complete a copy

Implement `completeSession(session: StudySession): StudySession`.

Return a **new session** with the same topic and minutes, and status `COMPLETED`. Preserve the supplied session. Use the data class's generated `copy` operation.

If the original was already completed, still return a new copy with equal values. Calling this function must not affect other sessions or reuse a previous call's topic or duration. Keep the properties read-only; there is no need for global state.

## Mission 4 Describe the actual outcome

Implement `describeResult(result: SessionResult): String`.

| Result | Exact description |
| --- | --- |
| Accepted session with `PLANNED` status | `Planned: TOPIC for N min` |
| Accepted session with `COMPLETED` status | `Completed: TOPIC for N min` |
| Rejected result | Its supplied message, unchanged |

Use an exhaustive `when` on the result, with `is` checks for its cases. For an accepted session, use another exhaustive `when` to choose the status label. A branch block can declare local values and finish with the string it produces. Avoid `else` so an added case or status would require a deliberate decision.

Return text from the helper and let `main` print it. A rejection message can be anything the caller supplies; do not replace it with a fixed message or a prefix.

The original `main` request should now print:

```text
Planned: KMP for 45 min
```

If stuck, open the [optional hints](HINTS.md) one at a time.

## Check your work

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch04-classes-and-modeling:test
```

The **26 supplied tests** cover value equality, each model property, topic fallbacks, duration boundaries, typed outcomes, completion, preserved originals, distinct copies, exact descriptions, and independent calls. Test-framework code is provided; writing your own tests comes later.

An untouched starter intentionally fails some tests. Expected-versus-actual failures mean the code ran and disagreed with the contract. A compile error means Kotlin could not build it. Work through one mission at a time and read the failing test names.

The report is generated at `kotlin-labs/ch04-classes-and-modeling/build/reports/tests/test/index.html`. The tests check the listed behavior; use the change request below to practise beyond their examples.

## Try a changed request

In `main`, create a session with topic `" Coroutines "` and 60 minutes. If the result is accepted, complete its session and print descriptions of both the original and the completed copy. Predict these two lines before running:

```text
Planned: Coroutines for 60 min
Completed: Coroutines for 60 min
```

Then try 121 minutes and describe the rejection. Finally, construct two separate sessions with identical details. Explain why `==` should be true and `===` should be false. Restore the original `main` call when finished.

For an interview rehearsal, explain why `val updated = original` is an alias, why the status belongs in the data class constructor, and why `copy()` would need more thought if the model held a mutable child. A spoken minute or a short written answer is enough.

## Finish the mission

- [ ] I can construct an instance and explain its properties and default status.
- [ ] I can distinguish value equality, identity, aliases, and shallow copies.
- [ ] I can return and handle accepted or rejected outcomes without nullable field combinations.
- [ ] My working copy passes all 26 checks.
- [ ] Completing a session preserves the original, including in the changed request.

Your answer is local and is not backed up by normal course commits. To restart, save it outside the chapter's `src` folders before copying the tracked starter template over the working file. Git history does not contain edits to the ignored copy.

**[Next Review the explained solution](SOLUTION.md)**

[Back to the lesson](LESSON.md) · [Chapter overview](README.md)
