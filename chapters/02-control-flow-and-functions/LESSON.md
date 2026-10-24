# Lesson 02 Control flow and functions

Our next study planner must reject invalid durations, choose a session label, and repeat a round. Before building those rules, learn the smaller pieces: functions, decisions, ranges, and loops.

This lesson builds on [Chapter 01](../01-values-and-types/LESSON.md). Paste one complete Kotlin example at a time into [Kotlin Playground](https://play.kotlinlang.org/) if you want to experiment without changing your exercise files.

You can split the lesson across two sittings: functions and decisions first, then validation and loops. Predict each example's output before running it.

## Write a function that returns a result

A function gives a piece of work a name. Its parameters describe the inputs; its return type describes the result.

```kotlin
fun snackCost(quantity: Int, price: Int): Int {
    return quantity * price
}

fun main() {
    val cost = snackCost(3, 2)
    println(cost)
}
```

This prints:

```text
6
```

`quantity` and `price` are **parameters** in the declaration. `3` and `2` are the **arguments** supplied by this call. Their order matters in this form. The `: Int` after `)` is the result type; `return` sends that result back to the caller.

You can also insert a call's result directly into a string template with `${...}`. The workshop starter uses `${sessionLabel(minutes)}` to call a function and put its returned label into the text. Storing the result in a `val` and inserting that variable's name is another option.

`return` and `println` do different jobs. The function returns a number that other code can use. `main` prints it. Printing a result inside `snackCost` would not replace returning the required `Int`.

This calculation is a **pure function**: its result depends only on its inputs, and it does not change anything outside itself. Calling it again with the same inputs gives the same result. Keeping rules this way makes them easier to check without a screen or keyboard.

## Name arguments and provide a default

```kotlin
fun makeBadge(name: String, level: Int = 1): String {
    return "$name - level $level"
}

fun main() {
    println(makeBadge(name = "Nova"))
    println(makeBadge(level = 3, name = "Nova"))
}
```

This prints:

```text
Nova - level 1
Nova - level 3
```

`level: Int = 1` supplies a default when that argument is omitted. It does not prevent a caller from choosing another level. A named argument uses `parameterName = value`; naming all arguments lets the second call put them in a different order while keeping their meaning clear.

Try supplying a different level, then removing it. The default belongs to the function declaration, so callers share the same starting behavior.

## Compare values

Conditions produce a `Boolean`: either `true` or `false`.

```kotlin
fun main() {
    val score = 8
    println(score >= 10)
    println(score == 8)
    println(score != 8)
}
```

This prints:

```text
false
true
false
```

| Operator | Question |
| --- | --- |
| `==` | Are these values equal? |
| `!=` | Are they different? |
| `<` and `>` | Is one smaller or larger? |
| `<=` and `>=` | Is one smaller or larger, including equality? |

`=` assigns a value; `==` compares values. Pay attention to whether a boundary should be included: `score > 8` and `score >= 8` disagree when the score is exactly eight.

## Choose a branch with if

```kotlin
fun main() {
    val lives = 2
    if (lives == 0) {
        println("Game over")
    } else if (lives == 1) {
        println("Last chance")
    } else {
        println("Keep going")
    }

    val status = if (lives > 0) "Playing" else "Finished"
    println(status)
}
```

This prints:

```text
Keep going
Playing
```

The program checks the conditions in order and takes the first matching branch. `else` handles the remaining case. Braces group the instructions in a branch.

Kotlin also lets `if` produce a value, as it does for `status`. In this use, include `else` so there is a result whichever way the condition goes. Change `lives` to zero, then one, and predict both printed lines.

## Combine conditions

```kotlin
fun main() {
    val age = 12
    val hasTicket = true
    if (age >= 12 && hasTicket) {
        println("Enter")
    }

    val hasPass = false
    val isGuest = true
    println(hasPass || isGuest)
    println(!hasTicket)
}
```

This prints:

```text
Enter
true
false
```

`&&` means both conditions must be true. `||` means at least one must be true. `!` reverses a Boolean. Use parentheses to make a longer combination's grouping clear.

For example, a number cannot be both below a minimum **and** above a maximum. To detect either invalid case, use `||`. We can also express a valid interval directly with a range.

## Describe an inclusive range

```kotlin
fun main() {
    val level = 5
    println(level in 1..5)
    println(6 in 1..5)
    println(0 !in 1..5)
}
```

This prints:

```text
true
false
true
```

`1..5` includes both endpoints: 1, 2, 3, 4, and 5. `in` checks membership; `!in` checks that a value is outside the range. For these integers, `level in 1..5` asks the same question as `level >= 1 && level <= 5`.

Test a rule just below, at, and just above each limit. A single value in the middle cannot reveal an excluded endpoint.

## Choose among several cases with when

```kotlin
fun main() {
    val coins = 8
    val reward = when (coins) {
        0 -> "Try again"
        in 1..9 -> "Sticker"
        else -> "Badge"
    }
    println(reward)
}
```

This prints:

```text
Sticker
```

`when` compares `coins` with the branches from top to bottom. The first matching branch supplies the result after `->`. Here, `else` covers all other integer values, so the expression always has a result.

Keep the intended cases clear. A broad range placed before a narrower, overlapping one can catch a value before it reaches the branch you meant to use. Try 0, 9, and 10.

**A useful pause:** explain how a function receives arguments, how `if` selects work, and whether `1..5` contains five. Continue when those feel familiar.

## Reject invalid input before doing the work

```kotlin
fun ticketMessage(seat: Int): String {
    if (seat !in 1..8) {
        return "Invalid seat"
    }
    return "Seat $seat"
}

fun main() {
    println(ticketMessage(0))
    println(ticketMessage(4))
}
```

This prints:

```text
Invalid seat
Seat 4
```

The first `return` exits the function immediately for an invalid seat. This early check is often called a **guard clause**. The final line is reached only for an accepted seat.

Our workshop receives `Int` arguments that already exist in the program. Validation means checking whether those numbers meet the planner's rules. Reading text from a user, converting it to a number, and choosing richer error types come in later chapters.

## Repeat a known range with for

```kotlin
fun main() {
    var total = 0
    for (number in 1..3) {
        total += number
        println("Added $number")
    }
    println(total)
}
```

This prints:

```text
Added 1
Added 2
Added 3
6
```

The loop runs once for each integer in the range. `number` is the current value inside the loop body. `total += number` means `total = total + number`; `total` needs `var` because it changes.

The upper endpoint is included. `1..3` gives three iterations; `0..3` gives four. Choose the range from the numbering your output needs.

## Repeat while a condition remains true

```kotlin
fun main() {
    var countdown = 3
    while (countdown > 0) {
        println(countdown)
        countdown -= 1
    }
    println("Go!")
}
```

This prints:

```text
3
2
1
Go!
```

`while` checks its condition before each iteration, including the first. `countdown -= 1` subtracts one. Without that update, this example would keep printing three because its condition would never become false.

If `countdown` starts at zero, the loop body is skipped. Use `for` for the workshop's numbered rounds; use this countdown to understand a loop controlled by a changing condition. You do not need to force both loop styles into the same solution.

## Build text and return it

```kotlin
fun main() {
    var message = "First"
    message += "\nSecond"
    println(message)
}
```

This prints:

```text
First
Second
```

With strings, `+=` joins more text onto the current value and reassigns the variable. `\n` represents a newline within a string. This is different from `println`, which writes to the output area.

A function can build text in a local `var` and return it while remaining pure: local reassignment does not change anything outside that call. In the workshop, start fresh on each call, build the plan, and let `main` print the returned string.

## Check your understanding

1. What is the difference between a parameter, an argument, and a return value?
2. What happens when a caller omits an argument that has a default?
3. Why does `1..3` include three? How many iterations does `0..3` give?
4. What makes the countdown stop, and what happens if it starts at zero?
5. Why should validation happen before repeating work?
6. Can a function use a local `var` and still be pure? Explain what it must avoid changing.

**[Next Repair the study planner](WORKSHOP.md)**

[Chapter overview](README.md) · [Previous lesson](../01-values-and-types/LESSON.md) · [Back to the course](../../README.md)

For further reading, see Kotlin's official [functions](https://kotlinlang.org/docs/functions.html), [conditions and loops](https://kotlinlang.org/docs/control-flow.html), and [ranges](https://kotlinlang.org/docs/ranges.html). The course examples use the workspace's pinned Kotlin 2.2.21.
