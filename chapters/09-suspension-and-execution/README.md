# Chapter 09 Suspension and execution

The dashboard waits for its topics before it even asks for completed minutes. Those requests are independent. Learn what suspension does, then overlap their waiting without losing track of their results or lifetime.

Start after [Chapter 08](../08-tests-and-kotlin-pitfalls/README.md). You should recognize functions as values, receivers, interfaces, injected dependencies, exceptions, and test assertions.

| Step | Open | What to do |
| --- | --- | --- |
| 1 Learn | [Lesson](LESSON.md) | Distinguish suspension from blocking; use scopes, dispatchers, `launch`, and `async` |
| 2 Practice | [Workshop](WORKSHOP.md) | Compare sequential and concurrent requests and honor a supplied dispatcher |
| 3 Review | [Solution](SOLUTION.md) | Trace request overlap, result waiting, and context ownership |

**[Begin the lesson](LESSON.md)**

Keep the same `kotlin-labs` workspace. This chapter adds the pinned coroutine library to its own module; no Android SDK, remote service, or account is required.

[Back to the course](../../README.md)
