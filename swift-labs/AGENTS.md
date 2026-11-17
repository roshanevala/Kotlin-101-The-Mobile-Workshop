# Swift exercise guidance

Apply the repository root guidance. These are small native Swift CLI exercises, independent of both Gradle workspaces and the native iOS client.

- Chapter 16 is checked with Xcode 27.0 (27A266a), Apple Swift 6.4, and Swift 6 language mode on macOS/Apple silicon. Do not call that a latest-version or cross-platform support claim.
- Use `python3 lab.py` from the chapter directory. It invokes the selected Xcode compiler through `xcrun swiftc`, with explicit practice/reference sources and isolated build output. No SwiftPM dependency or test framework is required yet.
- `src/starter/StudyPlan.swift` is the tracked exercise; `src/practice/StudyPlan.swift` is learner-owned and ignored. Prepare only if absent. Never overwrite or stage it. `clean` removes only `.build` and preserves answers.
- Reference actions must neither prepare nor compile practice; practice actions must not compile reference. Keep destructive/source-isolation checks in temporary copies.
- Run `check-solution` normally and with `--optimized`; the fixed acceptance runner checks behavior without Swift `assert`, which optimizations can disable. `examples` runs the six complete lesson programs.
- Keep optionals and enum validation explicit, preserve ID lookup/completion policy, and retain value semantics for plans/session records. Model ARC ownership with a class and an escaping closure; weak references must safely become nil when the last owner releases the instance.
- Do not introduce SwiftUI, tasks/actors, KMP, or an iOS app in this language chapter. Swift concurrency and framework testing are later topics.
- Keep compiler caches, binaries, generated state, and personal toolchain paths untracked. Report hosted CI and IDE/device limits accurately.
