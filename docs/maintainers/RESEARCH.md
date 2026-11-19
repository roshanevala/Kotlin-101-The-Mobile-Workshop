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

## Chapter 15 Android architecture dependencies

Reviewed on **15 November 2026**. The mobile toolchain and Compose pins remain unchanged. Add Lifecycle runtime-compose, viewmodel-compose, and viewmodel-savedstate **2.9.4**, Navigation Compose **2.9.5**, explicit coroutines-android **1.11.0**, and test-only coroutines-test **1.11.0** with JUnit **4.13.2**. The stable [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle#2.9.4) and [Navigation](https://developer.android.com/jetpack/androidx/releases/navigation#2.9.5) releases are selected for compatibility, not as latest releases. Coroutines matches the verified Kotlin-labs version without making either workspace depend on the other. The Android app, earlier Compose checks, and new JVM/device tests compile and run together; see [Chapter 15 verification](verification/ch15.md).

Keep one UI layer and one data boundary with constructor injection; a domain layer, DI container, network, and storage are unnecessary for the deterministic catalogue. The fixed factory uses owner creation extras for `SavedStateHandle`, and the ViewModel is Activity-owned outside NavHost so both destinations share it. Navigation carries an integer ID using the pinned Navigation 2 string-route API, without adding serialization tooling. Notes are small saved strings, not durable catalogue updates. Requests use ViewModel ownership and cancellation-preserving IO recovery, while UI collection uses `collectAsStateWithLifecycle`. These choices follow [architecture recommendations](https://developer.android.com/topic/architecture/recommendations), [ViewModel factories](https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories), [saved state](https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-savedstate), and [state collection](https://developer.android.com/develop/ui/compose/state). JVM tests replace/reset Main as described in [Android coroutine testing](https://developer.android.com/kotlin/coroutines/test).

## Chapter 16 Swift toolchain

Reviewed on **16 November 2026**. The first native Swift language lab is checked on **macOS 27.0.1/Apple silicon**, **Xcode 27.0 (27A266a)**, **Apple Swift 6.4**, and **Python 3.9.6**. It explicitly selects **Swift 6 language mode** through the Xcode compiler, without SwiftPM dependencies or a UI app. Apple's [Xcode requirements table](https://developer.apple.com/xcode/system-requirements) distinguishes compiler releases from supported language modes. This is the locally exercised configuration, not a latest-version or older-compiler compatibility claim.

Use a small Python command wrapper to prepare only a missing ignored answer, pass one source choice plus a fixed entry point to `xcrun swiftc`, and isolate output under `.build`. The reference passes 34 condition-based checks with both `-Onone` and `-O`; six complete lesson programs run independently. Swift Testing/XCTest and asynchronous work remain Chapter 18 topics. Teach value semantics, protocol constraints, and weak callback ownership using the official [structures/classes](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/classesandstructures/), [generics](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/generics/), and [ARC](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/automaticreferencecounting/) guidance. See [Chapter 16 verification](verification/ch16.md).

The Swift CI configuration selects **macos-26/Xcode 26.6**, an installation listed in the [official runner inventory](https://github.com/actions/runner-images/blob/main/images/macos/macos-26-Readme.md) on the review date, and reports its actual compiler before using Swift 6 language mode. Apple lists Swift 6.3 for that Xcode release. The [first hosted run](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/actions/runs/43851046977) passed starter preparation, reference checks in both optimization modes, and lesson examples for source commit `66ada36`. This verifies the configured hosted toolchain separately from the local Xcode 27/Swift 6.4 setup. The Kotlin/Android build configuration and published checkpoints remain unchanged.

## Chapter 17 native SwiftUI client

Reviewed on **17 November 2026**. The native client uses the installed **Xcode 27.0/Apple Swift 6.4**, **Swift 6 language mode**, and **iOS SDK 27.0**, with deployment target **iOS 17.0** because [SwiftUI Observation](https://developer.apple.com/documentation/swiftui/managing-model-data-in-your-app) begins there. The local simulator runtime is **iOS 26.5** on a dedicated iPhone 17 Pro device. SDK, deployment target, and tested runtime are distinct; no older-runtime or physical-device result is inferred. There are no external packages or project-generation dependencies.

Keep one root-owned observable store for the bounded native list/detail feature. Use managed [State](https://developer.apple.com/documentation/swiftui/state) at stable identity, [Binding](https://developer.apple.com/documentation/swiftui/binding) for per-ID draft access, and [Bindable](https://developer.apple.com/documentation/swiftui/bindable) for a borrowed observable property projection. The Xcode 27 SDK implements the State attribute through its macro API; teaching focuses on the storage/identity contract and avoids initializer-count claims. Navigation carries immutable IDs, and [scene phases](https://developer.apple.com/documentation/swiftui/scenephase) are observations rather than reset/persistence instructions. New-process data deliberately starts fresh; storage and asynchronous work remain later topics.

The checked-in project has independent practice/reference app and UI-test targets and uses simulator signing overrides only in the command wrapper. The shared model checker reuses the small compiler/condition-check pattern from Chapter 16; [XCUIApplication](https://developer.apple.com/documentation/xcuiautomation/xcuiapplication) provides fixed UI acceptance scaffolding with required UI-actor annotations. The configured iOS workflow reuses macos-26/Xcode 26.6 from the verified Swift workflow, compiling both app/UI-test bundles and running model checks without a simulator test run. The [corrected hosted run](https://github.com/roshanevala/Kotlin-101-The-Mobile-Workshop/actions/runs/44116383241) reports Xcode 26.6 (17F113)/Apple Swift 6.3.3 and passes both builds, all 12 model checks, and preparation/ignore comparisons. Both root views declare an explicit initializer after the first hosted compiler exposed the starter's inferred private initializer. See [Chapter 17 verification](verification/ch17.md).

## Chapter 18 Swift concurrency and tests

Reviewed on **18 November 2026**. The existing project uses Swift 6 language mode. The new app-hosted unit targets use the Swift Testing framework supplied with Xcode. XCTest continues to drive the running app. A controlled local source suspends each call until its test supplies a reply, which lets an assertion observe an old failure while a newer request is still loading. See [Swift Testing](https://developer.apple.com/documentation/testing) and [Apple’s guide to using both frameworks](https://developer.apple.com/documentation/testing/migratingfromxctest).

The review screen owns its task through a changing request ID. The main-actor loader rechecks its active request after suspension because other calls can enter while it waits. Cancellation signals and identity checks cover different cases when a source can return late. See [Swift concurrency](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/concurrency/), [SwiftUI task identity](https://developer.apple.com/documentation/swiftui/view/task(id:name:priority:file:line:_:)), and [Chapter 18 verification](verification/ch18.md).

## Chapter 19 shared build

Reviewed on **19 November 2026**. The mobile catalog pins Kotlin 2.2.21 and AGP 8.11.1. Both app and library resolve their plugins from these versions. The existing Gradle 8.14.3 wrapper and JDK 17 built both modules. The Android library uses Google's KMP plugin and its `androidLibrary` block for this AGP version. Host tests are opt-in. The two iOS targets establish an `iosMain` source set through Kotlin's default hierarchy. [Android KMP plugin](https://developer.android.com/kotlin/multiplatform/plugin) · [KMP hierarchy and compatibility](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html) · [Gradle version catalogs](https://docs.gradle.org/8.14.3/userguide/version_catalogs.html)

JetBrains lists Xcode **26.0** for Kotlin 2.2.21. Local tests and framework linking also succeeded with Xcode **27.0** and its iOS 27 SDK on Apple silicon, outside that published matrix. The hosted framework job selects Xcode **26.0.1** from the macOS 26 arm64 image. First builds need access to the native distribution and any uncached dependencies. [GitHub runner inventory](https://github.com/actions/runner-images/blob/main/images/macos/macos-26-arm64-Readme.md) · [Kotlin/Native Gradle setup](https://kotlinlang.org/docs/native-get-started.html#using-gradle) · [local verification](verification/ch19.md)

## Research boundaries and next checks

This research establishes the curriculum direction and initial stack. The course sequence and interview exercises are teaching recommendations. Employer-specific preparation will depend on actual role descriptions and the learner's interview feedback.

Before the first runnable lesson, verify the installed JDK and IDE setup, pin the exercise build, and run the documented commands. Before integrating the new library with SwiftUI, verify the generated API and link it in the intended Xcode configuration. Before adding each major library, verify its current compatibility and test the behavior the lesson teaches.
