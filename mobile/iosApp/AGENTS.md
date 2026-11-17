# Native iOS guidance

Apply root guidance. Parent mobile commands/toolchain pins concern Android; this child is the native SwiftUI client of the same Study Lab project.

- Chapter 17 uses Xcode 27.0/Apple Swift 6.4, Swift 6 language mode, iOS deployment target 17.0, and no external package. Observation is the reason for the iOS 17 minimum. Verify later changes against official documentation.
- Open StudyLab.xcodeproj after `python3 ios.py prepare`. Shared sources are fixed scaffolding. Starter/StudyScreens.swift is the tracked mission; Practice/StudyScreens.swift is learner-owned and ignored; Solution/StudyScreens.swift is the reference. Never overwrite or stage an existing practice file.
- Practice/reference app and UI-test targets have separate schemes, identifiers, source phases, and derived data. Reference actions must not prepare/compile practice. Do destructive/isolation checks only in temporary copies. Clean removes build output, preserving answers.
- Own the observable store with State at a stable root identity. Borrow writable projections with Bindable/Binding; keep one authoritative draft per ID. Pass stable integer navigation values and key rows by immutable IDs. Drafts survive Back/reopen, ordinary parent reconstruction, and background/foreground within the process; process termination starts fresh. No persistence claim is made.
- ScenePhase observation must not reset model values on activation. Keep logs free of user-entered topics. Keep network, storage, tasks/actors, and shared KMP integration for later chapters. UI-test MainActor annotations are fixed scaffolding, not a new asynchronous lesson.
- Use `python3 ios.py build --reference` and `test --reference --device <UDID>`. Run tests on an explicitly selected dedicated iOS 17+ simulator; do not reset or erase personal simulators. Check accessibility identifiers separately from meaningful labels, Dynamic Type, keyboard, and scene behavior.
- Published mobile tags remain immutable; corrections require a new versioned checkpoint with both source choices. Do not link an unpublished tag. Report simulator, physical-device, IDE/preview, and hosted-CI limits accurately.
