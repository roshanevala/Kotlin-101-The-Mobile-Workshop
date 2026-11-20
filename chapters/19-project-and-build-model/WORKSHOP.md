# Workshop 19 The label that only Android can find

A common test asks for the label `"Kotlin · 25 min"`. The Kotlin file is in the repository, its package name matches the test, and the Android check can find the function. Why would the iOS simulator compiler report an unresolved reference?

Your mission is to repair **which targets receive the file**. The label already works. Read the [lesson](LESSON.md) first. Run all Gradle commands from **`mobile`**. Use JDK 17, Android SDK 36, and the project's wrapper. It pins Gradle 8.14.3. The catalog pins Kotlin 2.2.21 and Android Gradle Plugin 8.11.1. [Mobile setup](../../mobile/README.md)

For the iOS steps, use an Apple silicon Mac with full Xcode and an installed iOS Simulator runtime. JetBrains lists **Xcode 26.0** in the compatibility matrix for Kotlin 2.2.21. This workshop was also exercised locally with **Xcode 27.0** and an **iOS 26.5** simulator, outside that listed Xcode version. Allow Gradle to download the Kotlin/Native toolchain on the first Apple build. [Kotlin compatibility guide](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html)

You can reuse the dedicated workshop simulator from Chapter 18, including one listed as Shutdown. The Kotlin/Native test runner can start the simulator test directly from that state. List the available devices, then replace the placeholder with your simulator's UDID in the same terminal.

```sh
xcrun simctl list devices available
study_simulator='REPLACE_WITH_YOUR_SIMULATOR_UDID'
```

Need to create a workshop device? Follow the simulator selection example in the [iOS setup guide](../../mobile/iosApp/README.md). On an Android-only machine, complete the project trace, source move, Android host tests, and AAR build. Use the Mac steps to verify the simulator separately.

## 1. Trace the build and predict the failure

```sh
./gradlew projects
./gradlew :shared:preparePractice
./gradlew :shared:tasks --all
```

Locate `:shared` in the project list. In its task list, find one task that runs Android host tests, one that runs simulator tests, and one that links a simulator framework. Read the two plugin aliases at the top of the [shared build script](../../mobile/shared/build.gradle.kts) and resolve each one in the [version catalog](../../mobile/gradle/libs.versions.toml). Which pin belongs to the Kotlin compiler plugin? Which belongs to the Android library plugin?

Preparation puts a fresh answer in `shared/src/practice/androidMain/kotlin/StudyLabel.kt`. If you have already moved your answer, preparation preserves it. Inspect the `kotlin.srcDir` configuration. From the path alone, predict which targets can compile a test that calls `studyLabel`.

Read the [three common tests](../../mobile/shared/src/commonTest/kotlin/StudyLabelTest.kt). Trimming `"  Kotlin "` produces `"Kotlin · 25 min"`, a blank topic and zero minutes produce `"Study · 0 min"`, and a negative duration throws `IllegalArgumentException`. Record those predictions before you run either target.

## 2. Compare the targets, then move one file

Run the label tests on your computer and then on the configured Mac. The filter selects the three cases for this chapter as more exercises join the module.

```sh
./gradlew :shared:testAndroidHostTest --tests 'workshop.studylab.shared.StudyLabelTest'
./gradlew :shared:iosSimulatorArm64Test --device "$study_simulator" --tests 'workshop.studylab.shared.StudyLabelTest'
```

With a fresh starter, Android can read the label. On the simulator path, find the compiler diagnostic in `StudyLabelTest.kt` that says `studyLabel` is unresolved. This happens while compiling the tests, before any assertions execute. A missing SDK, Gradle distribution, Kotlin/Native download, or simulator runtime is a separate setup problem.

Under `shared/src/practice/`, move **the existing** `StudyLabel.kt` from `androidMain/kotlin/` to `commonMain/kotlin/`. Create the destination directories if needed. You can move the file in the IDE. Keep its package declaration and its implementation. Before moving an answer, check whether the destination already contains your work. Two declarations with the same package, name, and parameters would collide in the Android compilation.

Run the two test commands again. For the repaired build, inspect the three cases in each HTML test report. The reports are under `shared/build/practice/reports/tests/`, in `testAndroidHostTest/index.html` and `iosSimulatorArm64Test/index.html`. Tell a partner, or write down, why just moving the file changed the simulator result even though the function body stayed the same.

## 3. Build the outputs and explain what they contain

Return to the two output-producing tasks you identified in Step 1. Compare their names with the commands below, then run them.

```sh
./gradlew :shared:bundleAndroidMainAar
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

Look under `shared/build/practice` for `outputs/aar/shared.aar` and `bin/iosSimulatorArm64/debugFramework/StudyShared.framework`. Which task produces an Android library a future app dependency can consume? Which produces a framework for an Apple silicon simulator? Open the framework's `Info.plist` and find the supported platform. Decide whether that output would be suitable for a physical phone. Building the framework and running the simulator test answer two different questions about this source code.

**Changed request.** A teammate suggests moving the label to `iosMain` because two targets use iOS. Imagine the declaration under the conventional `shared/src/iosMain/kotlin/` root. Trace its visibility for the simulator, the iOS device, and Android. Then decide where an eventual iOS-only formatter using Foundation ought to live. Revisit your answer with the source-set table in the [lesson](LESSON.md).

**Interview prompt.** In a minute, explain the path from `./gradlew` to the three tested Kotlin behaviors. Include the wrapper distribution, the `:shared` project, one plugin alias, one source-set relationship, and the artifact or test report you inspected.

[Progressive hints](HINTS.md) · [Worked solution](SOLUTION.md)
