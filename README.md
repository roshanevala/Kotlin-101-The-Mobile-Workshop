# Kotlin 101 — The Mobile Workshop

Learn Kotlin through clear lessons and small coding workshops. Start with the language, then build an Android app and extend it with iOS and Kotlin Multiplatform.

**[Start learning with Chapter 01](chapters/01-values-and-types/LESSON.md)**

## Your learning path

Build toward a **Study Planner app** for organizing topics, planning study sessions, and tracking progress. Small Kotlin labs teach the rules first; later chapters bring those ideas into one growing mobile app.

```mermaid
flowchart LR
    accTitle: From Kotlin basics to a mobile app and interview practice
    accDescr: Start with the available Kotlin lessons. Build an Android app, then add iOS and shared Kotlin logic in planned stages. Practise lesson questions and debugging along the way; dedicated coding challenges and mock interviews are planned.

    K("Learn Kotlin<br/>Kotlin basics + coroutines<br/>Chapters 01–12 available"):::basics
    A("Build an Android app<br/>Study Lab + Compose editor<br/>Chapters 13–14 available"):::android
    M("Add iOS + KMP<br/>Native UI + shared Kotlin<br/>Planned"):::multiplatform
    I("Interview practice<br/>Questions · coding · debugging<br/>Mock interviews planned"):::interview

    K --> A --> M
    K -.-> I
    A -.-> I

    classDef basics fill:#ecfdf5,stroke:#059669,color:#064e3b,stroke-width:2px
    classDef android fill:#eff6ff,stroke:#2563eb,color:#1e3a8a,stroke-width:2px
    classDef multiplatform fill:#f5f3ff,stroke:#7c3aed,color:#4c1d95,stroke-width:2px
    classDef interview fill:#fff1f2,stroke:#e11d48,color:#881337,stroke-width:2px
    linkStyle default stroke:#64748b,stroke-width:1.5px
```

**Learn it → build it → explain it.** Current lessons include code predictions, debugging tasks, and interview questions. Dedicated coding interview sets and mock interviews arrive in the planned later chapters.

<details>
<summary>Explore the chapter stages and what you will build</summary>

| Stage | What you will build | Availability |
| --- | --- | --- |
| Kotlin foundations · 01–08 | Session models, validation, progress reports, and tested Kotlin rules | **01–08 available** |
| Coroutines and Flow · 09–12 | Cancellable search and predictable, tested state updates | **09–12 available** |
| Android · 13–15 | The Study Lab app, followed by Compose screens, ViewModel, and repositories | **13–14 available**; 15 planned |
| Native iOS · 16–18 | A SwiftUI client with native state and concurrency | Planned |
| Kotlin Multiplatform · 19–22 | Shared Kotlin business rules used by Android and iOS | Planned |
| Data and platform features · 23–30 | Networking, saved data, offline behavior, and a native versus shared UI comparison | Planned |
| Quality and interview practice · 31–40 | Failure tests, profiling, an independent feature, and mock interviews | Planned |

</details>

The [full roadmap](docs/CURRICULUM.md) lists each chapter's topics and practical outcome.

## How to follow a chapter

1. **Learn:** read the lesson and follow its small examples. You can begin in your browser without installing anything.
2. **Practice:** follow the workshop and change the starter code. Hints are there if you get stuck.
3. **Review:** compare your work with the explained solution and try the follow-up challenge.

Read the lessons here on GitHub; their pages live in **`chapters/`**. The independent Kotlin exercise code lives in **`kotlin-labs/`**. The Android app starts in **`mobile/`**, with its own [setup guide](mobile/README.md).

When you are ready to code locally, follow the [one-time setup guide](kotlin-labs/README.md). In Android Studio, open **`kotlin-labs`** and keep the lesson open alongside it.

Workshop commands create a local working copy for your answers. Normal Git commits keep those answers out of the shared starter code.

## Available chapters

| Chapter | What you will learn | What you will build |
| --- | --- | --- |
| [01 Values and types](chapters/01-values-and-types/README.md) | Values, variables, types, arithmetic, and text | A study-time display that keeps the remaining minutes |
| [02 Control flow and functions](chapters/02-control-flow-and-functions/README.md) | Functions, conditions, ranges, validation, and loops | A planner with valid sessions and numbered rounds |
| [03 Null safety and errors](chapters/03-null-safety-and-errors/README.md) | Nullable types, safe calls, parsing, and error contracts | A form that handles missing and invalid session input |
| [04 Classes and modeling](chapters/04-classes-and-modeling/README.md) | Classes, properties, value equality, copies, enums, and sealed results | Session models that preserve the original when completing a copy |
| [05 Collections and complexity](chapters/05-collections-and-complexity/README.md) | Lists, sets, maps, snapshots, grouping, sorting, and Big O | A progress report with unique sessions, completed totals, and ID lookup |
| [06 Functions as values](chapters/06-functions-as-values/README.md) | Lambdas, higher-order functions, extensions, receivers, scope functions, and sequences | Composable session filters and eager versus lazy previews with callback counts |
| [07 Abstraction and dependencies](chapters/07-abstraction-and-dependencies/README.md) | Interfaces, composition, inheritance, generics, variance, delegation, and constructor injection | Replaceable in-memory storage shared by a planner and progress report |
| [08 Tests and Kotlin pitfalls](chapters/08-tests-and-kotlin-pitfalls/README.md) | Unit tests, edge cases, equality versus identity, shallow copies, and read-only collections | Regression tests that protect editable plans and saved history |
| [09 Suspension and execution](chapters/09-suspension-and-execution/README.md) | Suspension versus blocking, scopes, dispatchers, `launch`, and `async` | Sequential and concurrent dashboard loaders with controlled fake requests |
| [10 Structured concurrency](chapters/10-structured-concurrency/README.md) | Job ownership, cancellation, cleanup, timeouts, failures, supervision, and shared state | A search session that cancels obsolete work and protects active-request changes |
| [11 Flow and observable state](chapters/11-flow-and-observable-state/README.md) | Cold streams, buffering, latest operators, combining, StateFlow, and SharedFlow | Debounced topic search combined with progress in shared screen state |
| [12 Testing asynchronous behavior](chapters/12-testing-asynchronous-behavior/README.md) | Test scopes, virtual time, injected dispatchers, boundaries, and controlled interleavings | Learner-written regressions for retry deadlines and rapid search replacement |
| [13 Android runtime and lifecycle](chapters/13-android-runtime-and-lifecycle/README.md) | Application components, Context, intents, resources, recreation, and process restoration | A Study Lab Android draft that restores its topic and completed count |
| [14 Jetpack Compose](chapters/14-jetpack-compose/README.md) | Composition, hoisted/saveable state, keyed effects, lists, layouts, and accessibility | A controlled study list/editor with independent drafts and timed save feedback |

More chapters are coming one at a time. The Android draft and Compose list/editor are available; iOS and KMP clients remain planned.

Returning for interviews? Follow the [focused Android and Kotlin refresh](docs/CURRICULUM.md#focused-android-and-kotlin-refresh): a six-week core at 10–12 hours per week, with two buffer weeks. The [optional starting-point diagnostic](chapters/00-starting-point/README.md) can help identify what to revisit.

To improve a lesson or contribute code, see [Contributing](CONTRIBUTING.md).
