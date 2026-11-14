# Chapter 13 Worked solution

Use the immutable [ch13-v1 source](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch13-v1) or [its ZIP](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/archive/refs/tags/ch13-v1.zip) in a new folder for this chapter's View-based app. The current `mobile` launcher continues with Chapter 14; keep existing answers safe in their original workspace.

Attempt the [workshop](WORKSHOP.md) before reading the [reference Activity](../../mobile/androidApp/src/solution/kotlin/workshop/studylab/MainActivity.kt).

## Save current values and rebuild a new object

The reference keeps `super.onSaveInstanceState`, writes the input's current string under `TOPIC_KEY`, and writes the backing integer under `COMPLETED_KEY`. Reading the widget at saving time includes typing that has not triggered another action.

On creation, the layout and View references exist before restoration. The count is read as an integer with zero as its default. Topic text uses the default string resource only when the saved value is absent/null; an empty saved string remains empty. Restoring the backing count before rendering makes the next click continue from the restored number.

The two small values are sufficient to rebuild this draft. No old Activity, View, Context, static cache, running job, or database is retained. Reset updates the same current values, so a later save reflects the reset plan.

## Keep platform behavior visible

The shared layout deliberately disables automatic saving for the exercised widgets. Otherwise EditText's own text restoration could conceal a missing custom topic save. This is a teaching choice for this mission, not a recommendation to manually save every widget in every Android app.

The reference retains normal framework lifecycle handling and configuration recreation. It does not lock orientation or declare `configChanges` to bypass replacement. The inset/scroll scaffolding keeps the small UI usable with system bars and a keyboard; Compose UI design comes next.

The Activity identity can change while the process ID remains the same. A stopped retained task can also be restored after its background process is killed. That second observation requires separate process evidence; ActivityScenario recreation alone does not provide it. Neither result makes this draft permanent history, and `onDestroy` is not guaranteed before process termination.

## Build and test the reference

From `mobile`, with an API 30+ emulator/device running:

```sh
./gradlew --console=plain :androidApp:assembleReferenceDebug
./gradlew --console=plain :androidApp:lintReferenceDebug
./gradlew --console=plain :androidApp:connectedReferenceDebugAndroidTest
./gradlew --console=plain :androidApp:installReferenceDebug
```

Open **Study Lab reference**, or select **referenceDebug** and run `androidApp`. It installs separately from **Study Lab practice**. Select practiceDebug again to return to your own answer.

The same **10 instrumented tests** run against either variant. They check fresh defaults, current text without another click, count restoration/continuation, empty/Unicode text, repeated recreation, reset, background/return, and finishing followed by a fresh launch. Source sets keep the reference independent of your generated Activity; reference passes do not certify your attempt.

Expected repaired observation: **Coroutines / 2 sessions** survives recreation, then another completion and recreation shows **Coroutines / 3 sessions**. Empty topic remains empty. Reset plus recreation returns **Kotlin / 0 sessions**.

## Changed default and recall

With only the default-topic resource changed to KMP, a fresh plan and Reset choose KMP. A saved Coroutines draft continues to restore Coroutines because saved state takes priority over the default. The fixed acceptance suite still expects the original Kotlin resource, so restore it before those checks.

Enabling widget auto-save in a temporary broken copy can restore the topic even when the custom Bundle write is absent. Restoring the exercise's `saveEnabled=false` makes the typing regression detect that defect again. An in-memory singleton can likewise conceal state loss during recreation while failing the intended process/task boundaries.

**Next: [Chapter 14 Jetpack Compose](../14-jetpack-compose/README.md)** — grow the app with a controlled study list/editor and keyed feedback effect.

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
