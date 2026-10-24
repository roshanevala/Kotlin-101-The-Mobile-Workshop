# Kotlin exercise guidance

Apply the root repository guidance. Open only the exercise and build files relevant to the task.

- This workspace uses Kotlin 2.2.21, Gradle 8.14.3, and a JDK 17 compilation toolchain. Use the checked-in wrapper.
- Lesson prose belongs in `chapters`; executable exercises belong here.
- Each chapter is independent. Keep starter and solution source sets separate, with no dependency on each other's compiled output.
- Track exercise templates in `src/starter`. `preparePractice` copies the template into Git-ignored `src/main` only when the working file is absent; never overwrite a learner's existing file. `run` and `test` prepare it automatically.
- Treat `src/main` as learner-owned work. Do not change it, force-add it, or use its contents as the public starter. When migrating an already tracked working file, preserve it on disk and remove only its Git index entry.
- Chapter 01 commands: `./gradlew :ch01-values-and-types:run`, `:ch01-values-and-types:test`, `:ch01-values-and-types:runSolution`, and `:ch01-values-and-types:checkSolution`.
- Chapter 02 uses the same tasks with `:ch02-control-flow-and-functions:` and edits `StudyPlan.kt` in package `workshop.ch02`. Its 19 shared acceptance tests cover validation, label boundaries, rounds, and independent calls. Keep it independent of Chapter 01.
- A fresh working copy compiles and runs but intentionally fails some acceptance tests. Do not fix the published template or treat that expected failure as a CI regression. CI checks a fresh copy still contains the exercise and is ignored by Git.
- Verify the reference using `checkSolution`. Verify a learner fix with `test`. Keep the acceptance examples shared so both implementations face the same requirements.
- When editing build wiring, verify that a broken starter cannot block the solution and vice versa.
- Do not add Android plugins or SDK requirements to this workspace.
