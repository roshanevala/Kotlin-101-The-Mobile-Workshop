# Lesson 07 Abstraction and dependencies

The planner says a session was saved. The report says there are no sessions. Both created their own storage, so both are telling the truth about different objects. We need a shared object and an agreement about what storage does.

Read after [Chapter 06](../06-functions-as-values/LESSON.md). This lesson builds from interfaces to generic storage, then connects objects by passing dependencies into constructors. Split it into two sittings if needed: contracts and types first, then ownership and wiring. Each Kotlin block is a complete independent program; run one at a time in [Kotlin Playground](https://play.kotlinlang.org/) or a scratch file. Use the [setup guide](../../kotlin-labs/README.md) for local tools.

## Name a contract with an interface

```kotlin
interface TopicSource {
    fun topics(): List<String>
}

class FixedTopics(private val values: List<String>) : TopicSource {
    override fun topics(): List<String> {
        return values.toList()
    }
}

class TopicCounter(private val source: TopicSource) {
    fun count(): Int {
        return source.topics().size
    }
}

fun main() {
    val first = TopicCounter(FixedTopics(listOf("Kotlin", "KMP")))
    val second = TopicCounter(FixedTopics(listOf("Flow")))
    println(first.count())
    println(second.count())
}
```

Output:

```text
2
1
```

`TopicSource` declares an operation. `FixedTopics : TopicSource` implements that interface, and `override` supplies the operation's body. The counter knows the interface rather than this particular implementation. An interface can provide behavior and declare properties, but cannot hold backing-field state; this class owns the list. See [interfaces](https://kotlinlang.org/docs/interfaces.html).

The counter **has a** source. This is composition. Passing the source into its constructor is **constructor injection**: the caller chooses the dependency. It is ordinary Kotlin, and needs no library. Passing the same object to two consumers lets them share its state; constructing two objects does not.

## Recognize class inheritance

```kotlin
open class TopicLabeler(private val prefix: String) {
    open fun label(topic: String): String {
        return "$prefix $topic"
    }
}

class ExcitedLabeler : TopicLabeler("Study") {
    override fun label(topic: String): String {
        return "${super.label(topic)}!"
    }
}

fun main() {
    val labeler: TopicLabeler = ExcitedLabeler()
    println(labeler.label("Kotlin"))
}
```

Output:

```text
Study Kotlin!
```

A subclass **is a** parent type. `open` permits class inheritance and member overriding; Kotlin classes and concrete members are final by default. `TopicLabeler("Study")` initializes the parent, and `super.label` calls its implementation. An overridden member is selected at runtime, unlike the extension resolution from Chapter 06. A class has only one class parent but can implement multiple interfaces. An abstract class can share state and require implementations without being directly instantiated. See [inheritance](https://kotlinlang.org/docs/inheritance.html) and the [classes and interfaces tour](https://kotlinlang.org/docs/kotlin-tour-intermediate-classes-interfaces.html).

Our planner is not a kind of storage. It uses storage, so composition expresses that relationship. Inheritance is useful when the subtype can honor the parent's contract; sharing a few lines alone does not establish that relationship.

## Make a type parameter explicit

```kotlin
class Box<T>(val value: T)

fun <T> wrap(value: T): List<T> {
    return listOf(value)
}

fun main() {
    val minutes: Box<Int> = Box(30)
    val topic = Box("Kotlin")
    println(minutes.value + 15)
    println(topic.value.uppercase())
    println(wrap("KMP"))
}
```

Output:

```text
45
KOTLIN
[KMP]
```

`T` is a type parameter, chosen per use. `Box<Int>` contains an `Int`; `Box<String>` contains a `String`. The compiler can infer these arguments. A generic function declares its parameters before its name. Keeping the type relationship avoids casts and separate boxes for every model. `uppercase()` returns a string with its letters converted to uppercase. See [generics](https://kotlinlang.org/docs/generics.html).

## Follow the direction of values with variance

```kotlin
interface Source<out T> {
    fun read(): T
}

interface Sink<in T> {
    fun save(value: T)
}

class TextSource : Source<String> {
    override fun read(): String = "Kotlin"
}

class ConsoleSink : Sink<Any> {
    override fun save(value: Any) {
        println(value)
    }
}

fun main() {
    val texts: Source<String> = TextSource()
    val values: Source<Any> = texts
    println(values.read())
    val anySink: Sink<Any> = ConsoleSink()
    val textSink: Sink<String> = anySink
    textSink.save("KMP")
}
```

Output:

```text
Kotlin
KMP
```

Every non-null `String` is an `Any`, the common superclass. A producer of strings can supply an `Any`: `out` permits that direction. A consumer accepting any value can accept strings: `in` permits the reverse direction. A function without an explicit result type, such as `save` here, returns `Unit`, meaning no useful result.

A type that both reads and writes `T` is normally invariant: `Store<String>` cannot become `Store<Any>`, because the latter would permit saving an integer. `MutableList` has the same problem. Variance controls safe substitution, not whether an underlying object changes. Read the [variance explanation](https://kotlinlang.org/docs/generics.html#declaration-site-variance) with these two assignments in mind.

In the scratch program, try `textSink.save(42)` and observe the type error. Restore it afterward. Do not use a cast to bypass the contract.

## Store generic values by a supplied key

```kotlin
class MemoryShelf<T>(private val keyOf: (T) -> Int) {
    private val items = mutableMapOf<Int, T>()

    fun put(item: T) {
        items[keyOf(item)] = item
    }

    fun all(): List<T> {
        return items.values.toList()
    }
}

data class Note(val number: Int, val text: String)

fun main() {
    val shelf = MemoryShelf<Note> { it.number }
    shelf.put(Note(7, "Start"))
    shelf.put(Note(2, "Practice"))
    val before = shelf.all()
    shelf.put(Note(7, "Review"))
    println(before.map { it.text })
    println(shelf.all().map { it.text })
}
```

Output:

```text
[Start, Practice]
[Review, Practice]
```

The key function connects an unknown `T` to a known `Int` key. `items.values` supplies map values; `toList()` materializes a snapshot. A later replacement does not rewrite the previous list. This is a shallow snapshot: mutable objects stored as `T` are still shared. Keep identity stable while an item is stored.

`mutableMapOf` preserves insertion order. Updating an existing key replaces its value without moving its position, as the [map factory examples](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/mutable-map-of.html) illustrate. Here the latest explicit save wins. That differs deliberately from Chapter 05's first-record history rule: an editable store and an imported history have different contracts. Document the rule rather than assuming “deduplication” defines it.

## Delegate interface calls with `by`

```kotlin
interface TopicSource {
    fun topics(): List<String>
    fun count(): Int
}

class FixedTopics : TopicSource {
    override fun topics(): List<String> = listOf("Kotlin", "Skip", "KMP")
    override fun count(): Int = topics().size
}

class FilteredTopics(private val source: TopicSource) : TopicSource by source {
    override fun topics(): List<String> {
        return source.topics().filter { it != "Skip" }
    }
}

fun main() {
    val view = FilteredTopics(FixedTopics())
    println(view.topics())
    println(view.count())
}
```

Output:

```text
[Kotlin, KMP]
3
```

`TopicSource by source` generates forwarding members. An explicit override replaces the wrapper's forwarded member. Calls *inside the delegate* still use the delegate's own implementation: its `count` calls its own `topics`, so it counts three. See [interface delegation](https://kotlinlang.org/docs/delegation.html).

For an unchanged reader view, delegate all operations without overrides. This keeps forwarding simple. Returning a reader interface narrows the advertised operations; it is not an authorization boundary or a promise that other holders cannot save to the underlying store. Each read can see current storage while each returned snapshot stays fixed.

## Swap a dependency for a small fake

```kotlin
interface Messenger {
    fun send(message: String)
}

class ConsoleMessenger : Messenger {
    override fun send(message: String) {
        println(message)
    }
}

class RecordingMessenger : Messenger {
    val messages = mutableListOf<String>()
    override fun send(message: String) {
        messages.add(message)
    }
}

class TopicCoach(private val messenger: Messenger) {
    fun remind(topic: String) {
        messenger.send("Review $topic")
    }
}

fun main() {
    val fake = RecordingMessenger()
    TopicCoach(fake).remind("Kotlin")
    println(fake.messages)
    TopicCoach(ConsoleMessenger()).remind("KMP")
}
```

Output:

```text
[Review Kotlin]
Review KMP
```

The recording implementation is a **fake**, a controlled replacement that makes behavior observable without an external service. The coach's code is unchanged when the caller swaps messengers. A planner that creates a new concrete store inside each method defeats this substitution and loses shared state. Keep assembly near the entry point, as `main` does here. Constructor properties and visibility use the [class syntax](https://kotlinlang.org/docs/classes.html) from Chapter 04.

The workshop's supplied tests use the same idea to observe reads and writes. You do not need a mocking library or to write test classes yet; Chapter 08 will explain test structure.

## Optional stretch Transfer through narrow interfaces

```kotlin
interface Reader<out T> {
    fun all(): List<T>
}

interface Writer<in T> {
    fun save(value: T)
}

fun <T> copyItems(reader: Reader<T>, writer: Writer<T>) {
    for (item in reader.all()) {
        writer.save(item)
    }
}

class Topics : Reader<String> {
    override fun all(): List<String> = listOf("Kotlin", "KMP")
}

class Printer : Writer<Any> {
    override fun save(value: Any) {
        println(value)
    }
}

fun main() {
    copyItems<String>(Topics(), Printer())
}
```

Output:

```text
Kotlin
KMP
```

The generic function connects the produced and consumed types. `Writer<Any>` can safely handle each string. A read-and-write store itself stays invariant; splitting capabilities gives callers the part they need.

## Check your understanding

1. Where should the planner and report get the same storage instance?
2. Which operations belong to the contract, and which collection is a private implementation choice?
3. Why can a report take `Reader<StudySession>` while a completion operation needs both reading and writing?
4. Why is `Store<String>` unsafe to treat as `Store<Any>`?
5. Does `by` make a copy or redirect calls made inside the delegate?
6. What evidence would show that the planner uses an injected fake rather than an internal store?

**[Continue to the workshop](WORKSHOP.md)** · [Chapter entry](README.md)
