# Chapter 14 Worked solution

Attempt the [workshop](WORKSHOP.md) before opening the [reference Activity/editor](../../mobile/androidApp/src/solution/kotlin/workshop/studylab/StudyPlannerActivity.kt). The [fixed screen](../../mobile/androidApp/src/main/kotlin/workshop/studylab/StudyPlannerScreen.kt) handles layout, Material styling, list identity, and semantics.

## One owner, one displayed draft

The route keeps six small authoritative values in `rememberSaveable`: two topics, two completion flags, selected ID, and draft. It derives immutable row values from them rather than synchronizing another list or progress counter. This is a bounded two-record UI lesson; larger data ownership comes next.

`SessionEditor` uses its `draftTopic` parameter and forwards edits through `onDraftChange`. It holds no second authoritative copy. Save/Cancel callbacks run only in event handlers; parent changes immediately become the displayed controlled value.

Different-row selection loads that row's committed topic and discards the old unsaved draft. Same-row selection does nothing. Save trims and commits a nonblank draft only to the selected topic, leaving flags and the other row alone. Cancel reloads the selected committed topic. Completion events change only the identified flag, preserving selection/draft. Stable IDs support equal or edited topic names.

## Separate restoration and feedback

Saveable authoritative values reconstruct the screen through eligible saved state; the effect does not reload the editor. An unsaved restored draft therefore remains distinct from the committed topic. This does not create durable storage or independently prove real process-death restoration.

The ordinary remembered revision and visibility are transient. `LaunchedEffect(savedRevision)` gives every successful save its own two-second delay, including a second save of the same value. A new revision cancels the old timer. The effect clears only feedback, while edits, selection changes, and Cancel can clear it earlier. Recreation does not replay a prior notice.

## Run the separate reference

From `mobile`, with an API 30+ emulator/device running:

```sh
./gradlew --console=plain :androidApp:assembleReferenceDebug
./gradlew --console=plain :androidApp:lintReferenceDebug
./gradlew --console=plain :androidApp:connectedReferenceDebugAndroidTest
./gradlew --console=plain :androidApp:installReferenceDebug
```

Open **Study Lab reference**. Select practiceDebug again to return to your answer. Both variants include the preserved Chapter 13 Activity and current Compose screen; the launcher opens Compose.

The **32 device checks** include 22 Compose checks and the 10 retained lifecycle checks. Isolated editor checks verify incoming values/outgoing callbacks; Activity checks verify event policies, IDs, boundaries, recreation, and feedback. Compose-clock checks drive frames and cross the notice deadline without guessed wall-clock sleeps. They do not use recomposition counts as a business contract.

For the old lesson's original launcher/setup, use the immutable [ch13-v1 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch13-v1). Its sources are unchanged by this chapter's new commit.

## Changed request answer

With the second initial topic changed to Kotlin, both rows still have distinct IDs. Select session 2, rename it to Compose, save, then complete it: session 1 remains Kotlin/incomplete, session 2 becomes Compose/complete, and progress is one of two. The lazy-list keys and callbacks use IDs rather than duplicate text. Restore Android as the initial second topic before the fixed acceptance suite.

A selection-keyed effect that assigns the committed value to draft can destroy an unsaved restored draft when the new composition enters. Keep that assignment in the explicit selection event instead, and use effects only for their intended composition-owned work.

**Next: Chapter 15 will introduce Android app architecture, ViewModel, repositories, navigation, and lifecycle-aware state collection. Its lesson is planned.**

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
