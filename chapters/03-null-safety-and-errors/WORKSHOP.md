# Workshop 03 Stop the invented session

Bug report: “I typed `banana` into the minutes field. The planner scheduled 30 minutes.” It also replaces every topic with `Kotlin`. Your mission is to handle what was actually supplied and explain what cannot be accepted.

Read the [lesson](LESSON.md) first. Work through four short helpers in order. Keep their supplied names, parameters, and result types.

## Run and find your working copy

Keep **`kotlin-labs`** open in Android Studio. Click **Sync Project with Gradle Files** in the top toolbar after updating the course, and wait for sync to finish. Then run this in its terminal:

```sh
./gradlew --console=plain :ch03-null-safety-and-errors:run
```

On Windows, use `./gradlew.bat`. The command creates your editable file:

```text
ch03-null-safety-and-errors/src/main/kotlin/workshop/ch03/SessionInput.kt
```

Open that file and edit `src/main` only. Existing work is preserved on later runs, and this local copy stays out of normal Git commits. It is still a normal file you can edit in Android Studio. Your Chapter 01 and 02 answers stay separate.

The published question lives in [src/starter](../../kotlin-labs/ch03-null-safety-and-errors/src/starter/kotlin/workshop/ch03/SessionInput.kt). For a browser attempt, paste that template into [Kotlin Playground](https://play.kotlinlang.org/). The [one-time setup guide](../../kotlin-labs/README.md) is available if needed.

A fresh copy prints:

```text
Study Kotlin for 30 min
```

Find the call in `main`. Where did those 30 minutes come from? Predict what the current program would do with a different topic and with missing minutes before changing anything.

## Mission 1 Keep the topic or choose a fallback

Implement `topicOrDefault(rawTopic: String?): String`.

| Input | Exact result |
| --- | --- |
| `null`, an empty string, or text containing only whitespace | `Untitled` |
| Other text | Trim the beginning and end, preserving case and spaces inside |

For example, `" KMP "` becomes `"KMP"`, and `" Kotlin  basics "` becomes `"Kotlin  basics"`. The text `"null"` is an ordinary topic, not an absent value.

Use safe access or a null check. Remember that Elvis alone does not handle an empty or all-space string.

## Mission 2 Parse without hiding a failure

Implement `parseMinutes(rawMinutes: String?): Int?`.

Trim supplied text, then parse it as a decimal `Int` using `toIntOrNull`. Missing, empty, malformed, or out-of-`Int` input returns `null` without throwing for those inputs. A valid integer is returned even if the planner will later reject its duration.

| Input | Required result |
| --- | --- |
| `" 30 "` | `30` |
| `"+30"` | `30` |
| `"0"` and `"-5"` | `0` and `-5` |
| `null`, `""`, or `"   "` | `null` |
| `"banana"`, `"4.5"`, `"30 min"`, or `"3 0"` | `null` |
| `"2147483648"` | `null` because it exceeds `Int.MAX_VALUE` |

This function answers “Can this become an `Int`?” It does not apply the 1–120 minute session rule. Do not substitute zero or 30 for a failed conversion.

## Mission 3 Explain a rejected number

Implement `durationError(minutes: Int): String?`.

| Input | Exact result |
| --- | --- |
| 1 through 120, including both limits | `null`, meaning no error |
| Any other `Int` | `Minutes must be 1..120` |

Keep the difference between the two nullable contracts clear: `parseMinutes` can have **no value**; `durationError` can have **no error**.

## Mission 4 Describe the request

Implement `describeSession(rawTopic: String?, rawMinutes: String?): String` by calling the three helpers.

Apply these rules in order:

1. Parse the minutes. If there is no parsed value, return exactly `Enter whole minutes`.
2. Check the parsed duration. If there is an error, return that exact message.
3. For an accepted duration, use the cleaned topic or fallback and return `Study TOPIC for N min`.

Examples:

| Topic | Minutes text | Exact result |
| --- | --- | --- |
| `" KMP "` | `" 45 "` | `Study KMP for 45 min` |
| `null` | `"30"` | `Study Untitled for 30 min` |
| `"Kotlin"` | `"banana"` | `Enter whole minutes` |
| `"Kotlin"` | `null` | `Enter whole minutes` |
| `"Kotlin"` | `"0"` | `Minutes must be 1..120` |
| `"Kotlin"` | `"121"` | `Minutes must be 1..120` |

The function returns text; `main` prints it. Keep the helpers pure and let each call depend only on its inputs. No keyboard input, screen UI, custom classes, or exceptions are required. Avoid `!!`: missing input is part of this mission's contract.

If stuck, open the [optional hints](HINTS.md) one at a time.

## Check your work

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch03-null-safety-and-errors:test
```

The **26 supplied tests** cover missing and blank inputs, preserved topic text, signed integers, parsing limits, invalid formats, duration boundaries, exact error messages, and independent calls. Test-framework code is supplied; you do not need to write it yet.

Some tests fail on the unfinished starter by design. Run them after each mission and read the failing names to find the remaining contract. An expected-versus-actual failure means the code ran and disagreed with the specification. A compile error means Kotlin could not build it.

The report is generated at `kotlin-labs/ch03-null-safety-and-errors/build/reports/tests/test/index.html`. Passing the checks confirms the listed examples rather than proving every possible implementation correct.

## Try a changed request

In `main`, try a missing topic with `" 45 "` minutes. Predict the result before running it. Then keep that topic and change the minutes to `"0"`, followed by `"banana"`. Explain why the two invalid requests need different messages.

Next, use the topic `"null"` and one minute. Explain why that word is preserved. Finish by restoring the original `main` call with `" Kotlin "` and `"banana"`.

For an interview rehearsal, explain why `toIntOrNull() ?: 30` would hide a mistake, why a successful parse does not guarantee a valid duration, and how you chose between a nullable result and an exception. Speaking for a minute or writing a short answer is enough.

## Finish the mission

- [ ] I can distinguish a missing value from blank text.
- [ ] I can use a safe call, a smart cast, and an Elvis fallback or early return.
- [ ] My working copy passes all 26 checks.
- [ ] I can explain the different meanings of `null` in the two helper contracts.
- [ ] I tried the changed inputs without hiding failure behind a made-up duration.

Your working answer is local and is not backed up by normal course commits. To restart, save it outside the chapter's `src` folders, then copy the tracked starter template over the working file. Git history does not contain edits to the ignored copy.

**[Next Review the explained solution](SOLUTION.md)**

[Back to the lesson](LESSON.md) · [Chapter overview](README.md)
