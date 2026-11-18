# Lesson 18 Swift concurrency and tests

Imagine two requests for a Swift question. The first has been waiting a long time. You try again, and the second request returns a useful prompt. A moment later the first one fails. Should Study Lab replace your prompt with an error?

Work through that question before starting the [workshop](WORKSHOP.md). The screen builds on the view ownership and scene lifetimes from [Chapter 17](../17-swiftui-and-app-lifecycle/LESSON.md).

## Read one asynchronous call

The screen asks a source for a prompt using this Swift protocol.

```swift
protocol ReviewSource: Sendable {
    func prompt(for topic: String) async throws -> String
}
```

A source accepts a topic and returns a string. `async` means the call can suspend. `throws` means it can report an error. The call site spells out both possibilities.

```swift
let question = try await source.prompt(for: "Swift")
```

In this fragment, `source` is a value that implements the protocol. Read it as “ask for the Swift prompt, then continue here with the answer.” If the source has to wait, the task can suspend and let its thread run other work. On success, execution continues with `question`. An error skips the assignment and goes to a matching `catch` or propagates to the caller. `await` marks a *possible* suspension point. Two successive calls to `try await` in one task still run in sequence. [The Swift Programming Language, Concurrency](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/concurrency/)

## Give the work an owner

A task is one unit of asynchronous work. Calling an async function directly continues the current task. Creating tasks changes who can wait for the work and who can cancel it.

| Swift construct | Who owns completion? |
| --- | --- |
| `async let` or a task group | The enclosing scope waits for its child tasks before it exits. Cancelling a parent task also signals its children. This is structured concurrency. |
| `Task { … }` | This creates an unstructured task. Its creator can keep the handle, call `cancel()`, and await `value` to know when it has finished. |
| SwiftUI `.task(id:)` | SwiftUI starts work for the view and restarts it when the ID changes. The previous task receives cancellation. SwiftUI can also cancel work when the view disappears. |

Here the user can request another prompt for **the same topic**. If the topic string were the task ID, a second Swift tap would still compare equal to the first. The screen pairs each choice with a fresh identifier. Each task captures the request it was created for and awaits the loader directly. That keeps the source call inside the lifetime SwiftUI manages. [SwiftUI task with an ID](https://developer.apple.com/documentation/swiftui/view/task(id:name:priority:file:line:_:))

## Follow the data across actors

An actor protects its isolated mutable state by allowing one task at a time to execute actor-isolated code. Study Lab's local source is an actor. It keeps track of whether to simulate the first Swift failure. The loader lives on `@MainActor`, the shared actor used here to serialize updates to the screen's observable state. Its four cases tell the view what to draw.

| State | What it means |
| --- | --- |
| `.idle` | The user can choose a prompt. |
| `.loading(topic)` | The screen has a current request for that topic. |
| `.ready(question)` | The current request returned a prompt. |
| `.failed` | The current request failed and the screen can offer Retry. |

`any ReviewSource` lets the same loader call the demo source in the app and a controlled source in tests. The protocol requires `Sendable`, a promise Swift checks about passing values between concurrency domains. The topic and question are strings, which have value semantics and are sendable. A reference to the source actor can also be shared safely because access to its isolated state goes through that actor. [Swift actors and sendable types](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/concurrency/)

Now trace two calls on the *same* loader. At each row, predict which request should still be allowed to change the visible state.

| Event on the loader | State that belongs on screen |
| --- | --- |
| Request A starts for Swift, then suspends inside the source. | Loading Swift |
| Request B starts for Swift while A is suspended. | Loading Swift |
| B returns a question. | B's question |
| A resumes and throws an error. | B's question |

The main actor executed each individual state change in turn. While A was suspended, the actor was available to run B. This is **actor reentrancy**. A local value such as A's request identifier still has its original value when A resumes. Shared state such as the loader's current request may have changed. Checking the topic would give the same answer for both calls. An identifier per request lets the loader decide who may publish after the `await`.

## Separate a cancelled task from a failed request

Cancellation is cooperative. A task marked cancelled can still resume, return a value, or throw an ordinary error. `Task.isCancelled` reads the current task's flag. `try Task.checkCancellation()` throws `CancellationError` when that flag is set. The delay in the demo source responds to cancellation. The test source can deliberately deliver a result after cancellation. [Swift Task cancellation](https://developer.apple.com/documentation/swift/task)

Two decisions follow. First, a saved request ID tells a resumed call whether the loader still belongs to that request. A superseded call should leave the newer state alone, even if it has an error to report. Second, a current call whose task was cancelled should return to idle. Explicitly tapping Cancel can make the screen idle immediately, while its source call is still winding down. A later result from that call has lost its permission to publish.

## Test the order of events

Swift Testing discovers functions marked `@Test`. A test can be `async throws`, which lets it await work and report errors. `#expect` records a failed expectation with the values involved. Try reading this example from the point of view of a learner who sees a failure. The shared test helper can pause a request at the source, then deliver an ordinary error on demand. These names are available inside the supplied unit-test target.

```swift
@Test @MainActor
func currentRequestCanFail() async throws {
    let source = ControlledReviewSource()
    let loader = ReviewLoader(source: source)
    let waiting = try await source.start("Swift", on: loader)
    #expect(loader.state == .loading("Swift"))
    await waiting.fail()
    #expect(loader.state == .failed)
}
```

The first expectation runs after the request reaches the source and before it finishes. The second runs after the loader handles the failure. For two requests, the test can arrange A starts, B starts, B returns, A fails, in that exact order. Underneath the helper, each load has a separate `Task` handle. Finishing a pending request releases the suspended source and awaits the task's `value` before the test reads the state. If the test simply awaited A inline, it could never reach the step that releases A. Each test creates its own source and loader. [Swift Testing](https://developer.apple.com/documentation/testing)

An XCTest UI test asks a different question. It starts the app, taps the buttons a learner would tap, and reads the visible prompt or retry control. The unit test controls the source's completion order precisely. The UI test establishes that the real SwiftUI controls reach the loader and display its answer. Keep timing guesses out of both. A UI wait can look for a visible result within a bound, while a model test can arrange a specific suspension and release. [Apple's testing framework guidance](https://developer.apple.com/documentation/testing/migratingfromxctest)

**[Start the workshop](WORKSHOP.md)**
