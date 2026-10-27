# Project structure and chapter checkpoints

Use one repository containing small Kotlin exercises and one growing mobile application. Learners open the Kotlin exercise workspace first and the mobile workspace when they reach Android. They do not create a new Android project for every chapter.

This is the implementation structure selected for the course. The Kotlin workspace and Chapters 01–03 exercises now exist; the mobile workspace and its clients are planned.

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
  kotlin-labs/              Kotlin exercise workspace, available from Chapter 01
    gradlew
    settings.gradle.kts
    ch01-values-and-types/  Independently runnable exercise module
      src/starter/          Tracked exercise template
      src/main/             Generated learner working copy, ignored by Git
      src/solution/         Tracked reference implementation
    ch02-control-flow-and-functions/  Independent planner exercise, same source layout
    ch03-null-safety-and-errors/      Independent input exercise, same source layout
  mobile/                   Growing app workspace, added with Android chapters
    gradlew
    settings.gradle.kts
    androidApp/             Android app that grows through the course
    shared/                 Shared KMP code, introduced with the KMP chapters
    iosApp/                 Native iOS client and Xcode project, added later
  docs/
    CURRICULUM.md           Optional full course roadmap
    maintainers/            Plans, research, agent workflow, and verification
```

Both Gradle workspaces have their own wrapper and settings. Opening `kotlin-labs` must not require an Android SDK, emulator, or Xcode. Opening `mobile` introduces the Android tools when the learner needs them. Shared version conventions can be introduced when both builds exist and there is a concrete need; early lessons should not depend on custom build infrastructure.

Gradle supports related subprojects under one build. Use that capability for independently runnable exercise modules, sharing straightforward configuration where useful. [Gradle multi-project builds](https://docs.gradle.org/current/userguide/multi_project_builds.html).

## Kotlin chapters

Each chapter has a small exercise module in `kotlin-labs`, added when that chapter is implemented. The learner opens the workspace once, follows the lesson's exact run command, and edits a local working copy. A tracked template lives in `src/starter`; `preparePractice` copies it into Git-ignored `src/main` only when absent. Compilation prepares the copy automatically, so the regular run and test commands are unchanged.

Never overwrite an existing working copy during preparation, cleanup, or routine verification. Keep answers out of course commits by leaving `src/main` untracked. When migrating an existing tracked main file, obtain the template from its committed starter version and preserve the working file exactly as it is. Learning work is local by default; a separate repository or an explicit solutions folder in a personal fork can hold answers learners choose to version.

Exercise modules do not depend on earlier exercise modules. Running Chapter 05 must not compile a learner's unfinished Chapter 03 exercise. Avoid a single source folder where every previous exercise must compile successfully before the next one runs.

Keep starter and reference solution compilation separate so the starter cannot call the answer or compile against it accidentally. Verify that separation when implementing the first exercise. Reuse the resulting small pattern for later chapters.

Each lesson names the files to edit, the run action, expected output, and how to restore its starter. The chapter folder owns teaching prose; its exercise module owns executable code. Link between them instead of duplicating code in multiple maintained projects.

## Android and KMP chapters

Introduce one Android application in `mobile` and extend it through the Android lessons. The study planner gains a topic list, session editor, saved drafts, navigation, persistence, search, and reminders as the relevant concepts arrive.

Start with a small app structure. Extract modules when a lesson introduces a useful boundary, particularly the shared KMP core. Android guidance treats modularization patterns as adaptable to the application's needs. [Android modularization patterns](https://developer.android.com/topic/modularization/patterns).

Add the SwiftUI client and shared KMP module during their course stages. Later, compare one shared Compose screen with the native reference. Keep these as clients and modules of the same growing mobile project. A chapter number identifies a learning step, not a reason to duplicate the whole app.

Optional experiments can remain separate little exercises when isolation helps explain a concept. They must not become another full application learners are expected to maintain.

## Chapter checkpoints

For app-building missions, provide a runnable start checkpoint and a verified finish checkpoint. Use immutable Git tags, for example `ch13-start-v1` and `ch13-finish-v1`, when those checkpoints actually exist. A correction creates a new versioned checkpoint rather than moving a published tag.

The lesson links directly to its starting snapshot and provides a GitHub ZIP download path so Git expertise is optional. The start contains the prerequisite app behavior and the mission instructions. The worked implementation belongs in the finish checkpoint and separate solution links.

Learners can continue their own work between chapters or start from the supplied checkpoint. Recovery instructions should create a fresh copy or preserve their changes before switching versions.

Keep `main` as the latest integrated course state. Maintain chapter history through checkpoints rather than a permanent branch for every chapter. Do not create empty future modules, tags, or branches in advance.

## First implementation

Chapter 01 creates only the Kotlin workspace and its timer exercise module, alongside the chapter lesson, hints, and explained solution. Verify the run commands, boundary cases, and starter isolation. The Android workspace is introduced when the Android course stage begins.
