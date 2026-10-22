# Chapter 00 Starting point and learning habits

Use this chapter to identify what you remember and what needs practice. The course focuses first on Android and Kotlin, then extends those skills to KMP and iOS. Plan for 4–6 hours per week, with small sessions and practical work.

## Outcomes

After this chapter, you should have a record of your current skills, understand the difference between KMP and shared UI, and know how to use each later chapter.

New to programming? The code prediction later in this chapter uses classes and collections that will be taught in subsequent lessons. You can mark those topics as new and begin with [Chapter 01](../01-values-and-types/README.md). Returning developers can use the prediction to find a useful practice topic.

## Record your starting point

For each topic below, mark one of: **new**, **can explain**, **can implement with help**, or **can implement independently**.

- Kotlin values, null safety, functions, classes and collections.
- Coroutines, cancellation, Flow and asynchronous testing.
- Android lifecycle, Compose state, ViewModel and process death.
- HTTP, SQL, repositories and offline behavior.
- Swift, SwiftUI, memory management and concurrency.
- KMP source sets, shared boundaries and Swift integration.
- Coding problems, complexity analysis, debugging and system design.

Choose one recent interview question you struggled with. Record the question as you remember it, your answer, what became unclear, and a small exercise that would demonstrate understanding. Keep private interview details in your personal notes.

## Understand what KMP shares

A shared Kotlin rule can decide whether a study session is valid. Android can display that result using Jetpack Compose, while iOS displays it using SwiftUI. Both clients call the same rule.

Compose Multiplatform adds the option to share a UI implementation as well. We will first learn the underlying platform concepts and later compare UI approaches on a working feature. See the [technology research](../../docs/maintainers/RESEARCH.md) for official references.

## Predict a small Kotlin program

Read this code without running it. Write down the four printed lines and explain whether `copy` creates an independent list.

```kotlin
data class StudyPlan(val topics: MutableList<String>)

fun main() {
    val original = StudyPlan(mutableListOf("Kotlin"))
    val copy = original.copy()
    copy.topics.add("Coroutines")

    println(original.topics)
    println(original == copy)
    println(original === copy)
    println(original.topics === copy.topics)
}
```

This is a reasoning diagnostic. If you want to run it immediately, paste it into [Kotlin Playground](https://play.kotlinlang.org/). The local exercise workspace is introduced in [Chapter 01](../01-values-and-types/README.md).

Try these follow-up questions before reading the answer:

1. Why can a list held in a `val` change?
2. Would declaring the property as `List<String>` guarantee deeply immutable data?
3. How could you create a copy whose mutable list is independent?
4. What test would demonstrate that independence?

## Diagnostic explanation

The expected output is:

```text
[Kotlin, Coroutines]
true
false
true
```

The generated data class `copy` performs a shallow copy. The two `StudyPlan` instances are different objects, but both refer to the same list. Their generated equality comparison checks the constructor properties, which are equal here.

`val` prevents reassignment of that property; it does not freeze the referenced object. A `List` exposes a read-only interface, which does not guarantee that another reference cannot mutate its underlying data.

For this model containing strings, `original.copy(topics = original.topics.toMutableList())` creates an independent list container. A useful behavioral check adds a topic to the copy and verifies that the original topics remain unchanged. With nested mutable objects, copying the container alone would not isolate those objects.

These concepts will be taught and tested in the Kotlin foundations chapters. Getting this wrong identifies a useful practice topic. See the official explanation of [data class copying](https://kotlinlang.org/docs/data-classes.html#copying).

## Use the weekly routine

1. Start by explaining one earlier concept without notes.
2. Read a short lesson and predict the example's behavior.
3. Run it, change a requirement, and observe the result.
4. Complete the exercise before opening the solution.
5. Write or run checks for normal behavior and edge cases.
6. Explain the solution aloud as if answering an interviewer.
7. Record one mistake to revisit next week.

At this pace, spend about 30 minutes on recall, 60–90 minutes on guided learning, 90–120 minutes building, and 60–120 minutes on testing and interview practice. Deeper chapters can span multiple weeks.

## Completion checklist

- [ ] I recorded my current confidence for the main topics.
- [ ] I identified one interview gap and an exercise to address it.
- [ ] I can explain shared logic and shared UI in my own words.
- [ ] I attempted the prediction before reading the explanation.
- [ ] I chose study sessions totaling 4–6 hours for the coming week.

Next: [Chapter 01 Values and types](../01-values-and-types/README.md), with a runnable local Kotlin exercise and a separate worked solution.
