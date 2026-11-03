# Lesson 08 Tests and Kotlin pitfalls

Your planner's clone changed the original. A saved history entry also changed after the caller cleared a list. Printing “copy created” did not tell us which mutable objects were still shared. A useful test changes one object and checks the behavior that must stay stable.

Read after [Chapter 07](../07-abstraction-and-dependencies/LESSON.md). We will first structure a test, then investigate identity, shallow copies, and ownership. Console programs below are independent and can run in [Kotlin Playground](https://play.kotlinlang.org/). The three test-file examples require the course's `kotlin.test` dependencies; use the local path explained below rather than a plain browser program.

## Give each test one observable question

A unit test calls a small piece of code with controlled inputs and checks its result or observable effects. Separate three steps: **arrange** inputs, **act** once, and **assert** the promised behavior.

This is a complete test file, including a tiny function under test:

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals

fun leftoverMinutes(total: Int): Int = total % 60

class DurationTest {
    @Test
    fun `95 minutes leaves 35 after whole hours`() {
        val total = 95                         // Arrange
        val actual = leftoverMinutes(total)    // Act
        assertEquals(35, actual)               // Assert
    }

    @Test
    fun `an exact hour has no leftover minutes`() {
        assertEquals(0, leftoverMinutes(60))
    }
}
```

Expected result: **2 tests pass**.

`@Test` marks a function for discovery. Backticks allow a readable test name. `assertEquals(expected, actual)` fails when values differ; the runner reports the name and both values. An assertion failure means the code ran and contradicted the expectation. A compiler error or missing dependency happens before that check. Tests in this workspace use `kotlin.test` with JUnit 5, already configured; do not copy new dependency versions from a tutorial. See the [Kotlin/JUnit guide](https://kotlinlang.org/docs/jvm-test-using-junit.html) and [`assertEquals`](https://kotlinlang.org/api/core/kotlin-test/kotlin.test/assert-equals.html).

To run any of the three complete test files in this lesson, first follow the [workshop setup](WORKSHOP.md#prepare-two-working-files). Keep your existing test answer safe outside the source folder. Temporarily replace the contents of the generated `SnapshotPracticeTest.kt` with one complete example, including its imports. These independent examples omit a package intentionally. Then, from `kotlin-labs`, run the matching command:

```sh
./gradlew --console=plain :ch08-tests-and-kotlin-pitfalls:test --tests DurationTest
```

For the later examples, replace `DurationTest` with `SessionValueTest` or `DraftCopyTest`. Restore your original working test file afterward. Filtering runs only the selected test class; it does not skip compiling the rest of the module's test source. See [Gradle test filtering](https://docs.gradle.org/current/userguide/java_testing.html#test_filtering).

Try changing the first expectation from 35 to 0, observe the failure, then restore it. A test that has never failed may be checking the wrong thing. A passing empty-input test does not establish behavior for nonempty inputs.

## Distinguish value equality from identity

```kotlin
data class Session(val topic: String, val minutes: Int)

fun main() {
    val first = Session("Kotlin", 30)
    val separate = Session("Kotlin", 30)
    val alias = first
    println(first == separate)
    println(first === separate)
    println(first === alias)
}
```

Output:

```text
true
false
true
```

`==` uses equality semantics; a data class normally derives those from its primary-constructor properties. `===` asks whether two references point to the same instance. An alias is another reference to the same object. Ordinary classes inherit identity-based equality unless they override it, so `==` does not mean “compare every property” for every type. Avoid identity experiments with boxed numbers or interned strings; use explicit objects as above. See [equality](https://kotlinlang.org/docs/equality.html).

A complete test file can express the distinction without assuming a memory address:

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

private data class SessionValue(val topic: String, val minutes: Int)

class SessionValueTest {
    @Test
    fun `separate sessions can have equal values`() {
        val first = SessionValue("Kotlin", 30)
        val separate = first.copy()
        assertNotSame(first, separate)
        assertEquals(first, separate)
    }

    @Test
    fun `changed duration changes the value`() {
        val first = SessionValue("Kotlin", 30)
        assertEquals(false, first == first.copy(minutes = 31))
    }
}
```

Expected result: **2 tests pass**.

Use `assertNotSame` when distinct identity is part of the question; use `assertEquals` for the value contract. `assertTrue` and `assertFalse` are convenient for Boolean results. Avoid testing equality only with `sameSessionValue(item, item)`: even the wrong identity comparison would pass. See [`assertNotSame`](https://kotlinlang.org/api/core/kotlin-test/kotlin.test/assert-not-same.html).

## A new data object can still share its list

```kotlin
data class Draft(val title: String, val topics: MutableList<String>)

fun main() {
    val original = Draft("Week 1", mutableListOf("Kotlin"))
    val edited = original.copy()
    edited.topics.add("KMP")
    println(original.topics)
    println(original === edited)
    println(original.topics === edited.topics)
}
```

Output:

```text
[Kotlin, KMP]
false
true
```

`copy()` creates another `Draft`, but copies its property values, including the list reference. It is shallow. Changing the shared list is visible through both drafts. `val topics` prevents reassigning that property, not editing its mutable contents. Data-class equality can still say two drafts are equal while their aliasing violates the product's editing contract. See [data-class copying](https://kotlinlang.org/docs/data-classes.html#copying).

## Copy the mutable level the contract needs

```kotlin
data class Draft(val title: String, val topics: MutableList<String>)

fun main() {
    val original = Draft("Week 1", mutableListOf("Kotlin"))
    val edited = original.copy(topics = original.topics.toMutableList())
    edited.topics.add("KMP")
    println(original.topics)
    println(edited.topics)
}
```

Output:

```text
[Kotlin]
[Kotlin, KMP]
```

Here strings are immutable, so a separate editable list is enough. The test should check the required independence after a change, rather than demand a particular collection class.

This complete test file captures the contract for both populated and empty drafts:

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals

private data class TestDraft(val topics: MutableList<String>)
private fun fork(draft: TestDraft): TestDraft = TestDraft(draft.topics.toMutableList())

class DraftCopyTest {
    @Test
    fun `editing a branch preserves original topics`() {
        val original = TestDraft(mutableListOf("Kotlin"))
        val branch = fork(original)
        branch.topics.clear()
        assertEquals(listOf("Kotlin"), original.topics)
    }

    @Test
    fun `empty drafts can be edited independently`() {
        val original = TestDraft(mutableListOf())
        val branch = fork(original)
        branch.topics.add("KMP")
        assertEquals(emptyList(), original.topics)
    }
}
```

Expected result: **2 tests pass**.

Keep mutable fixtures local to each test so one test cannot depend on another's changes. Use distinct values and nonempty inputs when testing independence; checking two empty lists without editing either reveals little. After a red test, make the smallest justified repair, run it again, then simplify while keeping the test green.

## A read-only reference is not a saved value

```kotlin
fun main() {
    val source = mutableListOf("Kotlin")
    val view: List<String> = source
    val saved = source.toList()
    source.add("KMP")
    println(view)
    println(saved)
}
```

Output:

```text
[Kotlin, KMP]
[Kotlin]
```

`List` exposes read operations, but another holder of the mutable object can still edit it. `toList()` snapshots membership; it does not freeze the source. A getter returning a read-only interface can therefore expose changing internal state. Do not downcast a read-only list to mutate it: implementations differ, and that bypasses its contract. See [read-only and mutable collections](https://kotlinlang.org/docs/collections-overview.html).

## An outer snapshot does not copy its mutable elements

```kotlin
data class Note(var text: String)

fun main() {
    val note = Note("Practice")
    val source = mutableListOf(note)
    val saved = source.toList()
    note.text = "Review"
    source.clear()
    println(saved.size)
    println(saved.first().text)
}
```

Output:

```text
1
Review
```

The saved list kept its membership but retained the same `Note` reference. “Copy the list” and “make every reachable value immutable” are different contracts. With a plan containing an editable session list, protecting an outer history list alone is insufficient. See [collection copying](https://kotlinlang.org/docs/constructing-collections.html#copy).

## Capture inputs and protect reads

```kotlin
class TopicArchive {
    private val entries = mutableListOf<List<String>>()

    fun capture(topics: List<String>) {
        entries.add(topics.toList())
    }

    fun all(): List<List<String>> {
        return entries.toList()
    }
}

fun main() {
    val archive = TopicArchive()
    val topics = mutableListOf("Kotlin")
    archive.capture(topics)
    topics.clear()
    val before = archive.all()
    archive.capture(listOf("KMP"))
    println(before)
    println(archive.all())
}
```

Output:

```text
[[Kotlin]]
[[Kotlin], [KMP]]
```

This API never exposes editing operations on captured inner lists, whose elements are immutable strings. The workshop's plans deliberately expose `MutableList`, so they need an additional independent list when read back. Decide what callers can change, then test both directions: edit the input after saving, and edit the returned object before reading again.

## Choose boundary cases from the promise

For completed-minute totals, use empty input, planned-only input, mixed statuses, and valid duration endpoints 1 and 120. Use `Long` expectations such as `121L` for a `Long` result. Test input preservation when callers keep their own list. Do not add invalid-input expectations when validation belongs to an earlier stage.

The workshop has fixed acceptance tests plus a separate file for your regressions. A passing suite verifies its tested cases; it does not prove coverage of every alias or tell us whether your own assertions would catch the original defect. Keep one bug at a time in view and reproduce its failure before repairing it.

**[Continue to the workshop](WORKSHOP.md)** · [Chapter entry](README.md)
