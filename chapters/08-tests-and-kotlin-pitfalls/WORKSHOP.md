# Workshop 08 Catch the clone that edits yesterday

Bug report: “Editing a copied plan edits the original. Clearing today's plan also clears the saved history. Two sessions with identical values fail the equality check, and planned work counts as completed.” Write small regressions, watch them fail, then repair the behavior.

Read the [lesson](LESSON.md) first. Split the work into test structure and equality, then copying and history, then boundaries and review.

## Prepare two working files

Keep **`kotlin-labs`** open in Android Studio. After updating the course, use **Sync Project with Gradle Files** and wait for indexing. From its terminal:

```sh
./gradlew --console=plain :ch08-tests-and-kotlin-pitfalls:run
```

On Windows use `./gradlew.bat`. This prepares two editable files:

```text
ch08-tests-and-kotlin-pitfalls/src/main/kotlin/workshop/ch08/SessionSnapshots.kt
ch08-tests-and-kotlin-pitfalls/src/practiceTest/kotlin/workshop/ch08/SnapshotPracticeTest.kt
```

Both paths are Git-ignored. Edit these working files only; keep the [production starter](../../kotlin-labs/ch08-tests-and-kotlin-pitfalls/src/starter/kotlin/workshop/ch08/SessionSnapshots.kt), [test starter](../../kotlin-labs/ch08-tests-and-kotlin-pitfalls/src/starterTest/kotlin/workshop/ch08/SnapshotPracticeTest.kt), and fixed acceptance tests unchanged. Existing work is preserved in both paths. Use the [setup guide](../../kotlin-labs/README.md) if needed. Console experiments can run in [Kotlin Playground](https://play.kotlinlang.org/); the test commands need the local workspace.

The deliberate bugs print:

```text
Original topics: [Kotlin, KMP]
Saved topics: []
Equal sessions: false
Completed minutes: 75
```

This is a successful program run with incorrect behavior, not a setup failure. Predict the corrected four lines before changing code.

## Run the first test and identify what it proves

The starter test contains one passing empty-input example. Run only your test class:

```sh
./gradlew --console=plain :ch08-tests-and-kotlin-pitfalls:test --tests workshop.ch08.SnapshotPracticeTest
```

That pass establishes only the empty-input case. It leaves the four bugs unchecked. Add tests to this class, with the `kotlin.test` imports shown in the lesson. Keep each fixture local to its test. Use a descriptive name and an assertion about observable behavior, not just `println` or a call that cannot fail.

For each mission: add the regression first, run this filtered command and confirm an assertion failure, then repair production code and rerun. Avoid changing expected values to match the bug. A compiler error is not the intended red test; fix it so the assertion can execute.

## Agree on the contracts

- `sameSessionValue` compares all supplied data-class properties by value. Separate equivalent instances compare true; a different ID, topic, duration, or completion state compares false. This helper is not an ID-only identity check.
- `independentPlan` preserves title, session order, and repeats. Its editable list must be independent in both directions. `StudySession` has immutable properties, so list independence is enough here; no generic deep-copy mechanism is needed.
- `PlanHistory.save` appends a version containing the values at save time. Saving a plan again adds another version, even if its title matches. It does not replace a version by title or deduplicate entries.
- `snapshots()` returns versions in save order. Later saves cannot change an earlier outer result. Editing a returned plan's session list must change neither stored history nor another returned version. Each history instance owns its own state.
- `completedMinutes` totals only completed entries as `Long`, preserving input. Empty and planned-only input return `0L`. Count entries as supplied, including repeats; deduplication belongs upstream.
- Inputs already have valid topics and durations in `1..120`. Keep validation out of this mission. The model's mutable plan list is intentional: tests must expose where it is shared.

## Mission 1 Values are not addresses

Create two separately constructed equivalent sessions or use `copy()` to obtain the second. Write an assertion that `sameSessionValue` returns true, and confirm the starter fails. A same-instance-only test would miss this bug.

Repair the helper. Add at least one changed-property case that must return false. When distinct identity is part of the setup, `assertNotSame` can document it; the helper's required result is a value decision.

## Mission 2 A copied wrapper can share its contents

Create a nonempty plan. Obtain `independentPlan`, edit the copied list, and assert the original's contents stay the same. Confirm failure before changing the helper. Repair the ownership boundary while keeping the public models and function signatures.

Add a check in the other direction: edit the original after copying and read the copy. Also try an empty plan, then add a session only to its copy. Merely comparing the plans immediately after creation does not establish editing independence.

## Mission 3 Save history without leaving editing paths into it

Write separate regressions for these two paths:

1. Save a plan, edit or clear its input list, then read history. The saved values must survive.
2. Save a plan, edit a plan obtained from `snapshots()`, then read again. The stored values must survive.

Fix capture on save and protection on read. Copying just the outer `List<StudyPlan>` leaves editable lists inside its elements. Check that two saves of the same source preserve separate versions and that a previously returned history list does not grow when another version is saved.

Use controlled list edits through the model's public `MutableList`; do not cast the read-only outer list to force mutation.

## Mission 4 Boundaries that say something

Write a planned-only test that expects `0L`, see it fail, and repair `completedMinutes`. Add completed durations **1** and **120**, expecting **121L**. Then test a mixed-status input. Keep the existing empty-input example.

Explain why an all-completed test would not catch the starter's missing status selection. Use expected constants or explicit expected lists; calculating the expectation with the same flawed implementation can hide a bug.

After the repairs, `run` should print:

```text
Original topics: [Kotlin]
Saved topics: [Kotlin]
Equal sessions: true
Completed minutes: 30
```

Use the [progressive hints](HINTS.md) if needed.

## Check both your tests and the acceptance suite

Run without the filter:

```sh
./gradlew --console=plain :ch08-tests-and-kotlin-pitfalls:test
```

This runs **26 fixed acceptance tests plus however many tests you have written** in the working test file. The fresh starter has one learner test, so its unfiltered total is 27. Your final count will differ as you add regressions. The HTML report is at `ch08-tests-and-kotlin-pitfalls/build/reports/tests/test/index.html`.

Acceptance tests check the product's behavior; they do not grade the strength of your own tests. Before finishing, temporarily restore the original wrong equality operator in your working production file. Run only your test class and confirm it fails. Restore the fix immediately, then run the full suite again. Keep changes confined to the working copies.

To start over, move either working file somewhere safe outside its source folder. `:ch08-tests-and-kotlin-pitfalls:preparePractice` recreates missing files and preserves ones still present. `:ch08-tests-and-kotlin-pitfalls:preparePracticeTests` prepares only a missing test file. `clean` keeps both answers.

## Explain and revisit

Explain in 60 seconds, or write your answer: “Why can `copy()` produce another plan but still share its list? Why is a read-only return type insufficient for this history contract?” Include one regression that distinguishes a real fix from a shallow outer-list copy.

Changed request: immediately after the sample's first `history.save(original)`, append `StudySession(9, "Flow", 60, completed = true)` to the original and save it again, then let the sample clear the original. Predict both saved topic lists and the second saved plan's completed-minute total. Add a test for the two versions before running the change. Restore the sample afterward.

On another day, repeat a regression from memory with different IDs and topics. Explain the arrange, act, and assert steps before opening the solution. Public sharing is optional.

## Kotlin foundations checkpoint

After the missions, try a separate scratch tracker from this written requirement: accept a supplied list of valid sessions, keep the latest record per ID without moving first-insertion order, complete a requested ID without changing the caller's list, and print completed minutes plus alphabetically sorted completed topics. No interactive input or Android setup is required. Write tests for empty input, a missing ID, a replacement, and caller preservation; explain the collection choices. Use the earlier lessons for prerequisites, then try again without them. This transfer task has no supplied implementation or automatic completion claim.

For a related map problem, count exact topic-name occurrences in a list, including repeated names. Write your own examples and explain how the work grows with the input. These checkpoint tasks are independent practice, not additional requirements hidden in the 26 acceptance tests.

**[Review the worked solution](SOLUTION.md)** · [Review the lesson](LESSON.md)
