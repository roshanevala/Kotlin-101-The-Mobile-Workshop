# Chapter 20 Worked solution

The repaired planner decides whether a session exists before asking what its ID should be. Trace the three workshop requests with a freshly constructed `RecordingIds`.

| Attempt | Return value | ID source calls so far |
| --- | --- | --- |
| 0 minutes, padded topic | `null` | 0 |
| 25 minutes, `"  Kotlin  flows  "` | `PlannedSession("fixture-1", "Kotlin  flows", 25)` | 1 |
| 120 minutes, `null` topic | `PlannedSession("fixture-2", "Untitled", 120)` | 2 |

The starter spends `fixture-1` on the zero-minute session. Its next valid session receives `fixture-2`, and the topic still contains padding. Returning `null` for an invalid duration fixes only half the bug if an earlier line has already called `nextId()`. The rejecting test inspects both the return value and the source's call count. [Starter](../../mobile/shared/src/starter/commonMain/kotlin/SessionPlanner.kt) · [Common tests](../../mobile/shared/src/commonTest/kotlin/SessionPlannerTest.kt)

In the worked planner, the range guard returns immediately for an invalid duration. On the accepting path, `topic?.trim()` yields either trimmed text or `null`. Calling `orEmpty()` turns `null` into an empty string, and `ifEmpty` supplies `"Untitled"` for that case and for whitespace that trimmed to empty. The final constructor call requests one ID and keeps both its exact string and the validated minutes. [Reference planner](../../mobile/shared/src/solution/commonMain/kotlin/SessionPlanner.kt)

## Follow a real ID through both targets

The shared contract exposes a string. On Android, `platformSessionIds()` supplies an object whose `nextId()` calls `UUID.randomUUID().toString()`. On iOS, the corresponding object calls Foundation through Kotlin/Native's `NSUUID().UUIDString()`. The compiler can match both factories to the common declaration because each uses its package and signature. The planner calls its constructor-supplied interface, so a test can count requests without invoking either platform generator. [Common contract](../../mobile/shared/src/commonMain/kotlin/PlannedSession.kt) · [Android factory](../../mobile/shared/src/androidMain/kotlin/PlatformSessionIds.android.kt) · [iOS factory](../../mobile/shared/src/iosMain/kotlin/PlatformSessionIds.ios.kt)

The fifth test deliberately uses the real factory. It checks the shape of one generated UUID on the target running it. The other tests can assert exact values such as `fixture-1`, making order and count visible. An assertion about the uniqueness of all future IDs would require evidence beyond a sample of generated strings.

## Run the independent reference

From `mobile`, the reference property selects the tracked solution for both chapters in the shared module. Use the simulator UDID selected in the workshop for the Apple command.

```sh
./gradlew -PsharedReference :shared:testAndroidHostTest --tests 'workshop.studylab.shared.SessionPlannerTest'
./gradlew -PsharedReference :shared:iosSimulatorArm64Test --device "$study_simulator" --tests 'workshop.studylab.shared.SessionPlannerTest'
```

Expect five planner tests on each target. To include the three Chapter 19 label tests too, run each command again without `--tests` and its class name. Reference reports are under `shared/build/reference/reports/tests/`.

## Revisit the importer

The importing caller can pass its own `SessionIdSource` to `SessionPlanner`. Give the source one ID, `"import-007"`, and submit a 121-minute row. Check for `null` and zero calls. Submit a valid row next and check that its ID is exactly `"import-007"` and the count is one. This last call catches a planner that discarded or rewrote an ID supplied by the caller.

Continue to [Chapter 21, Swift integration](../21-swift-integration/README.md), to call the planner from a SwiftUI form.

[Back to the workshop](WORKSHOP.md) · [Back to the course](../../README.md)
