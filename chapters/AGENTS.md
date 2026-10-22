# Chapter guidance

Apply the repository root guidance as well as these chapter rules.

- Keep numbered chapter folders and searchable technical titles. Use a playful problem in the opening.
- Start with a readable `LESSON.md` before the hands-on `WORKSHOP.md`. Keep the chapter README as a short entry page with the sequence Lesson → Workshop → Solution.
- After teaching the concepts, use the mission loop: predict, explore, build, investigate, explain, revisit.
- Put progressive hints in one `HINTS.md` with collapsed sections. Keep maintainer verification records under `docs/maintainers/verification`.
- Introduce prerequisites before using them. Label optional stretches; public sharing never gates completion.
- Explain exact run commands, expected behavior, boundary cases, and the toolchain actually checked.
- Keep exercises, progressive hints, and worked solutions separate. Chapter 00 is an introductory diagnostic and can retain its current inline explanation.
- Make starter work runnable even when the learner has a deliberate bug to fix. Show how to distinguish that bug from a setup failure.
- Use plain language and small examples. Explain decisions, not just syntax. Include one useful debugging or interview prompt.
- Use deterministic fixtures for core lessons; Android lessons should not require Xcode.
- Add chapter-local build instructions once a build exists. Do not copy unrelated platform guidance into every chapter.
- For a normal lesson change, verify that lesson and documentation links. Do not run every future platform target for a prose edit.

Use `docs/maintainers/LEARNING_EXPERIENCE.md` when designing a mission, and `docs/CURRICULUM.md` when changing topic scope.
