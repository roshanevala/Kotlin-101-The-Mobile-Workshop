# Workshop 14 The editor saved the wrong topic

Bug report: “I changed the text to Coroutines, but Save kept Kotlin. Switching sessions also leaves stale text, and completing one session changes both rows.” Repair the owner/editor contract in the growing Study Lab app.

Read the [lesson](LESSON.md) first. Work in three sessions if needed: controlled input, owner events/restoration, then effects and independent checks.

## Sync and open your new answer

Continue in **`mobile`**, following [setup](../../mobile/README.md). Sync after updating. Keep JDK **17**, Kotlin **2.2.21**, AGP **8.11.1**, Gradle **8.14.3**, SDK **36**, and an API **30+** emulator/device. Compose uses compiler plugin **2.2.21**, BOM **2025.10.01**, and Activity Compose **1.11.0**. Dependencies are pinned for this verified toolchain, not advertised as the latest releases.

From `mobile`:

```sh
./gradlew --console=plain :androidApp:assemblePracticeDebug
./gradlew --console=plain :androidApp:installPracticeDebug
```

Use `./gradlew.bat` on Windows. Select **practiceDebug** and launch **Study Lab practice**. The current launcher is the Compose study list/editor. Your new ignored answer is:

```text
androidApp/src/practice/kotlin/workshop/studylab/StudyPlannerActivity.kt
```

Edit that file, including its `StudyPlannerRoute` and `SessionEditor`. Leave the tracked [starter](../../mobile/androidApp/src/starter/kotlin/workshop/studylab/StudyPlannerActivity.kt), shared screen/theme, and acceptance tests unchanged. Chapter 13's `MainActivity.kt` answer is preserved alongside it. Preparation fills missing files and never replaces an existing answer.

The same growing Android module compiles its Activities together. If an earlier edited Activity has a compile error, fix that error or use a fresh source snapshot in a new folder, keeping your original answer safe. Reference builds remain isolated from practice sources. Use the immutable [ch13-v1 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch13-v1) to follow the old View-based lesson; do not overwrite your answer to switch chapters.

## Predict and reproduce the split state

The screen starts with **Kotlin**, **Android**, **Completed 0 of 2**, and session 1 selected. In the untouched starter:

1. Enter Coroutines in the editor and Save. Observe whether row 1 agrees with the text you saw.
2. Select session 2 and inspect its editor.
3. Toggle a completion control and inspect both rows.

The app builds and runs while these behaviors are deliberately wrong. In the code, identify the six authoritative values, the two transient feedback values, and the extra local text copy inside the child. Predict which value Save actually reads.

## Agree on the contracts

- The catalogue has two fixed IDs, **1** and **2**, initially Kotlin/Android with neither completed. Topic text may be edited or become equal; it never becomes row identity. Adding/removing records is outside this mission.
- One route owns committed topics, completion flags, selected ID, and unsaved draft. Derive immutable `StudySession` rows from those values; do not create another progress counter or hidden mutable list to synchronize.
- `SessionEditor` is controlled: display the supplied `draftTopic`, forward text edits through `onDraftChange`, and invoke Save/Cancel callbacks only from user events. Parent changes must appear without a cached child copy.
- Typing changes only the draft. The owner accepts a replacement when `String.length <= 80`; longer replacements leave the prior draft unchanged. This is the lab's Kotlin string-length boundary. Save is disabled for whitespace-only text.
- Save trims the draft, commits a nonblank result only to the selected row, and updates the draft to that committed text. Completion flags and the other row's topic are preserved.
- Cancel reloads the selected row's committed topic, without changing selection or completion. Selecting a different row discards the old unsaved draft and loads that row's topic. Selecting the same row is a no-op and preserves its draft.
- A completion event toggles only its identified row once. It must preserve the selected row and unsaved draft, including when the clicked checkbox belongs to another row.
- Preserve the six authoritative values using small saveable state. Activity recreation restores both committed rows, completion, selection, and an unsaved draft. Do not copy committed topic into draft from a selection effect or the rendering body.
- Each successful Save shows **Topic saved** and increments a transient revision. A keyed effect dismisses that feedback after its two-second delay. Another Save restarts the interval, even when saving the same value. An accepted edit, a different selection, or Cancel clears the notice immediately.
- The revision and feedback visibility use ordinary remembered state. Recreation restores the draft without replaying a stale saved notice. This is eligible saved state, not durable storage or a promise after every task dismissal.

The fixed screen provides Material controls, headings/labels, selected/completed semantics, stable list keys, scrolling, and safe insets. Do not call Chapter 13's View inset helper on this Compose tree. No repository, ViewModel, navigation, database, runtime permission, or network is required.

## Mission 1 Give the child one source of text

Repair `SessionEditor`: remove its authoritative `localDraft` copy. Bind `OutlinedTextField.value` to the parameter and forward `onValueChange` to the owner callback. Compute whether Save is enabled from the supplied text.

Check typing → Cancel → typing → select another row. A child that appears to type correctly may still ignore a parent reset; the isolated editor tests exercise both directions. Keep save/cancel callbacks out of composable rendering and initialization.

## Mission 2 Complete owner events and saved state

Repair the six authoritative declarations using `rememberSaveable`. Leave transient revision/notice state in `remember`. This bounded first Compose feature saves primitive values, then derives its two immutable rows; a larger data owner arrives in Chapter 15.

On a different selection, update selected ID and load that row's committed topic. Preserve the existing same-ID guard. Toggle only the requested completion flag. Save into the selected topic, and implement Cancel by reloading that topic. Keep input-length and nonblank guards intact.

Try this sequence: edit/save session 1 → complete it → select/edit/save session 2 → complete it → type a new unsaved draft for session 2 → recreate the Activity. Both saved rows and flags should survive, session 2 should remain selected, and its unsaved draft should still be in the editor. Use a rotation or the emulator's recreation mechanism; automated tests also call ActivityScenario.recreate.

## Mission 3 Key the notice to each successful Save

The starter's `LaunchedEffect(Unit)` sees the initial revision and does not restart for later saves. Key it to `savedRevision` so a successful save cancels the old timer and starts a new one. Keep the delay and notice update inside the effect.

Do not key this effect to draft text, and do not use an effect to commit changes or reload the editor. Ordinary typing must not save a topic. Restoration must not overwrite an unsaved draft.

Save twice within the first interval: the second save gets its own interval. Then type or switch rows; the notice should clear. Exact frame display is not a wall-clock latency guarantee. The tests use a Compose clock rather than guessed sleeps.

Use the [progressive hints](HINTS.md) if needed.

## Check on Android

```sh
./gradlew --console=plain :androidApp:connectedPracticeDebugAndroidTest
```

The current workspace has **32 device tests**: **22 Compose checks** (18 Activity scenarios and four isolated editor checks), plus the **10 retained Chapter 13 checks**. To focus on the new chapter while an earlier Activity is unfinished:

```sh
./gradlew --console=plain :androidApp:connectedPracticeDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=workshop.studylab.StudyPlannerTest,workshop.studylab.SessionEditorTest
```

Both commands still compile the complete practice variant. Reports are under `androidApp/build/reports/androidTests/connected/debug/flavors/practice/`. Use `ANDROID_SERIAL` if multiple devices are connected.

The tests check observed values/events, selected IDs, controlled text, save/cancel policies, completion, input boundaries, recreation, and feedback lifetime. The untouched Compose starter intentionally fails some new checks. Chapter 13's old starter also retains its own expected failures until you complete it; reference passes do not complete either learner attempt.

The Android tests use JUnit 4 and Compose's test APIs. They include semantic queries for the editable value separately from its field label, and control rendering frames for timed effects. They do not assert a universal recomposition count or infer real process-death behavior from Activity recreation.

To reset only this chapter, move your `StudyPlannerActivity.kt` answer somewhere safe outside its source folder and run `:androidApp:prepareComposePractice`. It fills only the missing Compose file and preserves `MainActivity.kt`. `preparePractice` prepares both missing files; `clean` preserves both answers.

## Investigate a believable stale-state fix

In a temporary copy, change only the effect key to selected ID and assign the committed topic to draft inside that effect. It can appear to fix selection but overwrite a restored unsaved draft when the new composition enters after recreation. Restore the correct event-driven draft assignment before acceptance checks.

Explain a second tempting mistake: why does using editable topic text as a lazy-list key fail when two rows receive the same topic? Why does adding a test tag not replace the control's accessible label and role?

## Explain and revisit

Explain in 60 seconds, or write: “What triggers recomposition, where is the source of truth, and what survives Activity recreation? Why does the editor emit events instead of remembering its input? Why does the save-notice effect need a revision key?” Include the difference between model state, draft state, and transient feedback.

Changed request: in a temporary copy, change the initial **planner_second_topic** resource from Android to **Kotlin**, matching the first row. Predict IDs, selection, completion, and editing behavior. Verify the second row can be renamed/completed independently. Restore the resource before fixed tests, which specify Android initially.

On another day, recreate the Activity while editing an unsaved draft, then Cancel and toggle the other row. Predict every committed value and flag before running. Review portrait, landscape, keyboard visibility, and a larger font setting separately from state behavior. Public sharing is optional.

**[Review the worked solution](SOLUTION.md)** · [Review the lesson](LESSON.md)
