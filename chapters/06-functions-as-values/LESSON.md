# Lesson 06 Functions as values

Your planner has acquired three almost identical report functions: one for completed sessions, one for long sessions, and one for both. The changing part is a decision. Kotlin lets a function receive that decision as a value.

Read after [Chapter 05](../05-collections-and-complexity/LESSON.md). We will move from loops to callbacks, add readable helpers to models, and count the work in a preview. Each Kotlin block below is a complete, independent program; run one at a time in [Kotlin Playground](https://play.kotlinlang.org/) or a scratch file. Local setup is in the [exercise guide](../../kotlin-labs/README.md).

## Store and pass a function

```kotlin
fun accepts(minutes: Int, rule: (Int) -> Boolean): Boolean {
    return rule(minutes)
}

fun main() {
    val longEnough: (Int) -> Boolean = { minutes -> minutes >= 30 }
    println(longEnough(29))
    println(accepts(30, longEnough))
    println(accepts(45) { it <= 60 })
}
```

Output:

```text
false
true
true
```

`(Int) -> Boolean` means a function receiving one `Int` and producing a `Boolean`. Calling `longEnough(29)` executes it; passing `longEnough` passes the function. `accepts` is a **higher-order function** because it takes a function argument. That supplied function is also called a callback.

`{ minutes -> minutes >= 30 }` is a lambda. Its last expression supplies the result. A single unnamed parameter can be called `it`. When the final argument is a lambda, it can sit outside the parentheses, as in the last call. Use a name when `it` becomes hard to follow. Prefer final expressions here; a bare `return` has additional rules outside this lesson. See [function types and lambdas](https://kotlinlang.org/docs/lambdas.html).

## Replace selection and transformation loops

```kotlin
data class Session(val topic: String, val minutes: Int, val completed: Boolean)

fun main() {
    val sessions = listOf(
        Session("Android", 20, false),
        Session("Kotlin", 30, true),
        Session("KMP", 45, true)
    )
    val selected = sessions.filter { session -> session.completed }
    val labels = selected.map { session -> "${session.topic}: ${session.minutes} min" }
    println(labels)
    println(sessions.size)
}
```

Output:

```text
[Kotlin: 30 min, KMP: 45 min]
3
```

A **predicate** returns a Boolean decision. `filter` keeps entries whose predicate is true. `map` transforms each entry to its callback result; here a `Session` becomes a `String`. Both preserve encounter order, including repeats; neither deduplicates. These list operations create results without changing the input. The library handles iteration; the lambda describes one element. Compare the loops from Chapter 05 with Kotlin's [filtering](https://kotlinlang.org/docs/collection-filtering.html) and [mapping](https://kotlinlang.org/docs/collection-transformations.html) guidance.

## Return a rule and combine decisions

```kotlin
fun both(first: (Int) -> Boolean, second: (Int) -> Boolean): (Int) -> Boolean {
    return { value -> first(value) && second(value) }
}

fun main() {
    var maximum = 60
    val allowed = both({ it >= 30 }, { it <= maximum })
    println(allowed(45))
    maximum = 40
    println(allowed(45))
}
```

Output:

```text
true
false
```

Constructing `allowed` creates a function; calling it evaluates the decisions. `&&` skips the second decision if the first is false. The lambda captures `maximum` and reads its current value when invoked. Capturing a mutable variable does not freeze a snapshot. Use stable values for predictable rules; the workshop's counters deliberately capture mutable variables to observe execution.

## Add an extension with a receiver

```kotlin
data class Session(val topic: String, val minutes: Int)

fun Session.label(): String {
    return "${this.topic}: $minutes min"
}

fun main() {
    val session = Session("Kotlin", 30)
    println(session.label())
    println(session)
}
```

Output:

```text
Kotlin: 30 min
Session(topic=Kotlin, minutes=30)
```

`Session` before the dot is the **receiver type**. Inside the extension, `this` is the receiving session; its accessible members can be used without `this.`. An extension adds call syntax without modifying the class or granting access to private members. Resolution uses the declared receiver type, and an applicable class member takes precedence over an extension of the same signature. Across packages, import an extension like other top-level functions. See [extensions](https://kotlinlang.org/docs/extensions.html).

## Give a lambda a receiver

```kotlin
fun describeTopic(topic: String, describe: String.() -> String): String {
    return topic.describe()
}

fun main() {
    val description = describeTopic("Kotlin") {
        "$this has $length letters"
    }
    println(description)
}
```

Output:

```text
Kotlin has 6 letters
```

`String.() -> String` is a function type with a `String` receiver, no ordinary parameters, and a `String` result. The call supplies the receiver; inside the block, `this` and `length` refer to that string. An ordinary `(String) -> String` instead receives a parameter such as `it`. This distinction explains the scope functions next. It is also a foundation for reading later Android APIs; no UI knowledge is needed yet.

## Choose a scope function by its result

| Function | Object inside the block | Expression returns |
| --- | --- | --- |
| `let` | Parameter, usually `it` | Last expression |
| `also` | Parameter, usually `it` | Original receiver |
| `apply` | Receiver, `this` | Original receiver |
| `run` on an object | Receiver, `this` | Last expression |
| `with(object)` | Receiver, `this` | Last expression |

```kotlin
fun main() {
    val topics = mutableListOf<String>().apply {
        add("Kotlin")
        add("KMP")
    }.also {
        println("Prepared ${it.size} topics")
    }
    println(topics.let { it.size })
    println(topics.run { first() })
    println(with(topics) { last() })
    val missing: String? = null
    println(missing?.let { "Topic: $it" } ?: "Choose a topic")
}
```

Output:

```text
Prepared 2 topics
2
Kotlin
KMP
Choose a topic
```

`apply` configures an object; `also` observes it while returning the same object. Neither copies it. `let`, `run`, and `with` compute a result. The safe call skips `let` for null. Scope functions do not add safety by themselves; avoid nesting them when receiver ownership becomes unclear. A named local variable is often easier to read. See [scope functions](https://kotlinlang.org/docs/scope-functions.html).

## Count eager and lazy work

Predict the callback counts before running this program:

```kotlin
fun main() {
    val minutes = listOf(20, 30, 45, 15)
    var filters = 0
    var maps = 0
    val keep: (Int) -> Boolean = { filters += 1; it >= 30 }
    val label: (Int) -> String = { maps += 1; "$it min" }
    val eager = minutes.filter(keep).map(label).take(1)
    println("$eager filter=$filters map=$maps")
    filters = 0
    maps = 0
    val lazy = minutes.asSequence().filter(keep).map(label).take(1).toList()
    println("$lazy filter=$filters map=$maps")
}
```

Output:

```text
[30 min] filter=4 map=2
[30 min] filter=2 map=1
```

On the list, `filter` finishes, then `map` finishes, then `take` selects a prefix. `take(0)` returns empty; a negative count is invalid, and an oversized count keeps all available entries. See [collection parts](https://kotlinlang.org/docs/collection-parts.html) and the [`take` contract](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/take.html).

`asSequence` enables lazy processing. `filter`, `map`, and `take` build a pipeline; `toList` is terminal and requests results. Items pass through stages one at a time, stopping when the limit is satisfied. Here only 20 and 30 reach the predicate, and only 30 reaches the mapper. A zero limit requests no elements. See [sequences](https://kotlinlang.org/docs/sequences.html).

These counts measure callback work, not elapsed time. Both approaches can scan all input when matches are scarce. Sequences avoid intermediate filtered and mapped lists in this pipeline, but have execution overhead and still build the final list. A small eager chain may be the clearest choice. Sorting before taking would require consuming all input; laziness alone does not guarantee early stopping.

## A sequence is not a saved result

```kotlin
fun main() {
    val topics = mutableListOf("Kotlin")
    var calls = 0
    val pipeline = topics.asSequence().map {
        calls += 1
        it.uppercase()
    }
    println(calls)
    topics.add("KMP")
    val saved = pipeline.toList()
    println(saved)
    println(calls)
    topics.add("Flow")
    println(saved)
    println(pipeline.toList())
    println(calls)
}
```

Output:

```text
0
[KOTLIN, KMP]
2
[KOTLIN, KMP]
[KOTLIN, KMP, FLOW]
5
```

This list-backed pipeline reads the source at traversal time and runs its mapper again on a later traversal. The materialized string list stays unchanged. Some other sequence sources allow only one traversal. Keep callback side effects out of normal transformations: changing when work executes can change behavior. The counting examples are controlled experiments.

## Check your understanding

1. How do passing a predicate and invoking it differ?
2. Which callbacks run when the first half of a combined `&&` rule rejects an item?
3. Why does `also { it.size }` return the object, rather than its size?
4. Does a lambda receiver copy or modify its object automatically?
5. What does the counter experiment establish? What would require a timing benchmark?
6. Why materialize a preview before returning it to a caller who expects a snapshot?

The workshop uses `require(limit >= 0) { "limit must be nonnegative" }` before processing. As in earlier validation lessons, `require` throws `IllegalArgumentException` when its condition is false. The braces supply the error-message lambda.

**[Continue to the workshop](WORKSHOP.md)** · [Chapter entry](README.md)
