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

## Research boundaries and next checks

This research establishes the curriculum direction and initial stack. The course sequence and interview exercises are teaching recommendations. Employer-specific preparation will depend on actual role descriptions and the learner's interview feedback.

Before the first runnable lesson, verify the installed JDK and IDE setup, pin the exercise build, and run the documented commands. Before the KMP chapters, check the installed Xcode and simulator, choose supported targets, and build a shared sample on Android and iOS. Before adding each major library, verify its current compatibility and test the behavior the lesson teaches.
