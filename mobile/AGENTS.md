# Mobile workspace guidance

Apply the repository root guidance. This is the growing Android workspace; keep `kotlin-labs` independent.

- Pin AGP 8.11.1, Kotlin 2.2.21, Gradle 8.14.3, JDK 17, compile/target SDK 36, Build Tools 35.0.0, and minimum SDK 30 until a later chapter makes a verified change.
- Chapter 13 uses platform Activity and XML Views to teach runtime/lifecycle before Compose in Chapter 14. Do not add Compose, ViewModel, storage, KMP, or iOS scaffolding prematurely.
- Tracked `androidApp/src/starter` holds the exercise and `src/solution` its reference. `preparePractice` creates `src/practice/kotlin/workshop/studylab/MainActivity.kt` only when absent. Treat that ignored file as learner-owned; never overwrite or stage it. `clean` must preserve it.
- Shared manifest, resources, window scaffolding, and instrumented acceptance tests live in tracked `src/main` and `src/androidTest`. There is no MainActivity in shared main. The practice/reference flavors compile independently and install under different IDs.
- Build with `./gradlew :androidApp:assemblePracticeDebug :androidApp:assembleReferenceDebug`; lint with `:androidApp:lintReferenceDebug`. Reference device checks use `:androidApp:connectedReferenceDebugAndroidTest`; learner checks use `:androidApp:connectedPracticeDebugAndroidTest`. Scope `ANDROID_SERIAL` when multiple devices are attached.
- The 10 instrumented checks use ActivityScenario and require a running API 30+ emulator/device. They establish recreation behavior, not real process death. Check process restoration separately by backgrounding, killing only that app's background process, and reopening its retained task; never equate force-stop with system restoration.
- Automatic widget saving is deliberately disabled for the topic/count mission. Preserve this in both variants so it cannot mask a missing Bundle implementation. Keep empty text valid, read current text when saving, and restore the backing count before rendering.
- Keep saved state small; do not store Context, View, Activity, or long-lived objects in a Bundle/static singleton. Never promise onDestroy before process termination.
- Use temporary workspace copies for broken-source and seeded-defect checks. Verify neither variant compiles the other's Activity. Do not infer a learner walkthrough from reference passes.
- `local.properties`, signing material, answers, build/bin output, and emulator state stay untracked. Do not place personal SDK paths in documentation.
- Source start/finish checkpoints are the practice template/reference variant. Publish an immutable `ch13-v1` tag with the chapter commit when publication is requested; both variants belong to that single source snapshot. Never move a published tag. Do not link a tag before it exists.
