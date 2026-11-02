# Chapter 07 Abstraction and dependencies

The planner saves a session into one store, but its progress report reads a different, empty store. Give both the same dependency, then make storage replaceable through a clear contract.

Start after [Chapter 06](../06-functions-as-values/README.md). You should recognize classes, visibility, copies, nullable results, collections, and function parameters.

| Step | Open | What to do |
| --- | --- | --- |
| 1 Learn | [Lesson](LESSON.md) | Read interfaces, inheritance, generics, variance, delegation, and constructor injection |
| 2 Practice | [Workshop](WORKSHOP.md) | Build an in-memory store and connect a planner and report through interfaces |
| 3 Review | [Solution](SOLUTION.md) | Compare replacement rules, snapshots, and behavior with a fake dependency |

**[Begin the lesson](LESSON.md)**

Keep the same `kotlin-labs` workspace. This independent console module uses no Android APIs, database, or dependency-injection library.

[Back to the course](../../README.md)
