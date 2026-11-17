# Workshop 17 The scene woke up and forgot the plan

Bug report: “The second row opens the first session. Save reads a different draft, a tip change loses progress, and returning from Home can reset everything.” Repair the native SwiftUI owner/editor contract.

Read the [lesson](LESSON.md) first. Work in three sessions if useful: owner/identity, draft binding/events, then scene behavior and checks.

## Prepare and run your source choice

Follow [iOS setup](../../mobile/iosApp/README.md). Use the locally checked **Xcode 27.0/Apple Swift 6.4**, **Swift 6 language mode**, and an explicitly selected supported iOS Simulator. The project deploys to **iOS 17.0+**; **iOS 26.5** is the checked runtime. No Gradle, package manager, signing account, or physical device is required for these simulator commands.

From `mobile/iosApp`:

```sh
python3 ios.py prepare
python3 ios.py build
```

Open **StudyLab.xcodeproj** and select **StudyLabPractice**, or use the setup guide's simulator commands:

```sh
python3 ios.py run --device "$study_simulator"
```

`study_simulator` must contain your intended simulator's UDID and that device must be booted for run. Edit only the generated **`Practice/StudyScreens.swift`**. It contains StudyRoot and StudyDetail. Keep shared Sources, Starter/Solution, project settings, and fixed checks unchanged during the core mission. Earlier Kotlin, Swift CLI, and Android answers remain untouched.

Practice/reference schemes install separately and compile separate screen files. Preparation fills a missing answer and never replaces existing work. The starter builds and runs while the reported behaviors are deliberately wrong.

## Predict the owners and reproduce the bug

The list starts with Kotlin/25 minutes and Swift/40 minutes, both incomplete, with **Completed 0 of 2**. Before editing:

1. Open the Swift row. Compare its ID with the destination title.
2. Type a new draft, dismiss the keyboard with Done, and inspect Save/Cancel behavior.
3. Complete a record, then change Show study tip. Predict the progress count.
4. Return from Home while editing. Distinguish an activation from a fresh process.

Trace the fixed StudyShell's state, the root's store, the typed path, the detail field, and the model draft. Which values have two competing owners? Which values must remain different until Save?

## Agree on the contracts

- One root owns the observable StudyStore through managed State at a stable identity. The tip toggle's ordinary parent reconstruction preserves topics, drafts, completion, filter choice, and the navigation path.
- Row identity uses the immutable session ID. Each NavigationLink passes that row's integer ID, and the destination reads the current matching record. Equal/edited topic text must not merge records or redirect navigation.
- The field displays the owner's per-ID draft and writes through to that same ID. No second authoritative State string lives in the detail editor. Local keyboard focus can remain local.
- Typing changes only a draft. Save trims a nonblank value and commits only that record, preserving completion and the other topic. Cancel reloads the selected committed value. Clear produces an empty draft; whitespace-only drafts disable Save while Cancel remains usable.
- Back/reopen retains that record's unfinished draft in the root store. Opening the other record reads its independent draft. Rotation keeps the current destination and draft.
- Completion updates only the identified record. Hiding completed rows changes visibility, while progress still uses the two domain records.
- Background/foreground within the running process preserves all these values. Observing scene activation must not call reset or reload a default catalogue.
- Terminating and launching a fresh process starts the two-record catalogue again. This mission supplies no durable storage, SceneStorage, or process-restoration contract.

The shared model implements the data rules. The mission repairs how its owner, bindings, navigation, and lifecycle connect to that model.

## Mission 1 Retain one owner and stable identity

Repair the root's store declaration using managed State. Keep the shell's tip state separate and its root position stable. The tip is a normal presentation change, not a reset action.

Use Identifiable session IDs in ForEach and pass the selected ID into navigation. Keep the existing Int destination lookup. Do not repair the problem by assigning a fresh `.id(...)` to the root; changing identity discards its state.

## Mission 2 Give the field a binding to its owner

Remove the detail's authoritative local draft and its on-appear copy. Build a `Binding<String>` whose get/set operations use the selected ID and the owner's draft methods. Supply that binding to the fixed TopicField.

Connect Clear, Save, and Cancel to the same selected record. Compute Save's disabled state from the owner's current draft. Watch the field after Cancel and normalization: a correctly controlled field updates when its parent changes.

Try session 1 draft → Back → session 2 draft → Back → session 1. Compare draft and saved-topic text before/after Save. Then make both saved topics Kotlin and confirm that each row still opens its own ID.

## Mission 3 Give scene transitions their stated meaning

Remove the reset-on-active behavior. Read the fixed App's phase logging and explain why neither a body evaluation nor a return to active means “start with new data.” Keep entered topic text out of logs.

Use the simulator's Home action, then return through the app icon or app switcher; inspect both an unsaved draft and a committed topic. Keep the same running process for this observation. Separately repeat `python3 ios.py run --device "$study_simulator"` to terminate/relaunch the practice app and expect fresh data. Do not describe the first observation as a process-restoration test.

## Run checks at the right boundary

```sh
python3 ios.py check-model
python3 ios.py test --device "$study_simulator"
```

The **12 shared-model checks** do not compile your UI file and can pass before you repair it. The **13 UI checks** execute your selected app's real controls, navigation, rotation, parent updates, and scene/relaunch behavior. Some intentionally fail on the starter. Result bundles are under `build/practice/results.xcresult`; open one in Xcode to inspect a failure. The tests use observed control values and UI waits, not arbitrary sleeps or a universal body-evaluation count.

The provided UI helpers target the visible switch in a labeled Toggle's row-sized accessibility frame, then check its value. An automation tap that never changed the switch is different from a model rule failing after a real event. Do not weaken the expected progress, draft, or ID assertions to make a test green.

For the independent reference:

```sh
python3 ios.py build-tests --reference
python3 ios.py test --reference --device "$study_simulator"
python3 ios.py run --reference --device "$study_simulator"
```

`build-tests` compiles the app and test bundle without running UI tests. Reference commands do not prepare or compile your Practice file. XCTest/UI-actor annotations are fixed scaffolding; Chapter 18 covers testing/concurrency in more depth.

## Investigate and revisit

Bug Museum: retain the observable class in a plain stored property. Why can simple completion still appear to update while reconstructing the parent loses the model? Then give a correctly managed root a changing explicit identity in a temporary copy. Explain why managed State still cannot preserve a value across a deliberately replaced identity.

Changed request: in a temporary copy, add `completedMinutes` as a computed property of StudyStore and display it in the progress section. Complete session 2, then hide completed records. The minutes total must stay **40**, because visibility does not delete the completed domain record. Restore the fixed files before the normal acceptance suite.

Explain in 60 seconds, or write: “What does Observation track? Who owns a store, a draft, and keyboard focus? When do bindings share access? Which scene changes preserve this app's data, and what happens after a new process?” Revisit with a different draft after a break, predicting each value before running.

To reset only this mission, move `Practice/StudyScreens.swift` somewhere safe outside the source folder and run `python3 ios.py prepare`. `clean` removes build output and preserves answers. Use [progressive hints](HINTS.md), then [review the solution](SOLUTION.md).
