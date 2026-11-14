# Study Lab Android setup

This is the growing Android application, introduced in [Chapter 13](../chapters/13-android-runtime-and-lifecycle/README.md) and extended with [Compose in Chapter 14](../chapters/14-jetpack-compose/README.md). Keep `kotlin-labs` for the independent Kotlin exercises; Android builds do not compile those answers.

## Install and open

Use Android Studio with Android SDK support. Open **`mobile`**, not the repository root or `kotlin-labs`. In **Settings → Build, Execution, Deployment → Build Tools → Gradle**, select **JDK 17**. The wrapper pins **Gradle 8.14.3**; the project pins **AGP 8.11.1** and **Kotlin 2.2.21**. Compose uses compiler plugin **2.2.21**, BOM **2025.10.01**, and Activity Compose **1.11.0**. Sync/download those dependencies after updating.

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
androidApp/src/practice/kotlin/workshop/studylab/StudyPlannerActivity.kt
```

In **Build Variants**, choose **practiceDebug** for `androidApp`. Run the app configuration on the started emulator, or open **Study Lab practice** from its launcher after installation. Edit the generated Activity, not the tracked template. Existing work is preserved; `clean` does not reset it.

The current launcher opens Chapter 14's two-session Compose list/editor: Kotlin, Android, and **Completed 0 of 2**. The untouched editor has deliberately split state; saving/selection/completion/restoration need repairs. Follow [Workshop 14](../chapters/14-jetpack-compose/WORKSHOP.md).

Your Chapter 13 `MainActivity.kt` answer remains in the same ignored folder, and preparation preserves it. To follow that lesson's original launcher and commands, use its immutable [ch13-v1 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch13-v1) in a new folder. The current module compiles both Activities, so earlier edited code still needs to compile; do not overwrite it to switch chapters.

A build variant combines a source choice (practice or reference) with a build type (debug here). **practiceDebug** means your Activity in a debug build; the build setup already supplies that choice.

The fixed manifest/resources/window helper live in `src/main`. Shared main contains neither variant Activity class. Its Compose screen/theme is shared between variants; each flavor supplies its own Activity/owner/editor. The **referenceDebug** variant compiles `src/solution`, installs separately as **Study Lab reference**, and never compiles your practice Activity. Select practiceDebug again when returning to your answer.

## Check behavior

With one emulator/device connected:

```sh
./gradlew --console=plain :androidApp:connectedPracticeDebugAndroidTest
```

The **32 device tests** contain 22 Compose checks and 10 retained Chapter 13 Activity checks. Some intentionally fail on the starter. Reports appear under `androidApp/build/reports/androidTests/connected/`. They require Android; they are not the Kotlin labs' JVM tests. If several devices are connected, set `ANDROID_SERIAL` to the intended device's identifier from `adb devices` before running.

For the isolated reference:

```sh
./gradlew --console=plain :androidApp:assembleReferenceDebug
./gradlew --console=plain :androidApp:lintReferenceDebug
./gradlew --console=plain :androidApp:connectedReferenceDebugAndroidTest
./gradlew --console=plain :androidApp:installReferenceDebug
```

Install explicitly after connected tests when you want to inspect the app; the test tooling may remove installed test/app packages at completion. The reference has the same Compose UI with correct event/owner behavior, plus the preserved reference Activity for Chapter 13 checks. Lint can report that newer versions exist; the course deliberately pins a verified compatible set.

## Trace and recover

In Logcat, filter on **StudyLifecycle**. The old View Activity's log line contains the process ID, Activity instance identity, and callback name. No topic text is logged. Changing Activity identity demonstrates recreation; a changed process ID is separate evidence of process restart.

To recreate a fresh starter, first move your Activity answer somewhere safe outside its source folder. Run `:androidApp:preparePractice`. It fills only missing Activities. To prepare only the Chapter 14 file use `:androidApp:prepareComposePractice`; both commands preserve existing work. Do not delete the whole workspace or use `clean` as an answer reset.

If Gradle cannot find the SDK, check the IDE SDK path and `local.properties`/`ANDROID_HOME`. If no device is available, start an API 30+ emulator and wait for boot. If Kotlin reports duplicate Activity classes, make sure you have not copied an answer into shared `src/main`; use the single generated `src/practice` file. Never force-add answers or commit local SDK paths.

The [Compose workshop](../chapters/14-jetpack-compose/WORKSHOP.md) provides current prediction, repair, and verification steps. The [Chapter 13 workshop](../chapters/13-android-runtime-and-lifecycle/WORKSHOP.md) explains its separate lifecycle/process-restoration checks. The immutable [ch13-v1 source checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch13-v1) and [ZIP download](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/archive/refs/tags/ch13-v1.zip) include both variants. Extract the ZIP into a new folder and open `mobile`; keep any existing answer safe in its original workspace. The installed variant follows whichever checkpoint you build/install; the checkpoints are versions of this same app.
