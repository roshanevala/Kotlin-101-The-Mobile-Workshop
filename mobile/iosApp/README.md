# Study Lab iOS setup

This is the native SwiftUI client introduced in [Chapter 17](../../chapters/17-swiftui-and-app-lifecycle/README.md). It gains an asynchronous review screen in [Chapter 18](../../chapters/18-swift-concurrency-and-tests/README.md). [Chapter 21](../../chapters/21-swift-integration/README.md) adds a form that calls the Kotlin session planner. [Chapter 22](../../chapters/22-shared-presentation-decisions/README.md) compares Swift and Kotlin implementations of the draft in that form. For the earlier Swift-only project, extract the original [Chapter 18 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch18-v1) in a fresh folder.

## Check Xcode and a simulator

The locally checked setup is **Xcode 27.0 (27A266a)**, **Apple Swift 6.4**, **Swift 6 language mode**, **iOS SDK 27.0**, and **Python 3.9+** on macOS/Apple silicon. The app's deployment target is **iOS 17.0**, which provides SwiftUI Observation support. The checked runtime is **iOS 26.5**; minimum OS, SDK, and simulator runtime are different selections. Older runtimes and physical devices are not established by that observation.

```sh
xcodebuild -version
xcrun swift --version
xcrun simctl list runtimes
xcrun simctl list devices available
```

Finish Xcode's first-launch setup and install a supported iOS runtime through its components/settings if needed. Set up JDK 17 for the mobile Gradle wrapper. Allow the first build to download Gradle, Kotlin/Native, and the shared library's dependencies. Xcode runs the wrapper from the checked-in project. Both app schemes build the same Kotlin reference framework for their selected SDK, so you can work on the Swift form with earlier Kotlin exercises unfinished. See [shared build setup](../../chapters/19-project-and-build-model/WORKSHOP.md).

From `mobile/iosApp`, prepare your files before opening the project:

```sh
python3 ios.py prepare
open StudyLab.xcodeproj
```

Choose the **StudyLabPractice** scheme for your work, or **StudyLabReference** for the independent solution. Chapter 17 uses **`Practice/StudyScreens.swift`**. In Chapter 18 edit **`Practice/ReviewLoader.swift`** and **`Practice/ReviewRegressionTests.swift`**. In Chapter 21, edit **`Practice/SharedPlanAdapter.swift`**. In Chapter 22, edit **`Practice/PlannerDraftOwner.swift`**. The two schemes build different source files and install under separate IDs: `workshop.studylab.ios.practice` and `.reference`. Each scheme pairs its app with a Swift Testing unit bundle and an XCTest UI bundle.

If an older Chapter 17 answer reports that StudyRoot's initializer is private, preserve your edits and copy only `init(showTip:)` from Starter into your StudyRoot. Preparation intentionally leaves existing answers unchanged.

## Build, run, and check

```sh
python3 ios.py build
python3 ios.py build --reference
python3 ios.py check-model
```

Build uses a generic iOS Simulator destination and disables signing for that command. `check-model` compiles the fixed session/store sources into a Mac executable and runs 12 checks without a simulator. It does not prepare/compile the learner views; those UI repairs have separate checks.

Use an explicitly selected simulator for run/test. For the exact runtime/device combination checked locally, the following creates a new workshop device and stores its returned UDID:

```sh
study_simulator=$(xcrun simctl create "Study Lab workshop" com.apple.CoreSimulator.SimDeviceType.iPhone-17-Pro com.apple.CoreSimulator.SimRuntime.iOS-26-5)
xcrun simctl boot "$study_simulator"
xcrun simctl bootstatus "$study_simulator" -b
python3 ios.py run --device "$study_simulator"
python3 ios.py test --device "$study_simulator"
```

If using another existing simulator, set `study_simulator` to that intended UDID instead of creating another device. Start it before `run`; Xcode's test action can boot the selected destination. Do not erase or reset a personal simulator to follow this exercise.

For Chapter 22, open **Plan a session**, enter a topic and `025` under **Native**, then close and reopen the sheet. **Shared** gives you a separate draft in the same form. Run the ownership tests while working on the fix.

```sh
python3 ios.py test --device "$study_simulator" --only-test StudyLabPracticeTests/PlannerDraftTests
```

For Chapter 21, open **Plan a session**. Try `ten`, `0`, and `25` as minutes. A valid plan has a topic, duration, and ID from Kotlin. Confirm it to see a receipt with that ID. Run the adapter tests independently of earlier Swift exercises.

```sh
python3 ios.py test --device "$study_simulator" --only-test StudyLabPracticeTests/SharedPlanAdapterTests
```

For Chapter 18, open **Review prompts** in the running app. Tap **Swift** to see the first load fail, then **Retry** to get the prompt. Run the loader unit tests while repairing cancellation and late replies. The starter includes a failing marker for the regression you will write.

```sh
python3 ios.py test-unit --device "$study_simulator"
python3 ios.py test-unit --reference --device "$study_simulator"
```

The full `test` action runs both unit and UI tests, including the Chapter 17 list and editor checks. For the original Chapter 17 starter and its 13 UI tests, use the [Chapter 17 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch17-v2).

For the reference, keep the same simulator selection:

```sh
python3 ios.py test --reference --device "$study_simulator"
python3 ios.py run --reference --device "$study_simulator"
```

Run installs only the selected variant and starts a fresh process, terminating that variant first if it is running. For the background/foreground experiment, use the simulator's Home action, then return through the app icon or app switcher; do not repeat `ios.py run`, which resets this in-memory data.

Result bundles are `build/practice/results.xcresult` or `build/reference/results.xcresult`; open a result in Xcode to inspect executed tests and failures. The unit-only command writes `unit.xcresult` alongside it. Repeating an action replaces its previous result. App/runner products and caches also remain under ignored `build`.

To compile the app and both test bundles without running a simulator:

```sh
python3 ios.py build-tests
python3 ios.py build-tests --reference
```

For focused diagnosis, `test` accepts a repeatable selector such as `--only-test StudyLabReferenceUITests/StudyLabUITests/testSecondRowOpensItsOwnIdentity`. Use the PracticeUITests target name for a practice selector. Run the full suite again when the repair affects other behavior.

## Preserve your work and its lifetime

Preparation fills each missing Practice file independently. `python3 ios.py clean` removes only this project's build output, preserving your answer and every earlier exercise. To reset, move your Practice file somewhere safe outside its source folder, then prepare again. Reference commands never prepare or compile it. Normal Git staging ignores Practice and build output.

The current data is in memory: drafts, topics, completion, filters, and navigation survive the stated view/background interactions within the process. Terminating and relaunching the app starts fresh. No database, file storage, SceneStorage, or process-restoration feature is included. Scene logs use the **StudyScene** prefix and contain no entered topics.

The iOS workflow builds both apps and their unit and UI test bundles, and runs the standalone model checks. See [Chapter 22 verification](../../docs/maintainers/verification/ch22.md) for the draft ownership and simulator results.
