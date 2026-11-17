# Study Lab iOS setup

This is the native SwiftUI client introduced in [Chapter 17](../../chapters/17-swiftui-and-app-lifecycle/README.md). It belongs beside the Android client in the growing mobile project. It does not depend on either Gradle workspace or your Chapter 16 answer.

## Check Xcode and a simulator

The locally checked setup is **Xcode 27.0 (27A266a)**, **Apple Swift 6.4**, **Swift 6 language mode**, **iOS SDK 27.0**, and **Python 3.9+** on macOS/Apple silicon. The app's deployment target is **iOS 17.0**, which provides SwiftUI Observation support. The checked runtime is **iOS 26.5**; minimum OS, SDK, and simulator runtime are different selections. Older runtimes and physical devices are not established by that observation.

```sh
xcodebuild -version
xcrun swift --version
xcrun simctl list runtimes
xcrun simctl list devices available
```

Finish Xcode's first-launch setup and install a supported iOS runtime through its components/settings if needed. No package download, XcodeGen, CocoaPods, signing account, or physical phone is required for the simulator commands. The project and shared schemes are checked in.

From `mobile/iosApp`, prepare your file before opening the project:

```sh
python3 ios.py prepare
open StudyLab.xcodeproj
```

Choose the **StudyLabPractice** scheme for your work, or **StudyLabReference** for the independent solution. Edit **`Practice/StudyScreens.swift`**, not Starter/Solution or shared Sources. The two schemes build different source files and install under separate IDs: `workshop.studylab.ios.practice` and `.reference`. Shared UI tests are attached to the corresponding app, so they use the selected source choice.

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

The starter builds but has deliberate state/navigation/editor/lifecycle defects. The **13 UI tests** intentionally fail some contracts until repaired. They use XCTest/XCUIAutomation as fixed acceptance scaffolding; test-authoring and concurrency depth belong to Chapter 18. A failing Swift build is different from an executed UI assertion failure.

For the reference, keep the same simulator selection:

```sh
python3 ios.py test --reference --device "$study_simulator"
python3 ios.py run --reference --device "$study_simulator"
```

Run installs only the selected variant and starts a fresh process, terminating that variant first if it is running. For the background/foreground experiment, use the simulator's Home action, then return through the app icon or app switcher; do not repeat `ios.py run`, which resets this in-memory data.

Result bundles are `build/practice/results.xcresult` or `build/reference/results.xcresult`; open a result in Xcode to inspect executed tests and failures. Each new test command replaces that variant's old result bundle. App/runner products and caches also remain under ignored `build`.

To compile app/UI-test bundles without running a simulator:

```sh
python3 ios.py build-tests
python3 ios.py build-tests --reference
```

For focused diagnosis, `test` accepts a repeatable selector such as `--only-test StudyLabReferenceUITests/StudyLabUITests/testSecondRowOpensItsOwnIdentity`. Use the PracticeUITests target name for a practice selector. Run the full suite again when the repair affects other behavior.

## Preserve your work and its lifetime

Preparation fills only a missing Practice file. `python3 ios.py clean` removes only this project's build output, preserving your answer and every earlier exercise. To reset, move your Practice file somewhere safe outside its source folder, then prepare again. Reference commands never prepare or compile it. Normal Git staging ignores Practice and build output.

The current data is in memory: drafts, topics, completion, filters, and navigation survive the stated view/background interactions within the process. Terminating and relaunching the app starts fresh. No database, file storage, SceneStorage, or process-restoration feature is included. Scene logs use the **StudyScene** prefix and contain no entered topics.

The iOS workflow is configured to compile both apps/UI-test bundles and run the shared model checks; it does not run simulator UI tests. Hosted execution, interactive Xcode/preview actions, physical devices, and broader accessibility/device coverage must be distinguished from the [local verification](../../docs/maintainers/verification/ch17.md).
