# Study Lab Android setup

The native SwiftUI client has its own [iOS setup](iosApp/README.md). Chapter 19 introduces the shared library in this Gradle build. For its prerequisites, build commands and source-set exercise, start with [Chapter 19](../chapters/19-project-and-build-model/README.md). The commands below concern the Android app.

This is the growing Android application from [Chapter 13](../chapters/13-android-runtime-and-lifecycle/README.md), extended with [Compose](../chapters/14-jetpack-compose/README.md) and [architecture in Chapter 15](../chapters/15-android-app-architecture/README.md). Keep `kotlin-labs` for independent Kotlin exercises; Android builds do not compile those answers.

## Install and open

Open **`mobile`** in Android Studio. In **Settings → Build, Execution, Deployment → Build Tools → Gradle**, select **JDK 17**. The wrapper pins **Gradle 8.14.3**, with **AGP 8.11.1** and **Kotlin 2.2.21**. Compose compiler matches Kotlin; Compose BOM is **2025.10.01** and Activity Compose is **1.11.0**. Chapter 15 adds Lifecycle **2.9.4**, Navigation Compose **2.9.5**, and coroutines-android/test **1.11.0**. Sync/download dependencies after updating; these are verified compatible pins, not latest-version claims.

In **SDK Manager**, install **Android SDK Platform 36**, **Build Tools 35.0.0**, **Platform-Tools**, and **Android Emulator**. Create/start an **API 30+** virtual device in **Device Manager**. API 36 is the checked runtime. Use an ARM64 image on Apple silicon or an image suitable for your host. Initial downloads require internet access and disk space.

Android Studio normally writes the SDK path to ignored `local.properties`. A terminal setup can use `sdk.dir` there or a correct `ANDROID_HOME`. Check that any existing environment variable points to the intended SDK. Do not copy another machine's path.

Minimum SDK **30** selects the oldest supported runtime; compile SDK **36** selects available symbols; target SDK **36** opts into platform behavior. Build Tools is **35.0.0**. No NDK, Xcode, network service, or physical phone is required for the mission.

## Prepare and run your answer

From the IDE terminal in `mobile`:

```sh
./gradlew --console=plain :androidApp:assemblePracticeDebug
./gradlew --console=plain :androidApp:installPracticeDebug
```

Use `./gradlew.bat` on Windows. Select **practiceDebug** in Build Variants and run the app, or open **Study Lab practice** after installation. The current launcher is the fixed `CatalogueActivity`. Its deterministic fixture fails once, then **Try again** loads Kotlin/Android. Follow [Workshop 15](../chapters/15-android-app-architecture/WORKSHOP.md).

The new ignored answer is:

```text
androidApp/src/practice/kotlin/workshop/studylab/CatalogueViewModel.kt
```

Edit that ViewModel, leaving the tracked template and fixed factory/repository/screens/tests unchanged. Build prepares it only if missing. Your earlier `MainActivity.kt` and `StudyPlannerActivity.kt` answers stay in the same ignored folder; preparation and `clean` preserve all three.

A build variant combines the practice/reference source choice with a build type, such as debug. The growing practice variant compiles all three answers, so an older edited file must still compile. Repair that error or use a fresh snapshot folder without overwriting original work. Use immutable [ch13-v1](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch13-v1) or [ch14-v2](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch14-v2) for those lessons' original launchers.

Shared `src/main` contains the fixed catalogue Activity, repository, state/model, navigation, resources, and window scaffolding. Each flavor supplies its own two older Activities and current ViewModel. **referenceDebug** compiles `src/solution`, installs separately as **Study Lab reference**, and never compiles the practice answers. Select practiceDebug again to return to your work.

## Check behavior

Local Android unit tests need the configured SDK/JDK but no running device:

```sh
./gradlew --console=plain :androidApp:testPracticeDebugUnitTest
```

The **20 local tests** use JUnit 4 and coroutine virtual time to check the new ViewModel/repository. Reports: `androidApp/build/reports/tests/testPracticeDebugUnitTest/`. Some deliberately fail on the starter.

With an API 30+ emulator/device running, focus on this chapter:

```sh
./gradlew --console=plain :androidApp:connectedPracticeDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=workshop.studylab.CatalogueNavigationTest,workshop.studylab.CatalogueScreenTest
```

There are **14 new device checks** and **46 in the full workspace**: those 14 plus the earlier 22 Compose and ten lifecycle checks. Use `:androidApp:connectedPracticeDebugAndroidTest` unfiltered to run all. Earlier unsolved answers retain their expected failures. Device reports are under `androidApp/build/reports/androidTests/connected/`; these tasks are separate from unit tests. Set `ANDROID_SERIAL` to the intended identifier from `adb devices` when several devices are attached.

For the independent reference:

```sh
./gradlew --console=plain :androidApp:assembleReferenceDebug
./gradlew --console=plain :androidApp:testReferenceDebugUnitTest
./gradlew --console=plain :androidApp:connectedReferenceDebugAndroidTest
./gradlew --console=plain :androidApp:lintReferenceDebug
./gradlew --console=plain :androidApp:installReferenceDebug
```

Install after connected tests for manual inspection; tooling may remove app/test packages afterward. Reference checks do not complete a learner attempt. Lint may report newer versions; the course keeps a verified compatible set.

## Trace and recover

The catalogue's first source failure is a deterministic fixture, not a real network outage. After retry, navigate by ID and edit a **draft planning note**. Back/reopen and rotation retain the Activity-owned model. Eligible process restoration reconstructs notes/navigation and reloads data; the new fixture fails once again. A note is not durable catalogue data. See [Lesson 15](../chapters/15-android-app-architecture/LESSON.md) for these lifetimes.

For Chapter 13's separate lifecycle logs, filter Logcat on **StudyLifecycle** in its original checkpoint. Activity identity and process ID establish different observations; force-stop is not a substitute for eligible system restoration.

To reset one mission, move its answer somewhere safe outside the source folder, then use `:androidApp:prepareArchitecturePractice` for Chapter 15 or `:androidApp:prepareComposePractice` for Chapter 14. `:androidApp:preparePractice` fills all missing answers, including Chapter 13. Existing files remain intact; `clean` is not a reset command.

If the SDK is missing, check `local.properties` and `ANDROID_HOME`. If a device is unavailable, start it and wait for boot. Duplicate classes usually mean an answer was copied into shared `src/main`; keep a single answer in `src/practice` and never force-add answers or SDK paths.

The source starter/reference are linked from [Workshop 15](../chapters/15-android-app-architecture/WORKSHOP.md) and its [solution](../chapters/15-android-app-architecture/SOLUTION.md). The immutable [ch15-v1 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch15-v1) and [Chapter 15 ZIP](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/archive/refs/tags/ch15-v1.zip) contain both variants. Earlier immutable downloads are [Chapter 14 ZIP](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/archive/refs/tags/ch14-v2.zip) and [Chapter 13 ZIP](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/archive/refs/tags/ch13-v1.zip). Extract into a new folder and open `mobile`, preserving original answers. Installing another checkpoint changes that variant of the same app.
