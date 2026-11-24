# Chapter 21 Worked solution

The starter substitutes 25 whenever Swift cannot parse the minutes. Kotlin receives a perfectly valid request, allocates an ID, and returns a plan. The source of the unexpected session is the Swift caller. The starter also fills the displayed title from the original draft, so a successful Kotlin cleanup can disappear between the planner and the screen. [Starter adapter](../../mobile/iosApp/Starter/SharedPlanAdapter.swift)

## Follow one submission

The [reference adapter](../../mobile/iosApp/Solution/SharedPlanAdapter.swift) trims the minutes text and attempts to construct an `Int32`. It reports `.invalidInput` if conversion fails. Once the adapter has a representable value, it calls `plan(topic:minutes:)`. A missing result maps to `.rejectedDuration`. An accepted result supplies every field in `.planned`, including the opaque ID.

| Submission | Shared behavior | Swift outcome |
| --- | --- | --- |
| Topic `"Kotlin"`, minutes `"ten"` | No call reaches Kotlin | Invalid input |
| Topic `"Kotlin"`, minutes `"0"` | Returns `nil` and issues zero IDs | Rejected duration |
| Missing topic, minutes `"  25  "` | Supplies `"Untitled"` and one ID | Displays the accepted title, 25 minutes, and the returned ID |
| Topic `"  Kotlin  flow "`, minutes `"120"` | Preserves the inner spaces and assigns one ID | Displays `"Kotlin  flow"` for 120 minutes |

A Swift test can supply a predictable source of IDs to the same exported planner. It can then check the exact returned ID and how many times the source was asked. The running app supplies the platform implementation through Kotlin's exported factory. [Swift adapter tests](../../mobile/iosApp/Tests/SharedPlanAdapterTests.swift) · [Shared rules](../../mobile/shared/src/solution/commonMain/kotlin/SessionPlanner.kt)

## Follow a suspended receipt

The generated header gives `awaitReceiptWithCompletionHandler:` a callback with an optional string and an optional error. Swift imports an asynchronous call, `try await request.awaitReceipt()`. Kotlin's one-shot confirmation remembers a decision even if the learner taps before Swift reaches the wait. Confirmation returns the original session ID. Explicit cancellation cancels the pending Kotlin confirmation. The view model also installs a Swift task cancellation handler that forwards cancellation to the request, and checks the task again before displaying its result. You can follow the two lifetimes in the [Kotlin request](../../mobile/shared/src/commonMain/kotlin/PlanReceiptRequest.kt) and [Swift receipt section](../../mobile/iosApp/Sources/PlanReceiptSection.swift).

The blank-ID test constructs a receipt with a space and awaits it. Kotlin's declared error makes its way to Swift's `catch`, and the model reports failure. The planner's ordinary rejection goes through optional binding. [Receipt tests](../../mobile/iosApp/Tests/PlanReceiptTests.swift)

## Run the independent Swift reference

From `mobile/iosApp`, use the workshop simulator's UDID and select the reference test targets. Each command exercises the compiled Kotlin library through its Swift caller.

```sh
python3 ios.py test --reference --device "$study_simulator" --only-test StudyLabReferenceTests/SharedPlanAdapterTests --only-test StudyLabReferenceTests/PlanReceiptTests
python3 ios.py test --reference --device "$study_simulator" --only-test StudyLabReferenceUITests/StudyLabUITests/testSharedPlanRejectsDurationThenDisplaysKotlinSession
```

## Revisit the changed request

Padding can be removed before parsing `"  25  "`, so the planner receives 25 and allocates one ID. The digits in `"2147483648"` exceed `Int32.max`. Conversion fails before the planner receives a request. If Kotlin later accepts 180 minutes, a Swift caller that already trusts the optional Kotlin result can accept that session without changing its own validation logic. Review any displayed range text alongside such a rule change.

Continue with [Chapter 22 · Shared presentation decisions](../22-shared-presentation-decisions/README.md) to decide who should keep an unfinished draft when this sheet closes.

[Back to the workshop](WORKSHOP.md) · [Back to the course](../../README.md)
