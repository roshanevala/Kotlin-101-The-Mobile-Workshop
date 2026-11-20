# Lesson 20 Who gets to spend an ID?

Study Lab accepts sessions from 1 to 120 minutes. Picture two Save attempts on the same form. The first says 0 minutes. The second says 25. A source of IDs has two values ready, `ticket-A` and `ticket-B`. Which value should go on the 25-minute session? What could you observe if the first attempt quietly used one up?

In Chapter 19 you moved a function into a source set both platforms could see. Now you have to choose **what crosses the boundary** between shared code and platform code. The planner makes a decision about study time. An ID source supplies a value when the planner asks for one. [Chapter 19](../19-project-and-build-model/README.md)

## Give both callers the same rule

Chapter 3 already has tests for a missing topic, blank text, surrounding spaces, and the two ends of the duration range. Here the planner receives a nullable topic and minutes already parsed as an `Int`. An accepted plan carries three values a future client can use. A rejected request returns `null`. Rendering an error message or reading a text field belongs with the caller. [Chapter 3 input rules](../03-null-safety-and-errors/WORKSHOP.md)

| Input | What the planner owes its caller |
| --- | --- |
| `"  Kotlin  flows  "` and 25 | A session titled `"Kotlin  flows"` for 25 minutes, with the internal spaces preserved |
| A missing or whitespace-only topic and 1 | A session titled `"Untitled"` for 1 minute |
| Any topic and 120 | An accepted session at the upper limit |
| Any topic and 0 or 121 | `null`, with the ID source untouched |

A public function signature is a promise both clients depend on. Keeping the input and result in common Kotlin types lets each target compile that promise. In this module, `PlannedSession` holds an ID string, topic string, and integer duration. The planner treats the ID as opaque text. Even if one source returns uppercase characters and another returns lowercase characters, each caller gets exactly the value its source supplied.

## Make a dependency you can observe

`SessionIdSource` is a one-method interface. Calling `nextId()` asks its implementation for the next string. The planner takes a `SessionIdSource` in its constructor, so the code that creates a planner decides which source to supply. This is constructor injection. The planner depends on the small common interface. Platform implementations also depend on that interface to learn which method they need to implement.

Here's a source you could use at a desk with pencil and paper. The real planner can accept this object because it implements the same interface as a platform source.

```kotlin
class TicketIds : SessionIdSource {
    var issued = 0
        private set

    override fun nextId(): String {
        issued += 1
        return "ticket-$issued"
    }
}
```

Starting from `val ids = TicketIds()`, the first call to `ids.nextId()` returns `"ticket-1"`. The second returns `"ticket-2"`. A test can give `ids` to `SessionPlanner(ids)`, try an invalid request, and then inspect `ids.issued`. Even when two implementations both return `null`, that count reveals whether either one spent an ID. A small working replacement like this is called a **fake**.

## Choose an implementation for each target

When a caller wants a real ID, it can construct `SessionPlanner(platformSessionIds())`. A *factory* is a function that supplies an object. Read the declaration for this one in the [common contract](../../mobile/shared/src/commonMain/kotlin/PlannedSession.kt).

```kotlin
expect fun platformSessionIds(): SessionIdSource
```

An `expect` function declares the name, parameters, and return type that common source can use. It has no body. Each target needs a matching `actual` declaration in the same package. During compilation for that target, Kotlin pairs the declarations. The code below the factory can then use APIs that exist on that platform. [Expected and actual declarations](https://kotlinlang.org/docs/multiplatform/multiplatform-expect-actual.html)

| Source set | What supplies an ID here? |
| --- | --- |
| `androidMain` | The Android implementation calls Java's UUID API and returns its string. [Android implementation](../../mobile/shared/src/androidMain/kotlin/PlatformSessionIds.android.kt) |
| `iosMain` | The Apple implementation uses Foundation's `NSUUID`. Both the device and simulator targets inherit this source set. [iOS implementation](../../mobile/shared/src/iosMain/kotlin/PlatformSessionIds.ios.kt) |
| `commonTest` | A test can pass a predictable source straight to the planner's constructor. [Planner tests](../../mobile/shared/src/commonTest/kotlin/SessionPlannerTest.kt) |

The compiler chooses the factory implementation when building the target. The caller chooses which object to pass when constructing a planner. That second choice makes it possible to run the same predictable test on two platforms with different native UUID APIs.

If both clients can use a common ID generator, a single implementation of the interface can live in `commonMain`. For example, Kotlin offers `kotlin.uuid.Uuid.random()` with an experimental opt-in in the workshop's pinned Kotlin 2.2.21. This exercise uses native factories so you can trace a real call across each platform boundary. The interface also lets an app that already owns its ID source pass that source directly. [Common UUID support](https://kotlinlang.org/docs/whatsnew2020.html#support-for-uuids-in-the-common-kotlin-standard-library)

**Before the workshop**, draw two paths. Start one at the Android factory and the other at the test's fake. Where do they first use the same code, and which object owns the decision to issue an ID?

**[Start the workshop](WORKSHOP.md)**
