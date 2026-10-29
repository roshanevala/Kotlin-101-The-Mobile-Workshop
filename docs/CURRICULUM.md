# Kotlin and mobile development curriculum

Build fluency in Kotlin, confidence in Android interviews, and the ability to develop and evaluate shared Android and iOS features with Kotlin Multiplatform. Chapter 00 is an introductory diagnostic. [Available lessons](../README.md#available-chapters) cover Chapters 01–05, from values and types through collections and complexity. Chapters 06–40 are planned and will be implemented sequentially.

The main audience is an Android developer returning to regular practice. For a near-term interview refresh, follow the [focused route](#focused-android-and-kotlin-refresh). Beginner explanations remain part of every chapter so other learners can follow without previous professional experience. Advanced chapters develop engineering judgment through measurement, failures, and design decisions.

## Course structure

Kotlin exercises come first so language mistakes can be understood without emulator or UI complexity. Concurrency precedes asynchronous application features. Native Android and iOS foundations establish what the shared layer must integrate with. KMP then connects those skills before the course adds networking, persistence, shared UI, and production concerns.

Chapters are topic groups, not weekly deadlines. Split each chapter into sessions that fit the weekly time budget. A returning developer may move quickly through familiar material after demonstrating the exit criteria.

The [learning experience and public release plan](maintainers/LEARNING_EXPERIENCE.md) turns these chapters into small missions with prediction exercises, optional hints, a Bug Museum entry, and working outcomes. A single chapter may contain several missions. The proposed public release starts with Chapters 00–03 and expands as lessons are implemented and verified.

Follow the [project structure and checkpoint plan](maintainers/PROJECT_STRUCTURE.md): one repository, independently runnable Kotlin exercise modules, and one growing mobile app. Kotlin exercises use their own workspace before Android setup is introduced. Mobile missions provide start and finish checkpoints rather than a new Android project per chapter.

## Focused Android and Kotlin refresh

For a returning Android developer with **10–12 hours per week**, aim for a **six-week core: 60–72 hours** of learning, building, and interview practice. Keep **Weeks 7–8 as a buffer** for gaps; using all eight weeks gives an **80–96 hour planning budget**. This route selects core topics from the full course; completing a week's selected work does not mean completing every mapped chapter. Treat the schedule as a target and use the practical checks below to adjust it.

Start after Chapters 01–03 or demonstrate their basics through the diagnostic. Keep the completed exercises for occasional recall. The next coding step is Chapter 04's modeling essentials, followed by Chapter 05's collections essentials.

| Week | Focus and selected chapters | Practical check |
| --- | --- | --- |
| 1 | Kotlin modeling, collections, lambdas, interfaces, and unit testing: selected essentials from 04–08 | Model and summarize study sessions, test a rule with a fake, and explain collection choices and one Kotlin pitfall |
| 2 | Suspension, dispatchers, structured concurrency, cancellation, Flow, StateFlow, and asynchronous tests: selected 09–12 | Build a small cancellable search with fake data, test its state changes, and explain why `suspend` does not select a thread |
| 3 | Android setup, lifecycle, Compose state, state hoisting, and effects: selected 13–14 | Build a study list and editor with fake data; explain state ownership, recreation, and process death |
| 4 | ViewModel, repositories, lifecycle-aware collection, and one real data source: selected 15 and either 23 or 24 | Connect the feature to a small HTTP path or local persistence; test loading, empty, content, error, and recovery behavior |
| 5 | Android debugging and KMP build and sharing boundaries: selected 19–20 and 31–32 | Diagnose one feature bug and explain common versus platform code; after the Android checks pass, move one tested rule into a small shared module |
| 6 | Coding, project explanation, and mock interviews: selected 38–39 | Attempt two mock interviews, solve an unseen Kotlin problem, and implement a changed feature requirement without the solution |
| 7–8, if needed | Repair gaps identified in checkpoints and mocks; extend toward role-specific Android topics or native iOS prerequisites | Repeat the failed task independently and explain the fix; choose the next topic from demonstrated gaps |

Start short coding and explanation practice in Week 1 and continue every week. Pick two or three small string, array, map, or set problems as their prerequisites are covered. Explain the approach and complexity, then revisit one mistake. From Week 2, use a short weekly mock to practise a Kotlin or Android explanation. Week 6 consolidates practice already underway.

Use one growing Android feature and deterministic fake data first. Week 4 adds one real data path; richer offline synchronization and a second storage or networking stack can follow later. Learn any selected prerequisite before using it, even when its full chapter is scheduled for a later course stage.

Keep the KMP lab bounded to one shared rule and its tests; give unresolved Android gaps priority. Native Swift and SwiftUI depth, iOS integration, shared UI comparisons, advanced runtime details, and production release work continue through the full course afterward. A real iOS client follows its Swift and toolchain prerequisites. The focused route preserves those learning goals while concentrating the six-week core on Kotlin and Android.

Use this weekly budget, shifting time toward mocks in Week 6:

| Activity | Time per week |
| --- | --- |
| Learn concepts and experiment with examples | 2 hours |
| Build the mission, test, and debug | 5 hours |
| Coding problems and interview explanations | 2 hours |
| Recall earlier work and review mistakes | 1 hour |
| Extra practice on the weakest area | Up to 2 hours |

Public posts and presentation polish are optional; repository write-ups should reuse the work rather than consume the study budget.

Only Chapters 01–05 currently have complete course lessons and runnable missions. The weeks above include planned learning targets. Build the next compact mission one at a time, and use the relevant official exercises or documentation while further course material is being prepared. The [project status](maintainers/PROJECT_STATUS.md) records what has actually been delivered and checked.

Assess progress after Weeks 2, 4, and 6. Before moving past a topic, rebuild its small example, explain a decision, and handle a changed requirement. If a checkpoint exposes a gap, use the next practice block to address it, reduce an optional task, or use Weeks 7–8. Familiar material can move faster when these checks already pass.

The intended result is a Kotlin and Android refresh supported by runnable code, tests, debugging examples, and project explanations. Interview readiness depends on the target role and mock performance; the calendar alone does not establish it.

The selected Android topics follow the official [architecture recommendations](https://developer.android.com/topic/architecture/recommendations) and [Compose state guidance](https://developer.android.com/develop/ui/compose/state). Asynchronous work follows the [Kotlin coroutines guide](https://kotlinlang.org/docs/coroutines-guide.html); the KMP lab starts with [sharing business logic](https://developer.android.com/kotlin/multiplatform). The six-week scope, buffer, and time budget are this workshop's planning choices.

## Part 0 Starting point

| Chapter | Topics | Practical outcome |
| --- | --- | --- |
| 00 Starting point and learning habits | Current skills, interview reflection, KMP mental model, study routine | A baseline assessment and a first code prediction exercise |

## Part 1 Kotlin foundations

| Chapter | Topics | Practical outcome |
| --- | --- | --- |
| 01 Values and types | `main`, `val`, `var`, inference, numbers, strings, expressions, compilation | A study time calculator with boundary cases |
| 02 Control flow and functions | Conditions, `when`, loops, ranges, named and default arguments, pure functions | Study session rules and input validation |
| 03 Null safety and errors | Nullable types, smart casts, safe calls, Elvis operator, exceptions and explicit error models | Safe parsing of incomplete learner input |
| 04 Classes and modeling | Constructors, properties, visibility, data classes, enums, sealed types, equality and copying | Topic and session models with meaningful states |
| 05 Collections and complexity | Lists, sets, maps, mutability, transformations, grouping, sorting and Big O | Progress reports, duplicate detection and indexed lookup |
| 06 Functions as values | Lambdas, higher order functions, extensions, receivers, scope functions and sequences | Composable filters and a measured eager versus lazy example |
| 07 Abstraction and dependencies | Interfaces, composition, inheritance, generics, variance, delegation and manual injection | Replaceable in-memory storage with a clear contract |
| 08 Tests and Kotlin pitfalls | Unit test structure, edge cases, `==` versus `===`, shallow copies, read-only collections | Tests that reveal realistic modeling bugs |

**Checkpoint:** implement a small command line study tracker from a written requirement, test its behavior, and explain collection choices. Solve a related map or set problem without copying the lesson.

## Part 2 Coroutines and streams

| Chapter | Topics | Practical outcome |
| --- | --- | --- |
| 09 Suspension and execution | Suspension versus blocking, threads, scopes, dispatchers, `launch`, `async` | Compare sequential and concurrent fake data requests |
| 10 Structured concurrency | Job hierarchy, cancellation, timeouts, exceptions, supervision and shared state | Cancel an obsolete search without swallowing cancellation |
| 11 Flow and observable state | Cold flows, `StateFlow`, `SharedFlow`, combining, buffering and latest operators | Debounced topic search and observable progress |
| 12 Testing asynchronous behavior | Test scopes, virtual time, injected dispatchers, ordering and race conditions | Deterministic tests for retries and rapid search changes |

**Checkpoint:** explain why `suspend` does not automatically move work off the main thread. Build and test a cancellable search pipeline.

## Part 3 Android foundations

| Chapter | Topics | Practical outcome |
| --- | --- | --- |
| 13 Android runtime and lifecycle | Application components, Context, intents, permissions, configuration changes, process death and resources | Diagnose lost session state after recreation |
| 14 Jetpack Compose | Composition, state, recomposition, state hoisting, effects, lists, layouts and accessibility | A study list and session editor with clear state ownership |
| 15 Android app architecture | UI and data layers, repositories, ViewModel, lifecycle-aware collection, navigation and saved state | A tested Android feature with loading, empty and error states |

**Checkpoint:** implement an Android feature from a specification and explain what survives rotation, navigation, and process death. Start Android interview practice here rather than waiting for KMP completion.

## Part 4 Native iOS foundations

| Chapter | Topics | Practical outcome |
| --- | --- | --- |
| 16 Swift for Kotlin developers | Optionals, structs, classes, enums, protocols, generics, value semantics and ARC | Equivalent Swift models and a retain-cycle exercise |
| 17 SwiftUI and app lifecycle | State ownership, observation, view identity, navigation, scene lifecycle and accessibility | A small native study list and detail screen |
| 18 Swift concurrency and tests | Tasks, cancellation, actors, MainActor, Sendable, Swift Testing and XCTest roles | A tested native asynchronous screen |

**Checkpoint:** explain differences between Kotlin and Swift value behavior and memory management. Keep the iOS client small enough to understand completely.

## Part 5 Kotlin Multiplatform foundations

| Chapter | Topics | Practical outcome |
| --- | --- | --- |
| 19 Project and build model | Gradle wrapper, Kotlin DSL, version catalog, targets, source sets and hierarchy | A minimal shared module built for Android and an iOS simulator |
| 20 Shared boundaries | Common APIs, platform dependencies, interfaces, `expect` and `actual`, dependency direction | Move tested study rules into common code |
| 21 Swift integration | Framework integration, exported API shapes, nullability, errors, asynchronous bridging and cancellation | Consume the shared study core from SwiftUI |
| 22 Shared presentation decisions | State holders, lifecycle ownership, shared versus platform ViewModels, injection and test doubles | Compare two small presentation designs and record the tradeoff |

**Checkpoint:** run the same business rules on both platforms, test both compilation targets, and identify which behavior still belongs to each platform. Inspect actual generated Swift APIs for the pinned toolchain.

## Part 6 Networking and local data

| Chapter | Topics | Practical outcome |
| --- | --- | --- |
| 23 HTTP and serialization | HTTP semantics, Ktor client engines, JSON, DTO mapping, timeouts and error handling | Load study content through a shared client with fake responses |
| 24 SQL and persistence | SQL basics, Room KMP, schema design, transactions, migrations and preferences | Persist topics and sessions on both platforms |
| 25 Offline behavior and synchronization | Local source of truth, refresh, pending changes, idempotency, conflicts and backoff | Define and test offline edits and recovery behavior |
| 26 Pagination and search | Page versus cursor APIs, deduplication, caching, request cancellation and invalidation | A searchable library that handles refresh and additional pages |

Use bundled fixtures and an in-process fake or mock server before an optional external service. Core lessons should run without paid APIs, production credentials, or a custom deployed backend.

**Checkpoint:** demonstrate the feature offline, during a failed refresh, and after recovery. Explain the difference between a repository contract, a database, and a cache.

## Part 7 Shared UI and platform features

| Chapter | Topics | Practical outcome |
| --- | --- | --- |
| 27 Compose Multiplatform | Moving a selected UI to common code, resources, navigation and platform conventions | Run one shared screen on Android and iOS beside the SwiftUI reference |
| 28 UI quality across platforms | Adaptive layouts, text scaling, localization, keyboard, back behavior and screen readers | A usability and accessibility review on both platforms |
| 29 Platform capabilities | Notifications, reminders, permission denial, deep links, background limits and platform adapters | Add a revision reminder with explicit platform behavior |
| 30 Existing app integration | Java interoperability, Views and Fragments, UIKit hosting, incremental adoption | Integrate a small legacy component without rewriting the feature |

**Checkpoint:** justify whether a particular feature should share UI, share logic only, or remain platform-specific. Use working examples to support the decision.

## Part 8 Production engineering

| Chapter | Topics | Practical outcome |
| --- | --- | --- |
| 31 Testing across boundaries | Common and platform unit tests, repository contracts, database integration, Compose and iOS UI tests | A focused test suite covering failures at each boundary |
| 32 Performance and memory | Profiling, main thread work, recomposition, allocations, leaks, startup and binary size | Measure and fix one Android and one iOS performance issue |
| 33 Security and privacy | Threat modeling, credential storage, TLS, redacted logging, permissions and data deletion | Review and improve the app's handling of sensitive data |
| 34 Build engineering and CI | Build variants, dependencies, compatible toolchains, CI caching, macOS runners and reproducibility | Automated shared checks, Android builds and iOS simulator checks |
| 35 Release and operations | Signing concepts, distribution, crash reports, observability, migrations and rollback planning | A local release candidate and a written release runbook |

Store uploads, paid services, public publication, and signing credentials are separate real-world deployment steps. The course can teach and verify a local release without requiring them.

## Part 9 Advanced practice and interview readiness

| Chapter | Topics | Practical outcome |
| --- | --- | --- |
| 36 Advanced Kotlin and runtime behavior | Inline and reified types, value classes, JVM bytecode, boxing, type erasure, Java interop and Native runtime differences | Inspect generated behavior and explain when an abstraction costs something |
| 37 Architecture and system design | Modularity, API contracts, scale, offline design, synchronization and migration tradeoffs | Design a larger study platform with explicit constraints |
| 38 Coding interviews | Arrays, strings, maps, stacks, queues, linked structures, trees, graphs, search and introductory dynamic programming | A curated set of Kotlin solutions with complexity analysis |
| 39 Debugging and interview communication | Code review, unfamiliar code, incident analysis, timed feature work and behavioral examples | Mock interviews and evidence-based project stories |
| 40 Independent capstone | Requirements, implementation, tests, accessibility, performance, documentation and review | An independently built Android and iOS feature plus a portfolio walkthrough |

Small coding and explanation exercises recur from Chapter 01. Chapter 38 consolidates these skills; it is not the first exposure to problem solving.

## Chapter format

The learner follows **Lesson → Workshop → Solution**. The chapter README is a short entry page. `LESSON.md` teaches the concepts before `WORKSHOP.md` asks the learner to apply them. Optional hints live in one page with collapsed sections; execution records live with the maintainer documentation.

Every implemented chapter should contain:

1. Prerequisites, learning outcomes, and the problem being solved.
2. A short explanation with a concrete mental model.
3. A minimal runnable example with exact commands and expected behavior.
4. A guided change to the example or growing app.
5. An independent exercise, a debugging task, and edge cases.
6. A solution kept separately from the exercise, explaining decisions.
7. Relevant automated tests and an honest record of which targets were checked.
8. Interview questions with reasoning and follow-up prompts.
9. A completion checklist and a small retrieval exercise for a later session.
10. Official references and the toolchain used for the example.

After the lesson, use the workshop sequence of see the problem, predict, explore, build, investigate, explain, and revisit. Keep the ordinary topic title for navigation, and use the workshop's opening problem to add personality. Challenges should have clear prerequisites and acceptance criteria. Public sharing and optional stretch tasks are separate from completion requirements.

Early pure Kotlin labs should be runnable without an emulator or Xcode. Android lessons must remain usable by learners without a Mac; iOS build and execution steps require a Mac with Xcode.

## Weekly rhythm

The focused interview refresh uses the 10–12 hour budget in its [focused route](#focused-android-and-kotlin-refresh). For learners taking the full course at a slower 4–6 hours per week, begin with this proposed allocation:

| Activity | Time |
| --- | --- |
| Recall earlier topics and explain one interview answer aloud | 30 minutes |
| Read and experiment with the next lesson | 60–90 minutes |
| Implement the independent exercise or feature | 90–120 minutes |
| Test, debug, solve a small coding problem, and record learning | 60–120 minutes |

Advance when you can rebuild the exercise, explain it, and handle a changed requirement. Revisit mistakes in the next week and again a few weeks later. Adjust chapter size after the first two weeks rather than promising a fixed completion date for the entire course.

## Initial delivery milestones

- **Milestone 1:** Chapters 01–08, runnable Kotlin exercises and meaningful tests.
- **Milestone 2:** Chapters 09–15, a tested Android feature and an initial interview practice set.
- **Milestone 3:** Chapters 16–22, a native iOS reference and shared KMP business logic.
- **Milestone 4:** Chapters 23–30, persistent data and a comparison of native and shared UI.
- **Milestone 5:** Chapters 31–40, measured quality improvements, independent work, and mock interviews.

## Keeping the course useful for others

Use numbered chapter folders, searchable topic names, prerequisite links, small examples, reproducible commands, and separate exercises and solutions. As code is introduced, make each exercise runnable independently or provide a documented checkpoint so learners do not have to reproduce every earlier change.

Before public release, decide the code and documentation licenses, add contribution guidance, and verify the documented setup from a clean checkout. Record dependency upgrades separately from teaching changes so older lessons remain understandable.
