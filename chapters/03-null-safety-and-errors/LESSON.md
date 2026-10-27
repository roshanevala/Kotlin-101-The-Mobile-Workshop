# Lesson 03 Null safety and errors

Chapter 02 checked numbers that already existed as `Int` values. A real form starts with text, and some fields may be missing. We now need to distinguish a missing value, text that cannot become a number, and a number the planner does not accept.

This lesson builds on [Chapter 02](../02-control-flow-and-functions/LESSON.md). Paste one complete example at a time into [Kotlin Playground](https://play.kotlinlang.org/) if you want to experiment without editing your workshop files.

Take this in two sittings if helpful: nullable values and string operations first, then parsing and errors. Predict each example's output before running it.

## Represent a missing value

```kotlin
fun main() {
    val missing: String? = null
    val supplied: String? = "Kotlin"
    println(missing)
    println(supplied)
}
```

This prints:

```text
null
Kotlin
```

`String?` allows either a string or `null`, which represents no value. A regular `String` does not allow `null`. The question mark belongs to the type; the variable can still use `val`.

These inputs differ:

| Value | Meaning |
| --- | --- |
| `null` | No value |
| `""` | A present string with no characters |
| `"   "` | A present string containing spaces |
| `"null"` | A present string containing the four letters n, u, l, l |

Printing `null` displays those four letters, but that display does not turn absence into an actual string value.

## Check before using a nullable value

```kotlin
fun labelSize(label: String?): Int? {
    if (label == null) {
        return null
    }
    return label.length
}

fun main() {
    println(labelSize(null))
    println(labelSize("KMP"))
}
```

This prints:

```text
null
3
```

`.length` reads the string's length. After the guard returns for `null`, Kotlin knows this unchanged parameter is a `String` in the remaining code. This is a **smart cast**; you do not need a manual conversion.

The function's return type is `Int?`: a count when a label exists, or `null` when it does not. It could return zero for an empty string, which is different from having no label at all.

## Use a safe call

```kotlin
fun main() {
    val missing: String? = null
    val supplied: String? = "KMP"
    println(missing?.length)
    println(supplied?.length)
}
```

This prints:

```text
null
3
```

`?.` accesses the member only when the value on its left is non-null. Otherwise, the expression produces `null`. Here the result is an `Int?`.

An ordinary `missing.length` would not compile with this nullable type. Replacing it with `missing!!.length` would assert that the value exists and throw a `NullPointerException` when it does not. `!!` does not validate or supply a missing value. The workshop can be completed without it.

## Choose a fallback with Elvis

```kotlin
fun main() {
    val missing: String? = null
    val empty: String? = ""
    val missingDisplay = missing ?: "Guest"
    val emptyDisplay = empty ?: "Guest"
    println(missingDisplay)
    println("[$emptyDisplay]")
}
```

This prints:

```text
Guest
[]
```

The Elvis operator `?:` uses the left value when it exists and evaluates the fallback otherwise. It reacts to `null`, so the empty string stays empty. It also does not reject zero or a negative number.

A fallback is a product decision. A missing nickname may reasonably become `Guest`; an invalid duration should not silently become a made-up session length.

## Exit early when there is no value

```kotlin
fun announce(player: String?): String {
    val name = player ?: return "No player"
    return "Ready: $name"
}

fun main() {
    println(announce(null))
    println(announce("Nova"))
}
```

This prints:

```text
No player
Ready: Nova
```

The right side of `?:` can return from the function. If execution reaches the next line, `name` has a non-null string. This combines a guard and a local value without forcing the caller's input to exist.

## Clean text before checking whether it is empty

```kotlin
fun main() {
    val raw = "  Kotlin  basics  "
    val cleaned = raw.trim()
    println("[$cleaned]")
    println(cleaned.isEmpty())
    println("   ".trim().isEmpty())
}
```

This prints:

```text
[Kotlin  basics]
false
true
```

`.trim()` returns text with whitespace removed from its beginning and end. It preserves spaces inside the text and leaves `raw` unchanged. `.isEmpty()` asks whether a string has no characters.

Trimming first lets an all-space topic become empty, so you can give it the same fallback as an empty input. Check for `null` or use a safe call before invoking these operations on a nullable string.

**A useful pause:** explain the difference between `null`, `""`, and `"   "`. Then explain why `?: "Guest"` handles only one of those cases.

## Convert text without inventing a number

```kotlin
fun main() {
    println(" 42 ".toIntOrNull())
    println(" 42 ".trim().toIntOrNull())
    println("banana".toIntOrNull())
    println("-5".toIntOrNull())
    println("2147483648".toIntOrNull())
}
```

This prints:

```text
null
42
null
-5
null
```

`.toIntOrNull()` returns an `Int?`. Decimal integer text within the `Int` range becomes a number; an invalid representation becomes `null`. It accepts an optional leading `+` or `-`. This call does not trim the text for you.

The `Int` range is −2,147,483,648 through 2,147,483,647. The last input is too large, so it cannot be represented as an `Int`. Fractional text such as `"4.5"`, scientific notation such as `"1e2"`, and text containing units also do not parse through this function.

Parsing `"-5"` succeeds. Deciding whether −5 minutes is allowed is a separate validation rule. Keeping those steps separate gives the form a useful explanation for each kind of failure.

## Chain safe operations

```kotlin
fun parseScore(raw: String?): Int? {
    return raw?.trim()?.toIntOrNull()
}

fun main() {
    println(parseScore(null))
    println(parseScore(" 7 "))
    println(parseScore("lost"))
}
```

This prints:

```text
null
7
null
```

Each `?.` continues only if there is a value to use. A missing input remains missing, while supplied text is trimmed and offered to the parser. The function returns the nullable result to its caller.

Avoid adding `?: 0` unless zero is the intended meaning of a failed conversion. Otherwise, that fallback hides a failure as a successful parse.

## Make an error contract explicit

```kotlin
fun seatError(seat: Int): String? {
    if (seat !in 1..8) {
        return "Seat must be 1..8"
    }
    return null
}

fun main() {
    println(seatError(9) ?: "Seat accepted")
    println(seatError(2) ?: "Seat accepted")
}
```

This prints:

```text
Seat must be 1..8
Seat accepted
```

Here, a string means an error and `null` means **no error**. In `parseScore`, `null` means no parsed score. Nullability alone does not tell the whole story; the function's contract gives that result its meaning.

These are simple, explicit result contracts that fit the types taught so far. When the caller must distinguish several failures in code, a dedicated result model can make those cases clearer. Chapter 04 introduces the classes and sealed types needed to build richer models.

## Recognize an exception contract

Some APIs throw an exception instead of returning an error value. For example, `.toInt()` throws `NumberFormatException` for invalid numeric text, while `.toIntOrNull()` gives our form an ordinary nullable result.

Here is a separate strict rule using an exception:

```kotlin
fun checkedSeat(seat: Int): Int {
    if (seat !in 1..8) {
        throw IllegalArgumentException("Seat must be 1..8")
    }
    return seat
}

fun main() {
    try {
        println(checkedSeat(9))
    } catch (error: IllegalArgumentException) {
        println(error.message ?: "Invalid seat")
    } finally {
        println("Check finished")
    }
}
```

This prints:

```text
Seat must be 1..8
Check finished
```

`throw` interrupts the usual path. The matching `catch` handles this exception and reads its nullable message. `finally` runs when leaving this `try`/`catch` through normal completion or an exception; it is useful for cleanup. Changing the seat to two makes the first line `2`, and the final line still appears.

These exception types come from the standard library; you are using them before learning to define your own classes. Catch a failure you can handle, rather than turning every exception into a success value. An uncaught exception can terminate a console program.

Our form expects people to leave fields blank or type something invalid. Its helpers return values or error messages for those cases. You do not need to throw, catch, or use `!!` to complete the workshop.

## Check your understanding

1. Why are `null`, `""`, and the text `"null"` different inputs?
2. How does `?.` differ from `!!`?
3. Why does Elvis not replace an empty string or zero?
4. Why can parsing succeed while duration validation fails?
5. What does `null` mean in the parser's contract? What does it mean in the error helper's contract?
6. How does throwing an exception differ from returning a nullable result?

**[Next Repair the session input form](WORKSHOP.md)**

[Chapter overview](README.md) · [Previous lesson](../02-control-flow-and-functions/LESSON.md) · [Back to the course](../../README.md)

Further reading: Kotlin's official [null safety](https://kotlinlang.org/docs/null-safety.html), [exception handling](https://kotlinlang.org/docs/exceptions.html), [integer parsing](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/to-int-or-null.html), and [trimming](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/trim.html). The examples use the workspace's pinned Kotlin 2.2.21.
