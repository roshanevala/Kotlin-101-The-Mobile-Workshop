# Chapter 02 Worked solution

Try the [workshop](WORKSHOP.md) before continuing. The complete implementation lives in the separate [reference file](../../kotlin-labs/ch02-control-flow-and-functions/src/solution/kotlin/workshop/ch02/StudyPlan.kt).

## Keep each function responsible for one rule

`isValidDuration` returns whether the duration belongs to `1..120`. Both endpoints are valid. This is a Boolean calculation, so it can return the range check directly.

`sessionLabel` uses a `when` expression with three ranges and an `else` for invalid durations. The valid ranges meet without overlapping or leaving gaps: 24 is `Quick`, 25 is `Focus`, 49 is `Focus`, and 50 is `Deep`. Using `else` to mean `Deep` would incorrectly accept negative and excessive durations.

The planner calls the validation function first. If validation fails, an early return ends the call before any plan is built. It checks the round count second, which preserves the specified priority when both arguments are invalid.

These messages are the exercise's simple error contract. Later chapters introduce nullable inputs and more explicit error models; a display string is sufficient for this mission.

## Build lines without an extra separator

For accepted inputs, the reference gets the label once and starts a local empty string. The loop visits `1..rounds`, including the last round. Its body is:

```kotlin
if (round > 1) {
    plan += "\n"
}
plan += "Round $round: $label"
```

The first line gets no separator. Each later line gets one newline before its text. Nothing is appended after the final line, so the returned string has no trailing newline.

The local `var` starts fresh on every call. Changing it does not change anything outside the function, and the result depends only on the arguments. The function stays pure. `main` handles the separate job of printing, and its `println` adds the terminal's final line break.

`rounds: Int = 1` supplies one round only when the caller omits the argument. The sample names both arguments to make `30` and `3` easy to interpret. Named arguments do not change the validation rules.

String concatenation keeps this small exercise within the concepts taught so far. A builder or collection transformation can be compared later when those tools are introduced.

## Bug Museum The extra round

**Symptom:** a request for three rounds produces four lines or starts at `Round 0`.

**Cause:** `0..rounds` includes zero and the upper endpoint. For three rounds, its values are 0, 1, 2, and 3.

**Repair:** use the required numbering range, `1..rounds`, after validating the count.

**Useful check:** compare a one-round plan and a four-round plan with exact expected text. This catches extra lines, missing final rounds, and separators in the wrong place. The original starter has a simpler problem: it always returns one line and ignores the requested count entirely.

## Verify the reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch02-control-flow-and-functions:runSolution :ch02-control-flow-and-functions:checkSolution
```

The reference prints:

```text
Round 1: Focus
Round 2: Focus
Round 3: Focus
```

The same 19 tests check either your working copy through `test` or the reference through `checkSolution`. They compile separately. A passing reference check does not verify your implementation; use `test` for that.

## Change request answers

24 minutes and two rounds produces:

```text
Round 1: Quick
Round 2: Quick
```

Changing the duration to 25 gives two `Focus` lines. Changing the count to five gives `Invalid rounds` before the loop.

For the interview prompts: validate before repeating work so rejected inputs cannot create a plan; `return` gives a value to the caller while `println` writes output; checking 30 alone does not test whether 24 and 25 fall into different categories.

On a later day, explain the rules from memory and predict the result for 50 minutes with no round count supplied. It should be `Round 1: Deep`. This checks the label transition and the default together.

**[Next Learn null safety and errors in Chapter 03](../03-null-safety-and-errors/LESSON.md)**

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
