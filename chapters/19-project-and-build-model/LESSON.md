# Lesson 19 Follow one Kotlin file into two builds

Suppose a library contains a Kotlin function that turns `"  Coroutines  "` into `"Coroutines"`. An Android compiler can compile that function. So can Kotlin/Native for iOS. Yet putting the function in the Android app's source folder gives only the Android app access to it. How does a build tell each compiler to read the same file?

## From the wrapper to a task

In earlier chapters you ran Gradle tasks such as `:androidApp:assemblePracticeDebug`. Start by reading a task path from left to right. The first colon marks the build root. `androidApp` names a project included in that build. The final part names work Gradle can perform for that project. Multiple projects can have a task named `assemble`, so the project path matters.

From the `mobile` directory, run these two read-only queries.

```sh
./gradlew --version
./gradlew projects
```

The first reports the Gradle runtime selected by the **wrapper** (`gradlew.bat` on Windows). Its distribution URL and checksum live in `gradle/wrapper/gradle-wrapper.properties`. The Kotlin version printed by `--version` is the one embedded in Gradle for build scripts. The Kotlin plugin that compiles the library has its own version in the catalog. The second evaluates this build and lists the projects Gradle recognizes. Look for the Android app and the shared library. A folder on disk becomes a Gradle subproject when the build's `settings.gradle.kts` includes it. Opening a different wrapper, such as the independent Kotlin labs build, gives you a different set of projects. [Gradle Wrapper](https://docs.gradle.org/8.14.3/userguide/gradle_wrapper.html)

Next read the build script for the shared library. A `.gradle.kts` file is a build script using Kotlin DSL. The `plugins` block adds capabilities to the project. Applying the Kotlin Multiplatform plugin supplies the `kotlin { … }` extension, which is where you declare the targets you want Gradle to build. A plugin can register work to compile, link, package, or test the outputs of those targets. [Kotlin DSL](https://docs.gradle.org/8.14.3/userguide/kotlin_dsl.html)

The **version catalog**, normally `gradle/libs.versions.toml`, gives names to version numbers and plugin or library coordinates. In TOML, a plugin entry names the plugin ID and can use `version.ref` to refer to a key in `[versions]`. For example, the catalog alias `kotlin-multiplatform` becomes `libs.plugins.kotlin.multiplatform`. The module's `plugins` block uses `alias(libs.plugins.kotlin.multiplatform)` to apply it. Trace one alias from the build script to its actual plugin ID and version in the catalog. Changing a referenced version changes the plugin version Gradle requests. The project applies it when its build script uses the alias. [Gradle version catalogs](https://docs.gradle.org/8.14.3/userguide/version_catalogs.html)

## Targets say what to build

A **target** describes an environment for which the Kotlin compiler should produce code. The shared project's Android target compiles the library for Android. Its two iOS targets name both the platform and the processor architecture.

| Target | Where its code belongs |
| --- | --- |
| Android | An Android library consumed by an Android build |
| `iosArm64` | An iOS device using ARM64 |
| `iosSimulatorArm64` | An iOS Simulator running on an Apple silicon Mac |

Both Apple targets say “Arm64,” but an iPhone and a simulator have different platform identities, SDKs and linking requirements. Choose the simulator target to build for that environment. A Gradle project can declare both targets before it builds either one. Executing a target-specific task lets you test a specific claim about one output. [Kotlin Multiplatform project structure](https://kotlinlang.org/docs/multiplatform/multiplatform-discover-project.html)

## Source sets say which code to use

A **source set** groups source files and their dependencies. Consider this independent example of a portable function.

```kotlin
fun topicCaption(raw: String): String = raw.trim().ifEmpty { "Untitled" }
```

`topicCaption("  Coroutines  ")` returns `"Coroutines"`. It uses Kotlin string operations that are available on every target in this module. Placing that declaration in `commonMain` makes it available to all three target compilations. A source set has conventional source roots such as `src/commonMain/kotlin`. A build can also point the same source set at a different directory. A Kotlin `package` organizes the declaration names. Gradle's source roots determine which compiler receives the file.

A source set can **depend on** another source set in the Kotlin hierarchy. Imagine walking from an iOS simulator compilation toward its shared ancestors. Along the way it sees declarations in `iosSimulatorArm64Main`, in the intermediate `iosMain` source set shared with iOS devices, and ultimately in `commonMain`. The Android branch shares the `commonMain` ancestor with iOS. A file added under `iosMain` reaches only the two iOS branches. The default hierarchy template forms these relationships from the targets you declare. Some intermediate sets can remain empty. [Source set hierarchy](https://kotlinlang.org/docs/multiplatform/multiplatform-hierarchy.html)

| If a file uses… | Which source set could own it? | Why? |
| --- | --- | --- |
| Kotlin strings and numbers for the study label | `commonMain` | Every declared target can compile these types. |
| An Android framework type | `androidMain` | The Android compilation has the Android SDK. |
| An iOS API shared by device and simulator | `iosMain` | Both iOS targets can see this set and its platform APIs. |
| The label's portable test inputs and expected strings | `commonTest` | The same behavior can be exercised by each configured target's test runner. |

Dependencies follow the same visibility rules. A test-only dependency such as Kotlin's test assertions belongs with the test source set. Production code that both targets consume must use APIs available to those targets. Moving an Android import into a package called `shared` still leaves the iOS compiler without that Android type.

## Read a build result precisely

A successful compile means the selected compiler accepted its inputs. Packaging collects compiled output into an artifact a consumer can use. Running tests goes further by executing calls and checking their results. The Android build packages an AAR, a library archive. On the simulator side, the Kotlin/Native linker produces the `StudyShared.framework` bundle configured in this project. An Android host test runs the label code on the JVM. A simulator test runs compiled native test code. An app would have to consume the corresponding library to display that label. Client integration comes in a later lesson.

Before the workshop, predict which targets lose access to `topicCaption` if you move it from `commonMain` to `iosMain`. Then predict whether a successful Android library build, by itself, would tell you that the simulator test ran.

**[Start the workshop](WORKSHOP.md)**
