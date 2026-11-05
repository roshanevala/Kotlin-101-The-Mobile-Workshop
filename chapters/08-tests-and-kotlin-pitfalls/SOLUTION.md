# Solution 08 Copy the editing boundary, test the promise

Try the [workshop](WORKSHOP.md) before comparing with the [production reference](../../kotlin-labs/ch08-tests-and-kotlin-pitfalls/src/solution/kotlin/workshop/ch08/SessionSnapshots.kt) and [six worked regression tests](../../kotlin-labs/ch08-tests-and-kotlin-pitfalls/src/solutionTest/kotlin/workshop/ch08/SnapshotPracticeTest.kt). Keep your production and test answers in their generated working files.

## Value equality needs distinct instances in the test

`sameSessionValue` uses `==`. The supplied data class derives equality from all primary-constructor properties. The regression obtains a separate instance with `copy()`, documents distinct identity with `assertNotSame`, then checks that the helper accepts its value.

Comparing only an object with itself would let the starter's `===` pass. A changed ID or status must still compare false; this helper does not compare only IDs.

## Copy each mutable list that callers can edit

`independentPlan` returns `plan.copy(sessions = plan.sessions.toMutableList())`. That isolates membership and element replacement in either direction. The sessions themselves have only immutable properties, so sharing their references meets this model's contract. A later model with editable session properties would require revisiting that decision.

`PlanHistory.save` captures an independent plan and appends it. `snapshots` maps each stored version to another independent plan. That also produces an outer result whose membership cannot grow when later saves occur. Capturing on input without protecting returned editable plans would leave another editing path into history.

Neither equality immediately after copying nor an identity check on the outer list proves the desired isolation. The strongest regressions make a permitted edit and inspect the value that must be preserved.

## Select completed work before adding minutes

The reference filters for `completed`, then sums `minutes.toLong()`. Empty and planned-only inputs give zero. A mixed-status test exposes accidental unconditional addition. Testing completed endpoints 1 and 120 verifies both are included; a passing endpoint test alone would miss the starter bug because both entries are completed.

The total counts entries as supplied, including repeated IDs. This chapter does not silently import another chapter's deduplication rule.

## Bug Museum The read-only history that could still be edited

**Symptom:** clearing a returned plan's session list changes the next history read.

**Cause:** `List<StudyPlan>` restricts operations on the outer collection. It does not remove `MutableList` from each plan or prevent another holder from changing shared objects. Returning `entries.toList()` alone still shares the stored plans.

**Repair:** capture independent plan lists on save and return independent plan lists on read. Keep the history collection private.

**Useful check:** read a version, clear its public `sessions` list, read again, and expect the original sessions. Then save another version and confirm the earlier outer result still has its original size. These test different mutable levels.

## Verify the separate reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch08-tests-and-kotlin-pitfalls:runSolution :ch08-tests-and-kotlin-pitfalls:checkSolution
```

Output:

```text
Original topics: [Kotlin]
Saved topics: [Kotlin]
Equal sessions: true
Completed minutes: 30
```

The reference runs **32 tests**: 26 shared acceptance checks and six worked regressions. Your `test` task runs the same 26 acceptance checks plus your own working test file. It does not compile against the reference or its regression file.

The worked regressions cover distinct equal instances, editing a copied plan, editing an input after save, editing a returned history plan, planned-only totals, and duration endpoints. They are examples to compare, not a replacement for watching your own test fail before a repair. All assertions can pass on a limited suite while an untested editing path remains broken.

## Change request answer

The first saved version contains `[Kotlin]`; the second contains `[Kotlin, Flow]`. Their completed totals are **30** and **90**. Clearing the original afterward changes neither version. The sample's separate `completedMinutes` call still prints 30 because it constructs its own list; inspect the second saved plan to observe its 90-minute total.

The independent foundations checkpoint has no supplied solution. Use its written rules, test observable behavior, and explain where its latest-record rule differs from Chapter 05's imported-history rule. Completing this chapter's guided exercise alone does not establish independent checkpoint completion.

**[Next: Chapter 09 Suspension and execution](../09-suspension-and-execution/LESSON.md)** introduces coroutine scopes, dispatchers, and independent requests.

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
