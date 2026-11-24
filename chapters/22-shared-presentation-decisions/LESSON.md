# Lesson 22 Who keeps the draft?

Type `007` in the planner. At the keyboard, those three characters are exactly what you entered. After you tap **Create plan**, a seven-minute session makes sense. If you close the sheet and come back to finish editing, which version would you like to see?

## Keep an edit separate from a submission

A **state holder** keeps values and provides the operations that change them. Study Lab's draft holder keeps the raw topic and minutes, including empty or half-typed values. The planner from Chapter 20 still decides which submitted sessions to accept. Here is one edit sequence from the shared design.

```kotlin
val draft = PlanDraftHolder()
draft.editMinutes("0")
val before = draft.snapshot
draft.editMinutes("007")
```

`before.minutesText` still reads `"0"`. The holder replaced its snapshot with a new value whose minutes read `"007"`. Editing the topic preserves those three characters. Two separately constructed holders can therefore carry two different drafts. [Kotlin holder](../../mobile/shared/src/commonMain/kotlin/PlanDraftHolder.kt)

## Follow the same keystroke in two designs

The SwiftUI sheet offers **Native** and **Shared**. Both use the same text fields, **Create plan** action, and Kotlin business rules. The picker changes the code that takes responsibility for an edit. It also gives each design its own draft so you can switch between them.

| After entering `007` | Native | Shared |
| --- | --- | --- |
| Who receives the edit from SwiftUI? | Swift's observable `PlannerDraft` | The same Swift type |
| Who keeps the authoritative text? | `NativeDraftBacking` replaces a Swift value. | `KotlinDraftBacking` calls `PlanDraftHolder.editMinutes`. Kotlin replaces the snapshot. |
| What does the text field read? | `PlannerDraft.values.minutesText` | The same property, filled from the Kotlin snapshot. |

SwiftUI observes the Swift property it reads. After every action, `PlannerDraft` reads back the values its backing actually kept. The Kotlin holder in this example has synchronous methods and no stream of changes. If a timer could edit it independently, the Swift screen would need a way to hear about those edits too. [Swift draft implementations](../../mobile/iosApp/Sources/PlannerDraft.swift) · [SwiftUI form](../../mobile/iosApp/Sources/SharedPlanScreen.swift)

## Choose a lifetime

An owner answers a different question. When the sheet opens again, who can hand it the earlier holder? The sheet disappears on dismissal. Its parent, `StudyShell`, can keep a `PlannerDraftOwner` in `@State` for as long as that shell retains its identity. Keeping the raw text there lets the next sheet continue an edit. Keyboard focus, the last plan preview, and its receipt belong to the current form and can end with it. Starting a fresh process creates a new owner with empty drafts. [StudyShell](../../mobile/iosApp/Sources/StudyLabApp.swift) · [Apple on State](https://developer.apple.com/documentation/swiftui/state)

On Android a screen can obtain an AndroidX `ViewModel` scoped to an owner such as a navigation destination. It can survive an activity's configuration change and is cleared when its owner goes away permanently. A plain Kotlin object such as `PlanDraftHolder` gets its lifetime from whoever keeps it. A shared AndroidX `ViewModel` is another design you could adopt. Each host would still need to give it an appropriate owner and arrange cleanup. Data that must survive process death needs restoration or persistence. [Android ViewModel overview](https://developer.android.com/topic/libraries/architecture/viewmodel) · [ViewModel in Kotlin Multiplatform](https://developer.android.com/kotlin/multiplatform/viewmodel)

## Check the boundary

Suppose the screen shows exactly the text you typed. How can a test tell whether Swift asked the backing to make the edit? Pass a test double through the same `PlanDraftEditing` protocol. It can record the edit and return a recognizably different value. The test checks both what was sent and what SwiftUI would read. Running the same edits with the real shared backing checks the path into Kotlin. A different test can inject a factory into the owner and count how many times it created a holder when the same draft was requested twice.

If both apps need the same editing rules, a shared holder gives the team one place to change and test them. If the fields and interactions evolve only on iOS, a native holder keeps those changes within Swift. In either case, try closing and reopening the real screen before claiming the draft will survive.

[Open the workshop](WORKSHOP.md)
