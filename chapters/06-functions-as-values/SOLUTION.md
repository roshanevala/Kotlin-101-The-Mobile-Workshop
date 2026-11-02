# Solution 06 Rules are values; previews do work

Try the [workshop](WORKSHOP.md) before comparing with the [reference implementation](../../kotlin-labs/ch06-functions-as-values/src/solution/kotlin/workshop/ch06/SessionFilters.kt). Keep your answer in `src/main`; the reference is separately compiled.

## Compose now, evaluate later

`allOf` returns `{ session -> first(session) && second(session) }`. Creating this lambda does no checking. Its invocation supplies the session and applies the rules in order. `&&` skips the second if the first rejects. Captured mutable variables are read when the rule executes, so the function cannot safely cache a decision from an earlier call.

`selectSessions` delegates to `sessions.filter(predicate)`. This keeps order and repeats, invokes the supplied decision once per entry, and creates the selected list. The selection does not invent a completion rule or deduplicate by topic.

The extension returns `"$topic: $minutes min"`. Its receiver supplies the properties. It does not change the model or depend on status.

## Make the processing contract explicit

Both previews validate a negative limit before doing any work. This matters because an eager chain with `take(-1)` only at the end would invoke callbacks before reporting the invalid argument.

The eager implementation is `sessions.filter(predicate).map(transform).take(limit)`. The lazy implementation inserts `asSequence()` at the beginning and `toList()` at the end. The latter returns a materialized snapshot rather than a deferred view of the caller's list.

For the sample, the combined rule accepts Kotlin and KMP. Trace the operations with limit 1:

| Eager order | Lazy order |
| --- | --- |
| Filter Android, Kotlin, KMP, Flow | Filter Android, Kotlin |
| Map Kotlin, KMP | Map Kotlin |
| Take the first mapped string | Stop after one mapped string |

Both return `[Kotlin: 30 min]`. Counting calls demonstrates avoided callback work for this data and ordering. It is not a timing benchmark. A sequence has overhead, and a list chain may be clearer for a small full report. The normal callbacks should avoid side effects; counters deliberately expose them for this experiment.

## Bug Museum The transformation that went missing

**Symptom:** replacing `map` with `also` keeps `StudySession` entries instead of the formatted strings. In a helper declared to return `List<String>`, this causes a type error.

**Cause:** the list produced by `matches.map(transform)` inside `also` is discarded. `also` returns the original receiver regardless of the block's last expression.

**Repair:** use `map(transform)` directly. `let { matches -> matches.map(transform) }` would also return the new list, but adds an unnecessary scope here.

**Useful check:** inspect the expression's type before converting it to text. Printing a list can conceal which element type the program actually returns. `apply` also returns its receiver and would not solve this bug.

## Verify the reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch06-functions-as-values:runSolution :ch06-functions-as-values:checkSolution
```

The run prints:

```text
Selected topics: [Kotlin, KMP]
Eager: [Kotlin: 30 min] (filter=4, map=2)
Lazy: [Kotlin: 30 min] (filter=2, map=1)
```

All 26 acceptance tests are shared: `test` checks your working copy and `checkSolution` checks the separate reference. The test suite measures behavior and callback order, not speed.

## Change request answers

| Limit | Returned labels, both implementations | Eager filter / map | Lazy filter / map |
| --- | --- | --- | --- |
| 2 | `[Kotlin: 30 min, KMP: 45 min]` | 4 / 2 | 3 / 2 |
| 0 | `[]` | 4 / 2 | 0 / 0 |
| 5 | `[Kotlin: 30 min, KMP: 45 min]` | 4 / 2 | 4 / 2 |

With too few matches, the lazy pipeline must inspect the remaining input to discover it has finished. An eager zero-limit chain still evaluates its preceding list stages under this exercise's explicit contract. A production helper could choose a zero-limit fast path; it would then have a different callback contract.

For later recall, `anyOf` returns `{ session -> first(session) || second(session) }`. `||` skips the second after the first accepts. `preview.let { it.size }` returns an `Int`; `preview.also { println(it.size) }` logs the count and returns the same list.

**[Next: Chapter 07 Abstraction and dependencies](../07-abstraction-and-dependencies/LESSON.md)** introduces interfaces, composition, and replaceable storage.

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
