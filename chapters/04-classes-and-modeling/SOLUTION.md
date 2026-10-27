# Chapter 04 Worked solution

Try the [workshop](WORKSHOP.md) first. The complete implementation lives in the separate [reference file](../../kotlin-labs/ch04-classes-and-modeling/src/solution/kotlin/workshop/ch04/SessionModels.kt).

## Give session values a clear shape

`StudySession` is a data class whose topic, minutes, and status are primary-constructor properties. Generated equality therefore compares all three. They use `val`, so code cannot reassign the properties of an existing session.

`SessionStatus` is an enum because the status is one fixed choice. `SessionResult` has sealed cases because an accepted request carries a session while a rejected request carries a message. The caller handles a meaningful case without coordinating a nullable session and a nullable error.

These are simple public models. They do not stop someone constructing a session with an invalid duration directly. The validation promise belongs to `createSession` in this mission.

## Validate before accepting

The creation function first rejects a duration outside `1..120`. It then trims a supplied topic and chooses `Untitled` for a missing or empty result. Other text retains its case and internal spaces. The accepted session uses the supplied duration and the constructor's planned default.

This reuses Chapter 03's distinction between input representation and domain rules. Here, the duration is already an integer, and the result is a dedicated type instead of a nullable value or display string. Expected invalid durations produce `Rejected`; they do not throw or quietly become 30 minutes.

## Return the changed value

Completion uses:

```kotlin
return session.copy(status = SessionStatus.COMPLETED)
```

The copy keeps topic and minutes. The original retains its status, and the caller receives a separate object. Copying an already completed session creates another completed value, as requested by this exercise.

This model holds no mutable child object. The lesson's robot example shows why a data-class copy would not isolate a mutable meter. `copy()` remains shallow; do not assume it recursively duplicates an object graph.

## Handle the whole result

`describeResult` returns an exhaustive `when` over the sealed result. In the accepted branch, `is` smart-casts the parameter. Another exhaustive `when` maps the session's enum status to `Planned` or `Completed`, and the branch's final expression formats the actual values.

The rejected branch returns its message directly. Neither result case needs `!!` or an `else`. With this model, adding a new result case or status makes the corresponding incomplete `when` expression fail compilation, prompting a decision about its behavior.

## Bug Museum The original in disguise

**Symptom:** the completion helper returns the original planned session.

**Cause:** `return session`, or assigning it to another variable first, returns an alias to the same object. There is no new value with completed status. Changing the model to mutable properties and modifying that alias would instead change the caller's original.

**Repair:** keep read-only properties and return a copy with the changed status.

**Useful check:** compare both statuses after the call and check that the references differ. An updated label alone would not prove the original was preserved. A session's topic, minutes, and status must also be in the constructor for the intended generated equality and copying behavior.

## Verify the reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch04-classes-and-modeling:runSolution :ch04-classes-and-modeling:checkSolution
```

The reference prints `Planned: KMP for 45 min`. All 26 acceptance tests are shared: `checkSolution` runs them against the reference, and `test` runs them against your working copy. The two implementations compile separately.

## Change request answers

The 60-minute request produces a planned Coroutines session. Completing it yields a separate completed session; describing each produces the two lines shown in the workshop. A 121-minute request is rejected and describes itself as `Minutes must be 1..120`.

Two separately constructed sessions with identical constructor properties compare equal through `==`, while `===` is false. A simple assignment to another variable makes an alias, so `===` is true for those references. Status belongs in the constructor because it contributes to equality and must be preserved or deliberately changed by copying.

On a later day, build a planned session, complete it twice, and predict the equality and identity of each returned value before running it. The completed copies have equal values and distinct identities. The planned original is still planned.

**Next Chapter 05 will introduce collections and complexity. Its lesson is planned.**

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
