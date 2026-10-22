# Kotlin 101 — The Mobile Workshop

[GitHub repository](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop)

A practical learning project for rebuilding Kotlin and Android skills, preparing for interviews, and learning Kotlin Multiplatform and iOS development. Beginners can follow the full course; returning developers can use the diagnostics to identify what needs practice.

Learn through small missions: predict what happens, fix a bug, build a feature, and explain your decisions. Each completed mission will include a runnable example, optional hints, a separate solution, and a useful challenge to try next.

The primary track is **Android and Kotlin**, with substantial KMP and native iOS coverage. The intended pace is **4–6 hours per week**. Chapters will be developed and verified one at a time.

## Start here

1. Try [Chapter 01 — Values and types](chapters/01-values-and-types/README.md): fix a timer that loses 35 minutes.
2. Use the [Kotlin setup guide](kotlin-labs/README.md) to run it locally, or use the lesson's browser option.
3. Returning to Kotlin? Try the [Chapter 00 diagnostic](chapters/00-starting-point/README.md).

Explore the [full course roadmap](docs/CURRICULUM.md) and [technology choices](docs/RESEARCH.md) when you want the wider picture.

See the [learning experience and public release plan](docs/LEARNING_EXPERIENCE.md) for the mission format, community ideas, and staged launch.

## Learning approach

Learn a concept, predict what code does, run a small example, change it, and explain the result. Apply the concept to a growing mobile application and return to it later through debugging and interview exercises.

The proposed practice app is a study planner: topics, study sessions, revision reminders, search, progress, and offline data. It starts with simple Kotlin models and grows into an Android app, a shared KMP core, and an iOS client. A later lab compares native and shared UI on the same feature.

The [project structure](docs/PROJECT_STRUCTURE.md) uses small, independently runnable Kotlin exercises and one growing mobile app. App chapters will provide start and finish checkpoints so learners can join or recover at a particular lesson.

## Current contents

Chapter 00 and the first runnable Kotlin mission are available. Chapter 01 includes starter code, three optional hints, a separate worked solution, and seven acceptance examples. Its starter intentionally contains the timer bug; some starter tests fail until you solve it.

The Android, iOS, and KMP applications and Chapters 02–40 are planned. See the [Chapter 01 verification record](chapters/01-values-and-types/VERIFICATION.md) for the toolchain and checks actually run.

## Contribute and work with agents

See [contribution guidance](CONTRIBUTING.md) for lesson fixes and ideas. Coding agents should follow [AGENTS.md](AGENTS.md); the [agent workflow](docs/AGENT_WORKFLOW.md) explains how to keep context and verification focused.

The [project status](docs/PROJECT_STATUS.md) records current delivery. The [GitHub setup guide](docs/GITHUB_SETUP.md) contains the repository metadata, publication commands, and license options. A reuse license will be selected before public release.

Run the current documentation check from the repository root:

```sh
python3 scripts/check_docs.py
```
