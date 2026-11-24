# Workshop 22 Bring back the right draft

A learner wants to close **Plan a session** and finish typing later. Give the native and shared versions the same behavior, then decide which you would ship. [Read the lesson](LESSON.md)

## 1. Predict what comes back

For the app comparison use an Apple silicon Mac, full Xcode, an iOS 17+ simulator, and the mobile Gradle setup with JDK 17. Follow the iOS guide to boot a workshop simulator and set `study_simulator` to its UDID in your shell. The simulator walkthrough was checked with Xcode 27.0 and iOS 26.5. Start in **`mobile/iosApp`**. If you're using the Android toolchain alone, start with the [Kotlin exercise](#4-try-the-kotlin-holder-on-its-own). [iOS setup](../../mobile/iosApp/README.md) · [Mobile setup](../../mobile/README.md)

```sh
python3 ios.py prepare
python3 ios.py run --device "$study_simulator"
```

Open **Plan a session**, choose **Native**, and type `Loops` and `025`. Tap **Create plan**. The preview should read **Loops · 25 min**. Before tapping **Close**, predict what will happen to the two text fields, the preview, and its confirmation controls. Reopen the sheet. Repeat this experiment with **Shared**. Write down what each design actually recovered.

The starter accepts and displays the plan, then loses the draft on reopening. If the app can't import `StudyShared`, check the framework build and simulator setup first. Both Swift schemes use the reference Kotlin framework. You can leave earlier Swift exercises at their starters while running this chapter’s focused tests.

## 2. Find who created the second draft

From the same directory run the Swift tests.

```sh
python3 ios.py test --device "$study_simulator" --only-test StudyLabPracticeTests/PlannerDraftTests
```

Two of six tests fail on the starter. `reopeningFindsEarlierEdits` asks the same owner for a draft twice. `factorySuppliesOneHolderPerDesign` counts creations through an injected factory. Read the passing test that injects a recording backing. What result would it get if Swift updated the display directly from the typed text and skipped reading the backing? [Swift draft tests](../../mobile/iosApp/Tests/PlannerDraftTests.swift)

Find where `StudyShell` creates its owner and where the form asks for a draft. Your editable file is **`Practice/PlannerDraftOwner.swift`**, created by `prepare`. Repair its `draft(for:)` method and any storage it needs. A second request for the same design must recover the same edits. The two designs must each keep their own fields, and constructing a second owner must give you a fresh start. Use the supplied factory so the tests can observe creation. [StudyShell](../../mobile/iosApp/Sources/StudyLabApp.swift) · [Form](../../mobile/iosApp/Sources/SharedPlanScreen.swift) · [Progressive hints](HINTS.md)

## 3. Take both drafts through the sheet

Run the six Swift tests again. Then exercise the actual sheet with both picker settings.

```sh
python3 ios.py test --device "$study_simulator" --only-test StudyLabPracticeUITests/StudyLabUITests/testNativeDraftSurvivesClosingPlanner --only-test StudyLabPracticeUITests/StudyLabUITests/testSharedDraftSurvivesClosingPlanner
```

Try the following by hand after `python3 ios.py run --device "$study_simulator"`. Use **Native** to plan `Loops` for `025` minutes again. Switch to **Shared** and enter `Maps` for `15` minutes. Switch back. You should recover `Loops` and the raw `025`, with the old preview gone. Tap **Clear draft** and return to **Shared**. Can you still finish the Maps plan? Close and reopen the sheet, then compare that result with starting a new process using the same run command.

**Interview prompt.** A teammate proposes a shared ViewModel to fix the disappearing drafts. Which observation identifies the lifetime problem? Which test would still be worth keeping if the team shipped only the native implementation?

## 4. Try the Kotlin holder on its own

With JDK 17 and the Android SDK configured, start in **`mobile`**. This exercise runs on the host JVM. You can complete it without an emulator or Xcode. [Mobile setup](../../mobile/README.md)

```sh
./gradlew -PsharedReference :shared:testAndroidHostTest --tests 'workshop.studylab.shared.PlanDraftHolderTest'
```

The three common tests should pass. Open the test that saves an earlier snapshot, edits the minutes, and clears the holder. Before reading its assertions, predict the minutes in the saved snapshot, the current holder, and a brand-new holder. Explain which object a host would need to keep between two visits. This checks the shared design on Android's JVM target. The Swift tests and simulator exercise compare the two designs in a real UI. [Common draft tests](../../mobile/shared/src/commonTest/kotlin/PlanDraftHolderTest.kt)

## Part 5 checkpoint

**Brief.** Study Lab has one iOS planner today. The Android team may add an editor next term. Both platforms must accept the same submitted sessions. Either editor should keep unfinished typing while its planner screen is temporarily closed. Decide what to share now. Use your own evidence for the following deliverables.

1. From **`mobile`**, run the same business-rule and draft tests on both targets. Pick one accepted and one rejected submission from `SessionPlannerTest`. Record the inputs, results, and which part supplied the ID. Identify the target for each run. With only the Android toolchain, record the Apple run as outstanding. [Business-rule tests](../../mobile/shared/src/commonTest/kotlin/SessionPlannerTest.kt)

   ```sh
   ./gradlew -PsharedReference :shared:testAndroidHostTest --tests 'workshop.studylab.shared.SessionPlannerTest' --tests 'workshop.studylab.shared.PlanDraftHolderTest'
   ./gradlew -PsharedReference :shared:iosSimulatorArm64Test --device "$study_simulator" --tests 'workshop.studylab.shared.SessionPlannerTest' --tests 'workshop.studylab.shared.PlanDraftHolderTest'
   ```

2. On the Mac, inspect the API this project's Kotlin compiler actually exports. From **`mobile`**, build the framework and search its generated header. Record the imported Swift spelling for one draft edit and the nullability of the planner's return value. Point to one call that uses each detail in the Swift app. [Chapter 21 header exercise](../21-swift-integration/WORKSHOP.md)

   ```sh
   ./gradlew -PsharedReference :shared:linkDebugFrameworkIosSimulatorArm64
   header=shared/build/reference/bin/iosSimulatorArm64/debugFramework/StudyShared.framework/Headers/StudyShared.h
   grep -nE 'PlanDraftHolder|editMinutes|planTopic:' "$header"
   ```

3. Write a short decision in your learning notes. Choose native or shared draft edits for the brief. Cite a result from the two designs, name the lifetime of the owner, and give one responsibility each host must still handle. Explain one cost you accept and a specific change that would make you reconsider. If the requirement changes to recovering drafts after the app is killed, which part of your design would need new evidence?

[Worked solution](SOLUTION.md) · [Back to the course](../../README.md)
