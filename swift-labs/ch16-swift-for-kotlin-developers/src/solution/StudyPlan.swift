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
    guard let text else { return nil }
    return Int(text.trimmingCharacters(in: .whitespacesAndNewlines))
}

func makeSession(id: Int, topic: String?, minutes: Int) -> SessionResult {
    guard id > 0 else { return .invalid(.invalidID) }
    guard (1...180).contains(minutes) else { return .invalid(.invalidMinutes) }
    let cleaned = topic?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
    return .valid(StudySession(id: id, topic: cleaned.isEmpty ? "Untitled" : cleaned,
                               minutes: minutes, isCompleted: false))
}

struct StudyPlan {
    var sessions: [StudySession]

    mutating func complete(id: Int) -> Bool {
        guard let index = sessions.firstIndex(where: { $0.id == id }) else { return false }
        guard !sessions[index].isCompleted else { return false }
        sessions[index].isCompleted = true
        return true
    }

    var completedMinutes: Int {
        sessions.filter { $0.isCompleted }.reduce(0) { $0 + $1.minutes }
    }
}

func topicNames<T: TopicNamed>(_ values: [T]) -> [String] {
    values.map { $0.topic }
}

final class StudyCoach {
    let name: String
    var encouragement: (() -> String)?

    init(name: String) { self.name = name }

    func installEncouragement() {
        encouragement = { [weak self] in
            guard let self else { return "Coach unavailable" }
            return "Keep going, \(self.name)!"
        }
    }
}
