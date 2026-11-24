# Chapter 22 Worked solution

Both versions lose `Loops` and `025` in the starter. The decisive clue is that asking *one owner* for the *same design* twice produces two different objects. Switching the language of the second object gives you another empty draft.

## Keep the first draft for each design

The reference owner keeps a dictionary as an instance property. On the first request for a design it calls the supplied factory and stores the result. Later requests return that object, including any edits. A newly created owner has its own empty dictionary. [Reference owner](../../mobile/iosApp/Solution/PlannerDraftOwner.swift)

```swift
private var drafts: [DraftDesign: PlannerDraft] = [:]

func draft(for design: DraftDesign) -> PlannerDraft {
    if let existing = drafts[design] { return existing }
    let draft = makeDraft(design)
    drafts[design] = draft
    return draft
}
```

`StudyShell` owns both the draft owner and the selected design. Closing and reopening the sheet preserves both. Changing designs gives the form a different SwiftUI identity through `.id(design)`. Its preview, focus, and receipt disappear with the previous form. **Clear draft** clears only the selected holder. The next process begins with a fresh shell and empty fields. [Root view](../../mobile/iosApp/Sources/StudyLabApp.swift) · [Plan form](../../mobile/iosApp/Sources/SharedPlanScreen.swift)

## Match each observation to a check

| Observation | What establishes it |
| --- | --- |
| A previously captured Kotlin snapshot still reads `" 025 "` after the holder is cleared. | The common test keeps both references and examines their values. |
| Swift sends the typed value `input` and then shows the value supplied by its backing. | The injected fake records `topic:input` and returns `[input]`. The Swift test checks the event list and the displayed values. |
| Asking twice for Native or twice for Shared returns one object per design. | An injected factory counts constructions. The Swift test also compares object identity with `===`. |
| The sheet reopens with `025`, while the previous preview of 25 minutes is gone. | The UI tests type, submit, close, and reopen the sheet for each design. |

From **`mobile/iosApp`**, use the workshop simulator's UDID to run the independent Swift reference.

```sh
python3 ios.py test --reference --device "$study_simulator" --only-test StudyLabReferenceTests/PlannerDraftTests
python3 ios.py test --reference --device "$study_simulator" --only-test StudyLabReferenceUITests/StudyLabUITests/testNativeDraftSurvivesClosingPlanner --only-test StudyLabReferenceUITests/StudyLabUITests/testSharedDraftSurvivesClosingPlanner
```

## One decision for Study Lab

I would keep draft editing in Swift for the current iOS-only form and continue sharing submission rules. Both implementations preserve the raw `025` through dismissal once the shell owns their drafts. The native design lets the iOS developer change the two fields and inspect their transitions in Swift. Its cost is that a future Android editor would need equivalent transitions. I would revisit the shared Kotlin holder when an Android editor is scheduled and both clients need the same edits, such as undoing the last change. Each platform would still choose the lifetime of its editor and manage keyboard focus and preview presentation.

If people need to recover a draft after the system kills the app, I would first define when to save, restore, and discard it. I would test by entering a draft, terminating the process, and relaunching. The current in-memory owner starts empty in that experiment whichever design is selected.

## Compare your Part 5 evidence

With the reference business rules, `plan("Kotlin", 1)` accepts the lower limit and uses one ID from the injected source. A zero-minute submission returns no session and uses zero IDs. The common suite runs these same assertions on Android's host JVM and the Apple simulator. Another test asks the platform's ID source for a UUID. [Planner tests](../../mobile/shared/src/commonTest/kotlin/SessionPlannerTest.kt)

The header's Swift name for the draft edit is `editMinutes(text:)`. The Swift backing passes the raw string there and reads `snapshot` afterward. The planner method returns a nullable session. The Swift adapter handles the imported optional before displaying a result. Pinpointing these calls in the generated header ties the decision to the framework the app actually imports. [Swift backing](../../mobile/iosApp/Sources/PlannerDraft.swift) · [Reference adapter](../../mobile/iosApp/Solution/SharedPlanAdapter.swift)

This completes Part 5. The next part opens with HTTP and serialization. [Course roadmap](../../docs/CURRICULUM.md)

[Back to the workshop](WORKSHOP.md) · [Back to the course](../../README.md)
