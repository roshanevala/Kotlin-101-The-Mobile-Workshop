# Project structure and chapter checkpoints

Use one repository containing small Kotlin/Swift exercises and one growing mobile application. Learners open the Kotlin exercise workspace first and the mobile workspace when they reach Android. They do not create a new Android project for every chapter.

This is the implementation structure selected for the course. The independent Kotlin exercises for Chapters 01–12, Android missions for Chapters 13–15, the Swift language lab for Chapter 16, and the native SwiftUI client for Chapter 17 now exist. Shared KMP remains planned.

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
    15-android-app-architecture/  Repository/ViewModel catalogue and navigation
    16-swift-for-kotlin-developers/  Native Swift models and ARC mission
    17-swiftui-and-app-lifecycle/  Native list/detail, ownership, and scene mission
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
  swift-labs/               Native Swift language exercises, available from Chapter 16
    ch16-swift-for-kotlin-developers/
      lab.py                Explicit compiler/run commands, no downloaded dependencies
      src/starter/          Tracked Swift exercise template
      src/practice/         Ignored learner answer, prepared only if absent
      src/solution/         Tracked Swift reference
      src/runner/           Fixed demonstration entry point
      src/checks/           Fixed behavioral acceptance entry point
      examples/             Six independently compiled lesson programs
  mobile/                   Growing Android workspace, available from Chapter 13
    gradlew
    settings.gradle.kts
    androidApp/             Android app that grows through the course
      src/main/             Fixed catalogue Activity, data/state, navigation, and resources
      src/starter/          Tracked Chapter 13/14 Activities and Chapter 15 ViewModel
      src/practice/         Three generated learner answers, ignored
      src/solution/         Tracked reference Activities/editor/ViewModel
      src/androidTest/      Shared device acceptance checks
      src/test/             Android local ViewModel/repository checks
    shared/                 Planned KMP code, introduced with the KMP chapters
    iosApp/                 Native iOS client, introduced in Chapter 17
      StudyLab.xcodeproj/    Separate practice/reference schemes and UI-test targets
      ios.py                Preparation, simulator builds/runs/tests, and model checks
      Sources/              Fixed App, model, and reusable controls
      Starter/              Tracked screen exercise
      Practice/             Ignored learner screen answer
      Solution/             Tracked reference screens
      Tests/                Shared model and UI acceptance checks
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

## Swift language chapter

Chapter 16 introduces a bounded Swift CLI lab under `swift-labs` before the native iOS app. Its Python wrapper invokes `xcrun swiftc` in Swift 6 language mode and supplies only the chosen practice/reference model plus a fixed entry point. Reference commands do not prepare the ignored learner answer; clean removes only compiler output. The same 34 condition-based checks run with normal/optimized compilation, and six standalone lesson programs teach the prerequisite syntax.

The local checked toolchain is Xcode 27.0/Apple Swift 6.4 on macOS/Apple silicon. There is no SwiftPM dependency, app project, simulator requirement, or connection to either Gradle workspace. Keep this a language exercise; use the separate SwiftUI client introduced in Chapter 17 and share tested rules when the KMP stage reaches that boundary. See [Swift setup](../../swift-labs/README.md).

## Android and KMP chapters

The Android application introduced in `mobile` in Chapter 13 grows through later Android lessons. The study planner gains a topic list, session editor, saved drafts, navigation, persistence, search, and reminders as the relevant concepts arrive.

Chapter 13 uses a platform Activity and XML Views to expose lifecycle mechanics before Compose. A single `androidApp` module has practice/reference flavors with distinct application IDs. Shared main contains the fixed manifest/resources/window helper but no Activity; the generated Activity lives in ignored `src/practice`, while reference builds use tracked `src/solution`. The variants never compile each other's Activity. `preparePractice` fills only a missing answer. Device tests are shared in `src/androidTest`.

Chapter 14 extends that same module with Compose and `StudyPlannerActivity`, without modifying either Chapter 13 Activity answer or its immutable tag. The current launcher is Compose; the old Activity remains internal for its ten retained checks. `prepareComposePractice` fills only the missing new answer, and `preparePractice` prepares both missing Activities. A growing practice variant compiles both files, so edited older code must still compile; reference compilation remains separate. All Chapter 13 entry pages direct that lesson to its original checkpoint. That checkpoint has 32 device checks: 22 Compose contracts and the ten old lifecycle contracts. See [mobile setup](../../mobile/README.md).

Chapter 15 adds a fixed `CatalogueActivity`, repository, state model, and list/detail navigation in shared Android main. It extracts the unchanged immutable `StudySession` type into its own file and leaves both older Activity answers intact. The new learner-owned `CatalogueViewModel.kt` is supplied separately by practice/reference; `prepareArchitecturePractice` fills only its missing answer, while `preparePractice` prepares all three missing files. The Activity owns the ViewModel outside NavHost; notes use SavedStateHandle and IDs, while the catalogue reloads. The current reference has 20 local unit checks and 46 device checks (14 new plus 32 retained). Chapter 14 entry pages route its old launcher to `ch14-v2`.

Chapter 17 introduces `mobile/iosApp` as the native client of the same growing app. Its checked-in Xcode project has independent Practice/Reference app and UI-test targets, with distinct bundle IDs and source phases. The fixed store/control/App files do not depend on Swift-lab answers or Android sources. `ios.py prepare` fills only a missing ignored `Practice/StudyScreens.swift`; reference commands do not prepare/compile it, and clean removes only build output. The current 12 model checks and 13 simulator UI checks establish the specified in-process ownership/navigation/scene behavior, not persistence. Use [iOS setup](../../mobile/iosApp/README.md).

Start with a small app structure. Extract modules when a lesson introduces a useful boundary, particularly the shared KMP core. Android guidance treats modularization patterns as adaptable to the application's needs. [Android modularization patterns](https://developer.android.com/topic/modularization/patterns).

The native SwiftUI client now exists; add the shared KMP module during its course stage. Later, compare one shared Compose screen with the native reference. Keep these as clients and modules of the same growing mobile project. A chapter number identifies a learning step, not a reason to duplicate the whole app.

Optional experiments can remain separate little exercises when isolation helps explain a concept. They must not become another full application learners are expected to maintain.

## Chapter checkpoints

For app-building missions, provide a runnable start checkpoint and a verified finish checkpoint. Use immutable Git tags when the source checkpoints are published. Chapter 13 keeps the starter and finish reference as separate build variants in one source snapshot; its published tag is `ch13-v1`. Chapters 14 and 15 use the same policy with their published `ch14-v2` and `ch15-v1` tags. Chapter 17 will use `ch17-v1` when publication is requested, including its separate starter/reference schemes. Future chapters may use distinct start/finish tags when their snapshots differ. A correction creates a new versioned checkpoint rather than moving a published tag.

During local preparation, link the tracked starter/reference sources and exact variant commands. Once published, link the immutable tag and its GitHub ZIP so Git expertise is optional; never claim an unpublished tag exists. The start contains the prerequisite app behavior and the mission instructions. The worked implementation belongs in the finish checkpoint and separate solution links.

Learners can continue their own work between chapters or start from the supplied checkpoint. Recovery instructions should create a fresh copy or preserve their changes before switching versions.

Keep `main` as the latest integrated course state. Maintain chapter history through checkpoints rather than a permanent branch for every chapter. Do not create empty future modules, tags, or branches in advance.

## First implementation

Chapter 01 creates only the Kotlin workspace and its timer exercise module, alongside the chapter lesson, hints, and explained solution. Verify the run commands, boundary cases, and starter isolation. Chapter 13 now adds the separate mobile workspace with the saved-draft mission; Chapter 17 adds the native iOS child project, while shared KMP remains later work.
