# Chapter 12 Testing asynchronous behavior

Your search test passes with a sleep and fails when the machine is busy. Replace the guess with virtual deadlines, controlled query changes, and regressions that fail before you fix the bug.

Start after [Chapter 11](../11-flow-and-observable-state/README.md). Recall Chapter 08's test-writing practice and Chapter 10's cancellation rules.

| Step | Open | What to do |
| --- | --- | --- |
| 1 Learn | [Lesson](LESSON.md) | Control test scheduling, virtual time, injected dispatchers, and collector lifetimes |
| 2 Practice | [Workshop](WORKSHOP.md) | Write retry and replacement regressions, then repair search |
| 3 Review | [Solution](SOLUTION.md) | Compare both implementation and test reasoning |

**[Begin the lesson](LESSON.md)**

Use the existing `kotlin-labs` workspace. This independent console exercise adds coroutines-test 1.11.0 only to test sources; Android setup comes next.

[Back to the course](../../README.md)
