func demonstrate() {
    switch makeSession(id: 1, topic: nil, minutes: 25) {
    case .valid(let session): print("Topic: \(session.topic)")
    case .invalid: print("Topic rejected")
    }
    let original = StudyPlan(sessions: [
        StudySession(id: 1, topic: "Kotlin", minutes: 25, isCompleted: false),
        StudySession(id: 2, topic: "Swift", minutes: 40, isCompleted: false)
    ])
    var changed = original
    _ = changed.complete(id: 2)
    print("Original completed: \(original.completedMinutes)")
    print("Copy completed: \(changed.completedMinutes)")
    print("Topics: \(topicNames(changed.sessions).joined(separator: ", "))")
    var coach: StudyCoach? = StudyCoach(name: "Ada")
    coach?.installEncouragement()
    let action = coach?.encouragement
    coach = nil
    print("After release: \(action?() ?? "No action")")
}

demonstrate()
