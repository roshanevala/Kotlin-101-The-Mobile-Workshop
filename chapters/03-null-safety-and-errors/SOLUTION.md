# Chapter 03 Worked solution

Try the [workshop](WORKSHOP.md) first. The complete implementation lives in the separate [reference file](../../kotlin-labs/ch03-null-safety-and-errors/src/solution/kotlin/workshop/ch03/SessionInput.kt).

## Give each helper a clear contract

`topicOrDefault` uses a safe call to trim a supplied topic. If the value is missing, an Elvis early return supplies `Untitled`. If the trimmed value is empty, the next check supplies the same fallback. Other text is returned with its case and internal spaces preserved.

`parseMinutes` chains safe operations:

```kotlin
return rawMinutes?.trim()?.toIntOrNull()
```

It returns no number for missing or unparseable text. Zero, negative integers, and the `Int` endpoints still parse. This helper converts a representation; it does not decide whether a session is allowed.

`durationError` owns the session rule. Values in `1..120` have no error, represented by `null`. Other numbers get the specified error string. The function name and contract make this different meaning of `null` explicit.

## Keep failure visible in the final description

The final function starts with:

```kotlin
val minutes = parseMinutes(rawMinutes) ?: return "Enter whole minutes"
```

If execution continues, `minutes` is a non-null `Int`. There is no need for `!!` or a fake default.

The reference then asks for a duration error and returns it when present. Once both checks succeed, it gets the topic and formats `Study TOPIC for N min`. All values are local, and the helpers do not print or change shared state. `main` handles printing.

These simple nullable contracts work with the types taught so far. They intentionally group missing, malformed, and overflowing numeric text under one message. If a future screen must react differently to each case, use richer result models after learning classes and sealed types in Chapter 04.

The lesson's strict seat example shows an alternative exception contract. This form handles expected bad input through return values, so the reference needs no `try`/`catch` or forced null assertion.

## Bug Museum The invented 30 minutes

**Symptom:** `banana` creates a 30-minute session.

**Cause:** the original starter ignores the input. A tempting replacement, `toIntOrNull() ?: 30`, preserves the same problem by turning failed conversion into a plausible duration.

**Repair:** keep the failed conversion as `null` until the caller returns `Enter whole minutes`.

**Useful checks:** compare `"banana"`, `"0"`, and `"30"`. The first cannot become an integer, the second is an integer outside the session rule, and the third is accepted. One valid input alone cannot reveal a hidden fallback.

Elvis has another limit: `rawTopic ?: "Untitled"` does not handle an all-space topic. Trimming and checking emptiness are separate steps.

## Verify the reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch03-null-safety-and-errors:runSolution :ch03-null-safety-and-errors:checkSolution
```

The reference prints `Enter whole minutes` for the original `banana` input. All 26 tests check the reference through `checkSolution`, or your working copy through `test`. The two implementations compile separately. Use `test` to check your own fix.

## Change request answers

| Topic | Minutes | Result |
| --- | --- | --- |
| `null` | `" 45 "` | `Study Untitled for 45 min` |
| `null` | `"0"` | `Minutes must be 1..120` |
| `null` | `"banana"` | `Enter whole minutes` |
| `"null"` | `"1"` | `Study null for 1 min` |

The word `null` is present text, so it is preserved. Missing or malformed minutes do not become zero; parsing and duration validation remain different decisions.

For the interview prompts: a default duration hides failed conversion; a parsed number still needs a domain rule; nullable results suit this form's expected input failures, while a strict API can document an exception contract. Neither a nullable type nor an exception automatically defines what the caller should do.

On a later day, predict the behavior for `"2147483647"` and `"2147483648"` without running them. The first parses but fails duration validation; the second cannot become an `Int` and gets the parsing message.

**[Next Chapter 04 Classes and modeling](../04-classes-and-modeling/LESSON.md)**

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
