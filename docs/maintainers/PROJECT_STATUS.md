# Project status

Updated **11 November 2026**. Keep this file short; it records delivery state rather than session history.

## Current stage

Chapters 01–11 are implemented. Display name: **Kotlin 101 — The Mobile Workshop**. Repository: [roshanevala/Kotlin-101-The-Mobile-Workshop](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop).

Learner navigation now starts with a dedicated lesson, then the workshop and solution. Planning guides and execution records are grouped in this maintainer section; the course README stays focused on starting the course.

Exercise templates live in tracked `src/starter`; normal run and test commands create an editable, Git-ignored `src/main` copy if absent. Chapter 08 also prepares learner-authored tests in Git-ignored `src/practiceTest` from `src/starterTest`. Existing code and test answers are preserved and excluded from normal course commits.

Available: curriculum, technology research, public learning plan, Chapter 00 diagnostic, Chapters 01–11 lessons and missions, agent guidance, contribution templates, and a local documentation checker.

Chapters 01–03 have completed learner walkthroughs: the working implementations passed seven, 19, and 26 tests respectively. Chapter 03 covers safe text parsing, topic fallbacks, and explicit error contracts, with 10 verified lesson examples. The modules run independently and keep working-copy and reference compilation separate. Preparation, cleanup, and normal Git staging preserve local answers. See the [Chapter 01](verification/ch01.md), [Chapter 02](verification/ch02.md), and [Chapter 03](verification/ch03.md) verification records.

Chapter 04's learner walkthrough is complete: the working implementation passed all 26 acceptance tests on 28 October 2026. Review confirmed value equality, typed validation results, copied completion with original preservation, and exhaustive descriptions. Its reference and all eight complete lesson examples were verified separately. The learner answer remains local, and the published starter retains the exercise. See [Chapter 04 verification](verification/ch04.md).

Chapter 05 is ready for a learner attempt. Its progress report covers first-record ID deduplication, completed-minute totals, ID lookup, sorted topics, snapshots, and complexity. The reference passes 32 tests, and all eight complete lesson examples match their stated output. The working copy retains the unsolved starter; learner completion is pending. See [Chapter 05 verification](verification/ch05.md).

Chapter 06 is ready for a learner attempt. Its lesson covers lambdas, higher-order functions, extensions, receivers, scope functions, and sequences. The composable-filter and preview reference passes 26 tests; all eight complete lesson examples match their stated output. The eager-versus-lazy experiment measures callback counts, not elapsed time. The working copy retains the unsolved starter. See [Chapter 06 verification](verification/ch06.md).

Chapter 07 is ready for a learner attempt. Its generic in-memory store, reader view, planner, and report use interfaces, variance, delegation, and constructor injection. The reference passes 30 tests; all eight complete lesson examples match their stated output. Compiler checks confirm safe variance directions and reject unsafe writes. The working copy retains the unsolved starter. See [Chapter 07 verification](verification/ch07.md).

Chapter 08 is ready for a learner attempt. Its test-writing mission covers value versus identity comparisons, shallow plan copies, read-only history boundaries, and completed totals. The reference passes 32 tests: 26 shared acceptance checks plus six worked regressions. Six console examples and six lesson tests pass; seeded-fault checks demonstrate the regressions catch the original defects. The guided Kotlin foundations lessons are available; learner attempts and the independent checkpoint remain pending. See [Chapter 08 verification](verification/ch08.md).

Chapter 09 is ready for a learner attempt. It introduces suspension, blocking, scopes, `launch`, `async`, and dispatchers through sequential and concurrent dashboard requests. Its reference passes 26 tests and all eight complete lesson programs match their documented output. The module pins coroutines 1.11.0 on the existing toolchain; peak counts and controlled signals demonstrate overlap without claiming elapsed-time speedups. The working copy retains the unsolved starter. See [Chapter 09 verification](verification/ch09.md).

Chapter 10 is ready for a learner attempt. Its search-session mission preserves cancellation, distinguishes local deadlines from other cancellation, and protects replacement/stop transitions with a mutex. The reference passes 32 tests; all eight lesson programs match their stated output, including supervised failures and bounded cleanup. Six seeded defects were detected. The module reuses coroutines 1.11.0, and the real working copy retains the unsolved starter. See [Chapter 10 verification](verification/ch10.md).

Chapter 11 is ready for a learner attempt. Its cold search pipeline trims and deduplicates queries, replaces obsolete work before a new quiet period, combines independent progress, and shares current screen state in a caller-owned scope. The reference passes 36 tests; all eight complete lesson programs and both changed-request experiments match their stated output. The module reuses coroutines 1.11.0, and the real working copy retains the unsolved starter. See [Chapter 11 verification](verification/ch11.md).

GitHub workflows check documentation and a matrix of the available Kotlin exercises, including Chapter 11's starter baseline and reference checks; see [Actions](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/actions) for hosted results. Chapters 12–40 and the Android, iOS, and KMP applications remain planned.

The active learning priority is a [focused Android and Kotlin refresh](../CURRICULUM.md#focused-android-and-kotlin-refresh) for a near-term interview goal: a six-week core at the confirmed 10–12 hours/week, with Weeks 7–8 available for gaps. Chapters 01–11 are delivered; the accelerated weeks include planned targets. The full roadmap remains the longer-term course.

## Decisions

- Prioritize Android and Kotlin interviews, with native iOS and KMP coverage.
- Prioritize a six-week refresh at 10–12 hours/week, with two buffer weeks. Use practical checks to adjust pace and keep public publishing from delaying study.
- Use the study planner as the growing app and shared logic before the shared UI comparison.
- Use separate Kotlin and mobile workspaces in one repository: independent exercises first, then one growing app with chapter checkpoints. See [project structure](PROJECT_STRUCTURE.md).
- Prefer one coding agent and targeted context. See [agent workflow](AGENT_WORKFLOW.md).
- Teach concepts before the workshop. Use a short chapter entry page, one optional hints page, and a separate maintainer section.

## Next deliverable

Review the learner's pending Chapter 05–11 attempts and foundations checkpoint, then implement Chapter 12's testing-asynchronous-behavior lesson and mission. Teach test scopes, virtual time, injected dispatchers, and ordering before deterministic retry and rapid-query exercises. Verify the coroutine test library against the pinned toolchain when adding it, implement one chapter at a time, and continue improving earlier instructions from learner feedback.

## Publication decisions

The owner created the GitHub repository. A reuse license remains to be selected. [GitHub setup](GITHUB_SETUP.md) contains the metadata, proposed settings, and publication steps.
