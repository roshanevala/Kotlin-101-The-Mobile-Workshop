# Chapter 11 Flow and observable state

The search box and progress counter change independently. Build one current screen state without restarting search for every progress update or allowing an obsolete search to publish later.

Start after [Chapter 10](../10-structured-concurrency/README.md). You should recognize suspending calls, cooperative cancellation, scopes, sealed results, and injected sources.

| Step | Open | What to do |
| --- | --- | --- |
| 1 Learn | [Lesson](LESSON.md) | Explore cold streams, buffering, latest operators, combining, and hot state |
| 2 Practice | [Workshop](WORKSHOP.md) | Build debounced search and combine it with observable study progress |
| 3 Review | [Solution](SOLUTION.md) | Compare replacement, recovery, conflation, and sharing policies |

**[Begin the lesson](LESSON.md)**

Use the same `kotlin-labs` workspace and coroutines 1.11.0. This independent console module adds no dependency or mobile setup.

[Back to the course](../../README.md)
