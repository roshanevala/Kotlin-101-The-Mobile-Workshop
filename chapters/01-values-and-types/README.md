# Chapter 01 Values and types

Your study timer lost 35 minutes. A 95-minute session displays as `1 h 0 min`. Your mission is to preserve those minutes and make the display trustworthy.

You will learn values, variables, basic types, integer arithmetic, and string templates. No previous Kotlin knowledge is required. Allow about 60–90 minutes for a first attempt, split into shorter sessions if useful; setup downloads are separate.

## Get your first run

Follow the [Kotlin setup guide](../../kotlin-labs/README.md), then run this from `kotlin-labs`:

```sh
./gradlew --console=plain :ch01-values-and-types:run
```

On Windows, use `./gradlew.bat`. Among Gradle's task messages, find:

```text
1 h 0 min
```

The program has run successfully. The duration is the bug you will fix.

Prefer to explore in a browser first? Copy the contents of the [starter file](../../kotlin-labs/ch01-values-and-types/src/main/kotlin/workshop/ch01/Timer.kt) into [Kotlin Playground](https://play.kotlinlang.org/) and press Run. The repository's checks run locally through Gradle.

## Meet the supplied program

Open the starter file in `kotlin-labs/ch01-values-and-types/src/main/kotlin/workshop/ch01/Timer.kt`.

- `package workshop.ch01` groups the code under a name. Leave it unchanged.
- `fun main()` is the entry point: Kotlin starts executing your application there.
- Braces `{ }` surround a function's instructions.
- `println(...)` displays its value and moves to a new line.
- Text following `//` on a line is a comment, not an executed instruction.

The program also supplies a small function named `formatStudyTime`. A function is a named piece of work. In `formatStudyTime(totalMinutes: Int): String`, the input called `totalMinutes` must be an `Int`, and the result must be a `String`. `return` sends that result back to the caller. The call `formatStudyTime(95)` supplies the value 95.

For this mission, keep that function shell and change the calculation inside it. Chapter 02 will explore writing your own functions in more depth.

## Values have names and types

```kotlin
val totalMinutes = 95
```

`val` introduces a name that cannot be reassigned after initialization. Kotlin infers that `95` is an `Int`, a whole-number type. You can also write the type explicitly:

```kotlin
val totalMinutes: Int = 95
```

Both declarations mean the same thing here. Try adding `totalMinutes = 125` after the declaration in `main`. Kotlin rejects reassignment to this `val`. Remove the extra line to make the program compile again.

Use `var` when the same variable needs a new value later:

```kotlin
var plannedMinutes = 30
plannedMinutes = 45
println(plannedMinutes)
```

This prints `45`. A `var` still has a type: assigning `"forty-five"` to this variable is a compile error. Use `val` for the mission's intermediate calculations, since their values do not need reassignment. Read-only references to mutable objects are a later topic; `val` does not freeze every object it can refer to.

Some useful basic types are:

| Type | Example | Meaning |
| --- | --- | --- |
| `Int` | `95` | A whole number within the Int range |
| `Double` | `1.5` | A floating-point number, useful for fractional quantities |
| `String` | `"Kotlin"` | Text enclosed in double quotes |
| `Boolean` | `true` | One of `true` or `false` |

The compiler checks types before your program runs. A compile error means the program could not be built; incorrect printed output means a runnable program produced the wrong behavior.

## Predict before changing the timer

Both operands in `95 / 60` are integers. Integer division gives the whole-number quotient, so the result is `1`. Fractional information is discarded. By comparison, `95.0 / 60.0` uses `Double` arithmetic and produces a fractional number.

Predict each result, then try the expressions in Playground or print them temporarily in `main`:

```kotlin
println(125 / 60)
println(59 / 60)
println(60 / 60)
```

Multiplication uses `*`; subtraction uses `-`. Kotlin also has `%`, the remainder operator. For example, `17 % 5` is `2`: after three whole groups of five, two remain. Predict `20 % 5` and `21 % 5` before running them.

These observations give you the pieces to recover the timer's lost minutes. Our input contract is a nonnegative `Int` representing whole minutes. Negative durations and input parsing belong to later validation lessons.

## Put values into text

The timer returns a string containing `$hours` and `$minutes`. A dollar sign followed by a name inserts that variable's value into the text. This is called a string template.

```kotlin
val topic = "Kotlin"
val plannedMinutes = 30
println("Study $topic for $plannedMinutes minutes")
```

This prints `Study Kotlin for 30 minutes`. Double quotes matter: `95` is a number, while `"95"` is text. The timer needs numeric arithmetic first and a text result afterward.

## Build the fix

Change the calculation in `formatStudyTime` so these examples work. Keep its function name, input type, output type, and output format unchanged. Changing only the number in `main` does not fix the formatter for other inputs.

| Total minutes | Required output |
| --- | --- |
| 0 | `0 h 0 min` |
| 59 | `0 h 59 min` |
| 60 | `1 h 0 min` |
| 95 | `1 h 35 min` |
| 125 | `2 h 5 min` |

Need a nudge? Open one hint at a time: [Hint 1](HINT_1.md), [Hint 2](HINT_2.md), [Hint 3](HINT_3.md).

## Check your work

A test calls your function with a known input and compares its actual result with the expected text. The supplied tests cover the examples above, one minute, and the largest nonnegative `Int`. You do not need to write or understand the test framework yet.

Run this from `kotlin-labs`:

```sh
./gradlew --console=plain :ch01-values-and-types:test
```

An untouched starter fails some tests on purpose. If a test fails, inspect its expected and actual values in the report at `kotlin-labs/ch01-values-and-types/build/reports/tests/test/index.html`. Successful tests mean those examples passed; they do not prove every possible program correct.

Try your fix before opening the [worked solution and Bug Museum entry](SOLUTION.md). To run the separate reference, use `:ch01-values-and-types:runSolution`; to check it, use `:ch01-values-and-types:checkSolution` after `./gradlew --console=plain`.

## Change request and interview rehearsal

Independently change the session in `main` to **143 minutes**. Predict the display, run it, and explain where each number came from. Then change it to exactly two hours and explain why the remaining minutes should be zero. Finish by restoring `main` to 95 minutes.

Answer aloud or in writing:

1. When would you choose `val` instead of `var`?
2. Why does `95 / 60` produce a different kind of result from `95.0 / 60.0`?
3. What is the difference between `95` and `"95"`?
4. Why should a duration formatter be tried with both 59 and 60 minutes?

Next week, return to the 143-minute example without opening the solution. A useful private learning note is: “I assumed __. Running __ showed __. I can now explain __.” Sharing it is optional.

## Completion checklist

- [ ] I ran the starter and identified its incorrect output.
- [ ] I can explain the supplied function's input and result.
- [ ] I tried a `val` reassignment, understood the error, and removed it.
- [ ] I fixed the formatter and passed its seven acceptance tests.
- [ ] I solved the changed requirement and explained my reasoning.
- [ ] I know how to return to the original starter using the recovery instructions below.

To restart safely, keep a copy of your edited file elsewhere, then use Git's file history or a fresh repository download to recover the starter. Do not overwrite work you want to keep. The original published starter contains `val minutes = 0` and `main` uses 95 minutes.

Next planned chapter: **Chapter 02 Control flow and functions**, adding rules for study sessions.

## References

See Kotlin's official [basic syntax](https://kotlinlang.org/docs/basic-syntax.html), [number operations](https://kotlinlang.org/docs/numbers.html), and [string templates](https://kotlinlang.org/docs/strings.html). Toolchain and execution evidence are in the [verification record](VERIFICATION.md).
