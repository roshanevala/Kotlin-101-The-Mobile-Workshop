import Foundation

protocol TopicNamed {
    var topic: String { get }
}

struct StudySession: Equatable, TopicNamed {
    let id: Int
    let topic: String
    let minutes: Int
    var isCompleted: Bool
}

enum ValidationIssue: Equatable {
    case invalidID
    case invalidMinutes
}

enum SessionResult: Equatable {
    case valid(StudySession)
    case invalid(ValidationIssue)
}

func parseMinutes(_ text: String?) -> Int? {
    Int(text ?? "") ?? 0
}

func makeSession(id: Int, topic: String?, minutes: Int) -> SessionResult {
    .valid(StudySession(id: id, topic: topic ?? "", minutes: minutes, isCompleted: false))
}

final class StudyPlan {
    var sessions: [StudySession]

    init(sessions: [StudySession]) { self.sessions = sessions }

    func complete(id: Int) -> Bool {
        for index in sessions.indices { sessions[index].isCompleted = true }
        return true
    }

    var completedMinutes: Int {
        sessions.reduce(0) { $0 + $1.minutes }
    }
}

func topicNames<T: TopicNamed>(_ values: [T]) -> [String] {
    values.first.map { [$0.topic] } ?? []
}

final class StudyCoach {
    let name: String
    var encouragement: (() -> String)?

    init(name: String) { self.name = name }

    func installEncouragement() {
        encouragement = { "Keep going, \(self.name)!" }
    }
}
