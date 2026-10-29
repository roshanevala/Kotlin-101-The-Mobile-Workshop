# Workshop 05 Stop the double-counted progress

Bug report: “My session history repeats a record, and the totals count it twice. If a later record disagrees, nobody knows which version wins.” Build a predictable report with one rule: **the first record for each session ID wins**.

Read the [lesson](LESSON.md) first. Work through four helpers in order. The supplied models use concepts from Chapter 04 and add an `id` for identity; this exercise does not depend on that chapter's code.

## Sync, run, and open your working copy

Keep **`kotlin-labs`** open in Android Studio. **Sync Project with Gradle Files** after updating the course, then wait for sync and indexing. This imports the new module so the Run arrow can appear beside its `main` function.

From its terminal, run:

```sh
./gradlew --console=plain :ch05-collections-and-complexity:run
```

On Windows, use `./gradlew.bat`. The command creates your editable working file:

```text
ch05-collections-and-complexity/src/main/kotlin/workshop/ch05/SessionCollections.kt
```

Edit **`src/main` only**. It stays out of normal Git commits, and existing answers are preserved. The tracked [starter template](../../kotlin-labs/ch05-collections-and-complexity/src/starter/kotlin/workshop/ch05/SessionCollections.kt) is for copying, not IDE execution. A browser attempt can use that template in [Kotlin Playground](https://play.kotlinlang.org/). See the [setup guide](../../kotlin-labs/README.md) if needed.

The untouched starter prints:

```text
Completed topics: []
```

Inspect the four records in `main`. Predict the distinct completed topics before changing anything. The program runs, but the report helper currently ignores its input.

## Agree on the report's rules

- Two records are duplicates when their **IDs match**, even if their other properties differ. Different IDs describe different sessions, even when the remaining values match.
- Keep the first record for each ID, in its original position. Resolve duplicates **before** selecting completed sessions.
- Inputs already have nonblank topics and durations in `1..120`. Parsing, cleanup, and duration validation belong to earlier stages. Topic keys are used exactly as supplied and are case sensitive.
- An ID is an `Int` key, not a list index. Zero and negative IDs work as keys too; this mission does not validate IDs.
- Do not mutate the input list. Returned collections are snapshots: later additions or removals in a caller's mutable input must not change an earlier result. Expose the supplied read-only return types, without claiming deep immutability.

## Mission 1 Keep one record per ID

Implement `uniqueSessions(sessions: List<StudySession>): List<StudySession>`.

Use a set of seen IDs and a separate result list. Visit the input once, keeping a record only the first time its ID appears. Do not sort the records or repeatedly scan the growing result list for each incoming ID.

| Incoming IDs | Result IDs |
| --- | --- |
| Empty | Empty |
| `9, 2, 9, 7, 2` | `9, 2, 7` |
| `7, 7` with conflicting topic, minutes, or status | One record: the first one, unchanged |

A set of whole `StudySession` objects uses all data-class constructor properties for equality. That is not the duplicate rule here.

## Mission 2 Total completed minutes by topic

Implement `completedMinutesByTopic(sessions: List<StudySession>): Map<String, Long>`.

Reuse `uniqueSessions`. From that result, include only sessions with `COMPLETED` status. Accumulate their minutes by topic. Use `Long` totals, a `0L` starting value for an unseen topic, and `toLong()` when adding an integer duration.

Topics with only planned sessions do not appear in the map. If an ID's first record is planned and a later duplicate is completed, neither contributes completed minutes. The later record does not override the first.

For `main`'s fixture, the map has exactly:

| Key | Value |
| --- | --- |
| `Kotlin` | `30L` |
| `KMP` | `45L` |

Empty input or planned-only input produces an empty map. Map entry order is not part of this contract.

## Mission 3 Build a lookup by ID

Implement `indexSessionsById(sessions: List<StudySession>): Map<Int, StudySession>`.

Reuse the duplicate rule and map each unique ID to its first record. Include planned and completed sessions. For the fixture, ID `2` returns the planned Kotlin session, while ID `99` returns `null` through normal map lookup.

Do not use list positions as keys. Be careful with unconditional map assignments over the original input: a later duplicate would replace the required first record.

## Mission 4 List the report's topics

Implement `completedTopics(sessions: List<StudySession>): List<String>`.

Reuse the completed-totals map, take its keys, and return them sorted in natural, case-sensitive string order. A topic appears once. No completed sessions means an empty list. Sorting this result must not reorder the input sessions.

The original `main` should now print:

```text
Completed topics: [KMP, Kotlin]
```

Use the loops and collection operations taught in the lesson. Lambdas and more compact transformations come in Chapter 06. If stuck, open the [optional hints](HINTS.md) one at a time.

## Check your work

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch05-collections-and-complexity:test
```

The **32 supplied tests** cover empty inputs, order preservation, repeated and conflicting IDs, exact topic keys, completed-only totals, duration endpoints, missing lookups, snapshots, and independent calls. Framework code is supplied; writing tests comes later.

Some tests fail on the untouched starter by design. An expected-versus-actual failure means the code ran; a compile error means Kotlin could not build it. Read the failing names and complete one helper at a time. The tests check observable behavior, not whether an algorithm has the intended complexity.

The report is generated at `kotlin-labs/ch05-collections-and-complexity/build/reports/tests/test/index.html`.

## Try a changed request

Append these records to `main`'s fixture: ID `2`, topic `Flow`, 60 minutes, completed; then ID `4`, topic `Kotlin`, 15 minutes, completed. Temporarily print the helper results and explain these expectations:

| Check | Expected value |
| --- | --- |
| Unique session count | `4` |
| Completed Kotlin minutes | `45L` |
| Completed Flow minutes lookup | `null` |
| Status found at ID `2` | `PLANNED` |
| Lookup of ID `99` | `null` |
| Completed topics | `[KMP, Kotlin]` |

Why does the later Flow record not count? Then restore the original fixture and output.

For an interview rehearsal, explain why a list, set, and map each have a job here. Compare building an index once with scanning the list for every lookup, and include the index's memory cost. A spoken minute or a short written answer is enough.

## Finish the mission

- [ ] I can explain the duplicate-ID rule and why it is applied before status filtering.
- [ ] My helpers preserve input order and values and return independent collection snapshots.
- [ ] My working copy passes all 32 checks.
- [ ] I can explain average hash lookup cost and the cost of building an index.
- [ ] I tried the changed request and can explain its totals without copying the solution.

Your answer is local and is not backed up by normal course commits. To restart, save it outside the chapter's `src` folders before copying the tracked starter template over the working file. Git history does not contain edits to the ignored copy.

**[Next Review the explained solution](SOLUTION.md)**

[Back to the lesson](LESSON.md) · [Chapter overview](README.md)
