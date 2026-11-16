# Workshop 16 The copied plan changed both schedules

Bug report: “A copied plan completes every session in the original. Missing input becomes zero, and the coach's callback keeps its owner alive.” Repair the small Swift model before connecting it to a native UI.

Read the [lesson](LESSON.md) first. Split the mission into input/modeling, value mutation/generics, then closure ownership if useful.

## Build and find your answer

Follow [Swift setup](../../swift-labs/README.md). The checked Mac setup uses **Xcode 27.0 (27A266a)**, **Apple Swift 6.4**, **Swift 6 language mode**, and **Python 3.9+**. No Android SDK, Gradle, simulator, iOS project, or downloaded package is needed.

From the repository root:

```sh
cd swift-labs/ch16-swift-for-kotlin-developers
python3 lab.py run
```

Edit the generated **`src/practice/StudyPlan.swift`**. Keep the tracked starter/reference, runner, and acceptance checks unchanged. The compiler can warn that the starter's `changed` binding was never mutated; this is useful evidence about a class reference, not a failed setup.

The untouched starter builds and its program output is deliberately wrong:

```text
Topic:
Original completed: 65
Copy completed: 65
Topics: Kotlin
After release: Keep going, Ada!
```

The empty value on the first line follows `Topic:`. Compilation diagnostics appear separately. Before editing, predict why copying `StudyPlan` did not isolate it and which reference keeps Ada alive. Preparation preserves existing answers; `clean` only removes `.build`.

## Agree on the contracts

- `parseMinutes` accepts optional text, trims outer whitespace/newlines, and returns an optional representable Int. Missing, blank, malformed, fractional, or overflowing input produces nil. Representable zero/negative values remain integers for the separate validator.
- `makeSession` checks a positive ID first, then a duration in **1...180 inclusive**. It returns the appropriate `.invalid` case, otherwise `.valid` with the same ID/minutes and `isCompleted = false`.
- Trim a topic's outer whitespace/newlines, retaining interior text. Missing or blank topics become **Untitled**. This factory validates external input; directly constructing stored-value fixtures is separate.
- Sessions and plans have value behavior. Assigning/copying a plan and editing its records or appending a record must preserve the original. Its session array contains value records.
- `complete(id:)` finds the **first matching ID**. If absent or already complete, return false and preserve the plan. Otherwise complete just that record and return true. Do not use topic text or array position as identity, and do not fall through to later duplicate IDs.
- `completedMinutes` sums only completed records, with zero for an empty plan.
- `topicNames<T: TopicNamed>` works for arrays of any conforming element type, preserves order and duplicate topics, and returns an empty array for empty input.
- A coach's installed callback returns **Keep going, Ada!** while an Ada owner is kept alive. The stored callback must not retain its coach. Once the last outside strong owner releases it, a weak observer becomes nil; a separately retained callback safely returns **Coach unavailable**. Choose ownership that permits that lifetime.

## Mission 1 Restore absence and validation

Repair parsing using optional binding and Int conversion. Repair the factory using the two validation cases and a cleaned/fallback topic. Use guard/if/switch as taught; no force unwrap or thrown-error API is required.

Run the checks:

```sh
python3 lab.py test
```

The fixed checker runs **34 acceptance cases** and exits unsuccessfully when a contract fails. The untouched starter intentionally fails some. Compiler errors mean the edited Swift no longer compiles; a completed build followed by FAIL lines is different evidence. These checks use ordinary conditions, so optimization cannot disable them like `assert`.

## Mission 2 Give the plan independent values

Choose a value type for StudyPlan and mark its editing method as mutating. Keep the initializer call shape and the stored session values usable; a struct's memberwise initializer can replace the class's explicit initializer if you choose.

Find the first matching array index, preserve all no-op cases, and edit one record. Calculate completed minutes without counting planned records. Repair the generic helper so it handles every element, rather than returning only the first topic.

Check the reference behavior you are aiming for by predicting each line, then running your answer again:

```text
Topic: Untitled
Original completed: 0
Copy completed: 40
Topics: Kotlin, Swift
After release: Coach unavailable
```

The final line needs Mission 3 too. Explain why changing the caller's binding from let to var alone did not make the original class independent.

## Mission 3 Break the callback's ownership cycle

Draw the coach → stored closure → coach edges. The callback may be retained outside the coach, so it must also handle its owner being gone. Use a weak capture and safely unwrap or supply a fallback when invoked. An unowned lifetime assumption does not match this contract.

The checker keeps an owner alive explicitly for its live-callback case, then observes release through a weak reference in separate cases. It also invokes a retained callback after release. Compare that with merely printing a deinitializer at an assumed end of scope.

```sh
python3 lab.py test --optimized
```

Check both modes before declaring the repair complete. ARC behavior and the acceptance conditions must not depend on debug-only assertions or guessed timings.

## Investigate, explain, and revisit

Use the [progressive hints](HINTS.md) as needed. Bug Museum: put a class-valued property inside a struct, like Example 2's MinuteBox. Which part is copied and which object remains shared? Avoid claiming that every nested object is cloned by a struct assignment.

Changed request: in a temporary copy, define a `Flashcard` struct with a topic property and conform it to TopicNamed. Pass three cards, including duplicate topic text, through the generic helper. Predict the order. Then copy a plan whose first duplicate-ID record is already completed; `complete(id:)` must return false without changing the later record.

Explain in 60 seconds, or write: “How do Swift optionals represent absence? When does a mutating method require var? How do value equality, object identity, and ARC ownership differ? What does a weak stored callback do after its owner is released?” On another day, reconstruct one small example and explain it before opening this answer again.

To reset this mission, move `src/practice/StudyPlan.swift` somewhere safe outside the source folder and run `python3 lab.py prepare`. Existing files remain intact. Reference actions do not compile or prepare your answer:

```sh
python3 lab.py run-solution
python3 lab.py check-solution
python3 lab.py check-solution --optimized
```

**[Review the worked solution](SOLUTION.md)** · [Review the lesson](LESSON.md)
