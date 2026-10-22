# Lesson 01 Values and types

Before fixing the study timer, learn the Kotlin you will use: naming a value, choosing a type, doing arithmetic, and displaying the result.

Kotlin is a programming language. We begin with small console programs: their instructions produce text in an output area. The same language will be used when we build the Android app later in the course.

You can read this lesson without setting up Android Studio. If you want to try an example, paste one complete code block at a time into [Kotlin Playground](https://play.kotlinlang.org/) and press Run.

## Start with a small program

```kotlin
fun main() {
    println("Hello, Kotlin!")
}
```

This prints:

```text
Hello, Kotlin!
```

`main` is the application's starting point. `fun` declares a function, a named piece of work. The braces `{ }` contain its instructions. `println` prints the value inside its parentheses and starts a new line. Text inside double quotes is a string.

Read this as: “When the program starts, display this message.”

## Give a value a name

```kotlin
fun main() {
    val topic = "Kotlin"
    val plannedMinutes = 30
    println(topic)
    println(plannedMinutes)
}
```

This prints `Kotlin` and `30` on separate lines. `topic` and `plannedMinutes` are names you chose. The `=` assigns an initial value to each name.

`val` means the variable cannot be reassigned after initialization. Adding `plannedMinutes = 45` after this declaration would be a compile error. The compiler checks your code before the program can run.

Use `var` when the same variable needs a new value later:

```kotlin
fun main() {
    var plannedMinutes = 30
    plannedMinutes = 45
    println(plannedMinutes)
}
```

This prints `45`. Prefer `val` when a value does not need reassignment. Later chapters will explain how this differs from changing the contents of a mutable object.

## Know what kind of value you have

A type tells Kotlin what kind of value a variable holds and which operations make sense.

| Type | Example | Meaning |
| --- | --- | --- |
| `Int` | `30` | A whole number within the Int range |
| `Double` | `1.5` | A floating-point number that can represent fractional quantities |
| `String` | `"Kotlin"` | Text |
| `Boolean` | `true` | Either `true` or `false` |

In `val plannedMinutes = 30`, Kotlin infers the type `Int` from the value. You can also write the type explicitly:

```kotlin
fun main() {
    val plannedMinutes: Int = 30
    val sessionHours: Double = 1.5
    val topic: String = "Kotlin"
    val completed: Boolean = false
    println(plannedMinutes)
    println(sessionHours)
    println(topic)
    println(completed)
}
```

This prints `30`, `1.5`, `Kotlin`, and `false`, each on a new line. The colon introduces a type.

Reassignment does not change a variable's type. If `plannedMinutes` were a `var` holding an `Int`, assigning `"forty-five"` to it would still be a compile error. Also notice that `30` is a number while `"30"` is text; the quotes change what the value means.

## Use numbers for calculations

Kotlin uses `+` for addition, `-` for subtraction, `*` for multiplication, and `/` for division.

```kotlin
fun main() {
    println(7 / 2)
    println(7.0 / 2.0)
}
```

The first line prints `3`. Both operands are integers, so the result is the whole-number quotient. The fractional part is discarded. The second line prints `3.5` because the operands are `Double` values.

Choosing a type therefore changes how a calculation behaves. A session duration in whole minutes can use `Int`; a fractional number of hours would be a different representation.

## Find what remains after division

Imagine putting 17 stickers into groups of five. You can make three complete groups, with two stickers left over.

```kotlin
fun main() {
    val stickers = 17
    val groupSize = 5
    println(stickers / groupSize)
    println(stickers % groupSize)
}
```

This prints `3` and then `2`. The `%` operator finds the remainder of division. Our examples use nonnegative whole numbers.

Before changing the example, predict what happens with 20 stickers, then with 21. Try it and explain why the two printed numbers change.

## Put values into a message

```kotlin
fun main() {
    val topic = "Kotlin"
    val plannedMinutes = 30
    println("Study $topic for $plannedMinutes minutes")
}
```

This prints `Study Kotlin for 30 minutes`. A `$` followed by a variable name inserts its value into the text. This is called a string template.

The program can calculate with numbers and then display them in a readable message.

## Read the workshop's supplied function

The workshop gives you a function to edit. Here is a smaller example of the same idea:

```kotlin
fun makeGreeting(name: String): String {
    return "Hello, $name!"
}

fun main() {
    println(makeGreeting("Kotlin"))
}
```

This prints `Hello, Kotlin!`. `name: String` describes the input. The `: String` after the closing parenthesis describes the result. `return` sends that result back to the caller. In `main`, the function is called with the input `"Kotlin"`, and its result is printed.

The timer's `formatStudyTime(totalMinutes: Int): String` works similarly: it receives whole minutes and returns display text. You will keep that supplied function shell and fix the arithmetic inside. Chapter 02 will go deeper into writing functions yourself.

## Check your understanding

Before moving on, explain these in your own words:

1. How is `val` different from `var`?
2. How is `30` different from `"30"`?
3. What do `/` and `%` tell you when dividing nonnegative integers?
4. How does a string template use a variable's value?

You are ready for the workshop when you can follow the examples and explain their output. Keep the lesson open while you practice; recalling the syntax takes repetition.

**[Next Try the timer workshop](WORKSHOP.md)**

[Chapter overview](README.md) · [Back to the course](../../README.md)

For further reading, see Kotlin's official [basic syntax](https://kotlinlang.org/docs/basic-syntax.html), [numbers](https://kotlinlang.org/docs/numbers.html), and [string templates](https://kotlinlang.org/docs/strings.html).
