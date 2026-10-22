# Workshop 01 Recover the missing minutes

A 95-minute study session displays as `1 h 0 min`. You will fix the formatter so it keeps the remaining minutes.

Start with the [lesson](LESSON.md) if values, types, division, or remainder are new to you. Then follow the [one-time local setup](../../kotlin-labs/README.md), or use the browser option below.

## Run the starter

In Android Studio, open the repository's **`kotlin-labs`** folder. Open its terminal and run:

```sh
./gradlew --console=plain :ch01-values-and-types:run
```

On Windows, use `./gradlew.bat`. The program prints `1 h 0 min` among Gradle's messages. This confirms that setup worked and shows the bug you will investigate.

For a browser attempt, copy the [starter file](../../kotlin-labs/ch01-values-and-types/src/main/kotlin/workshop/ch01/Timer.kt) into [Kotlin Playground](https://play.kotlinlang.org/). Local acceptance checks use the Gradle commands in this workshop.

## Find the code to change

Open [Timer.kt in the starter](../../kotlin-labs/ch01-values-and-types/src/main/kotlin/workshop/ch01/Timer.kt). Work in `src/main`; the `src/solution` file is the separate reference.

- `main` sets the sample duration and prints the formatter's result.
- `formatStudyTime` receives the duration and returns the display text.
- `package workshop.ch01` groups this chapter's code. Leave it unchanged.
- Text after `//` is a comment, which is not executed.

Before editing, identify how the code calculates complete hours and where it gets the remaining minutes. What makes the initial output incorrect?

## Build the fix

Change the calculation inside `formatStudyTime`. Keep its name, input type, result type, and display format unchanged. The input is a nonnegative `Int` representing whole minutes. Input parsing and negative-duration validation come in later lessons.

Your implementation should handle these examples:

| Total minutes | Required output |
| --- | --- |
| 0 | `0 h 0 min` |
| 59 | `0 h 59 min` |
| 60 | `1 h 0 min` |
| 95 | `1 h 35 min` |
| 125 | `2 h 5 min` |

Changing only the sample number in `main` does not fix the formatter for other inputs. Rerun the program after editing the calculation. If you get stuck, open the [optional hints](HINTS.md) one at a time.

## Check your work

A test calls your function with a known input and compares the actual result with expected text. The supplied checks cover the examples above, one minute, and the largest nonnegative `Int`. You do not need to write test-framework code yet.

From `kotlin-labs`, run:

```sh
./gradlew --console=plain :ch01-values-and-types:test
```

Some tests fail on the untouched starter on purpose. A compile error means the program could not be built; an expected-versus-actual test failure means the program ran but returned a different value. The report is generated at `kotlin-labs/ch01-values-and-types/build/reports/tests/test/index.html`.

Aim to pass all seven examples. Passing these checks gives evidence for the listed behavior; it does not prove every possible program correct.

## Try a changed requirement

Change the sample in `main` to **143 minutes**. Predict the display, run it, and explain where each part came from. Then try exactly two hours. Finish by restoring `main` to 95 minutes.

Explain why checking only 60 minutes would have missed the original bug. Record one thing you misunderstood and what helped you resolve it. You can keep this note private.

## Finish the mission

- [ ] I can explain the starter's original behavior.
- [ ] I fixed the calculation and passed all seven checks.
- [ ] I tried a changed input and explained the result.

To restart, preserve a copy of your edits, then recover the starter from Git's file history or a fresh repository download. The original starter has `val minutes = 0` and its `main` uses 95 minutes. Keep any work you want before replacing that file.

**[Next Review the explained solution](SOLUTION.md)**

[Back to the lesson](LESSON.md) · [Chapter overview](README.md)
