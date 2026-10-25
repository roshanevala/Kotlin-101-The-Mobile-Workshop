# Workshop 02 Repair the study planner

Bug report: “I requested three study rounds. The planner gave me one. It also accepts zero-minute sessions.” Your mission is to give it rules it can explain and follow.

Read the [lesson](LESSON.md) first. Complete this in three small steps: validate a duration, choose its label, and build a plan. Keep each function's name, parameters, and return type as supplied.

## Run and find your working copy

Open the existing **`kotlin-labs`** workspace in Android Studio. After a course update adds a chapter, click **Sync Project with Gradle Files** in the top toolbar and wait for sync to finish. This imports the new chapter as a module so Android Studio can recognize its code and show the Run button beside `main()`.

From its terminal:

```sh
./gradlew --console=plain :ch02-control-flow-and-functions:run
```

On Windows, use `./gradlew.bat`. If this is your first local chapter, follow the [one-time setup](../../kotlin-labs/README.md).

The command creates your editable file at:

```text
ch02-control-flow-and-functions/src/main/kotlin/workshop/ch02/StudyPlan.kt
```

You do not need to create folders manually. Open that file in Android Studio and work in `src/main`. It is ignored by Git so your answer stays out of course commits; it is still a normal editable file on your computer. Existing work is preserved on later runs. Your Chapter 01 timer stays separate.

The original question is in [src/starter](../../kotlin-labs/ch02-control-flow-and-functions/src/starter/kotlin/workshop/ch02/StudyPlan.kt). The reference answer is in `src/solution`; leave it for review. For a browser attempt, paste the starter template into [Kotlin Playground](https://play.kotlinlang.org/).

A fresh copy prints:

```text
Round 1: Focus
```

Setup has worked, but the request in `main` asks for **three** rounds. Find that call and predict which arguments would currently make no difference to the result.

## Mission 1 Accept sensible durations

Implement `isValidDuration(minutes: Int): Boolean`.

| Input | Required result |
| --- | --- |
| 1 through 120, including both limits | `true` |
| Zero, any negative number, or anything above 120 | `false` |

Use comparisons or a range. This function returns an answer to a question; it should not print anything or change external state.

Before coding, predict the results for 0, 1, 120, and 121. Then implement the rule.

## Mission 2 Give each duration a label

Implement `sessionLabel(minutes: Int): String` using `when` or `if` branches.

| Minutes | Exact result |
| --- | --- |
| 1–24 | `Quick` |
| 25–49 | `Focus` |
| 50–120 | `Deep` |
| Any invalid duration | `Invalid duration` |

The labels describe this planner's session categories. Check both sides of each transition: 24/25 and 49/50. The first and last valid values need labels too.

## Mission 3 Build the requested rounds

Implement `buildStudyPlan(minutes: Int, rounds: Int = 1): String`.

Apply these rules in order:

1. If the duration is invalid, return exactly `Invalid duration`. Reuse `isValidDuration`.
2. Otherwise, if `rounds` is outside 1 through 4 inclusive, return exactly `Invalid rounds`.
3. For valid inputs, use `sessionLabel` and a loop to create one line per round, numbered from 1. Each line has the form `Round N: Label`.

Join lines with `\n`, with no blank line at the beginning or newline at the end of the returned string. Keep the default of one round when the argument is omitted. Use a fresh local string for each call; return it rather than printing from the function.

For `buildStudyPlan(minutes = 30, rounds = 3)`, the returned text is:

```text
Round 1: Focus
Round 2: Focus
Round 3: Focus
```

For `buildStudyPlan(minutes = 10)`, it is `Round 1: Quick`. For `buildStudyPlan(minutes = 0, rounds = 0)`, it is `Invalid duration`: the duration check takes priority when both inputs are invalid.

Changing `main` to print three constant lines does not satisfy other calls. Keep the rules in the functions. You can complete the mission with the syntax taught in the lesson; no collections, lambdas, or input parsing are required.

If stuck, open the [optional hints](HINTS.md) one at a time.

## Check your work

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch02-control-flow-and-functions:test
```

The supplied **19 tests** call your functions with known inputs. They cover valid endpoints, label transitions, rejected values, the default argument, round numbering, exact text, validation priority, and independent calls. You do not need to write test-framework code yet.

Some checks pass even on the incomplete starter; others fail until you finish the corresponding mission. Rerun after each step and use the failing test names to find the remaining rule. An expected-versus-actual failure means the code ran and returned different text or a different Boolean. A compile error means Kotlin could not build it.

The report is generated at `kotlin-labs/ch02-control-flow-and-functions/build/reports/tests/test/index.html`. Passing all 19 checks confirms the listed examples, rather than proving every possible implementation correct.

## Try a changed request

Change the call in `main` to 24 minutes and two rounds. Predict the text, run it, then change only the duration to 25. Explain why the label changes while the number of lines stays the same.

Now request five rounds. Which rule should reject it? Finish by restoring `main` to 30 minutes and three rounds.

For an interview rehearsal, explain in your own words why the validation checks come before the loop, why `return` differs from `println`, and why checking only 30 minutes would miss a label-boundary bug. Speaking for a minute or writing a short answer is enough.

## Finish the mission

- [ ] I can explain parameters, named arguments, defaults, and return values.
- [ ] I checked both sides of the duration and label boundaries.
- [ ] My working copy passes all 19 checks.
- [ ] I can explain why the loop produces exactly the requested number of rounds.
- [ ] I tried the changed request before opening the solution.

Your answer stays local and is not backed up by normal course commits. To restart, save it outside the chapter's `src` folders, then copy the tracked starter template over the working file. Git history does not contain edits to the ignored copy.

**[Next Review the explained solution](SOLUTION.md)**

[Back to the lesson](LESSON.md) · [Chapter overview](README.md)
