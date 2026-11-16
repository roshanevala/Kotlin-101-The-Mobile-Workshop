# Swift exercise setup

This workspace introduces native Swift through a small command-line exercise before the iOS UI client. Start with [Chapter 16](../chapters/16-swift-for-kotlin-developers/README.md). Keep the Kotlin labs and Android app in their existing workspaces; this lab needs neither Gradle nor an Android SDK.

## Check the toolchain

The locally checked setup is **macOS/Apple silicon, Xcode 27.0 (27A266a), Apple Swift 6.4**, and **Python 3.9+**. The commands select the installed Xcode compiler through `xcrun` and explicitly use **Swift 6 language mode**. Language mode and compiler version are separate settings. No simulator, iOS project, SwiftPM package, external dependency, or test-framework installation is needed for this chapter.

Install Xcode and finish its first-launch setup if needed. In Terminal, inspect the selection:

```sh
xcodebuild -version
xcrun swift --version
xcrun --find swiftc
python3 --version
```

The first two commands should identify the toolchain above for the exact checked setup. If `xcrun` cannot find the compiler, check Xcode's selected Command Line Tools and finish first-launch setup. An installation containing only another toolchain is a different setup; its compatibility is not established by the local verification record. This guide does not change your machine's Xcode selection.

## Run the mission

From the repository root:

```sh
cd swift-labs/ch16-swift-for-kotlin-developers
python3 lab.py run
```

The command creates `src/practice/StudyPlan.swift` from the tracked starter only if missing. Open that generated file in Xcode or a text editor, keeping the lesson open alongside it. Terminal commands are the verified run path; no Xcode project needs to be generated.

The starter deliberately prints an empty topic, **65** completed minutes in both original/copy, only one topic, and a callback that still retains its coach. A compiler warning that `changed` was never mutated refers to the class-reference binding; the referenced object can still change. A successful build followed by incorrect output is the mission's bug, while compiler/tool lookup failures require separate diagnosis.

| Command in the chapter directory | Purpose |
| --- | --- |
| `python3 lab.py prepare` | Fill only a missing learner file |
| `python3 lab.py run` | Build/run your answer |
| `python3 lab.py test` | Run the 34 fixed behavioral checks against your answer |
| `python3 lab.py run-solution` | Run the separate reference |
| `python3 lab.py check-solution` | Check the reference with the same acceptance runner |
| `python3 lab.py check-solution --optimized` | Check the reference with `-O` |
| `python3 lab.py examples` | Build/run the six complete lesson programs |
| `python3 lab.py clean` | Remove only compiler output/cache, preserving your answer |

The wrapper invokes `xcrun swiftc -swift-version 6`, selects `-Onone` or `-O`, supplies one model file plus the fixed runner/checker, then runs the produced binary. Build output and module caches stay under the chapter's ignored `.build`. Practice and reference output folders are separate. Reference actions neither prepare nor compile practice; practice actions never compile reference.

The acceptance runner prints PASS/FAIL lines and returns a failing exit status when a contract fails. It uses ordinary conditions rather than Swift `assert`, so its checks remain active under optimization. Some checks intentionally fail on the untouched starter. Swift Testing and XCTest roles arrive in Chapter 18; this small runner is not a replacement framework for an app's future test suite.

Normal Git staging excludes `src/practice` and `.build`. Preparation and `clean` preserve existing answers. To reset, first move `StudyPlan.swift` somewhere safe outside its source folder, then run `prepare`. Keep existing work when updating the course; never copy a reference over your real answer during routine verification.

Windows/Linux, older local compilers, and interactive Xcode build actions have not been checked. The configured Swift workflow uses a separately selected hosted Xcode installation and reports its actual version; consult [verification](../docs/maintainers/verification/ch16.md) and hosted results after publication for the scope actually exercised.
