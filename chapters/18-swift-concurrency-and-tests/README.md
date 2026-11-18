# Chapter 18 Swift concurrency and tests

You ask Study Lab for a Swift review prompt. You tap Swift again while the first request is still loading. The second request returns a prompt. Then the first request finishes and replaces it. How can both requests run safely on the main actor and still show the wrong answer?

**[Begin the lesson](LESSON.md)** after [Chapter 17](../17-swiftui-and-app-lifecycle/README.md). Bring your understanding of Swift enums, protocols, view identity, and observable state.

| Step | What you'll do |
| --- | --- |
| [Lesson](LESSON.md) | Trace two overlapping requests across a suspension point. |
| [Workshop](WORKSHOP.md) | Repair the prompt loader, write a Swift Testing regression test, and try the screen in the iOS simulator. |
| [Solution](SOLUTION.md) | Compare your cancellation, state transitions, and test with the worked example. |

To work from the exact Chapter 18 sources, open [ch18-v1](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/tree/ch18-v1) or [download the ZIP](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/archive/refs/tags/ch18-v1.zip) and extract it into a new folder. Both practice and reference schemes are in `mobile/iosApp`.

[Back to the course](../../README.md)
