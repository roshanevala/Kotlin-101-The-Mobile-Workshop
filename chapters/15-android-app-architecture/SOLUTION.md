# Chapter 15 Worked solution

Attempt the [workshop](WORKSHOP.md) before opening the [reference ViewModel](../../mobile/androidApp/src/solution/kotlin/workshop/studylab/CatalogueViewModel.kt). The [repository](../../mobile/androidApp/src/main/kotlin/workshop/studylab/data/SessionsRepository.kt), [factory](../../mobile/androidApp/src/main/kotlin/workshop/studylab/CatalogueActivity.kt), and [screens/navigation](../../mobile/androidApp/src/main/kotlin/workshop/studylab/CatalogueScreen.kt) are fixed scaffolding.

## Requests and state

The model cancels its previous request before launching in `viewModelScope`. Each request publishes Loading, awaits the repository, snapshots its list, checks activity, and publishes Empty or Content. Cancellation propagates. Only the repository's expected IO failure maps to Error, with another activity check protecting against an obsolete failure.

The model exposes read-only StateFlow. The UI renders the four states and emits events. No composable starts a load during rendering, and no repository renders a message. There is no additional domain layer, global request scope, or dependency-injection framework for this small feature.

## Notes, owners, and navigation

The model reads and writes saved strings under `note-$id`. Edits require membership in current Content and at most 120 Kotlin string units. Blank/whitespace input is valid and unchanged. Loading, retry, and errors do not clear these strings. A new model can read a restored note immediately while reloading its catalogue.

The factory uses owner-provided creation extras for `SavedStateHandle`. The Activity's `viewModel()` call is outside `NavHost`; list/detail share that retained owner. Navigation carries an integer ID and resolves the record from current state. Missing IDs have recovery. Screen state and note streams use lifecycle-aware collection; pausing a subscriber does not cancel the model's request.

Back/reopen and rotation keep this Activity-owned model. A new process requires a new model plus eligible saved state, while data is loaded again. The demo's first failure therefore returns after a process restart, and Retry can reveal the restored detail and draft. None of this makes a planning note durable saved data.

## Run the independent reference

From `mobile`, with an API 30+ device/emulator for the connected task:

```sh
./gradlew --console=plain :androidApp:testReferenceDebugUnitTest
./gradlew --console=plain :androidApp:connectedReferenceDebugAndroidTest
./gradlew --console=plain :androidApp:lintReferenceDebug
./gradlew --console=plain :androidApp:installReferenceDebug
```

There are **20 local ViewModel/repository checks** and **46 device checks**: 14 new architecture/UI/navigation checks and the 32 retained checks. A test that constructs a handle from initial values checks reconstruction logic; a recreation test checks Activity retention/restoration. Neither alone is proof of a real process restart.

Reference compilation excludes all three learner answers. The immutable [ch15-v1 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch15-v1) and [ZIP download](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/archive/refs/tags/ch15-v1.zip) pin this chapter's starter and reference. Use [ch14-v2](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch14-v2) in a separate folder for the previous Compose mission's exact launcher and commands. Preserve original answers when switching snapshots.

## Changed request answer

With `DemoSessionsRepository(emptyList(), failFirst = false)` in a temporary factory, the model reaches Empty. The UI offers Reload and no session actions; an empty response is not Error. With equal topic strings, IDs still identify separate routes and saved note keys. Restore the default Kotlin/Android fixture before the fixed checks.

The Bug Museum map passes rotation because its ViewModel survives. It fails a fresh-owner test because the map has no saved representation. The right fix is the saved-state boundary, not an extra refresh or a navigation-side copy of the note.

**Next: Chapter 16 introduces Swift for Kotlin developers. Its lesson is planned.**

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
