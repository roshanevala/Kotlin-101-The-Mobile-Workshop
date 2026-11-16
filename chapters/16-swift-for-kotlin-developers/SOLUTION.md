# Chapter 16 Worked solution

Attempt the [workshop](WORKSHOP.md) before opening the [reference Swift model](../../swift-labs/ch16-swift-for-kotlin-developers/src/solution/StudyPlan.swift). The fixed [runner](../../swift-labs/ch16-swift-for-kotlin-developers/src/runner/main.swift) and [acceptance checker](../../swift-labs/ch16-swift-for-kotlin-developers/src/checks/main.swift) supply the executable entry points.

## Input and values

Parsing unwraps optional text, trims it, and lets Int conversion return nil on failure. The factory checks ID first and the inclusive duration range second, then normalizes the topic with a fallback. A valid result is an incomplete session; malformed parsing and invalid business input stay distinct.

StudySession and StudyPlan are structs containing value data. The plan's mutating completion method unwraps the first matching index, returns false for absent/already complete records, and changes only that row. The original copied value remains intact. Completed totals filter before summing. The generic topic helper maps the protocol requirement without dropping or deduplicating records.

## Callback ownership

StudyCoach is intentionally a class with an instance lifetime. Its stored closure weakly captures self, then checks that optional owner when invoked. While the owner is retained, the callback can read its name. After the last strong owner is gone, the callback returns the specified fallback. Keeping the callback alone therefore keeps no coach alive.

The separate class-in-struct example still shares its referenced object; value semantics do not clone that object. This is a different design from the mission's all-value session array. Choose the ownership/value contract before selecting a type keyword.

## Run the independent reference

From `swift-labs/ch16-swift-for-kotlin-developers`:

```sh
python3 lab.py run-solution
python3 lab.py check-solution
python3 lab.py check-solution --optimized
python3 lab.py examples
```

Expected reference program output:

```text
Topic: Untitled
Original completed: 0
Copy completed: 40
Topics: Kotlin, Swift
After release: Coach unavailable
```

The same 34 behavioral checks pass with normal and optimized compilation. The reference actions compile only the solution source and fixed entry point. They never overwrite or depend on a learner answer. The six complete lesson programs have their own smaller types and stated outputs.

## Changed request answer

A Flashcard with a readable topic property can conform to TopicNamed. The generic helper returns every topic in input order, including duplicates, without knowing the Flashcard's other fields. If a plan's first matching ID is already complete, the method returns false at that record; it never searches for a later incomplete duplicate.

**Next: Chapter 17 will introduce SwiftUI and the native iOS app lifecycle. Its lesson is planned.**

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
