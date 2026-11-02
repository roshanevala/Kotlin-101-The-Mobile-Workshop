# Workshop 07 One store, replaceable parts

Bug report: “The planner completes a session, but the report still shows no progress. Each part built its own empty store.” Build one in-memory store, give it explicit rules, and pass it to the consumers that need it.

Read the [lesson](LESSON.md) first. Start with the storage and reader view; take a second session for planner behavior, reports, and substitution.

## Sync, run, and open your copy

Keep **`kotlin-labs`** open in Android Studio. After updating the course, use **Sync Project with Gradle Files** and wait for indexing. In its terminal:

```sh
./gradlew --console=plain :ch07-abstraction-and-dependencies:run
```

On Windows use `./gradlew.bat`. The command prepares your editable file:

```text
ch07-abstraction-and-dependencies/src/main/kotlin/workshop/ch07/SessionStorage.kt
```

Edit **`src/main` only**. It is ignored by Git, and existing files are preserved. The [tracked starter](../../kotlin-labs/ch07-abstraction-and-dependencies/src/starter/kotlin/workshop/ch07/SessionStorage.kt) is a template, not the file to edit. For help, use the [setup guide](../../kotlin-labs/README.md). A browser attempt can paste the starter into [Kotlin Playground](https://play.kotlinlang.org/); the wrapper tests need the local workspace.

The untouched program prints:

```text
Complete 7: false
Stored topics: []
Completed minutes: 0
```

These placeholders deliberately do no storage work. That output demonstrates a successful run; it is not a setup failure.

## Predict and explore the supplied contracts

Read `main`. Draw arrows from the planner, reader view, and report to their dependencies. How many `InMemoryStore` objects are created? Predict the final topics and completed-minute total when the placeholders work.

The interfaces are supplied and should keep their signatures:

| Interface | Capability | Type direction |
| --- | --- | --- |
| `Reader<out T>` | `all(): List<T>` and `findById(id): T?` | Produces values |
| `Writer<in T>` | `save(item: T)` | Consumes values |
| `Store<T>` | Both interfaces | Reads and writes; invariant |

A reader is a live source: each invocation observes current storage. Each `all()` result is a materialized snapshot in first-insertion order. A missing ID returns null. `save` inserts a new key or replaces an existing value; replacement keeps the key's original position. Saving the same item twice leaves one record.

This is an editable store: **the latest save for an ID wins**. Chapter 05's imported history kept the first record instead. Use the contract of the current operation.

IDs are arbitrary `Int` keys, including zero and negatives, not list positions. Session inputs already have nonblank topics and durations in `1..120`. Do not add parsing or validation here. Generic values use the caller's key function and need stable identity while stored; do not cast them to `StudySession`. Snapshots are shallow, not deep copies of arbitrary `T`. The supplied session model has only immutable properties.

## Mission 1 Implement generic in-memory storage

Complete `InMemoryStore<T>` using a private map and its supplied `(T) -> Int` key function.

- `save` stores the value at its extracted key, following the replacement rule.
- `findById` retrieves that key or returns null.
- `all` returns a list snapshot of current values, preserving first insertion order. A later save cannot change an earlier returned list.
- Each store instance owns its own data. Empty stores return an empty list.

The class must also work for another model or a string when the caller supplies a suitable key function. Keep its collection private; consumers should need only the interfaces.

## Mission 2 Forward a reader view

Replace `ReaderView`'s placeholder methods with `Reader<T> by source` in its class header. Remove the redundant overrides so calls are forwarded.

Forward each `all` or `findById` invocation to the supplied reader, with the original lookup key. Do not create another store, cache a list at construction, or derive lookups by scanning `all()`. The source decides its lookup behavior. This class advertises only reading operations; other holders may still write to the underlying store.

Behavioral tests check forwarding, not the spelling of `by`. Review the implementation yourself for the requested delegation syntax.

## Mission 3 Use the injected store

Complete `StudyPlanner` using its constructor-supplied `Store<StudySession>`.

`schedule(session)` saves the supplied value unchanged. `complete(id)` asks that store for its current record. If absent, return false and do not save. If present, save a copy with `completed = true` and return true. Preserve ID, topic, and minutes; the original value stays unchanged. Even an already completed record succeeds and is saved again. A later call must ask the store again rather than cache a previous record.

Keep this class composed with a store; it is not a store subclass. Do not instantiate storage inside its methods. The supplied fake in the acceptance tests checks the interaction without relying on your `InMemoryStore` implementation.

## Mission 4 Read progress through a narrow dependency

Complete `ProgressReport.completedMinutes()` using only its supplied `Reader<StudySession>`. Read `all()` once per call and sum the minutes of completed entries as a `Long`. Planned entries contribute nothing; empty input totals `0L`. Read again on later calls so saves and completions become visible. The reader owns the record set; this report does not deduplicate or write.

Use the taught loops or collection operations. `sumOf { it.minutes.toLong() }` chooses a Long sum. The report must work with a reader that has no `save` method, including a small fixed fake.

After all missions, `run` should print:

```text
Complete 7: true
Stored topics: [Kotlin, KMP]
Completed minutes: 75
```

Reveal one level on the [hints page](HINTS.md) if needed.

## Check your work

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch07-abstraction-and-dependencies:test
```

The 30 tests cover generic keys, replacement order, snapshots, independent stores, forwarding, fake-store interactions, completion, and reports. Some intentionally fail on the fresh starter. Assertion failures mean setup compiled the exercise; compilation and dependency errors must be resolved first. The HTML report is at `ch07-abstraction-and-dependencies/build/reports/tests/test/index.html`.

To restore the starter, first move your working `SessionStorage.kt` somewhere safe outside the source folder. Then run `:ch07-abstraction-and-dependencies:preparePractice`. `clean` removes build output while preserving your working file.

## Investigate the invisible-store bug

In a scratch copy of `main`, construct a second `InMemoryStore<StudySession> { it.id }` and give that one to the report through a `ReaderView`. Leave the planner attached to the original store. Predict the printed total, then run it. Why did successful completion not reach the report? Repair the wiring by passing the intended shared dependency. Restore the original program afterward.

## Explain and revisit

Explain in 60 seconds, or write an answer: “Why does the report take a reader, while the planner takes a store? How would a fake reveal accidental concrete storage inside the planner?” Include the distinction between composition and inheritance.

For a changed requirement, save `StudySession(7, "Flow", 60)` through the planner after the sample's completion. Before running, predict the topics, ordering, and report total. Then call `complete(7)` again and predict the new total. Do not edit the reference or acceptance tests.

On a later day, use `InMemoryStore` with a small `Note(number, text)` data class. Save two notes, replace the first, and compare a saved list snapshot with a fresh read. As an optional stretch, adapt the lesson's generic transfer function to copy records from a `Reader<T>` into a `Writer<T>`; decide explicitly how matching IDs should behave.

Finish when the tests pass, you can repair the two-store bug, and you can explain the safe directions of `Reader<out T>` and `Writer<in T>`. Public sharing is optional.

**[Review the worked solution](SOLUTION.md)** · [Review the lesson](LESSON.md)
