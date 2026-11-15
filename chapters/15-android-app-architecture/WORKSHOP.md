# Workshop 15 The retry brought yesterday's error

Bug report: “Retry does not show loading. An empty response looks unfinished. An old request can replace a newer result, and a note survives rotation but disappears with a fresh owner.” Repair one ViewModel in the growing Study Lab app.

Read the [lesson](LESSON.md) first. Split the work into loading, request lifetime, and saved drafts/navigation if useful.

## Build and find your answer

Continue in [mobile setup](../../mobile/README.md). Use JDK **17**, Kotlin **2.2.21**, AGP **8.11.1**, Gradle **8.14.3**, compile/target SDK **36**, and Build Tools **35.0.0**. Compose stays on BOM **2025.10.01** and Activity Compose **1.11.0**. This chapter adds Lifecycle **2.9.4**, Navigation Compose **2.9.5**, and matching Android/core/test coroutines **1.11.0**. These are a tested compatible set, not a latest-version claim.

From `mobile`:

```sh
./gradlew --console=plain :androidApp:assemblePracticeDebug
./gradlew --console=plain :androidApp:installPracticeDebug
```

Use `./gradlew.bat` on Windows. Select **practiceDebug**, then open **Study Lab practice**. The current launcher is `CatalogueActivity`. The deterministic demo first shows Loading, then a recoverable error. Try again to see Kotlin and Android. The untouched starter builds; its later transitions, cancellation, and restoration are deliberately wrong.

Edit only the new generated answer:

```text
androidApp/src/practice/kotlin/workshop/studylab/CatalogueViewModel.kt
```

Leave the tracked template, reference, factory, repository, screens, and acceptance tests unchanged. Preparation preserves your existing Chapter 13/14 Activities. All three practice files must compile together, even when running only this chapter's tests; the reference compiles none of them. Keep earlier edited compile errors fixed, or use a fresh course copy in another folder while preserving your original work. The immutable [ch14-v2 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch14-v2) follows the old Compose launcher.

## Predict and trace ownership

Predict the state sequence for the first failure, a retry, and an empty response. Then trace a Retry click from `CatalogueScreen` to the ViewModel and repository, and follow the result back through StateFlow.

Find where the Activity creates its factory and obtains the ViewModel. Is it inside a navigation destination or outside `NavHost`? Predict whether Back clears this particular model. Identify the NavController's integer argument and the separate handle keys used for notes.

## Agree on the contracts

- Construction requests the catalogue once. Every refresh cancels the previous request and starts Loading before awaiting the result.
- An empty response becomes Empty. A nonempty response becomes Content with a list snapshot. Recoverable `IOException` becomes Error with UI-owned generic wording.
- Cancellation propagates and never becomes Error. A cancelled old request cannot publish success or failure after a replacement, even if its source finishes uncooperatively. Clearing the owning ViewModelStore cancels owned work.
- The UI observes state with lifecycle awareness. A paused collector does not imply that a ViewModel request has stopped.
- Navigation passes the session ID only. Detail looks it up in current Content; a missing ID has recovery and Back. Both destinations share the Activity's model. Back/reopen and Activity recreation keep per-ID notes.
- A note is a draft. Preserve accepted text exactly, including blank/whitespace. Accept `String.length <= 120`; reject a longer replacement without changing the old note. Ignore edits for an unknown ID or while state is not Content.
- Read/write each note through `SavedStateHandle` under `note-$id`. A fresh model can read a restored note before loading data. Loading/errors must not erase notes. The catalogue and request jobs are reconstructed, not bundled.
- No HTTP, database, permission, KMP, application singleton, or DI container is required. Never use Activity or View references in the ViewModel.

## Mission 1 Publish useful load states

Keep state privately mutable and publicly read-only. Add Loading on refresh. Classify the result as Empty or Content and snapshot the nonempty list. Catch the specified IO failure without treating arbitrary programming exceptions as recoverable data.

Run local checks from `mobile`:

```sh
./gradlew --console=plain :androidApp:testPracticeDebugUnitTest
```

These **20 local tests** compile the practice variant and run without an emulator. The starter intentionally fails some of them; distinguish assertion failures from a missing SDK or compilation error. Reports: `androidApp/build/reports/tests/testPracticeDebugUnitTest/`.

## Mission 2 Make the request belong to its owner

Use the existing request `Job` to cancel obsolete loading. Keep work in `viewModelScope`. Preserve cancellation, and check that the current coroutine is still active before publishing a result or a caught IO failure.

Inspect the tests with controlled completion signals. One older source deliberately finishes inside `NonCancellable`. Predict which state must remain after it is released. Do not “fix” the race with a guessed delay, global scope, or a second unmanaged job.

## Mission 3 Save a small draft, then navigate

Replace the private in-memory note map with the supplied saved-state handle. Its read-only note flow and its writes must use the same key. Keep ID/content validation and the length boundary in the owner, not just the text field.

On the app: retry → open session 1 → type a note → Back → open session 2 → type a different note → Back → reopen session 1. Both notes should remain independent. Recreate while session 2 is open; its destination, topic, and draft should remain. The current ViewModel survives rotation, so that observation alone does not prove saved-state reconstruction.

The lesson explains the factory, lifecycle-aware collection, route arguments, and Back handling in the fixed scaffolding. Trace these rather than moving navigation into the ViewModel. In the reference, the first source failure happens once per new ViewModel, not once per recomposition or rotation.

## Check on a device and compare the reference

With an API **30+** emulator/device running, use this chapter's two new classes:

```sh
./gradlew --console=plain :androidApp:connectedPracticeDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=workshop.studylab.CatalogueNavigationTest,workshop.studylab.CatalogueScreenTest
```

The **14 new device checks** cover loading/empty/error rendering, callbacks, IDs, controlled notes, lifecycle-paused collection, Back, reload, and recreation. The unfiltered current workspace has **46 device checks**, including the earlier 32. Earlier unsolved Activities retain their expected failures; a Chapter 15 pass does not complete those answers.

```sh
./gradlew --console=plain :androidApp:testReferenceDebugUnitTest
./gradlew --console=plain :androidApp:connectedReferenceDebugAndroidTest
./gradlew --console=plain :androidApp:lintReferenceDebug
./gradlew --console=plain :androidApp:installReferenceDebug
```

Reference and practice install separately. Use `ANDROID_SERIAL` if multiple devices are attached. Device reports are under `androidApp/build/reports/androidTests/connected/`; unit and device checks are different tasks. No hosted emulator is provided by this course's workflow.

To restart only this mission, move your `CatalogueViewModel.kt` answer somewhere safe outside the source folder and run `:androidApp:prepareArchitecturePractice`. It fills only the missing new answer. `preparePractice` prepares all missing answers; `clean` preserves them.

## Investigate and revisit

Bug Museum: keep notes in an ordinary ViewModel map. Why does rotation still work? Compare with creating a new model from a `SavedStateHandle` containing `note-2`. Explain which check distinguishes retention from reconstruction.

Optional process experiment: on an emulator, edit a reference note in detail, press Home so Android can save the task, kill only this app's **background process**, and reopen its retained task through Recents. Confirm a changed process ID separately from a new Activity. The new fixture fails once again; retry, then inspect the restored detail and note. Do not use force-stop or dismiss the task as a substitute for eligible system restoration. Keep any device-specific procedure and observation in a private log; this experiment is separate from ActivityScenario recreation tests.

Changed request: in a temporary workspace copy, give the demo repository an empty catalogue and disable its first failure. Predict the initial state and verify that Empty offers Reload rather than session actions. Then give two records equal topic text: route IDs and note keys must still distinguish them. Restore the original fixture before the fixed acceptance suite.

Explain in 60 seconds, or write: “Who owns loaded data, a request, a draft note, and the back stack? What survives rotation? What must be reconstructed after process termination? Why does stopping a collector not cancel every upstream job?” On another day, implement one of these state transitions from its contract before opening your previous answer.

Use [progressive hints](HINTS.md) as needed, then [review the solution](SOLUTION.md).
