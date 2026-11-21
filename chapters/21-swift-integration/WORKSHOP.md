# Workshop 21 The session nobody entered

Repair the Swift adapter so the learner sees exactly what the Kotlin planner accepted. An invalid duration typed into the form must never turn into a valid session by accident. [Read the lesson](LESSON.md)

## 1. See the bug in the simulator

Use an Apple silicon Mac with full Xcode, an iOS 17+ simulator, and the mobile Gradle toolchain with JDK 17. The first framework build may download Kotlin/Native and its dependencies. Follow the iOS setup guide to select and boot a dedicated simulator. Set `study_simulator` to its UDID in the terminal where you'll run the commands. Start in **`mobile/iosApp`**. [iOS setup](../../mobile/iosApp/README.md) · [Gradle and Kotlin/Native setup](../19-project-and-build-model/WORKSHOP.md)

```sh
python3 ios.py prepare
python3 ios.py run --device "$study_simulator"
```

Open **Plan a session**. Leave the topic blank, enter `ten` for minutes, and choose **Create plan**. Write down what happens. Try `0`, then `25`. Predict whether the shared planner should be called for each input and which one should produce an ID.

Both Swift schemes use the Kotlin reference framework. Your editable file for this mission is **`Practice/SharedPlanAdapter.swift`**. Preparation fills missing Swift files and preserves earlier answers. To repeat an earlier chapter with its original standalone Xcode project, use the download on the [Chapter 18 entry page](../18-swift-concurrency-and-tests/README.md) in a separate folder.

## 2. Find the calls Swift can make

From the same `mobile/iosApp` directory, build the Kotlin framework on its own and inspect its generated header. You can open the header in Xcode or search it from your terminal.

```sh
../gradlew -p .. -PsharedReference :shared:linkDebugFrameworkIosSimulatorArm64
header=../shared/build/reference/bin/iosSimulatorArm64/debugFramework/StudyShared.framework/Headers/StudyShared.h
grep -nE 'planTopic:|initWithIds:|platformSessionIds' "$header"
```

Find both `_Nullable` markers on the planner method. Which one describes an input? Which one describes the result? Find the integer type and the Swift method labels. Compare your answers with the call in the [starter adapter](../../mobile/iosApp/Starter/SharedPlanAdapter.swift). If Xcode reports that it cannot import `StudyShared`, check the Gradle task and simulator selection before debugging the planner's behavior.

## 3. Repair the adapter

Run only this chapter's adapter suite so you can work with an unfinished earlier Swift exercise. Earlier Swift source files still need to compile.

```sh
python3 ios.py test --device "$study_simulator" --only-test StudyLabPracticeTests/SharedPlanAdapterTests
```

Of the five adapter tests, the starter passes three. It already handles the two endpoints, a rejected duration, and an accepted plan with a real platform ID. Look for failures on malformed or overflowing minutes and on the topic that Swift displays. Read the assertions in the [tests](../../mobile/iosApp/Tests/SharedPlanAdapterTests.swift). For each input, decide which part of the program has enough information to make the next decision. Implement the following behavior in your practice copy of `SharedPlanAdapter.submit(topic:minutesText:)`.

- Trim surrounding whitespace from the minutes field. If the remaining text cannot represent a signed 32-bit integer, return `.invalidInput` before calling Kotlin.
- For representable minutes, give the shared planner the supplied topic and parsed value. A missing plan means `.rejectedDuration`.
- When a plan comes back, fill `.planned` with the ID, topic, and minutes from that object. A blank input topic should display Kotlin's `Untitled`. Spaces inside an accepted topic should survive.

Run the same focused suite again. Then try the UI check, which opens the sheet, submits a rejected duration and corrects it.

```sh
python3 ios.py test --device "$study_simulator" --only-test StudyLabPracticeUITests/StudyLabUITests/testSharedPlanRejectsDurationThenDisplaysKotlinSession
```

## 4. Follow a receipt until it finishes

Relaunch your repaired app with `python3 ios.py run --device "$study_simulator"`. Create a valid plan and find its receipt controls. Tap **Confirm plan** and compare the confirmed ID with the session ID above it. Create another valid plan and tap **Cancel confirmation**. Which action allowed the Kotlin wait to finish each time?

Search the same header for `awaitReceipt` and read the completion handler. Find its result and error parameters. Then trace the [Swift caller](../../mobile/iosApp/Sources/PlanReceiptSection.swift) back to the line that tells the Kotlin request to stop if its Swift task is cancelled. The supplied receipt tests include a blank-ID failure and cancellation. Run them independently of your adapter.

```sh
python3 ios.py test --device "$study_simulator" --only-test StudyLabPracticeTests/PlanReceiptTests
```

**Interview prompt.** A teammate wraps the planner call in `do/catch` to handle 121 minutes. What will the compiler's imported signature tell them? How does the receipt's error path differ?

**Changed request.** A learner pastes `"  25  "`, then `"2147483648"`. For each input, predict the screen result and whether the ID source should be called. If a later version extends the Kotlin upper limit to 180, point to the Swift decision that will still work.

[Progressive hints](HINTS.md) · [Worked solution](SOLUTION.md)
