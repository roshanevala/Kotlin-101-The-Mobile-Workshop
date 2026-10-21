# Project status

Updated **21 October 2026**. Keep this file short; it records delivery state rather than session history.

## Current stage

Repository foundation and course planning. Display name: **Kotlin 101 — The Mobile Workshop**. Repository: [roshanevala/Kotlin-101-The-Mobile-Workshop](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop).

Available: curriculum, technology research, public learning plan, Chapter 00 diagnostic, agent guidance, contribution templates, and a local documentation checker. A documentation CI workflow is prepared; its GitHub execution is pending publication.

Chapters 01–40, the application, and mobile CI are planned. There is no Gradle wrapper or runnable Android, iOS, or KMP project yet.

Local Git uses `main` and `origin` points to the repository above. The documentation check passed across 16 Markdown files; eight behavior checks for the checker and the workflow's initial-commit and whitespace-failure paths were verified locally. Hosted CI execution is pending the first push.

## Decisions

- Prioritize Android and Kotlin interviews, with native iOS and KMP coverage.
- Plan for 4–6 hours weekly; release small missions at a sustainable pace.
- Use the study planner as the growing app and shared logic before the shared UI comparison.
- Prefer one coding agent and targeted context. See [agent workflow](AGENT_WORKFLOW.md).

## Next deliverable

Implement Chapter 01's timer mission using its [pilot brief](LEARNING_EXPERIENCE.md#pilot-mission-for-chapter-01). Pin a reproducible Kotlin exercise build, separate starter, hints, and solution, and verify the boundary cases and run instructions.

## Publication decisions

The owner created the GitHub repository. A reuse license remains to be selected. [GitHub setup](GITHUB_SETUP.md) contains the metadata, proposed settings, and publication steps.
