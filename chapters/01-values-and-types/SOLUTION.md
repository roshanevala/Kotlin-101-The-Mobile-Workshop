# Chapter 01 Worked solution

Try the [mission](README.md) before reading further. The separate [reference file](../../kotlin-labs/ch01-values-and-types/src/solution/kotlin/workshop/ch01/Timer.kt) contains the complete implementation.

## Recover the minutes

```kotlin
val hours = totalMinutes / 60
val minutes = totalMinutes % 60
return "$hours h $minutes min"
```

For 95 minutes, integer division finds one complete hour. Remainder finds 35 minutes after that hour is removed. The template joins the two values with the agreed units, yielding `1 h 35 min`.

Subtraction is another valid approach: `totalMinutes - hours * 60`. For the mission's nonnegative Int inputs, both calculations find the leftover minutes. Explain whichever version you use.

The variables use `val` because each calculation produces a value that will not be reassigned. The function receives an `Int` and returns a `String`; the string is the display representation of the numeric calculation.

## Bug Museum The vanishing minutes

**Symptom:** the sample says `1 h 0 min` for 95 minutes.

**Cause:** the hours calculation keeps complete hours, while the minutes calculation always returns zero. Changing the input in `main` or printing a fractional number of hours does not satisfy the required hours-and-minutes format.

**Repair:** calculate the remaining minutes from the input instead of using a constant.

**Useful check:** 60 minutes alone is insufficient because the broken calculation already produces `1 h 0 min`. Include a duration below an hour and one with a remainder, such as 59 or 95 minutes.

The largest input check also matters: hours should continue beyond 23 because this is a duration, not a time of day. Input validation for negative values is outside this mission's contract.

## Verify the reference

From `kotlin-labs`:

```sh
./gradlew --console=plain :ch01-values-and-types:runSolution :ch01-values-and-types:checkSolution
```

The reference prints `1 h 35 min`. The same seven examples check either your edited starter through `test` or the reference through `checkSolution`. The reference can pass while your starter still fails: they compile separately. Use `test` to verify your own fix.

## Change request answers

143 minutes becomes `2 h 23 min`: two complete hours account for 120 minutes, leaving 23. Exactly 120 minutes becomes `2 h 0 min`.

For the interview prompts: use `var` when reassignment is needed; integer division and floating-point division operate on different numeric types; quotes create text rather than a numeric value; 59 and 60 test both sides of the first whole-hour boundary.
