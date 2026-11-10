# Chapter 10 Structured concurrency

An old “Ko” search finishes after “Kotlin” and replaces the newer result. Give each request an owner, stop obsolete work, and keep cancellation out of the error display.

Start after [Chapter 09](../09-suspension-and-execution/README.md). You should recognize suspending calls, scopes, result handles, context inheritance, sealed results, and injected dependencies.

| Step | Open | What to do |
| --- | --- | --- |
| 1 Learn | [Lesson](LESSON.md) | Trace job ownership, cancellation, cleanup, timeouts, failures, supervision, and shared state |
| 2 Practice | [Workshop](WORKSHOP.md) | Cancel obsolete searches and serialize active-request changes |
| 3 Review | [Solution](SOLUTION.md) | Compare cancellation, deadline, and publication behavior |

**[Begin the lesson](LESSON.md)**

Keep the same `kotlin-labs` workspace and pinned coroutine library. This independent console lab needs no network, Android SDK, Flow, or new dependency.

[Back to the course](../../README.md)
