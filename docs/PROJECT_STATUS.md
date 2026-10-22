# Project status

Updated **22 October 2026**. Keep this file short; it records delivery state rather than session history.

## Current stage

Chapter 01's timer mission is implemented. Display name: **Kotlin 101 — The Mobile Workshop**. Repository: [roshanevala/Kotlin-101-The-Mobile-Workshop](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop).

Available: curriculum, technology research, public learning plan, Chapter 00 diagnostic, Chapter 01 mission, agent guidance, contribution templates, and a local documentation checker.

Chapter 01 has a runnable Kotlin workspace, separate starter and reference, seven acceptance tests, progressive hints, and a lesson. Local execution and starter/solution isolation passed; see its [verification record](../chapters/01-values-and-types/VERIFICATION.md). GitHub workflows check documentation and the Chapter 01 reference; see [Actions](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/actions) for current hosted results. Chapters 02–40 and the Android, iOS, and KMP applications remain planned.

Local `main` tracks `origin/main`. The structure plan is committed as `180a912`. The initial foundation commit is `7d4380c`; its [hosted documentation check passed](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/actions/runs/39550166652).

## Decisions

- Prioritize Android and Kotlin interviews, with native iOS and KMP coverage.
- Plan for 4–6 hours weekly; release small missions at a sustainable pace.
- Use the study planner as the growing app and shared logic before the shared UI comparison.
- Use separate Kotlin and mobile workspaces in one repository: independent exercises first, then one growing app with chapter checkpoints. See [project structure](PROJECT_STRUCTURE.md).
- Prefer one coding agent and targeted context. See [agent workflow](AGENT_WORKFLOW.md).

## Next deliverable

Try [Chapter 01](../chapters/01-values-and-types/README.md) as a learner and collect setup and explanation feedback. Then implement Chapter 02's control flow and function exercises using the selected layout.

## Publication decisions

The owner created the GitHub repository. A reuse license remains to be selected. [GitHub setup](GITHUB_SETUP.md) contains the metadata, proposed settings, and publication steps.
