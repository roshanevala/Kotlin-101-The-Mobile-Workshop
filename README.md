# Kotlin 101 — The Mobile Workshop

[GitHub repository](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop)

A practical learning project for rebuilding Kotlin and Android skills, preparing for interviews, and learning Kotlin Multiplatform and iOS development. Beginners can follow the full course; returning developers can use the diagnostics to identify what needs practice.

Learn through small missions: predict what happens, fix a bug, build a feature, and explain your decisions. Each completed mission will include a runnable example, optional hints, a separate solution, and a useful challenge to try next.

The primary track is **Android and Kotlin**, with substantial KMP and native iOS coverage. The intended pace is **4–6 hours per week**. Chapters will be developed and verified one at a time.

## Start here

1. Read the [course roadmap](docs/CURRICULUM.md).
2. Review the [technology choices and official references](docs/RESEARCH.md).
3. Work through [Chapter 00 — Starting point and learning habits](chapters/00-starting-point/README.md).

See the [learning experience and public release plan](docs/LEARNING_EXPERIENCE.md) for the mission format, community ideas, and staged launch.

## Learning approach

Learn a concept, predict what code does, run a small example, change it, and explain the result. Apply the concept to a growing mobile application and return to it later through debugging and interview exercises.

The proposed practice app is a study planner: topics, study sessions, revision reminders, search, progress, and offline data. It starts with simple Kotlin models and grows into an Android app, a shared KMP core, and an iOS client. A later lab compares native and shared UI on the same feature.

## Current contents

The roadmap, research notes, public learning plan, and introductory diagnostic are available. The runnable Kotlin lessons and mobile application will be added chapter by chapter. See the roadmap for the distinction between planned topics and completed lessons.

The first implementation milestone is Chapter 01: a reproducible Kotlin exercise project, an explanation of values and types, practical exercises, and verified solutions. Its first mission fixes a timer that loses the remaining minutes when converting a session into hours.

## Contribute and work with agents

See [contribution guidance](CONTRIBUTING.md) for lesson fixes and ideas. Coding agents should follow [AGENTS.md](AGENTS.md); the [agent workflow](docs/AGENT_WORKFLOW.md) explains how to keep context and verification focused.

The [project status](docs/PROJECT_STATUS.md) records current delivery. The [GitHub setup guide](docs/GITHUB_SETUP.md) contains the repository metadata, publication commands, and license options. A reuse license will be selected before public release.

Run the current documentation check from the repository root:

```sh
python3 scripts/check_docs.py
```
