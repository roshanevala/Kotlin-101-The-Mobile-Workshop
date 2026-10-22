# Contributing to Kotlin 101

Help make Kotlin 101 — The Mobile Workshop clearer and easier to run. Useful contributions include a confusing sentence, a missing setup step, a better hint, a meaningful edge case, or a reproducible lesson bug.

The first chapter is available as a lesson, workshop, and explained solution. Learners can start from the [course page](README.md). For development plans, verification records, and agent workflows, use the [maintainer guide](docs/maintainers/README.md).

## Propose a change

- Use the lesson-problem issue template for incorrect behavior or broken instructions.
- Use the lesson-idea template for a new exercise or a substantial teaching change.
- Once Discussions is enabled, use it for learning questions, alternative approaches, and showcases.
- Keep personal exercise solutions in your own fork or a discussion. Course solutions are curated separately so exercises remain useful.

For a small correction, a focused pull request is welcome. For a substantial lesson or dependency change, describe the intended outcome first so it can fit the course sequence.

## Make the change

Read the applicable `AGENTS.md` guidance if using a coding agent. Keep changes focused, respect chapter prerequisites, and keep worked solutions separate from starter exercises. AI assistance is welcome; contributors remain responsible for understanding and verifying the result.

From the repository root, run:

```sh
python3 scripts/check_docs.py
git diff --check
```

For runnable exercises, also run the affected lesson's documented checks. State what was actually tested, including untested platforms. A wording correction does not require a mobile build.

## Open a pull request

Explain the learner problem, the resulting change, and the checks performed. Link the relevant issue when one exists. Include a screenshot only when visual behavior matters.

Be respectful, explain technical disagreements, and welcome beginner questions. Do not post private interview material or personal information about another learner.

## Reuse and attribution

The project's reuse license is still to be selected before public release. Do not assume that public visibility alone grants a license. New material should be your own contribution or include appropriate provenance and permission; the final contribution terms will follow the chosen project license.
