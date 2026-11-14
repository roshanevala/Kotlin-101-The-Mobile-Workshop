# Project structure and chapter checkpoints

Use one repository containing small Kotlin exercises and one growing mobile application. Learners open the Kotlin exercise workspace first and the mobile workspace when they reach Android. They do not create a new Android project for every chapter.

This is the implementation structure selected for the course. The independent Kotlin exercises for Chapters 01–12 and the Android draft/list/editor for Chapters 13–14 now exist. The iOS and shared KMP clients remain planned.

## Layout introduced gradually

```text
Kotlin-101-The-Mobile-Workshop/
  chapters/                 Learner-facing chapter material
    00-starting-point/
    01-values-and-types/
      README.md             Short chapter entry page
      LESSON.md             Concepts and small examples first
      WORKSHOP.md           Hands-on mission after the lesson
      HINTS.md              Optional hints in collapsed sections
      SOLUTION.md           Worked explanation after an attempt
    02-control-flow-and-functions/  Same lesson and workshop layout
    03-null-safety-and-errors/      Same lesson and workshop layout
    04-classes-and-modeling/        Same lesson and workshop layout
    05-collections-and-complexity/  Same lesson and workshop layout
    06-functions-as-values/         Same lesson and workshop layout
    07-abstraction-and-dependencies/ Same lesson and workshop layout
    08-tests-and-kotlin-pitfalls/    Adds learner-authored regression tests
    09-suspension-and-execution/    Introduces the coroutine library
    10-structured-concurrency/     Introduces cancellation and search ownership
    11-flow-and-observable-state/  Introduces cold streams and shared screen state
    12-testing-asynchronous-behavior/  Adds virtual-time regression practice
    13-android-runtime-and-lifecycle/  First Android draft and recreation mission
    14-jetpack-compose/        Compose study list/editor and state mission
  kotlin-labs/              Kotlin exercise workspace, available from Chapter 01
    gradlew
    settings.gradle.kts
    ch01-values-and-types/  Independently runnable exercise module
      src/starter/          Tracked exercise template
      src/main/             Generated learner working copy, ignored by Git
      src/solution/         Tracked reference implementation
    ch02-control-flow-and-functions/  Independent planner exercise, same source layout
    ch03-null-safety-and-errors/      Independent input exercise, same source layout
    ch04-classes-and-modeling/        Independent model exercise, same source layout
    ch05-collections-and-complexity/  Independent reporting exercise, same source layout
    ch06-functions-as-values/         Independent filter exercise, same source layout
    ch07-abstraction-and-dependencies/ Independent storage exercise, same source layout
    ch08-tests-and-kotlin-pitfalls/    Snapshot exercise plus editable test working copy
    ch09-suspension-and-execution/    Independent coroutine loading exercise
    ch10-structured-concurrency/     Independent search lifecycle exercise
    ch11-flow-and-observable-state/  Independent observable search/progress exercise
    ch12-testing-asynchronous-behavior/  Retry/search exercise plus editable test working copy
  mobile/                   Growing Android workspace, available from Chapter 13
    gradlew
    settings.gradle.kts
    androidApp/             Android app that grows through the course
      src/main/             Fixed manifest, resources, and window scaffolding
      src/starter/          Tracked Chapter 13/14 Activity exercises
      src/practice/         Generated learner Activities, ignored
      src/solution/         Tracked reference Activities/editors
      src/androidTest/      Shared device acceptance checks
    shared/                 Planned KMP code, introduced with the KMP chapters
    iosApp/                 Planned native iOS client and Xcode project
  docs/
    CURRICULUM.md           Optional full course roadmap
    maintainers/            Plans, research, agent workflow, and verification
```

Both Gradle workspaces have their own wrapper and settings. Opening `kotlin-labs` must not require an Android SDK, emulator, or Xcode. Opening `mobile` introduces the Android tools when the learner needs them. Shared version conventions can be introduced when both builds exist and there is a concrete need; early lessons should not depend on custom build infrastructure.

Gradle supports related subprojects under one build. Use that capability for independently runnable exercise modules, sharing straightforward configuration where useful. [Gradle multi-project builds](https://docs.gradle.org/current/userguide/multi_project_builds.html).

## Kotlin chapters

Each chapter has a small exercise module in `kotlin-labs`, added when that chapter is implemented. The learner opens the workspace once, follows the lesson's exact run command, and edits a local working copy. A tracked template lives in `src/starter`; `preparePractice` copies it into Git-ignored `src/main` only when absent. Compilation prepares the copy automatically, so the regular run and test commands are unchanged.

Chapters 08 and 12 add tracked `src/starterTest` and `src/solutionTest` files for test-writing practice. `preparePracticeTests` copies the chapter's test template to Git-ignored `src/practiceTest` only when absent; `preparePractice` prepares both the production and test files. The normal `test` task compiles the learner tests alongside fixed `src/test` acceptance checks. `checkSolution` instead compiles the worked regression tests alongside the same acceptance checks, keeping both paths independent. Preserve and exclude both answer folders during verification and staging.

Never overwrite an existing working copy during preparation, cleanup, or routine verification. Keep answers out of course commits by leaving `src/main` and both chapters' `src/practiceTest` untracked. When migrating an existing tracked main file, obtain the template from its committed starter version and preserve the working file exactly as it is. Learning work is local by default; a separate repository or an explicit solutions folder in a personal fork can hold answers learners choose to version.

Exercise modules do not depend on earlier exercise modules. Running Chapter 05 must not compile a learner's unfinished Chapter 03 exercise. Avoid a single source folder where every previous exercise must compile successfully before the next one runs.

Keep starter and reference solution compilation separate so the starter cannot call the answer or compile against it accidentally. Verify that separation when implementing the first exercise. Reuse the resulting small pattern for later chapters.

Each lesson names the files to edit, the run action, expected output, and how to restore its starter. The chapter folder owns teaching prose; its exercise module owns executable code. Link between them instead of duplicating code in multiple maintained projects.

## Android and KMP chapters

The Android application introduced in `mobile` in Chapter 13 grows through later Android lessons. The study planner gains a topic list, session editor, saved drafts, navigation, persistence, search, and reminders as the relevant concepts arrive.

Chapter 13 uses a platform Activity and XML Views to expose lifecycle mechanics before Compose. A single `androidApp` module has practice/reference flavors with distinct application IDs. Shared main contains the fixed manifest/resources/window helper but no Activity; the generated Activity lives in ignored `src/practice`, while reference builds use tracked `src/solution`. The variants never compile each other's Activity. `preparePractice` fills only a missing answer. Device tests are shared in `src/androidTest`.

Chapter 14 extends that same module with Compose and `StudyPlannerActivity`, without modifying either Chapter 13 Activity answer or its immutable tag. The current launcher is Compose; the old Activity remains internal for its ten retained checks. `prepareComposePractice` fills only the missing new answer, and `preparePractice` prepares both missing Activities. A growing practice variant compiles both files, so edited older code must still compile; reference compilation remains separate. All Chapter 13 entry pages direct that lesson to its original checkpoint. The current 32 device checks include 22 Compose contracts and the ten old lifecycle contracts. See [mobile setup](../../mobile/README.md).

Start with a small app structure. Extract modules when a lesson introduces a useful boundary, particularly the shared KMP core. Android guidance treats modularization patterns as adaptable to the application's needs. [Android modularization patterns](https://developer.android.com/topic/modularization/patterns).

Add the SwiftUI client and shared KMP module during their course stages. Later, compare one shared Compose screen with the native reference. Keep these as clients and modules of the same growing mobile project. A chapter number identifies a learning step, not a reason to duplicate the whole app.

Optional experiments can remain separate little exercises when isolation helps explain a concept. They must not become another full application learners are expected to maintain.

## Chapter checkpoints

For app-building missions, provide a runnable start checkpoint and a verified finish checkpoint. Use immutable Git tags when the source checkpoints are published. Chapter 13 keeps the starter and finish reference as separate build variants in one source snapshot; its published tag is `ch13-v1`. Chapter 14 uses the same policy with its published `ch14-v1` tag. Future chapters may use distinct start/finish tags when their snapshots differ. A correction creates a new versioned checkpoint rather than moving a published tag.

During local preparation, link the tracked starter/reference sources and exact variant commands. Once published, link the immutable tag and its GitHub ZIP so Git expertise is optional; never claim an unpublished tag exists. The start contains the prerequisite app behavior and the mission instructions. The worked implementation belongs in the finish checkpoint and separate solution links.

Learners can continue their own work between chapters or start from the supplied checkpoint. Recovery instructions should create a fresh copy or preserve their changes before switching versions.

Keep `main` as the latest integrated course state. Maintain chapter history through checkpoints rather than a permanent branch for every chapter. Do not create empty future modules, tags, or branches in advance.

## First implementation

Chapter 01 creates only the Kotlin workspace and its timer exercise module, alongside the chapter lesson, hints, and explained solution. Verify the run commands, boundary cases, and starter isolation. Chapter 13 now adds the separate mobile workspace with the saved-draft mission; shared KMP and iOS workspaces remain later additions.
