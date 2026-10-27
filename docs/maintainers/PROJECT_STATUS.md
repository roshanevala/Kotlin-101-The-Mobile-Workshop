# Project status

Updated **27 October 2026**. Keep this file short; it records delivery state rather than session history.

## Current stage

Chapters 01–03 are implemented. Display name: **Kotlin 101 — The Mobile Workshop**. Repository: [roshanevala/Kotlin-101-The-Mobile-Workshop](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop).

Learner navigation now starts with a dedicated lesson, then the workshop and solution. Planning guides and execution records are grouped in this maintainer section; the course README stays focused on starting the course.

Exercise templates live in tracked `src/starter`; normal run and test commands create an editable, Git-ignored `src/main` copy if absent. Existing learner work is preserved, and normal course commits exclude personal answers.

Available: curriculum, technology research, public learning plan, Chapter 00 diagnostic, Chapters 01–03 lessons and missions, agent guidance, contribution templates, and a local documentation checker.

Chapters 01–03 have completed learner walkthroughs: the working implementations passed seven, 19, and 26 tests respectively. Chapter 03 covers safe text parsing, topic fallbacks, and explicit error contracts, with 10 verified lesson examples. The modules run independently and keep working-copy and reference compilation separate. Preparation, cleanup, and normal Git staging preserve local answers. See the [Chapter 01](verification/ch01.md), [Chapter 02](verification/ch02.md), and [Chapter 03](verification/ch03.md) verification records.

GitHub workflows check documentation and a matrix of the available Kotlin exercises; see [Actions](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/actions) for hosted results. Chapters 04–40 and the Android, iOS, and KMP applications remain planned.

## Decisions

- Prioritize Android and Kotlin interviews, with native iOS and KMP coverage.
- Plan for 4–6 hours weekly; release small missions at a sustainable pace.
- Use the study planner as the growing app and shared logic before the shared UI comparison.
- Use separate Kotlin and mobile workspaces in one repository: independent exercises first, then one growing app with chapter checkpoints. See [project structure](PROJECT_STRUCTURE.md).
- Prefer one coding agent and targeted context. See [agent workflow](AGENT_WORKFLOW.md).
- Teach concepts before the workshop. Use a short chapter entry page, one optional hints page, and a separate maintainer section.

## Next deliverable

Implement Chapter 04's classes and modeling lesson and mission using the existing lesson-first layout and independent working-copy setup. Continue improving earlier instructions from learner feedback.

## Publication decisions

The owner created the GitHub repository. A reuse license remains to be selected. [GitHub setup](GITHUB_SETUP.md) contains the metadata, proposed settings, and publication steps.
