# Solution 07 Share the dependency, keep the contract

Try the [workshop](WORKSHOP.md) before comparing with the [reference implementation](../../kotlin-labs/ch07-abstraction-and-dependencies/src/solution/kotlin/workshop/ch07/SessionStorage.kt). Keep your answer in `src/main`; the reference compiles separately.

## Own state in the storage implementation

`InMemoryStore<T>` holds a private `MutableMap<Int, T>`. `save` connects `idOf(item)` to the supplied value; `findById` uses map lookup. `all` returns `items.values.toList()` so later insertions and replacements cannot change an earlier list.

The key function lets the same implementation store sessions, notes, or strings without assuming any properties of `T`. Each instance owns a separate map. The ordered map keeps a key's original slot on replacement, and the latest save supplies that slot's current value. This deliberately differs from Chapter 05's first-record imported-history rule.

A list snapshot is shallow. The supplied immutable session properties make copied completion easy to reason about; arbitrary mutable generic objects would still be shared. This exercise is synchronous, temporary storage, without persistence or concurrent-access guarantees.

## Forward the reader contract

`ReaderView<T>(source: Reader<T>) : Reader<T> by source` delegates the reader's operations. It creates neither a second store nor a cached result. Reads remain live, while each list returned by the in-memory store remains a snapshot.

The view's public API exposes reading operations. This helps consumers request only what they need; it does not prevent someone holding the original store from saving.

`Reader<out T>` can safely produce `T`, and `Writer<in T>` can safely consume it. Their combined `Store<T>` is invariant because it does both. The supplied tests check behavior; reviewing the class headers establishes that the requested variance and `by` syntax are present.

## Depend on the supplied objects

`StudyPlanner.schedule` forwards the session unchanged. `complete` looks up the ID, returns false when absent, or saves `original.copy(completed = true)` and returns true. It also saves already completed records, following the stated contract. Re-reading on each call preserves the latest topic and minutes.

The fake store records a lookup followed by a save. It can supply a different record on the next invocation, revealing caching or accidental use of concrete internal storage. No mocking library is necessary.

`ProgressReport` takes `Reader<StudySession>`. It reads once per invocation, selects completed records, and totals minutes as `Long`. A fixed read-only fake can satisfy it. It has no reason to construct storage, cast to a writable implementation, or change records.

## Bug Museum The successful save nobody could see

**Symptom:** completing session 7 succeeds, but the report returns zero.

**Cause:** the planner writes to one store and the report reads another. Matching class names do not mean matching instances or state.

**Repair:** create one intended store near the entry point and pass it to both paths. The report can receive a reader view over that same store.

**Useful check:** schedule through the planner, then read through the report's dependency. Also run the consumer with a controlled fake; a hardcoded concrete store would fail that substitution check. Shared mutable global state would hide the ownership problem rather than establish a clear dependency.

## Verify the reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch07-abstraction-and-dependencies:runSolution :ch07-abstraction-and-dependencies:checkSolution
```

Output:

```text
Complete 7: true
Stored topics: [Kotlin, KMP]
Completed minutes: 75
```

All 30 acceptance tests are shared: `test` checks your working copy and `checkSolution` checks the separate reference.

## Change request answers

Saving the new planned Flow record at ID 7 replaces Kotlin without moving that slot. Topics become `[Flow, KMP]`; the total falls to **45**, because only KMP is completed. Calling `complete(7)` again saves a completed Flow copy and raises the total to **105**. A previously captured list still contains its old values.

Giving the report a second empty store makes its total **0**, even though completion in the original store succeeds. Share the intended instance to recover 75 for the original sample.

For later recall, a note store uses a key function such as `{ it.number }`. A generic transfer can loop over `reader.all()` and call `writer.save(item)`; a target with the workshop's store contract replaces matching keys and keeps their positions.

**Next: Chapter 08 will introduce test structure and Kotlin pitfalls, including shallow copies and read-only collections. Its lesson is planned.**

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
