# Kotlin exercise guidance

Apply the root repository guidance. Open only the exercise and build files relevant to the task.

- This workspace uses Kotlin 2.2.21, Gradle 8.14.3, and a JDK 17 compilation toolchain. Use the checked-in wrapper.
- Lesson prose belongs in `chapters`; executable exercises belong here.
- Each chapter is independent. Keep starter and solution source sets separate, with no dependency on each other's compiled output.
- Chapter 01 commands: `./gradlew :ch01-values-and-types:run`, `:ch01-values-and-types:test`, `:ch01-values-and-types:runSolution`, and `:ch01-values-and-types:checkSolution`.
- A fresh Chapter 01 starter compiles and runs but intentionally fails some acceptance tests. Do not fix the starter in the published exercise or treat that expected failure as a CI regression.
- Verify the reference using `checkSolution`. Verify a learner fix with `test`. Keep the acceptance examples shared so both implementations face the same requirements.
- When editing build wiring, verify that a broken starter cannot block the solution and vice versa.
- Do not add Android plugins or SDK requirements to this workspace.
