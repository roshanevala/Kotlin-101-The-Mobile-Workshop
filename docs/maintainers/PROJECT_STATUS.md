# Project status

Updated **21 November 2026**. Keep this file short; it records delivery state rather than session history.

## Current stage

Chapters 01–21 are implemented. Display name: **Kotlin 101 — The Mobile Workshop**. Repository: [roshanevala/Kotlin-101-The-Mobile-Workshop](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop).

Learner navigation now starts with a dedicated lesson, then the workshop and solution. Planning guides and execution records are grouped in this maintainer section; the course README stays focused on starting the course.

Kotlin exercise templates live in tracked `src/starter`; normal run and test commands create an editable, Git-ignored `src/main` copy if absent. Chapters 08 and 12 also prepare learner-authored tests in Git-ignored `src/practiceTest` from `src/starterTest`. Existing code and test answers are preserved and excluded from normal course commits.

Available: curriculum, technology research, public learning plan, Chapter 00 diagnostic, Chapters 01–21 lessons and missions, agent guidance, contribution templates, and a local documentation checker.

Chapters 01–03 have completed learner walkthroughs: the working implementations passed seven, 19, and 26 tests respectively. Chapter 03 covers safe text parsing, topic fallbacks, and explicit error contracts, with 10 verified lesson examples. The modules run independently and keep working-copy and reference compilation separate. Preparation, cleanup, and normal Git staging preserve local answers. See the [Chapter 01](verification/ch01.md), [Chapter 02](verification/ch02.md), and [Chapter 03](verification/ch03.md) verification records.

Chapter 04's learner walkthrough is complete: the working implementation passed all 26 acceptance tests on 28 October 2026. Review confirmed value equality, typed validation results, copied completion with original preservation, and exhaustive descriptions. Its reference and all eight complete lesson examples were verified separately. The learner answer remains local, and the published starter retains the exercise. See [Chapter 04 verification](verification/ch04.md).

Chapter 05 is ready for a learner attempt. Its progress report covers first-record ID deduplication, completed-minute totals, ID lookup, sorted topics, snapshots, and complexity. The reference passes 32 tests, and all eight complete lesson examples match their stated output. The working copy retains the unsolved starter; learner completion is pending. See [Chapter 05 verification](verification/ch05.md).

Chapter 06 is ready for a learner attempt. Its lesson covers lambdas, higher-order functions, extensions, receivers, scope functions, and sequences. The composable-filter and preview reference passes 26 tests; all eight complete lesson examples match their stated output. The eager-versus-lazy experiment measures callback counts, not elapsed time. The working copy retains the unsolved starter. See [Chapter 06 verification](verification/ch06.md).

Chapter 07 is ready for a learner attempt. Its generic in-memory store, reader view, planner, and report use interfaces, variance, delegation, and constructor injection. The reference passes 30 tests; all eight complete lesson examples match their stated output. Compiler checks confirm safe variance directions and reject unsafe writes. The working copy retains the unsolved starter. See [Chapter 07 verification](verification/ch07.md).

Chapter 08 is ready for a learner attempt. Its test-writing mission covers value versus identity comparisons, shallow plan copies, read-only history boundaries, and completed totals. The reference passes 32 tests: 26 shared acceptance checks plus six worked regressions. Six console examples and six lesson tests pass; seeded-fault checks demonstrate the regressions catch the original defects. The guided Kotlin foundations lessons are available; learner attempts and the independent checkpoint remain pending. See [Chapter 08 verification](verification/ch08.md).

Chapter 09 is ready for a learner attempt. It introduces suspension, blocking, scopes, `launch`, `async`, and dispatchers through sequential and concurrent dashboard requests. Its reference passes 26 tests and all eight complete lesson programs match their documented output. The module pins coroutines 1.11.0 on the existing toolchain; peak counts and controlled signals demonstrate overlap without claiming elapsed-time speedups. The working copy retains the unsolved starter. See [Chapter 09 verification](verification/ch09.md).

Chapter 10 is ready for a learner attempt. Its search-session mission preserves cancellation, distinguishes local deadlines from other cancellation, and protects replacement/stop transitions with a mutex. The reference passes 32 tests; all eight lesson programs match their stated output, including supervised failures and bounded cleanup. Six seeded defects were detected. The module reuses coroutines 1.11.0, and the real working copy retains the unsolved starter. See [Chapter 10 verification](verification/ch10.md).

Chapter 11 is ready for a learner attempt. Its cold search pipeline trims and deduplicates queries, replaces obsolete work before a new quiet period, combines independent progress, and shares current screen state in a caller-owned scope. The reference passes 36 tests; all eight complete lesson programs and both changed-request experiments match their stated output. The module reuses coroutines 1.11.0, and the real working copy retains the unsolved starter. See [Chapter 11 verification](verification/ch11.md).

Chapter 12 is ready for a learner attempt. Its retrying-search mission adds coroutines-test 1.11.0 only to test sources, with one scheduler for injected dispatchers. The reference passes 32 tests: 26 shared acceptance checks plus six worked virtual-time regressions. All six lesson tests pass, and selected defects demonstrate retry boundaries, replacement ordering, and cancellation checks. The production and test working copies retain the starter; learner regression-writing and the independent Part 2 checkpoint remain pending. See [Chapter 12 verification](verification/ch12.md).

Chapter 13 is ready for a learner attempt. The first growing Android workspace now lives in `mobile`, independently of the Kotlin labs. Its small platform Activity/XML draft teaches saved Bundle values before Compose; practice/reference flavors isolate the Activity answers. The reference passes 10 device tests on Android 16/API 36, and a separate background-process-kill/Recents-return observation restored both values with a new PID. Build, lint, preservation, variant-isolation, and selected-defect checks were executed locally. The real practice Activity remains unsolved. See [Chapter 13 verification](verification/ch13.md).

Chapter 14 is ready for a learner attempt. The existing Android module now launches a Compose study list/editor with a single state owner, saved draft/selection/topics/completion, controlled child callbacks, and transient keyed feedback. Its reference passes 32 device checks: 22 new Compose contracts and ten retained lifecycle checks. The new answer is prepared separately without replacing Chapter 13's Activity answer; the immutable old checkpoint remains available. See [Chapter 14 verification](verification/ch14.md).

Chapter 15 is ready for a learner attempt. The same app now launches a repository-backed catalogue with explicit loading/content/empty/error states, an Activity-owned ViewModel, lifecycle-aware collection, list/detail navigation, and saved per-ID draft notes. Its reference passes 20 local tests and 46 device checks, including the earlier 32. All three answers are prepared independently and preserved; earlier launcher checkpoints remain immutable. See [Chapter 15 verification](verification/ch15.md).

Chapter 16 is ready for a learner attempt. A separate native Swift CLI lab teaches optional input, associated-value validation, value plans, protocol/generic helpers, and a stored closure's ARC ownership. Its reference passes 34 behavioral checks with normal and optimized compilation; six complete lesson programs match their stated outputs. The wrapper prepares a missing ignored answer and isolates practice/reference compilation without introducing SwiftPM dependencies or an iOS app. See [Chapter 16 verification](verification/ch16.md).

Chapter 17 is ready for a learner attempt. The native iOS client now lives in `mobile/iosApp`, with isolated practice/reference app and UI-test schemes. Its root-owned observable store, per-ID draft bindings, stable navigation, filters, and scene contract pass 12 shared-model checks and 13 simulator UI tests. Background/foreground and rotation preserve the in-memory owner; a fresh process deliberately starts again. Earlier answers remain intact. See [Chapter 17 verification](verification/ch17.md).

Chapter 18 adds a review-prompt sheet to the existing iOS client. Learners repair a main-actor loader and write a regression for two overlapping requests for the same topic. The simulator runs unit tests with a controlled source and an XCTest flow for failure and retry. The source checkpoint [ch18-v1](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch18-v1) includes both schemes. The Part 4 independent checkpoint is ready for a learner attempt. See [Chapter 18 verification](verification/ch18.md).

Chapter 19 introduces the first shared KMP library. The learner moves a study label from Android-only sources into common code. The repaired module builds an Android AAR and an arm64 iOS simulator framework, and the same three behavioral tests pass on each target. The clients consume their existing native code. The published [ch19-v1 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch19-v1) contains the starter and worked solution. See [Chapter 19 verification](verification/ch19.md).

Chapter 20 ports the Chapter 03 topic and duration rules into a common session planner. Practice and reference use an injected ID source, with Android and iOS implementations supplied by an `expect`/`actual` factory. The [ch20-v1 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch20-v1) contains both variants. [Chapter 20 verification](verification/ch20.md)

Chapter 21 links the shared Kotlin core into both SwiftUI schemes. Learners repair the Swift form adapter, then trace how confirmation, a declared error, and cancellation cross the framework boundary. The [ch21-v1 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch21-v1) includes both schemes. [Chapter 21 verification](verification/ch21.md)

GitHub workflows check documentation and a matrix of the available Kotlin exercises, including Chapter 12's starter baseline and reference checks; see [Actions](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/actions) for hosted results. The Android workflow builds both APKs, compiles device tests, runs the 20 architecture unit checks, and lints the reference; it does not run an emulator. Chapters 22–40 remain planned. The [ch13-v1 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch13-v1) pins the original View-based starter and reference. The [ch14-v2 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch14-v2) pins the Compose starter and reference. The [ch15-v1 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch15-v1) pins the architecture starter and reference. Each immutable source snapshot includes both build variants. The published [ch17-v2 checkpoint](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch17-v2) pins the native iOS starter/reference schemes, including the hosted-compiler compatibility correction; `ch17-v1` remains immutable. The separate Swift workflow checks the Chapter 16 starter, reference in both optimization modes, and examples; its [first hosted run passed](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/actions/runs/43851046977). The iOS workflow compiles both variants and their test bundles and runs model checks. The [Chapter 17 hosted run passed](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/actions/runs/44116383241) on Xcode 26.6/Swift 6.3.3. Simulator UI execution remains locally verified.

The active learning priority is a [focused Android and Kotlin refresh](../CURRICULUM.md#focused-android-and-kotlin-refresh) for a near-term interview goal: a six-week core at the confirmed 10–12 hours/week, with Weeks 7–8 available for gaps. Chapters 01–21 are delivered; the accelerated weeks include planned targets. The full roadmap remains the longer-term course.

## Decisions

- Prioritize Android and Kotlin interviews, with native iOS and KMP coverage.
- Prioritize a six-week refresh at 10–12 hours/week, with two buffer weeks. Use practical checks to adjust pace and keep public publishing from delaying study.
- Use the study planner as the growing app and shared logic before the shared UI comparison.
- Keep independent Kotlin/Swift exercise workspaces separate from the growing mobile app and its chapter checkpoints. See [project structure](PROJECT_STRUCTURE.md).
- Prefer one coding agent and targeted context. See [agent workflow](AGENT_WORKFLOW.md).
- Teach concepts before the workshop. Use a short chapter entry page, one optional hints page, and a separate maintainer section.

## Next deliverable

Next is Chapter 22, shared presentation decisions. Compare native and shared ownership of the planner’s presentation state and test how each handles a lifecycle change. Learner attempts and independent checkpoints remain open.

## Publication decisions

The owner created the GitHub repository. A reuse license remains to be selected. [GitHub setup](GITHUB_SETUP.md) contains the metadata, proposed settings, and publication steps.
