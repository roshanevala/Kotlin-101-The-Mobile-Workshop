# Lesson 21 Where did those 25 minutes come from?

Study Lab has a Kotlin function that accepts an integer duration and either returns a plan or rejects it. A SwiftUI text field produces a string, and the learner can type anything into it. Write down what should happen for `"ten"`, `"0"`, and `"25"`. Which can you turn into an integer? Which can you turn into a session?

## Trace the code Swift calls

The `mobile/shared` Gradle project builds a static framework named `StudyShared.framework` for the arm64 iOS simulator. It contains a compiled library and an Objective-C header describing its exported declarations. Both Xcode apps depend on a target named `BuildStudyShared`. For the simulator, that target asks Gradle for the arm64 simulator build of the Kotlin reference. Each app links the resulting library. A Swift source file can then write `import StudyShared` and use the declarations through Swift's Objective-C importer. The schemes compile their own Swift adapters against that same library. [Shared module build](../../mobile/shared/build.gradle.kts)

Here is a call using the Swift names in this framework. The explicit type on the second line tells you what to do before reading any properties.

```swift
let planner = SessionPlanner(ids: PlatformSessionIds_iosKt.platformSessionIds())
let example: PlannedSession? = planner.plan(topic: nil, minutes: 10)
```

`platformSessionIds` is a top-level declaration in Kotlin. In this build its iOS implementation is exported on a generated class named `PlatformSessionIds_iosKt`. The class is a detail of how this framework exposes that file. To find a name on a different build, inspect its header or jump to its generated interface in Xcode. [iOS implementation](../../mobile/shared/src/iosMain/kotlin/PlatformSessionIds.ios.kt) · [Kotlin's Objective-C and Swift interop](https://kotlinlang.org/docs/native-objc-interop.html)

| Evidence in `StudyShared.h` | What it means at this call site |
| --- | --- |
| `swift_name("plan(topic:minutes:)")` | Write the Swift argument labels `topic:` and `minutes:`. |
| `NSString * _Nullable` for the topic | A Swift `String?` can carry text or `nil` into the planner. |
| `int32_t` for minutes | A nonnullable Kotlin `Int` arrives here as a Swift `Int32`. |
| `StudySharedPlannedSession * _Nullable` for the return | The Swift result is optional. Unwrap it to access `id`, `topic`, and `minutes`. |

In the example, a missing topic is a valid *input*. Kotlin supplies `"Untitled"` and returns a plan. A missing plan is a different event. Try changing the duration to zero and predict which optional changes. [Chapter 20 planner](../../mobile/shared/src/solution/commonMain/kotlin/SessionPlanner.kt)

## Read two different failures

Swift can try the failable initializer `Int32(text)` before calling Kotlin. It handles malformed text and numbers outside the 32-bit range without trapping. For example, `Int32("2147483648")` returns `nil`. An ordinary Swift `Int` on this simulator can hold that number, so parsing an `Int` and then forcing it into an `Int32` creates a different failure mode. [Swift Int32](https://developer.apple.com/documentation/swift/int32)

| Minutes field | What Swift can pass | What the screen should learn |
| --- | --- | --- |
| `"ten"` or `"2147483648"` | No representable `Int32` | Ask for whole minutes. The shared planner has received no request. |
| `"0"` | `Int32(0)` | The planner returns `nil`. Explain the supported duration. |
| `"25"` | `Int32(25)` | The planner returns a session. Display the values it actually accepted. |

Kotlin already owns the 1–120 range, title cleanup, and ID allocation. The Swift adapter owns the form conversion and maps the returned object to a native value the view can display. If Kotlin turns a blank title into `"Untitled"`, reading the original text field when building that value loses the result of a successful call. The same applies to generating a second ID on the Swift side.

## Read the error and completion contracts

The planner's rejection travels as an optional result. Other Kotlin APIs can expose recoverable exceptions. A Kotlin function annotated with `@Throws` can export the listed exception types and their subclasses through an Objective-C error parameter. Swift surfaces those errors through `throws` and handles them with `try` and `catch`. An unexpected Kotlin exception that escapes the exported contract can terminate the process. Inspect the declaration when deciding which failure path to handle. [Kotlin exception translation](https://kotlinlang.org/docs/native-objc-interop.html#errors-and-exceptions)

A Kotlin `suspend` call appears in the Objective-C header with a completion handler. That handler receives the result or an error when the operation finishes. Swift can also import an `async throws` spelling. Suspension allows the caller to wait while other work proceeds. [Kotlin suspending functions](https://kotlinlang.org/docs/native-objc-interop.html#suspending-functions)

The receipt underneath an accepted plan gives that wait a visible ending. A `PlanReceiptRequest` remembers the accepted ID. Its `awaitReceipt()` suspends until someone confirms or cancels. Confirmation releases the wait and returns the original ID. An empty or whitespace-only ID would be a different error. The Kotlin function explicitly lists `IllegalArgumentException` and `CancellationException` in `@Throws`, so Swift can handle both. [Kotlin receipt request](../../mobile/shared/src/commonMain/kotlin/PlanReceiptRequest.kt)

The Swift task waiting for the receipt and the Kotlin object holding the pending request have separate lifetimes. The screen must arrange to call the Kotlin object's `cancel()` when Swift cancels its task. This gives the suspended call a way to finish. The caller must still decide whether the result belongs on screen, especially if the learner has already left it. [Chapter 18 request lifetimes](../18-swift-concurrency-and-tests/LESSON.md)

**Before the workshop**, trace a request with a blank topic and 25 minutes. Point to the place that should choose the title, the place that should generate the ID, and the place that should decide what the learner sees.

**[Start the workshop](WORKSHOP.md)**
