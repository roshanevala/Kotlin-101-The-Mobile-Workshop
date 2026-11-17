import Foundation

func runChecks() -> Int {
    let checks: [(String, () -> Bool)] = [
        ("initial values and drafts agree", {
            let store = StudyStore()
            return store.sessions.map(\.id) == [1, 2] && store.draft(id: 1) == "Kotlin" && store.draft(id: 2) == "Swift"
        }),
        ("typing changes only the draft", {
            let store = StudyStore(); store.editDraft(id: 1, text: "Draft")
            return store.draft(id: 1) == "Draft" && store.session(id: 1)?.topic == "Kotlin"
        }),
        ("save trims the selected topic only", {
            let store = StudyStore(); store.editDraft(id: 2, text: " \nObservation  ")
            return store.save(id: 2) && store.session(id: 2)?.topic == "Observation"
                && store.draft(id: 2) == "Observation" && store.session(id: 1)?.topic == "Kotlin"
        }),
        ("blank save preserves the committed value", {
            let store = StudyStore(); store.editDraft(id: 1, text: " \n ")
            return !store.save(id: 1) && store.session(id: 1)?.topic == "Kotlin"
        }),
        ("cancel reloads the committed value", {
            let store = StudyStore(); store.editDraft(id: 1, text: "Draft"); store.cancel(id: 1)
            return store.draft(id: 1) == "Kotlin"
        }),
        ("unknown IDs cannot create or edit records", {
            let store = StudyStore(); let before = store.sessions
            store.editDraft(id: 99, text: "Ghost"); store.cancel(id: 99)
            return !store.save(id: 99) && store.drafts[99] == nil && store.sessions == before
        }),
        ("completion affects its identified row", {
            let store = StudyStore(); store.setCompleted(id: 2, value: true)
            return store.completedCount == 1 && store.session(id: 1)?.isCompleted == false && store.session(id: 2)?.isCompleted == true
        }),
        ("completion can be reversed", {
            let store = StudyStore(); store.setCompleted(id: 1, value: true); store.setCompleted(id: 1, value: false)
            return store.completedCount == 0
        }),
        ("filter does not remove domain records", {
            let store = StudyStore(); store.setCompleted(id: 1, value: true); store.includeCompleted = false
            return store.visibleSessions.map(\.id) == [2] && store.sessions.count == 2 && store.completedCount == 1
        }),
        ("showing completed records restores visibility", {
            let store = StudyStore(); store.setCompleted(id: 1, value: true); store.includeCompleted = false
            store.includeCompleted = true
            return store.visibleSessions.map(\.id) == [1, 2]
        }),
        ("equal topics keep distinct identity and drafts", {
            let store = StudyStore(); store.editDraft(id: 2, text: "Kotlin"); _ = store.save(id: 2)
            store.editDraft(id: 1, text: "First draft")
            return store.sessions.map(\.id) == [1, 2] && store.session(id: 2)?.topic == "Kotlin" && store.draft(id: 2) == "Kotlin"
        }),
        ("a new owner starts fresh in-memory state", {
            let old = StudyStore(); old.editDraft(id: 1, text: "Saved"); _ = old.save(id: 1)
            let fresh = StudyStore()
            return fresh.session(id: 1)?.topic == "Kotlin" && fresh.completedCount == 0
        }),
    ]
    var failures = 0
    for (name, body) in checks {
        if body() { print("PASS \(name)") }
        else { print("FAIL \(name)"); failures += 1 }
    }
    print("\(checks.count - failures)/\(checks.count) model checks passed")
    return failures
}

exit(runChecks() == 0 ? EXIT_SUCCESS : EXIT_FAILURE)
