# Lesson 04 Classes and modeling

The planner now handles input, but a topic, a duration, and a status still need to travel together. A model gives that information a name. It also helps us decide what equality means and which outcomes a caller must handle.

Build on [Chapter 03](../03-null-safety-and-errors/LESSON.md). Paste one complete example at a time into [Kotlin Playground](https://play.kotlinlang.org/). Predict the output first. If useful, take two sittings: classes and copies, then enums and sealed results. Collections and lambdas come in later chapters.

## Give related information a class

```kotlin
class Player(val name: String, var points: Int = 0) {
    fun label(): String {
        return "$name: $points points"
    }
}

fun main() {
    val player = Player(name = "Nova")
    println(player.label())
    player.points = 5
    println(player.label())
}
```

This prints:

```text
Nova: 0 points
Nova: 5 points
```

`Player` defines a type. Calling `Player(...)` constructs an object, also called an instance; Kotlin needs no `new` keyword. The parameters in the class header form its **primary constructor**. Named arguments and defaults work here too.

`val name` and `var points` declare properties on each instance. Access them with a dot. A constructor parameter without `val` or `var` does not itself declare an accessible property. `label` is a member function: it can use the instance's properties.

`val player` prevents assigning a different player to that variable. It still allows changes through the object's `var points`. Likewise, `val name` cannot be reassigned through the player. A `val` reference alone does not make an object immutable.

## Keep a detail private

```kotlin
class SnackBox(private var portions: Int) {
    fun takeOne(): Boolean {
        if (portions <= 0) {
            return false
        }
        portions -= 1
        return true
    }
}

fun main() {
    val box = SnackBox(portions = 1)
    println(box.takeOne())
    println(box.takeOne())
}
```

This prints:

```text
true
false
```

`private` makes this property accessible inside `SnackBox`. Code in `main` uses `takeOne()` to request a change; `box.portions = 100` would not compile. This is a small boundary around state, rather than an instruction to make every property mutable or private.

Declarations are `public` by default. A top-level `private` helper is visible within its file. `internal` limits visibility to a compilation module; `protected` permits access inside a class and its subclasses. We will practise broader abstraction and inheritance in Chapter 07.

## Choose what equal means

```kotlin
class PlainBadge(val title: String)
data class Badge(val title: String)

fun main() {
    println(PlainBadge("Explorer") == PlainBadge("Explorer"))
    val first = Badge("Explorer")
    val second = Badge("Explorer")
    val alias = first
    println(first == second)
    println(first === second)
    println(first === alias)
}
```

This prints:

```text
false
true
false
true
```

`==` uses the type's equality behavior. An ordinary class inherits identity-based equality unless it defines different behavior. A **data class** generates equality from its primary-constructor properties, along with `hashCode`, a readable `toString`, and `copy`.

`===` asks whether two references point to the same object. Two separately created badges have equal values but different identities. `alias = first` creates another reference to the existing object.

Use data classes for values that should compare by their contents. This does not mean every class should be a data class; objects that own changing behavior may need different equality semantics.

## Return a changed copy

```kotlin
data class PracticeGoal(val title: String, val minutes: Int)

fun main() {
    val original = PracticeGoal("Read", 20)
    val longer = original.copy(minutes = 35)
    println(original)
    println(longer)
    println(original === longer)
}
```

This prints:

```text
PracticeGoal(title=Read, minutes=20)
PracticeGoal(title=Read, minutes=35)
false
```

`copy(minutes = 35)` constructs another goal, keeping the other constructor property. The original remains at 20 minutes. Read-only properties work well when a change should produce a new value for a caller to use.

Writing `val longer = original` would create an alias. It would not create a copy or change any property. Keep that distinction in mind for the workshop's completion function.

## Notice which properties participate

```kotlin
data class ReaderCard(val owner: String) {
    var stamp: String = ""
}

fun main() {
    val first = ReaderCard("Nova")
    val second = ReaderCard("Nova")
    first.stamp = "blue"
    second.stamp = "green"
    println(first == second)
    println(first.copy().stamp.isEmpty())
}
```

This prints:

```text
true
true
```

The generated data-class operations use primary-constructor properties. Here, `stamp` is in the body, so it does not participate in generated equality or get transferred by `copy`. The new card initializes it to `""` again.

If a property's value belongs in equality and copying, put it in the primary constructor. Our session's topic, duration, and status all belong there.

## Remember that copying is shallow

```kotlin
class Meter(var count: Int)
data class Robot(val name: String, val meter: Meter)

fun main() {
    val original = Robot("Pip", Meter(1))
    val duplicate = original.copy()
    duplicate.meter.count += 1
    println(original.meter.count)
    println(original.meter === duplicate.meter)
}
```

This prints:

```text
2
true
```

The outer robot was copied, but both robots refer to the same mutable meter. Data-class `copy()` does not recursively copy child objects. Even `val meter` allows changes to `meter.count`.

This is the **Bug Museum preview: the clone changed the original**. The workshop uses strings, integers, and enum values, without a mutable child. When we introduce mutable collections, we will need to consider ownership and copying again.

## Use an enum for a fixed set of values

```kotlin
enum class DoorState {
    OPEN,
    CLOSED
}

fun doorLabel(state: DoorState): String {
    return when (state) {
        DoorState.OPEN -> "Come in"
        DoorState.CLOSED -> "Knock first"
    }
}

fun main() {
    println(doorLabel(DoorState.OPEN))
    println(doorLabel(DoorState.CLOSED))
}
```

This prints:

```text
Come in
Knock first
```

`DoorState` is a type with two named values. A caller supplies one of those values instead of a string that might contain `"opne"`. The enum entry is accessed through the type name.

Because this `when` handles every enum entry, it needs no `else`. Adding another entry will make an incomplete `when` expression a compile error. Our session status will similarly be `PLANNED` or `COMPLETED`.

## Use sealed cases when outcomes carry different data

```kotlin
sealed class ParcelResult {
    data class Delivered(val locker: Int) : ParcelResult()
    data class Delayed(val minutes: Int) : ParcelResult()
}

fun parcelLabel(result: ParcelResult): String {
    return when (result) {
        is ParcelResult.Delivered -> {
            val locker = result.locker
            "Collect from locker $locker"
        }
        is ParcelResult.Delayed -> "Wait ${result.minutes} min"
    }
}

fun main() {
    println(parcelLabel(ParcelResult.Delivered(locker = 7)))
    println(parcelLabel(ParcelResult.Delayed(minutes = 10)))
}
```

This prints:

```text
Collect from locker 7
Wait 10 min
```

The result is either a delivery with a locker number or a delay with a wait time. Each nested data class is one case. `: ParcelResult()` declares that it extends the sealed base class and calls the base constructor. You create a concrete case; you cannot instantiate `ParcelResult` directly.

`is` checks a value's type. In that branch, Kotlin smart-casts the unchanged parameter so its case-specific property is available. A branch can contain a block; its last expression supplies that branch's value. A `when` can also be used to initialize a local value, as we will do for the session's status label.

Handling all cases makes this `when` exhaustive, so an `else` is unnecessary. For this JVM exercise, direct subclasses must be in the same package and module; nesting them together keeps the small model easy to find.

Use an enum for a fixed choice such as a status. Use sealed cases when alternatives need different data, such as an accepted session or a rejection message. Chapter 07 will develop the broader inheritance and interface concepts.

## Choose a validation boundary

A class declaration does not automatically validate its properties. `PracticeGoal("Read", -5)` is legal with the model above. You can validate construction inside a class; in this workshop, a separate creation function checks the duration and returns an accepted or rejected result.

That function promises to validate requests. The simple public `StudySession` constructor remains available, so callers can bypass the creation function. Do not claim the model itself prevents every invalid duration. We will revisit stricter boundaries as the app grows.

## Check your understanding

1. What does `val` protect for a variable, and what does it protect for a property?
2. Why can two separately constructed data objects satisfy `==` and fail `===`?
3. What happens to a body property when a data class is copied?
4. Why does `copy()` fail to isolate the robot's mutable meter?
5. When would you choose an enum rather than a sealed result?
6. What does an exhaustive `when` help you notice when a new case is added?

**[Next Give sessions meaningful values and states](WORKSHOP.md)**

[Chapter overview](README.md) · [Previous lesson](../03-null-safety-and-errors/LESSON.md) · [Back to the course](../../README.md)

Further reading: Kotlin's official [classes](https://kotlinlang.org/docs/classes.html), [visibility modifiers](https://kotlinlang.org/docs/visibility-modifiers.html), [data classes](https://kotlinlang.org/docs/data-classes.html), [equality](https://kotlinlang.org/docs/equality.html), [enum classes](https://kotlinlang.org/docs/enum-classes.html), and [sealed classes](https://kotlinlang.org/docs/sealed-classes.html). The examples use the workspace's pinned Kotlin 2.2.21.
