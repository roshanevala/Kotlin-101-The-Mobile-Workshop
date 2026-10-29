# Lesson 05 Collections and complexity

One session fits in a model. A history of sessions needs collections: a sequence to display, a way to recognize repeated IDs, and a lookup for a requested session. Choosing the collection also changes how much work the program does as the history grows.

Build on [Chapter 04](../04-classes-and-modeling/LESSON.md). Paste one complete example at a time into [Kotlin Playground](https://play.kotlinlang.org/), predicting its output first. Take two passes if helpful: collection types and ownership, then transformations and complexity.

## Keep a sequence in a list

```kotlin
fun main() {
    val topics: List<String> = listOf("Kotlin", "KMP", "Kotlin")
    println(topics.size)
    println(topics[1])
    for (topic in topics) {
        println(topic)
    }
}
```

This prints:

```text
3
KMP
Kotlin
KMP
Kotlin
```

A `List` preserves element order and permits repeats. Indices start at zero, so `[1]` reads the second element. An index outside the list throws an exception; an empty list has no valid index. A `for` loop visits the elements without requiring a manual index.

`List<String>` specifies the element type. The angle brackets are type arguments, not a comparison. Kotlin can infer the type from `listOf("Kotlin")`; for an empty collection, use an explicit type such as `emptyList<String>()`. Our functions will accept `List<StudySession>` in the same way.

## Separate a read-only view from a snapshot

```kotlin
fun main() {
    val editable = mutableListOf("Kotlin")
    val view: List<String> = editable
    val snapshot = editable.toList()
    editable.add("Flow")
    println(view)
    println(snapshot)
}
```

This prints:

```text
[Kotlin, Flow]
[Kotlin]
```

The mutable list supports `add`, removal, and element replacement. Assigning it to `val` prevents reassignment of the variable, while its contents can still change.

The `List` type exposes read operations. You cannot call `view.add`, but another alias can change the backing collection. A read-only API is not a guarantee of an immutable object.

`toList()` takes a snapshot of the elements. Later additions or removals in the original do not appear in that snapshot. The copy is shallow: mutable element objects would still be shared, just as the meter was shared in Chapter 04. Our session model has read-only properties holding strings, integers, and enum values.

## Recognize an item with a set

```kotlin
fun main() {
    val seen = mutableSetOf<Int>()
    println(seen.add(7))
    println(seen.add(7))
    println(7 in seen)
    println(seen.size)
}
```

This prints:

```text
true
false
true
1
```

A set keeps unique elements. `add` returns `true` when it inserts an element and `false` when an equal element already exists. That return value can decide whether a record should be kept. `in` checks membership.

Use a set when the question is “Have I seen this ID?” Choose a list when positions and repetitions matter. General `Set` code should not depend on sorted order; request an explicit order when displaying its contents.

## Associate a key with a value in a map

```kotlin
fun main() {
    val labels = mutableMapOf<Int, String>()
    labels[7] = "First"
    println(labels[7])
    println(labels[8])
    labels[7] = "Replacement"
    println(labels.size)
    println(labels[7])
}
```

This prints:

```text
First
null
1
Replacement
```

`Map<Int, String>` has integer keys and string values. A key identifies one entry; assigning another value at that key replaces the old value. This matters when duplicate records conflict: an unconditional assignment for every record makes the last one win.

Reading `[key]` returns a nullable value because the key may be absent. Use the safe calls and Elvis operator from Chapter 03 to handle that case. A map's integer key is an ID, not a list position.

For a fixed example, `mapOf(7 to "Kotlin")` creates a read-only map API. The `to` notation constructs a key-value pair. Empty helpers can use `emptyMap<Int, String>()`; the compiler can also infer these types from a declared return type.

## Filter and transform with an ordinary loop

```kotlin
fun main() {
    val numbers = listOf(2, 5, 8)
    val doubledEvens = mutableListOf<Int>()
    for (number in numbers) {
        if (number % 2 == 0) {
            doubledEvens.add(number * 2)
        }
    }
    println(doubledEvens)
    println(numbers)
}
```

This prints:

```text
[4, 16]
[2, 5, 8]
```

The condition filters: it selects even values. Multiplication transforms: it computes a new value for each selected element. A separate result list preserves the original.

These ideas do not require new function syntax. Chapter 06 will introduce lambdas and collection operations such as `filter` and `map`, after we can explain the work they perform.

## Group values and accumulate totals

```kotlin
data class Delivery(val zone: String, val parcels: Int)

fun main() {
    val deliveries = listOf(
        Delivery("North", 2),
        Delivery("South", 1),
        Delivery("North", 3)
    )
    val totals = mutableMapOf<String, Long>()
    for (delivery in deliveries) {
        val previous = totals[delivery.zone] ?: 0L
        totals[delivery.zone] = previous + delivery.parcels.toLong()
    }
    println(totals["North"])
    println(totals["South"])
}
```

This prints:

```text
5
1
```

The zone chooses the group. Each iteration reads that group's previous total, adds the current amount, and writes the updated total. A new zone has no entry, so `?: 0L` starts its total at zero.

`Long` is a 64-bit integer type. `0L` is a `Long` literal, and `toLong()` explicitly converts the `Int` amount before addition. Our workshop uses `Long` totals so adding many individually valid session durations is not limited to an `Int` total. Larger types still have limits; they do not provide unlimited arithmetic.

## Sort a result without rearranging the input

```kotlin
fun main() {
    val labels = listOf("Flow", "Android", "Flow", "Kotlin")
    println(labels.sorted())
    println(labels)
    val unique = setOf("Flow", "Android", "Flow", "Kotlin")
    println(unique.sorted())
}
```

This prints:

```text
[Android, Flow, Flow, Kotlin]
[Flow, Android, Flow, Kotlin]
[Android, Flow, Kotlin]
```

`sorted()` returns a list in natural order. Sorting a list keeps its repeats; converting to or starting with a set removes equal elements. String natural order is lexicographic and case sensitive, rather than a locale-aware display order.

A map's `keys` property is a set of its keys. Calling `totals.keys.sorted()` therefore gives distinct group names in natural order. It does not mutate the input sessions or change how duplicates were resolved.

## Define what counts as a duplicate

```kotlin
data class Badge(val id: Int, val label: String)

fun main() {
    val badges = listOf(Badge(7, "Explorer"), Badge(7, "Guide"))
    println(badges.toSet().size)
    val ids = mutableSetOf<Int>()
    for (badge in badges) {
        ids.add(badge.id)
    }
    println(ids.size)
}
```

This prints:

```text
2
1
```

The data objects differ in `label`, so a set of whole badges keeps both. A set of IDs recognizes one ID. Collection equality and the product's duplicate rule answer different questions.

Before implementing a report, choose the identity field, what to do when records disagree, and whether output order matters. Our workshop keeps the **first record for each ID**, in encounter order. Apply that rule before filtering by status, so a later completed duplicate cannot replace an earlier planned record.

## Explain how the work grows

Big O describes growth as the input size increases. It does not give milliseconds or make a collection universally faster. Let `n` be the input size, `u` the number of unique IDs, and `k` the number of completed topics.

| Work | Cost model |
| --- | --- |
| Scan a list for one matching ID | O(n) in the worst case |
| Scan the growing result list for every incoming ID | O(n²) in the worst case |
| Test or insert a key in a hash-backed set or map | O(1) on average under suitable hashing assumptions |
| Make one pass while recording seen IDs | O(n) average time and O(u) extra storage |
| Build an index, then make `q` key lookups | O(n + q) average time, including construction |
| Sort `k` topic names | O(k log k) comparisons; name length affects comparison cost |

These hash costs describe the ordinary hash-backed implementations used by this JVM exercise, not every possible `Set` or `Map`. Collisions and implementation choices matter. For an array-backed list, appending is amortized O(1): occasional resizing is spread across many appends. Do not confuse that with saying every append has constant cost.

An index costs time and memory to build, so a single lookup may be simpler as a scan. It becomes useful when the same snapshot will receive repeated lookups. In this mission, report generation is O(n + k log k) average time with extra storage for unique IDs and topic totals. We will practise measuring real workloads later.

## Check your understanding

1. Which collection fits a display sequence, seen IDs, and lookup by ID?
2. Why can a `val` mutable list change, and why can a read-only view reflect that change?
3. What does a map lookup return for a missing key?
4. Why can `toSet()` on whole data objects fail an ID-based duplicate rule?
5. Why must this report deduplicate before filtering completed sessions?
6. What do average hash lookup cost, index construction, and extra storage mean for your design?

**[Next Repair the progress report](WORKSHOP.md)**

[Chapter overview](README.md) · [Previous lesson](../04-classes-and-modeling/LESSON.md) · [Back to the course](../../README.md)

Further reading: Kotlin's [collection types](https://kotlinlang.org/docs/collections-overview.html), [construction and shallow copies](https://kotlinlang.org/docs/constructing-collections.html), [set insertion contract](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-mutable-set/add.html), [map operations](https://kotlinlang.org/docs/map-operations.html), and [ordering](https://kotlinlang.org/docs/collection-ordering.html). JVM cost assumptions follow the JDK 17 [HashMap](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/HashMap.html), [HashSet](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/HashSet.html), [ArrayList](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ArrayList.html), and [comparison sorting](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/List.html#sort(java.util.Comparator)) documentation. Examples use the workspace's pinned Kotlin 2.2.21.
