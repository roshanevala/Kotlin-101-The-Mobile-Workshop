# Chapter 18 Worked solution

After trying the [workshop](WORKSHOP.md), compare the [reference loader](../../mobile/iosApp/Solution/ReviewLoader.swift) and [worked regression test](../../mobile/iosApp/Solution/ReviewRegressionTests.swift) with your two practice files.

## Why the older call loses its turn

A task already cancelled on entry returns before it can replace an active load. The loader associates a fresh identity with each new active load. It remembers that identity locally for the duration of the call and records which identity is current on the main actor. Two requests for Swift therefore have distinct identities even though they share their topic and source.

When a source resumes its caller, the loader checks ownership before publishing a question, a failure, or a cancellation. Cancel invalidates the active identity and makes the screen idle immediately. A cancelled task whose request has already been replaced leaves the replacement alone. When cancellation belongs to the active request, the loader can return to idle. A normal error for the active request permits Retry.

SwiftUI owns the task through the view's request ID. The source actor owns the demo's first Swift failure. Once the first Swift attempt has failed, a retry calls the source again and returns a question. Injecting the source keeps this demo policy out of the loader and lets a unit test choose its own outcomes.

## What the regression catches

The worked test uses a fresh controlled source and a fresh loader. It starts two loads for Swift, with both waiting at the source. It fails the **first** call and waits for the loader to return. The second call is still waiting, so the expected state is `.loading("Swift")`. Next it finishes the second call successfully and checks that the new question appears.

A loader checking only the topic would accept that stale error. A loader checking only successful replies would also allow the error through. The test chooses the same topic twice, exercises the throwing path, and observes the state both while the replacement is waiting and after it completes. No timing guess is needed.

## Run the reference

Set `study_simulator` as described in the [iOS setup guide](../../mobile/iosApp/README.md). From `mobile/iosApp`, run the reference checks and open the reference app.

```sh
python3 ios.py test-unit --reference --device "$study_simulator"
python3 ios.py test --reference --device "$study_simulator"
python3 ios.py run --reference --device "$study_simulator"
```

The first command runs eight supplied Swift Testing cases and the worked regression. The second also drives the app through 14 XCTest UI checks covering the review prompt and Chapter 17's existing flows. In the running app, open **Review prompts**, choose Swift, then retry after the demo failure. The successful prompt asks how a Swift actor protects its state.

## Checkpoint discussion

Assigning a Swift struct with only string fields gives independent value behavior. Assigning a Kotlin data-class instance to another variable gives two references to the same instance. Kotlin's generated `copy()` makes a shallow copy, so a referenced mutable child may still be shared. A Swift struct can also contain a class reference whose object remains shared after the struct is copied.

On Kotlin/JVM, tracing garbage collection can reclaim an unreachable cycle. In Swift, a class storing a closure that strongly captures that same class can keep both alive under ARC. A weak capture permits the owner to go away while another caller can retain the closure. Cancelling an async task and breaking a strong reference cycle address different lifetime questions.

For the changed event order, suppose B succeeds before A fails. The screen should show B's question after the success and keep it when A finally returns. An assertion after both calls finish catches a loader that replaced B's result with A's error.

Next, build a Kotlin module for Android and an iOS simulator. [Chapter 19, Project and build model](../19-project-and-build-model/README.md)

[Back to the workshop](WORKSHOP.md) · [Back to the course](../../README.md)
