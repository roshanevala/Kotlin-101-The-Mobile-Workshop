# Kotlin 101 learning experience and public release plan

Kotlin 101 will be an open mobile development workshop: short explanations, small problems to solve, a growing application, and room to compare solutions. The primary path remains Kotlin and Android, followed by native iOS and KMP. The workshop format gives the [curriculum](../CURRICULUM.md) a repeatable learning experience without changing its technical scope.

The public display name is **Kotlin 101 — The Mobile Workshop**. Keep searchable topic names in chapter titles, then add a memorable problem in the opening. For example, “Values and types” opens with “Your study timer lost 35 minutes.”

## What makes this worth following

Learners leave each lesson with something they can run, explain, and change. They also see the author's learning process: a mistaken assumption, an experiment, the correction, and the evidence. Personal reflection is optional; technical accuracy comes from verified examples and references.

The public promise should be narrow and achievable: learn Kotlin through practical mobile problems, with interview reasoning along the way. Professional depth comes from independent implementation, debugging, measurement, and tradeoffs across the later course.

## The first visit

Give the README one primary start link to the first lesson. Keep the returning-developer diagnostic and full roadmap secondary:

| Learner | First action |
| --- | --- |
| New to Kotlin | Read the first lesson, then use the workshop to apply it |
| Returning Android developer | Attempt the baseline diagnostic and use the results to choose a chapter |
| Experienced developer exploring KMP | Check prerequisites and jump to the KMP module once its lessons are available |

Show available lessons separately from planned topics. Keep setup instructions at the point where learners start coding, and make the full roadmap optional. Planning and contributor material belongs in the maintainer section. Aim for a first successful run within 15 minutes on a supported, configured setup; use pilot feedback to see whether that target is realistic.

The early exercises should also offer a browser path where feasible. Core learning materials remain readable without an account, a social post, or access to an iOS machine.

## A lesson as a small mission

Every chapter has a short entry page leading to `LESSON.md`, `WORKSHOP.md`, and `SOLUTION.md` in that order. Teach the concepts with small examples before asking the learner to repair or build a feature. The workshop then follows this sequence, split across sessions when necessary:

| Step | Learner activity | Why it belongs |
| --- | --- | --- |
| See the problem | Read a tiny bug report or inspect incorrect output | Establish a concrete reason to learn the concept |
| Predict | Write what will happen before running code | Surface assumptions that can be checked |
| Explore | Read a focused explanation and change a working example | Connect syntax to observable behavior |
| Build | Complete a small feature with stated acceptance criteria | Practice using the idea independently |
| Investigate | Repair one deliberate bug and explain its cause | Practice diagnosis as well as implementation |
| Explain | Answer a short interview prompt in your own words | Connect implementation to communication |
| Revisit | Solve a changed version in a later session | Check whether the idea can be recalled and reused |

Start the lesson with its purpose and prerequisites. Start the workshop with its goal and the commands needed to run it. Keep the setup guide separate and reusable. Effort estimates are provisional until learners try the lesson. Provide three levels of hints in one optional page with collapsed sections: a question to consider, the relevant concept, and a partial approach. Link the worked solution separately so it is easy to avoid spoilers. Execution records belong with maintainer documentation.

Prediction, recall, and revisiting material draw on retrieval and spaced practice. The mission names, achievement ideas, and release cadence are design proposals to evaluate with learners. [Teaching the science of learning](https://link.springer.com/article/10.1186/s41235-017-0087-y).

## Recurring activities

Use a few recurring activities so learners recognize the format. Introduce them gradually.

| Activity | Example | When to use it |
| --- | --- | --- |
| Predict the output | Will changing a copied plan also change the original? | A two-minute opening or review |
| Bug Museum | A short exhibit showing a mistake, its cause, a fix, and a check | One useful misconception per lesson |
| Change request | The timer must now show a second session duration | A small transfer exercise after the guided work |
| Explain in 60 seconds | Why does `val` allow the contents of a mutable list to change? | An informal interview rehearsal |
| Same feature on two platforms | Compare the same state change in Compose and SwiftUI | After the relevant platform foundations |
| Boss challenge | Rebuild a feature from a specification with no step-by-step guide | At the existing curriculum checkpoints |

Timed explanations are optional practice, and learners can write their answer instead. Keep humor in the scenario and illustrations; use precise technical language in the explanation.

## Progress that shows capability

Start with a local Markdown learning log. Each row records a chapter, what the learner built, one mistake, and a future review task. Public sharing is optional.

Use descriptive milestones with explicit evidence:

| Milestone | Evidence |
| --- | --- |
| First working feature | Run and modify the study-time calculator |
| Model builder | Model valid and invalid session states and test the rules |
| Bug detective | Explain and fix a shallow-copy problem |
| Async investigator | Reproduce and repair a stale-search result |
| Android feature builder | Demonstrate state behavior through recreation |
| Multiplatform builder | Run the same tested rule through Android and iOS clients |

Learners may mark these themselves; they are course milestones, not professional certifications. Any later badges should name the work demonstrated. Reading a page, returning daily, or posting publicly should not be required for progression. A learner returning after a break gets a short recap and an easy re-entry exercise.

## A growing app with playful detours

Keep the study planner as the main app, using a working name such as **Study Lab** until the project owner chooses a final name. Learners gradually add topics, sessions, progress, saved drafts, reminders, search, and offline behavior.

Introduce brief standalone detours when a concept benefits from a different example: an inventory for collections, a snack queue for concurrency, or a broken elevator for state modeling. Each detour must teach the current concept and fit its prerequisites. It should not create another application to maintain.

Some planned mission openings are:

| Curriculum topic | Mission opening | Working outcome |
| --- | --- | --- |
| Values and types | Your timer lost 35 minutes | Correctly display a session duration |
| Null safety | The topic has no title | Handle incomplete input without hiding the problem |
| Equality and copying | The clone changed the original | Isolate a copied collection when required |
| Cancellation and Flow | An old search result won the race | Keep obsolete work from replacing current results |
| Android lifecycle | Your draft disappeared | Preserve the intended state across recreation |
| Shared rules in KMP | Two apps disagree about one session | Use a shared validation rule from both clients |
| Offline synchronization | The train entered a tunnel | Keep a defined edit flow working without connectivity |

## Pilot mission for Chapter 01

**Topic:** values, integer arithmetic, strings, and output.

**Opening:** the study planner displays a 95-minute session as “1 h 0 min.” The learner's task is to recover the missing minutes.

Use the first mission to test the whole experience. Start with a minimal Kotlin console program, explain each line, and show one exact run command. A browser version can offer the same small exercise. Local setup must be documented separately from the learning steps.

The learner predicts the incorrect output, learns integer division and remainder, fixes the calculation, and changes the input. The independent exercise uses a different duration. Keep input validation for the next chapter, where control flow is introduced.

| Total minutes | Required output |
| --- | --- |
| 0 | `0 h 0 min` |
| 59 | `0 h 59 min` |
| 60 | `1 h 0 min` |
| 95 | `1 h 35 min` |
| 125 | `2 h 5 min` |

Hints should first point to the leftover minutes, then the remainder operator, then a partial expression. The separate solution explains the calculation and contains checks for the boundary cases. Explain the test runner before asking a beginner to interpret its results.

The Bug Museum exhibit explains why converting to whole hours loses the fractional part. The interview prompt asks the learner to distinguish a value, its type, and a variable that can be reassigned. The later recall task uses 143 minutes and asks for an explanation before execution.

An optional share note can read: “I fixed a duration display bug using integer division and remainder. My check included a session shorter than an hour and one exactly an hour long.” Learners can instead keep the note private.

The [Chapter 01 lesson](../../chapters/01-values-and-types/README.md) implements this brief with separate starter, hints, solution, and acceptance checks.

## Publishing with a sustainable workload

Use GitHub as the proposed initial home for code, lessons, release notes, questions, and contributions. A reading site can follow once the lesson format is proven. Keep lesson content in the repository so another presentation layer can reuse it later.

Plan for one small, complete mission roughly every two weeks, adjusting after the pilot. A curriculum chapter may require several missions. Do not promise weekly full chapters across all 40 topics.

At a five-hour midpoint, allocate about 3.5 hours to learning, implementing, and verifying; one hour to editing the lesson; and 30 minutes to sharing and feedback. At four hours, reduce publishing scope. Use a sixth hour for optional refinement or community replies when available.

Each finished mission can produce three public pieces from the same work:

1. The repository lesson, exercise, hints, solution, and verification record.
2. One short post showing the problem and the lesson learned, with a link to try it.
3. One optional discussion prompt comparing explanations or approaches.

Choose one social channel you already use for the pilot. Start with text, code, and a small screenshot when useful. Add video only when it improves a lesson and fits the workload.

## Learning together

Use GitHub Discussions for help, explanations, alternative approaches, and showcases. Use Issues for reproducible lesson errors, broken setup instructions, and agreed improvements. GitHub documents Discussions as a space for open conversation and supports repository contribution guidance. [Discussions quickstart](https://docs.github.com/en/discussions/quickstart), [Contributor guidelines](https://docs.github.com/en/communities/setting-up-your-project-for-healthy-contributions/setting-guidelines-for-repository-contributors).

Begin with a welcome thread and three areas: Help, Show and tell, and Course feedback. A useful question includes the chapter, expected behavior, actual behavior, and the learner's current understanding. A useful showcase includes a changed requirement and the reason behind the solution.

Welcome small contributions: unclear sentences, missing setup steps, better hints, a useful edge case, or accessibility feedback. Keep a learner's personal exercise solutions in their own fork or a discussion; curated course solutions can be maintained separately. Credit accepted contributions and ask before featuring someone's story in a public post.

Review feedback in a short batch during each release cycle. Publish a realistic maintenance cadence so learners know when to expect a reply. A separate chat server can wait until a community needs it and someone can maintain it.

## Launch in stages

These stages are defined by useful deliverables, not fixed calendar dates.

| Stage | Deliverable | Exit condition |
| --- | --- | --- |
| 1 Build the pilot | Chapter 00 plus a complete Chapter 01 mission and documented run path | The example and checks run from a clean checkout |
| 2 Try it with readers | Two or three volunteer readers with different experience levels | Their setup blockers and unclear instructions are resolved or clearly documented |
| 3 Open the starter release | Chapters 00–03, clear availability labels, a start page, contribution guidance, and a chosen reuse license | A new learner can find, run, and finish the first mission; future chapters are clearly planned |
| 4 Continue the series | Small missions released at a measured pace | New material passes verification and recurring learner problems improve earlier lessons |

Before the public starter release, select the code and documentation licenses, prepare contribution and community guidance, add a lesson-error report template, and document the supported toolchain. These are launch deliverables. Remote repository creation, invitations, and announcements are later actions when the release is ready.

## How to judge the pilot

Ask each volunteer reader four questions:

- Could you find the first activity and run it? Where did you get stuck?
- Which instruction or concept needed another example?
- Could you solve the changed requirement without the solution?
- Would you choose to do another mission? Why or why not?

With a small pilot, summarize the actual responses and observed difficulties. Improve the lesson before interpreting the feedback as evidence about the whole course. If a challenge overwhelms beginners, strengthen the hints or split it; if publishing takes more time than learning, reduce the release package.

## Next implementation sequence

1. Try the implemented Chapter 01 and its changed requirement as a learner.
2. Use its verification record and actual setup experience to improve instructions.
3. Prepare the first public post and a pilot feedback prompt from the finished mission.
4. Collect reader feedback using the questions above.
5. Apply the pilot findings to Chapters 02 and 03 before the starter release.
