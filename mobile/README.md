# Study Lab Android setup

This is the growing Android application, introduced in [Chapter 13](../chapters/13-android-runtime-and-lifecycle/README.md). Keep `kotlin-labs` for the independent Kotlin exercises; Android builds do not compile those answers.

## Install and open

Use Android Studio with Android SDK support. Open **`mobile`**, not the repository root or `kotlin-labs`. In **Settings → Build, Execution, Deployment → Build Tools → Gradle**, select **JDK 17**. The wrapper pins **Gradle 8.14.3**; the project pins **AGP 8.11.1** and **Kotlin 2.2.21**.

In **SDK Manager**, install **Android SDK Platform 36**, **Build Tools 35.0.0**, **Platform-Tools**, and **Android Emulator**. In **Device Manager**, create and start an Android **API 30 or newer** virtual device; API 36 is the locally checked runtime. Use an ARM64 image on Apple silicon or an image suitable for your host. Initial downloads require internet access and disk space.

Android Studio normally writes your SDK path to ignored `local.properties`. A terminal-only setup can use `sdk.dir` there or a correct `ANDROID_HOME`. Do not copy another machine's SDK path. An old or different `ANDROID_HOME` can point to the wrong installation even when the IDE knows the right SDK.

The app's **minimum SDK is 30**, its **compile and target SDK are 36**, and **Build Tools is 35.0.0**. Minimum is the oldest runtime supported; compile selects available SDK symbols; target opts into platform behavior. They need not be the same number. No NDK, Xcode, or physical phone is required.

## Build and run your Activity

From the IDE terminal in `mobile`:

```sh
./gradlew --console=plain :androidApp:assemblePracticeDebug
./gradlew --console=plain :androidApp:installPracticeDebug
```

On Windows use `./gradlew.bat`. Build creates your ignored answer, if absent:

```text
androidApp/src/practice/kotlin/workshop/studylab/MainActivity.kt
```

In **Build Variants**, choose **practiceDebug** for `androidApp`. Run the app configuration on the started emulator, or open **Study Lab practice** from its launcher after installation. Edit the generated Activity, not the tracked template. Existing work is preserved; `clean` does not reset it.

The untouched screen starts at topic **Kotlin**, **0 sessions**. Change the topic, complete a few sessions, then press **Recreate screen**. Both values reset: that is the Chapter 13 bug, not a setup error.

A build variant combines a source choice (practice or reference) with a build type (debug here). **practiceDebug** means your Activity in a debug build; the build setup already supplies that choice.

The fixed manifest/resources/window helper live in `src/main`. Shared main contains no Activity class. The **referenceDebug** variant compiles `src/solution`, installs separately as **Study Lab reference**, and never compiles your practice Activity. Select practiceDebug again when returning to your answer.

## Check behavior

With one emulator/device connected:

```sh
./gradlew --console=plain :androidApp:connectedPracticeDebugAndroidTest
```

The **10 device tests** recreate activities and check observable values. Some intentionally fail on the starter. Reports appear under `androidApp/build/reports/androidTests/connected/`. They require Android; they are not the Kotlin labs' JVM tests. If several devices are connected, set `ANDROID_SERIAL` to the intended device's identifier from `adb devices` before running.

For the isolated reference:

```sh
./gradlew --console=plain :androidApp:assembleReferenceDebug
./gradlew --console=plain :androidApp:lintReferenceDebug
./gradlew --console=plain :androidApp:connectedReferenceDebugAndroidTest
./gradlew --console=plain :androidApp:installReferenceDebug
```

Install explicitly after connected tests when you want to inspect the app; the test tooling may remove installed test/app packages at completion. The reference has the same small UI and correct saved-state behavior. Lint can report that newer versions exist; the course deliberately pins a verified compatible set.

## Trace and recover

In Logcat, filter on **StudyLifecycle**. A line contains the process ID, Activity instance identity, and callback name. No topic text is logged. Changing Activity identity demonstrates recreation; a changed process ID is separate evidence of process restart.

To recreate a fresh starter, first move your Activity answer somewhere safe outside its source folder. Run `:androidApp:preparePractice`. It fills only a missing file. Do not delete the whole workspace or use `clean` as an answer reset.

If Gradle cannot find the SDK, check the IDE SDK path and `local.properties`/`ANDROID_HOME`. If no device is available, start an API 30+ emulator and wait for boot. If Kotlin reports duplicate Activity classes, make sure you have not copied an answer into shared `src/main`; use the single generated `src/practice` file. Never force-add answers or commit local SDK paths.

The [workshop](../chapters/13-android-runtime-and-lifecycle/WORKSHOP.md) provides the full prediction, repair, and process-restoration steps. Checkpoint source is included as tracked starter and reference; publication tags are recorded only after they exist.
