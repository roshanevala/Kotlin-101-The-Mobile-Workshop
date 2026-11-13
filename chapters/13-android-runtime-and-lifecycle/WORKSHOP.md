# Workshop 13 Your draft disappeared

Bug report: “I typed ‘Coroutines’ and completed two sessions. After the screen was recreated, the app showed ‘Kotlin’ and zero again.” Preserve this small draft without holding an obsolete Activity or introducing permanent storage.

Read the [lesson](LESSON.md) first. Work in three sessions if needed: setup/trace, save/restore, then independent lifecycle checks.

## Open the new workspace

Follow [mobile setup](../../mobile/README.md). Open **`mobile`** in Android Studio, select **JDK 17**, and sync. Install SDK Platform **36**, Build Tools **35.0.0**, and an emulator image suitable for your host. Start an API **30+** emulator; API **36** is the checked runtime. This workspace pins AGP **8.11.1**, Kotlin **2.2.21**, and Gradle **8.14.3**.

From the `mobile` terminal:

```sh
./gradlew --console=plain :androidApp:assemblePracticeDebug
./gradlew --console=plain :androidApp:installPracticeDebug
```

Use `./gradlew.bat` on Windows. Select **practiceDebug** in Build Variants, then run `androidApp` or open **Study Lab practice** on the emulator. Build creates your editable, ignored Activity:

```text
androidApp/src/practice/kotlin/workshop/studylab/MainActivity.kt
```

Edit that file. Existing answers are preserved. Leave the tracked [starter](../../mobile/androidApp/src/starter/kotlin/workshop/studylab/MainActivity.kt), shared resources/helper, and acceptance tests unchanged. `clean` preserves your answer. The [reference source](SOLUTION.md) is separate; it is easy to avoid until review.

The first screen has topic **Kotlin** and **0 sessions**. Change the topic, press **Complete one session** twice, and press **Recreate screen**. The starter resets both values. A screen that runs and loses this state has the intended exercise defect.

## Predict and trace

Before editing, write down which things you expect to change after recreation: topic text, completed count, Activity identity, process ID, and the installed app. In Logcat filter **StudyLifecycle**. Compare a launch, a Home/return, and the Recreate button.

The button calls `recreate()`; Android normally replaces the Activity in the same process. Rotation can also cause replacement. The app does not declare that it handles configuration changes itself. Keep lifecycle logs and `super` calls intact while repairing state.

The topic EditText and count label deliberately disable automatic widget saving. You are practicing explicit Bundle restoration for the small model, so automatic EditText restoration cannot hide a missing implementation. The 80-character topic bound keeps the saved value small.

## Agree on the contracts

- A genuinely fresh Activity launch with no saved draft starts at topic **Kotlin**, count **0**. Reset sets those same defaults.
- Save the topic's current text, including edits made without clicking another button. Preserve its spaces, Unicode, and explicitly empty value. There is no trimming, validation, or database history to add.
- Save the backing completed integer. On creation, restore it before rendering. Later completion clicks must continue from the restored number, not from a hidden zero.
- Use the provided stable `TOPIC_KEY` and `COMPLETED_KEY` constants and matching Bundle types. Choose defaults only when the Bundle/key has no value; empty topic text is a valid saved value.
- Preserve both values through repeated Activity recreation. Recreation itself must not complete a session. Saving must reflect the current values after Reset as well as after ordinary editing.
- Call `super` in lifecycle callbacks. Keep saved state small and synchronous; do not wait for a coroutine or save a View, Context, or Activity object.
- Finishing/dismissing a task and opening a fresh plan starts at defaults in this lab. Home/return can resume the existing task. Saved state can also restore an eligible retained task after its process is killed; it is not permanent storage or guaranteed recovery after every dismissal.

There is no Compose, ViewModel, service, runtime permission request, network, database, or singleton to implement. The shared window helper handles system bars, cutouts, and keyboard insets; changing it is outside the draft repair.

## Mission 1 Save the current small values

In `onSaveInstanceState`, keep the `super` call. Put the current input string and completed count into `outState` under their stable keys. Read the widget at saving time so typing without another click is included.

Avoid copying only a stale field initialized during creation, saving displayed formatted text as the count, or relying on `onDestroy` as a final-save hook. This callback receives a Bundle for the system's saved-state mechanism; it is not a storage file you write yourself.

## Mission 2 Restore the model, then render

In `onCreate`, keep the ordering that builds the layout and obtains View references. Replace the hardcoded initial values with reads from nullable `savedInstanceState`, using Kotlin/default values for missing state. Restore the backing integer and topic before `renderCompleted`.

Do not use `isBlank` to choose a default for saved text. Do not put draft values into a companion object, retain the old Activity, lock rotation, or add `android:configChanges` to prevent the test from exercising recreation.

After repair: type **Coroutines**, complete two sessions, recreate → **Coroutines / 2 sessions**. Complete once more, recreate → **Coroutines / 3 sessions**. Clear the topic entirely and recreate again → empty topic / 3 sessions. Reset and recreate → **Kotlin / 0 sessions**.

Use the [progressive hints](HINTS.md) if needed.

## Check on Android

With the emulator running:

```sh
./gradlew --console=plain :androidApp:connectedPracticeDebugAndroidTest
```

The **10 instrumented tests** cover defaults, typing without another action, counts, continuation after restoration, empty/Unicode text, repeated recreation, reset, background/return, and a fresh launch after finishing. Some fail on the starter. Reports are under `androidApp/build/reports/androidTests/connected/`. Use `ANDROID_SERIAL` when several devices are attached; see `adb devices`.

ActivityScenario's `recreate()` establishes Activity replacement. It does not establish process death. The Android runner uses JUnit 4; your earlier Kotlin exercises remain on their independent JUnit 5 setup. The reference and learner paths share the same acceptance checks.

To restore the starter, first move your Activity answer somewhere safe outside its source folder, then run `:androidApp:preparePractice`. It only creates a missing answer; do not expect `clean` to reset it.

## Observe retained-task process restoration

Use your fixed practice app, without an attached debugger/instrumentation test. Put the SDK's `platform-tools` on your PATH so `adb` works. If multiple devices are connected, select the intended one with `ANDROID_SERIAL` or `adb -s`.

1. Type **Coroutines**, complete two sessions, and record the PID with `adb shell pidof workshop.studylab.practice`.
2. Press **Home** and wait for the app's `onStop`/save trace. Keep its task in Recents.
3. Run `adb shell am kill workshop.studylab.practice`. Check `pidof` again; it should return no PID. If the process was not killed, resolve that condition before claiming process-restoration evidence.
4. Return to the **existing task in Recents**. Compare the PID and visible draft. A new PID plus **Coroutines / 2 sessions** demonstrates restoration in this particular retained-task experiment.

Do not use force-stop, swipe away the task, clear data, reinstall, or leave a debugger attached for this experiment. Those change the condition you are testing. If the task/state is unavailable, record what happened rather than infer a guarantee. The chapter's verification record separates this experiment from automated recreation checks.

Also inspect portrait, landscape, and the keyboard-visible screen. The input should stay usable, and content can scroll in short windows. Automatic rotation can be disabled on an emulator; the Recreate button remains the reliable state-repair experiment.

## Investigate automatic widget state

In a temporary workspace copy, enable automatic saving for the topic widget and try recreation with a broken custom topic save. The widget can restore its own text and make that incomplete solution appear successful. Restore the supplied setting afterward and rerun the typing-without-another-action test.

Explain why a count stored in a static field can pass a rotation check yet fail after a process restart or leak progress into a fresh plan. Passing one visible example is not enough to establish the intended lifetime.

## Explain and revisit

Explain in 60 seconds, or write: “Who creates an Activity, what does Context provide, and which values outlive this Activity? Why does a saved Bundle differ from ViewModel memory and a database? Why is `onDestroy` an unreliable last chance to save?” Include which checks prove Activity replacement and which prove a new process.

Changed request: change only the **default_topic** string resource from Kotlin to KMP in a temporary copy. A fresh plan and Reset should use KMP; a previously saved **Coroutines** draft should remain Coroutines. Explain why defaults apply only when there is no saved value. Restore the resource before the fixed acceptance suite, which specifies Kotlin.

On another day, attempt the draft repair from the starter without the reference. Include empty text, another count click after restoration, and an eligible retained-task process restart. Learner completion remains a separate observation from the course's reference verification. Public sharing is optional.

**[Review the worked solution](SOLUTION.md)** · [Review the lesson](LESSON.md)
