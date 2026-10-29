# Chapter 05 Worked solution

Try the [workshop](WORKSHOP.md) first. The complete implementation lives in the separate [reference file](../../kotlin-labs/ch05-collections-and-complexity/src/solution/kotlin/workshop/ch05/SessionCollections.kt).

## Resolve identity before interpreting a record

`uniqueSessions` creates a set of integer IDs and a result list. It walks the input in order. When adding an ID to the set returns `true`, the record is appended to the result. Later records with that ID are skipped, regardless of topic, duration, or status.

The set answers whether an ID has appeared; the list preserves the retained records' encounter order. A set of full models would use data-class equality across every constructor property, which would keep conflicting records with the same ID.

## Accumulate only completed work

`completedMinutesByTopic` reuses the unique records before selecting completed sessions. For each completed session it performs:

```kotlin
totals[session.topic] = (totals[session.topic] ?: 0L) + session.minutes.toLong()
```

A missing group starts at zero; an existing group receives the additional duration. Topic strings are exact keys, so `Kotlin` and `kotlin` stay separate. Planned-only topics never get an entry. Totals use `Long` arithmetic to avoid restricting a history to an `Int` sum.

This stage assumes the input models already meet the workshop's topic and duration contract. It does not parse text or silently repair invalid models.

## Build the index and display order separately

The index stores each unique record under its ID. It includes both statuses. Deduplicating first means assigning into this map cannot accidentally let a later conflicting record win. Missing IDs retain the ordinary nullable map lookup behavior.

`completedTopics` takes the completed-total map's keys and calls `sorted()`. Keys are already unique; natural string order supplies deterministic output without rearranging the input history. Map entry order is irrelevant to the contract.

Each helper builds its results locally. The returned types expose read operations, and later additions to a caller's mutable input do not change an earlier result. These are shallow snapshots: the same session objects can appear in the input, unique list, and index. Their current model has no mutable child; future mutable properties would require another ownership decision.

## Account for the work and storage

Let `n` be incoming records, `u` unique IDs, and `k` completed topics. Under the hash-backed JVM collection assumptions in the lesson, deduplication takes O(n) average time and O(u) extra storage. Totals and index construction also take O(n) average time; they reuse a deduplicated list and allocate their own maps. The topic report adds O(k log k) sorting comparisons, giving O(n + k log k) average time overall, with O(u + k) extra storage.

This accounts for building an index. Reusing it for `q` subsequent lookups adds O(q) average work; building it again for every lookup would lose that benefit. A list scan can be simpler for one lookup. These are cost models, not timing measurements or promises for every map implementation.

## Bug Museum The duplicate wearing a new label

**Symptom:** the same session ID contributes twice after its topic or minutes change.

**Cause:** a set of full data objects considers the records different. Another tempting implementation filters for completed sessions first, allowing a later completed duplicate to replace an earlier planned record. An unconditional map assignment over the original history instead makes the last record win.

**Repair:** apply the first-record ID rule once, before interpreting status or calculating totals. Reuse that rule across helpers.

**Useful check:** place a planned record first, followed by a completed record with the same ID and a different topic. Neither should contribute completed minutes for that ID.

## Verify the reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch05-collections-and-complexity:runSolution :ch05-collections-and-complexity:checkSolution
```

The sample prints `Completed topics: [KMP, Kotlin]`. All 32 acceptance tests are shared: `checkSolution` checks the separate reference, while `test` checks your working copy. The two implementations compile independently.

## Change request answers

The appended completed Flow record has ID `2`, whose earlier record was a planned Kotlin session. The first record remains authoritative. The new ID `4` contributes another 15 completed Kotlin minutes, making Kotlin's total 45. There are four unique IDs, no Flow total, and the completed topic list remains `[KMP, Kotlin]`. ID `2` stays planned, and ID `99` is absent.

On a later day, move the conflicting Flow record to the beginning of the input and predict the report again. Under this exercise's first-record rule, Flow now contributes 60 minutes. Kotlin still has 45 completed minutes from IDs `3` and `4`; the report becomes `[Flow, KMP, Kotlin]`. Changing order changes which conflicting record wins, so this rule should always be a deliberate product decision.

**Next Chapter 06 will introduce functions as values and lambdas. Its lesson is planned.**

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
