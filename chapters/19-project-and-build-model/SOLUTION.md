# Chapter 19 Worked solution

Compare the [tracked starter](../../mobile/shared/src/starter/androidMain/kotlin/StudyLabel.kt) and [reference label](../../mobile/shared/src/solution/commonMain/kotlin/StudyLabel.kt) after the [workshop](WORKSHOP.md). The important difference is in their paths.

## Why moving one file works

The starter's `studyLabel` already trims the topic, supplies `Study` for a blank value, validates nonnegative minutes, and formats the label. The practice build initially puts that declaration in `androidMain`. The Android compilation includes it, but a test compiled for `iosSimulatorArm64` cannot reach it. An unresolved reference in the test is consistent with correct Kotlin inside the label file.

Moving the practice file to `shared/src/practice/commonMain/kotlin/StudyLabel.kt` attaches it to `commonMain` via the source roots in the module script. Both Apple targets and Android now compile the declaration. Keeping its package `workshop.studylab.shared` preserves the name callers use. This is a source-location repair, so changing the assertions or making a platform copy of the function would conceal the cause.

## Read the build from the outside in

1. The mobile wrapper selects Gradle. The settings file includes `:shared`, which makes tasks in that directory addressable by project path.
2. `libs.plugins.kotlin.multiplatform` resolves through the catalog to `org.jetbrains.kotlin.multiplatform`. `libs.plugins.android.kmp.library` resolves to `com.android.kotlin.multiplatform.library`. Those plugin entries share the Kotlin and Android Gradle Plugin versions used by this build.
3. `androidLibrary`, `iosArm64()`, and `iosSimulatorArm64` declare the targets. The pinned Android plugin's `withHostTestBuilder` enables local Android unit tests. The default Kotlin hierarchy gives the two iOS targets shared access to `iosMain` and the common ancestors.
4. The `kotlin("test")` dependency in `commonTest` supplies portable test annotations and assertions. Each selected target compiles and runs those three checks using its own test runner.
5. The simulator target's `binaries.framework` block names a static framework `StudyShared`. It is possible to compile a target or link an artifact without executing its tests. Checking both the artifact and the report makes each result explicit.

## Run the independent reference

From `mobile`, use the Gradle project property `-PsharedReference` to select the tracked reference sources and their own build directory.

```sh
./gradlew -PsharedReference :shared:testAndroidHostTest :shared:bundleAndroidMainAar
./gradlew -PsharedReference :shared:iosSimulatorArm64Test --device "$study_simulator" :shared:linkDebugFrameworkIosSimulatorArm64
```

Expect three Android host tests and three iOS simulator tests, plus an Android library archive and a debug framework for `iosSimulatorArm64`. Use the simulator UDID selected in the workshop for the second command. Under `shared/build/reference/`, look in `reports/tests/` for the reports and `outputs/aar/shared.aar` for the Android library. The simulator bundle at `bin/iosSimulatorArm64/debugFramework/StudyShared.framework` has `iPhoneSimulator` in the supported-platform list in its `Info.plist`. The Android result comes from running the tests on the host JVM. The simulator result comes from a Kotlin/Native test executable. [Testing multiplatform code](https://kotlinlang.org/docs/multiplatform/multiplatform-run-tests.html)

## Changed request

If the portable label lived at `shared/src/iosMain/kotlin/StudyLabel.kt`, the two iOS targets could use it and Android would lose it. Place an iOS-only formatter in `iosMain` when its implementation uses Foundation APIs available on both iOS targets. Keep the portable study label in `commonMain`. A device build and a simulator build can share source while producing distinct binaries for their respective platforms.

Chapter 20 will explore the dependency boundaries around shared code. [Course roadmap](../../docs/CURRICULUM.md)

[Back to the workshop](WORKSHOP.md) · [Back to the course](../../README.md)
