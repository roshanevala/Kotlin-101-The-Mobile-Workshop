# Workshop 01 Recover the missing minutes

A 95-minute study session displays as `1 h 0 min`. You will fix the formatter so it keeps the remaining minutes.

Start with the [lesson](LESSON.md) if values, types, division, or remainder are new to you. Then follow the [one-time local setup](../../kotlin-labs/README.md), or use the browser option below.

## Run the starter

In Android Studio, open the repository's **`kotlin-labs`** folder. Open its terminal and run:

```sh
./gradlew --console=plain :ch01-values-and-types:run
```

On Windows, use `./gradlew.bat`. The first run creates your local working copy. A fresh copy prints `1 h 0 min` among Gradle's messages, showing the bug you will investigate. Existing work is preserved, so later runs show your own implementation's output.

For a browser attempt, copy the [starter template](../../kotlin-labs/ch01-values-and-types/src/starter/kotlin/workshop/ch01/Timer.kt) into [Kotlin Playground](https://play.kotlinlang.org/). Local acceptance checks use the Gradle commands in this workshop.

## Find the code to change

After running the command above, open `ch01-values-and-types/src/main/kotlin/workshop/ch01/Timer.kt` inside `kotlin-labs`. This is **your working copy**.

You do not need to create this file or its folders yourself. You can open and edit it normally in Android Studio. On GitHub, you will see the original `src/starter` template.

| Folder | Purpose |
| --- | --- |
| `src/starter` | The published question, kept in Git |
| `src/main` | Your editable working copy, ignored by Git |
| `src/solution` | The separate reference answer |

Edit `src/main` only. Running or preparing the exercise again does not overwrite it. Normal Git commits leave your answer out, so the published question remains useful to the next learner. Your working copy stays local; it is not backed up by those commits.

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

To restart, save your work outside the chapter's `src` folders, then copy the [starter template](../../kotlin-labs/ch01-values-and-types/src/starter/kotlin/workshop/ch01/Timer.kt) over your local working file. The template contains the original bug and uses 95 minutes. Git history does not contain edits to the ignored working copy.

**[Next Review the explained solution](SOLUTION.md)**

[Back to the lesson](LESSON.md) · [Chapter overview](README.md)
