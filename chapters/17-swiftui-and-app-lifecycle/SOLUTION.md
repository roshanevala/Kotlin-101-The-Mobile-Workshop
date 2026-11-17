# Chapter 17 Worked solution

Attempt the [workshop](WORKSHOP.md) before opening the [reference screens](../../mobile/iosApp/Solution/StudyScreens.swift). The shared [model](../../mobile/iosApp/Sources/StudyStore.swift), [controls](../../mobile/iosApp/Sources/StudyControls.swift), and [App/shell](../../mobile/iosApp/Sources/StudyLabApp.swift) provide fixed scaffolding.

## One root, one draft source

StudyRoot owns its observable store with State and keeps its typed navigation path separately. ForEach uses stable record identity; each link passes its own ID. A parent tip change leaves that root identity and its managed model intact.

StudyDetail reads the shared store and constructs a per-ID Binding. The field's get/set operations reach the same draft as Clear, Save, and Cancel. Removing the private cached string allows Cancel and trimmed commits to update the displayed field immediately. The shared model validates nonblank saves and preserves the other record and completion flags.

The filter borrows a writable observable projection with Bindable. The reusable TopicField borrows text through Binding and owns only its local focus state. Neither creates a competing model or text owner.

## Identity and lifetime

Back/reopen preserves drafts because the root store outlives a destination. Completion filters affect visibility while domain totals remain unchanged. Equal topic names still have independent IDs and drafts. Rotation retains the stated destination/owner.

The corrected root does not reset on scene activation. The fixed App logs phase names without user text. Backgrounding/activation is separate from terminating/relaunching; a new process starts fresh because storage/restoration is outside this chapter's contract.

## Run the independent reference

From `mobile/iosApp`, with the intended simulator selected as in [setup](../../mobile/iosApp/README.md):

```sh
python3 ios.py check-model
python3 ios.py build-tests --reference
python3 ios.py test --reference --device "$study_simulator"
python3 ios.py run --reference --device "$study_simulator"
```

The **12 shared-model checks** and **13 UI checks** cover different boundaries. Model checks alone cannot complete the learner's view repair. UI checks observe concrete input, control values, IDs, draft/committed text, owner retention, rotation, and scene/relaunch behavior. They do not establish process restoration, full accessibility certification, or a fixed number of body evaluations.

Reference builds neither prepare nor compile Practice. Both source choices are part of the same native app project and install separately; preserve existing answers when comparing them.

## Changed request answer

A `completedMinutes` computed property can filter the full sessions collection by completion and sum its minutes. The view reads that computed value, which depends on the observed sessions. Completing session 2 yields 40; hiding completed rows still yields 40. Using visibleSessions for the total would make the displayed metric depend on a presentation filter.

**Next: Chapter 18 will introduce Swift concurrency and tests. Its lesson is planned.**

[Back to the workshop](WORKSHOP.md) · [Review the lesson](LESSON.md) · [Back to the course](../../README.md)
