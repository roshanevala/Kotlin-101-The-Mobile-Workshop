# Kotlin and mobile development curriculum

Build fluency in Kotlin, confidence in Android interviews, and the ability to develop and evaluate shared Android and iOS features with Kotlin Multiplatform. Chapter 00 is an introductory diagnostic, and [Chapter 01](../chapters/01-values-and-types/README.md) is the first runnable mission. Chapters 02–40 are planned and will be implemented sequentially.

The main audience is an Android developer returning to regular practice. Beginner explanations remain part of every chapter so other learners can follow without previous professional experience. Advanced chapters develop engineering judgment through measurement, failures, and design decisions.

## Course structure

Kotlin exercises come first so language mistakes can be understood without emulator or UI complexity. Concurrency precedes asynchronous application features. Native Android and iOS foundations establish what the shared layer must integrate with. KMP then connects those skills before the course adds networking, persistence, shared UI, and production concerns.

Chapters are topic groups, not weekly deadlines. Split each chapter into sessions that fit the weekly time budget. A returning developer may move quickly through familiar material after demonstrating the exit criteria.

The [learning experience and public release plan](maintainers/LEARNING_EXPERIENCE.md) turns these chapters into small missions with prediction exercises, optional hints, a Bug Museum entry, and working outcomes. A single chapter may contain several missions. The proposed public release starts with Chapters 00–03 and expands as lessons are implemented and verified.

Follow the [project structure and checkpoint plan](maintainers/PROJECT_STRUCTURE.md): one repository, independently runnable Kotlin exercise modules, and one growing mobile app. Kotlin exercises use their own workspace before Android setup is introduced. Mobile missions provide start and finish checkpoints rather than a new Android project per chapter.

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

For 4–6 hours per week, begin with this proposed allocation:

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
