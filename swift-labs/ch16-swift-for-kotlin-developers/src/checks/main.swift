import Foundation

func sampleSessions() -> [StudySession] {
    [StudySession(id: 1, topic: "Kotlin", minutes: 25, isCompleted: false),
     StudySession(id: 2, topic: "Swift", minutes: 40, isCompleted: false)]
}

struct NamedCard: TopicNamed { let topic: String }

func runChecks() -> Int {
    let checks: [(String, () -> Bool)] = [
        ("parse nil stays absent", { parseMinutes(nil) == nil }),
        ("parse blank stays absent", { parseMinutes(" \n ") == nil }),
        ("parse trims surrounding whitespace", { parseMinutes(" \n25\t") == 25 }),
        ("parse representable positive integer", { parseMinutes("40") == 40 }),
        ("parse zero before business validation", { parseMinutes("0") == 0 }),
        ("parse negative before business validation", { parseMinutes("-1") == -1 }),
        ("parse noninteger text stays absent", { parseMinutes("2.5") == nil }),
        ("parse overflowing integer stays absent", { parseMinutes(String(repeating: "9", count: 100)) == nil }),
        ("reject zero ID first", { makeSession(id: 0, topic: "Swift", minutes: 0) == .invalid(.invalidID) }),
        ("reject negative ID", { makeSession(id: -1, topic: "Swift", minutes: 25) == .invalid(.invalidID) }),
        ("reject zero duration", { makeSession(id: 1, topic: "Swift", minutes: 0) == .invalid(.invalidMinutes) }),
        ("reject duration above 180", { makeSession(id: 1, topic: "Swift", minutes: 181) == .invalid(.invalidMinutes) }),
        ("accept lower duration boundary", {
            makeSession(id: 1, topic: "Swift", minutes: 1) == .valid(StudySession(id: 1, topic: "Swift", minutes: 1, isCompleted: false))
        }),
        ("accept upper duration boundary", {
            makeSession(id: 1, topic: "Swift", minutes: 180) == .valid(StudySession(id: 1, topic: "Swift", minutes: 180, isCompleted: false))
        }),
        ("nil topic has fallback", {
            makeSession(id: 1, topic: nil, minutes: 25) == .valid(StudySession(id: 1, topic: "Untitled", minutes: 25, isCompleted: false))
        }),
        ("blank topic has fallback", {
            makeSession(id: 1, topic: " \n ", minutes: 25) == .valid(StudySession(id: 1, topic: "Untitled", minutes: 25, isCompleted: false))
        }),
        ("trim topic while preserving interior spaces", {
            makeSession(id: 1, topic: "  Swift  models \n", minutes: 25) == .valid(StudySession(id: 1, topic: "Swift  models", minutes: 25, isCompleted: false))
        }),
        ("new validated session is incomplete", {
            guard case .valid(let session) = makeSession(id: 9, topic: "ARC", minutes: 30) else { return false }
            return session.id == 9 && !session.isCompleted
        }),
        ("complete only the identified record", {
            var plan = StudyPlan(sessions: sampleSessions())
            let changed = plan.complete(id: 2)
            return changed && !plan.sessions[0].isCompleted && plan.sessions[1].isCompleted
        }),
        ("copied plan does not mutate the original", {
            let original = StudyPlan(sessions: sampleSessions())
            var copy = original
            _ = copy.complete(id: 2)
            return original.sessions.allSatisfy { !$0.isCompleted } && copy.sessions[1].isCompleted
        }),
        ("appending to a copy preserves original array", {
            let original = StudyPlan(sessions: sampleSessions())
            var copy = original
            copy.sessions.append(StudySession(id: 3, topic: "ARC", minutes: 10, isCompleted: false))
            return original.sessions.count == 2 && copy.sessions.count == 3
        }),
        ("absent ID is a no-op returning false", {
            var plan = StudyPlan(sessions: sampleSessions())
            let before = plan.sessions
            return !plan.complete(id: 99) && plan.sessions == before
        }),
        ("repeated completion is a no-op returning false", {
            var plan = StudyPlan(sessions: sampleSessions())
            _ = plan.complete(id: 1)
            let before = plan.sessions
            return !plan.complete(id: 1) && plan.sessions == before
        }),
        ("duplicate IDs affect first match only", {
            var plan = StudyPlan(sessions: [
                StudySession(id: 1, topic: "One", minutes: 10, isCompleted: false),
                StudySession(id: 1, topic: "Two", minutes: 20, isCompleted: false)
            ])
            return plan.complete(id: 1) && plan.sessions[0].isCompleted && !plan.sessions[1].isCompleted
        }),
        ("total includes completed minutes only", {
            var rows = sampleSessions(); rows[0].isCompleted = true
            return StudyPlan(sessions: rows).completedMinutes == 25
        }),
        ("empty plan has zero completed minutes", { StudyPlan(sessions: []).completedMinutes == 0 }),
        ("session equality compares all stored values", {
            let first = sampleSessions()[0]
            var changed = first; changed.isCompleted = true
            return first == sampleSessions()[0] && changed != first && !first.isCompleted
        }),
        ("generic helper preserves order and duplicate topics", {
            topicNames([NamedCard(topic: "Swift"), NamedCard(topic: "ARC"), NamedCard(topic: "Swift")]) == ["Swift", "ARC", "Swift"]
        }),
        ("generic helper handles empty input", { topicNames([NamedCard]()).isEmpty }),
        ("generic helper also accepts session records", { topicNames(sampleSessions()) == ["Kotlin", "Swift"] }),
        ("callback works while owner is kept alive", {
            let coach = StudyCoach(name: "Ada"); coach.installEncouragement()
            return withExtendedLifetime(coach) { coach.encouragement?() == "Keep going, Ada!" }
        }),
        ("weak observer does not retain an ordinary owner", {
            var coach: StudyCoach? = StudyCoach(name: "Ada")
            weak var observer = coach
            coach = nil
            return observer == nil
        }),
        ("installed callback does not form an owner cycle", {
            var coach: StudyCoach? = StudyCoach(name: "Ada")
            weak var observer = coach
            coach?.installEncouragement(); coach = nil
            let released = observer == nil
            observer?.encouragement = nil
            return released
        }),
        ("retained callback is safe after owner release", {
            var coach: StudyCoach? = StudyCoach(name: "Ada")
            weak var observer = coach
            coach?.installEncouragement()
            let action = coach?.encouragement
            coach = nil
            let correct = observer == nil && action?() == "Coach unavailable"
            observer?.encouragement = nil
            return correct
        }),
    ]
    var failures = 0
    for (name, body) in checks {
        if body() { print("PASS \(name)") }
        else { failures += 1; print("FAIL \(name)") }
    }
    print("\(checks.count - failures)/\(checks.count) checks passed")
    return failures
}

exit(runChecks() == 0 ? EXIT_SUCCESS : EXIT_FAILURE)
