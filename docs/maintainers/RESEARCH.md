# Kotlin mobile course technology choices

Research reviewed on **20 October 2026**. The proposed course prioritizes Android and Kotlin interview preparation while teaching KMP and native iOS development in depth. The recommendations below are curriculum decisions informed by official documentation; they are not claims about the number of jobs using each framework.

## Why KMP fits this course

Google officially supports KMP for sharing business logic between Android and iOS. KMP allows incremental sharing, and Compose Multiplatform adds the option of shared UI. This makes KMP a suitable destination for a course rebuilding Kotlin and mobile skills. [Google KMP overview](https://developer.android.com/kotlin/multiplatform).

KMP and Compose Multiplatform address different concerns: sharing Kotlin code does not require sharing UI. JetBrains offers learning paths for both shared UI and native UI with shared logic. The proposed course starts with shared logic, teaches a small SwiftUI client, and later compares a shared Compose screen. [KMP learning paths](https://kotlinlang.org/docs/multiplatform/get-started.html).

Compose Multiplatform for iOS reached Stable with version 1.8.0 in May 2025. Shared UI is therefore a legitimate practical topic for this course. Framework stability does not decide whether a particular screen should share UI; the comparison lab will examine navigation, accessibility, integration, and platform conventions. [JetBrains stability announcement](https://blog.jetbrains.com/kotlin/2025/05/compose-multiplatform-1-8-0-released-compose-multiplatform-for-ios-is-stable/).

## Alternative course directions

| Direction | When it fits | Fit for this project |
| --- | --- | --- |
| Kotlin and native Android only | Focused Android interview recovery is the entire goal | Good initial milestone; leaves the requested iOS and KMP learning for later |
| KMP with native UI, then a shared UI lab | Learners want Kotlin reuse and enough platform knowledge to explain integration | Recommended course sequence |
| Shared Compose UI immediately | Learners prioritize building one UI across platforms and already understand the platforms | A useful later path; introduces several tools together for a beginner |
| Separate full Android and iOS courses | Deep specialist preparation in both ecosystems is the main goal | Valuable, but duplicates application work at this weekly pace |
| A course centered on Flutter or React Native | The learner's target work or team requires those ecosystems | Would require a separate framework track; does not directly serve the Kotlin-first goal |

This comparison concerns learning fit. It does not rank framework performance, employer demand, or development cost.

## Teaching foundations

The official Kotlin tour provides browser-based introductory exercises, allowing language learning to begin without full mobile setup. Use small language examples before application scaffolding. [Kotlin tour introduction](https://kotlinlang.org/docs/kotlin-tour-hello-world.html).

Kotlin's coroutine guide distinguishes language support for suspension from the higher-level coroutine library. Teach execution, cancellation, scopes, exceptions, and streams explicitly before relying on them in app architecture. [Coroutines guide](https://kotlinlang.org/docs/coroutines-guide.html).

Google's Android Basics with Compose course combines Kotlin, Compose, application architecture, data, and testing. It is a useful reference for the beginner Android sequence. Our course adds returning-developer diagnostics, KMP, native iOS comparisons, and interview practice. [Android Basics with Compose](https://developer.android.com/courses/android-basics-compose/course).

## Proposed application stack

| Concern | Proposed choice | Learning reason |
| --- | --- | --- |
| Primary language | Kotlin | The main interview and application skill being rebuilt |
| Shared logic | Kotlin Multiplatform | Share tested rules and data behavior between Android and iOS |
| Android UI | Jetpack Compose | Learn Android state and UI development directly |
| Native iOS reference | Swift and SwiftUI | Understand iOS state, lifecycle, concurrency, and shared-code consumption |
| Shared UI comparison | Compose Multiplatform | Evaluate reuse on an existing feature |
| Async work | kotlinx.coroutines and Flow | Make concurrency and cancellation visible and testable |
| Networking | Ktor client and kotlinx.serialization | Model a common client with platform engines and explicit data mapping |
| Persistence | SQL, then a compatible stable Room KMP release | Learn the storage model and connect Android experience to shared persistence |
| Dependency injection | Constructor injection first | Understand dependency ownership before adding a container |
| Verification | kotlin.test, coroutine tests, Android tests, Swift Testing and XCTest UI tests | Check behavior in shared code and at native integration boundaries |

Google recommends separating UI and data layers, using repositories and unidirectional state flow, and collecting Android state with lifecycle awareness. A domain layer is useful when complexity or reuse warrants it; it need not be mandatory ceremony in the first app. [Android architecture recommendations](https://developer.android.com/topic/architecture/recommendations).

Ktor documents a shared multiplatform client with platform-specific engines. Teach HTTP semantics, transport failures, serialization, and DTO mapping alongside the client API. [Ktor cross-platform tutorial](https://ktor.io/docs/client-create-multiplatform-application.html).

Room supports KMP persistence with common database definitions and platform setup. Its documentation includes APIs and example dependency versions that must be checked against the chosen release channel before implementation. The course should use a compatible stable release for core exercises. SQLDelight can be discussed later as an alternative when comparing SQL ownership and tooling. [Room KMP setup](https://developer.android.com/kotlin/multiplatform/room).

Apple's Develop in Swift tutorials teach Xcode, SwiftUI, data modeling, and application development. Use these for the native iOS foundation. Apple's testing guidance distinguishes Swift Testing for unit tests from XCTest's role in UI automation. [Develop in Swift](https://developer.apple.com/tutorials/develop-in-swift), [Testing in Xcode](https://developer.apple.com/documentation/xcode/testing).

## Tooling and interoperability decisions

Use Android Studio for Android and shared Kotlin work, and Xcode for Swift and iOS work. JetBrains announced that Swift language IDE features in the KMP plugin will be discontinued starting with IntelliJ IDEA 2026.3 and Android Studio Rabbit 2 2026.2.2. Instructions should identify the IDE used for each task. [JetBrains tooling announcement](https://blog.jetbrains.com/kotlin/2026/10/discontinuing-swift-language-ide-support-in-the-kotlin-multiplatform-plugin/).

For a single repository without a CocoaPods dependency requirement, direct iOS framework integration is a reasonable initial approach. SwiftPM and CocoaPods belong in later integration and distribution comparisons. [iOS integration methods](https://kotlinlang.org/docs/multiplatform/multiplatform-ios-integration-overview.html).

Swift export is currently Alpha. Its documentation describes mappings for suspending functions to Swift async APIs and Flow to AsyncSequence, alongside export limitations and expected breaking changes. Teach Objective-C framework interoperability as the initial integration route and put Swift export in a comparison lab unless its maturity changes before implementation. Verify generated types, cancellation, and error behavior against the pinned Kotlin version. [Swift export](https://kotlinlang.org/docs/native-swift-export.html).

Select Kotlin, Gradle, Android Gradle Plugin, Xcode, and Compose versions as a compatible set before implementing the build. Use the current Android KMP library plugin guidance for shared libraries rather than copying an older project's configuration. Compose compiler plugin versions must align with Kotlin. [KMP compatibility guide](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html), [Compose compatibility](https://kotlinlang.org/docs/multiplatform/compose-compatibility-and-versioning.html).

## Chapter 09 coroutine dependency

Reviewed on **5 November 2026**. The first coroutine exercise pins **`kotlinx-coroutines-core:1.11.0`** in its own module. Its [official release notes](https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.11.0) identify Kotlin **2.2.20** as the library's build version. Dependency resolution, compilation, and execution were verified with the course's Kotlin **2.2.21**, Gradle **8.14.3**, and JDK **17**, without upgrading earlier exercises; see the [Chapter 09 verification record](verification/ch09.md).

Use only the core library for this first comparison. The supplied test fixtures use one-shot signals to observe request lifetime without elapsed-time assertions. Cancellation and failure policies belong to Chapter 10; the coroutine test library and virtual-time instruction remain Chapter 12 work. The console fixture's mutable counters stay in its inherited single-threaded context. An already suspending API does not automatically need an IO dispatcher, and `runBlocking` is used at the console/test boundary rather than inside the loaders. These choices follow [composition](https://kotlinlang.org/docs/composing-suspending-functions.html), [dispatcher guidance](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html), and the [`runBlocking` contract](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/run-blocking.html).

Chapter 10 reviewed the same pinned core APIs on **10 November 2026** and reuses **1.11.0** without adding dependencies. Its lifecycle lab uses `cancelAndJoin`, cancellation-preserving recovery, local deadlines, and mutex-protected request transitions. Scoped supervision is taught as an explicit policy for independent work, not as a blanket fix for obsolete results. See [Chapter 10 verification](verification/ch10.md), [`cancelAndJoin`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/cancel-and-join.html), [`withTimeoutOrNull`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/with-timeout-or-null.html), and [`supervisorScope`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/supervisor-scope.html).

Chapter 11 reviewed Flow APIs on **11 November 2026** and reuses the same **1.11.0** core dependency. The lab places the quiet-period delay inside a latest branch so processed replacement queries cancel previous work before waiting. `flatMapLatest` uses a local experimental-API opt-in, and `buffer(0)` removes its queued output capacity. Shared screen state uses an explicit eager policy in the supplied scope; virtual-time tooling remains Chapter 12 work. See [Chapter 11 verification](verification/ch11.md), [`flatMapLatest`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/flat-map-latest.html), [`buffer`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/buffer.html), and [`stateIn`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/state-in.html).

## Chapter 12 virtual-time test dependency

Reviewed on **12 November 2026**. Chapter 12 adds **`kotlinx-coroutines-test:1.11.0`** to `testImplementation`, matching core **1.11.0**. The [official module documentation](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/) prescribes test-only usage. Dependency resolution selects Kotlin stdlib **2.2.21** and matching core/test **1.11.0**; compilation and execution pass with the existing Gradle **8.14.3** and JDK **17**. Earlier modules and production runtime do not acquire the test library. See [Chapter 12 verification](verification/ch12.md).

Use `StandardTestDispatcher(testScheduler)` for injected work, an explicit real-time `runTest` timeout, and `runCurrent` to execute tasks exactly at the target of `advanceTimeBy`. Long-lived collectors use `backgroundScope` with explicit time advancement. These decisions follow [`runTest`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/run-test.html), [`advanceTimeBy`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/advance-time-by.html), and [`backgroundScope`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-scope/background-scope.html). Controlled single-threaded interleavings do not prove correctness for every real parallel race.

## Chapter 13 Android workspace

Reviewed on **13 November 2026**. The new `mobile` workspace pins **AGP 8.11.1**, **Kotlin 2.2.21**, **Gradle 8.14.3**, **JDK 17**, **compile/target SDK 36**, **Build Tools 35.0.0**, and **minimum SDK 30**. Kotlin's [compatibility table](https://kotlinlang.org/docs/gradle-configure-project.html) lists AGP through 8.11.1 for this Kotlin version; the [AGP 8.11 release notes](https://developer.android.com/build/releases/agp-8-11-0-release-notes) support API 36, require Gradle 8.13 or newer and JDK 17, and default to Build Tools 35.0.0. Builds and device checks pass with the pinned combination; earlier Kotlin modules are unchanged.

Use a small platform Activity/XML screen for lifecycle instruction before Compose in Chapter 14. Native inset APIs motivate the initial API 30 minimum. Separate practice/reference flavors isolate learner code while sharing fixed resources and tests. Device tests use stable [AndroidX Test](https://developer.android.com/jetpack/androidx/releases/test) core/runner **1.7.0** and ext.junit **1.3.0**, only as `androidTestImplementation`. The Android runner uses JUnit 4, independent of Kotlin labs' JUnit 5. No Compose, ViewModel, KMP, iOS, NDK, or runtime DI library is added.

Widget saving is deliberately disabled for the explicit Bundle exercise. ActivityScenario recreation checks and a retained-task background-process-kill check provide different evidence; neither establishes durable history. These decisions follow [Activity lifecycle](https://developer.android.com/guide/components/activities/activity-lifecycle), [saved state](https://developer.android.com/topic/libraries/architecture/saving-states), and [edge-to-edge Views](https://developer.android.com/develop/ui/views/layout/edge-to-edge). See [Chapter 13 verification](verification/ch13.md).

## Chapter 14 Compose dependencies

Reviewed on **14 November 2026**. The existing mobile toolchain is unchanged. Chapter 14 adds the **Compose compiler Gradle plugin 2.2.21**, matching Kotlin, with `buildFeatures.compose = true`; it does not use the older compiler-extension version setting. It pins **Compose BOM 2025.10.01** for app and Android-test configurations, resolving UI/runtime/test **1.9.4** and Material3 **1.4.0**, plus **Activity Compose 1.11.0**. These are a verified compatible set, not a latest-version claim. See [compiler setup](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler), [BOM mapping](https://developer.android.com/develop/ui/compose/bom/bom-mapping), and [Activity releases](https://developer.android.com/jetpack/androidx/releases/activity).

Compose UI tests initially resolved older Espresso code that reflected an API removed on the tested Android 16 runtime. Add **Espresso core 3.7.0** only to `androidTestImplementation`; its [official release notes](https://developer.android.com/jetpack/androidx/releases/test) document replacing reflective `InputManager.getInstance` with system-service access. The updated device-test set compiles/runs with API 36. Preview tooling and the Compose test-host manifest are debug dependencies; there is no ViewModel, repository, database, or KMP dependency yet.

The first Compose mission uses six saved primitives for a bounded two-row catalogue and derives immutable models. Its transient save feedback uses a revision-keyed effect and polite live-region semantics; draft changes belong to events rather than an initialization effect. The old Activity/answers and `ch13-v1` snapshot remain preserved. These choices follow [state/hoisting](https://developer.android.com/develop/ui/compose/state), [effects](https://developer.android.com/develop/ui/compose/side-effects), [semantics](https://developer.android.com/develop/ui/compose/accessibility/semantics), and [testing](https://developer.android.com/develop/ui/compose/testing). See [Chapter 14 verification](verification/ch14.md).

## Research boundaries and next checks

This research establishes the curriculum direction and initial stack. The course sequence and interview exercises are teaching recommendations. Employer-specific preparation will depend on actual role descriptions and the learner's interview feedback.

Before the first runnable lesson, verify the installed JDK and IDE setup, pin the exercise build, and run the documented commands. Before the KMP chapters, check the installed Xcode and simulator, choose supported targets, and build a shared sample on Android and iOS. Before adding each major library, verify its current compatibility and test the behavior the lesson teaches.
