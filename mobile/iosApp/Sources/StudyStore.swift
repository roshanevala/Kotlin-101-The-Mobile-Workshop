import Foundation
import Observation

@Observable
final class StudyStore {
    private(set) var sessions: [StudySession] = []
    private(set) var drafts: [Int: String] = [:]
    var includeCompleted = true

    init() { reset() }

    var visibleSessions: [StudySession] {
        sessions.filter { includeCompleted || !$0.isCompleted }
    }
    var completedCount: Int { sessions.filter(\.isCompleted).count }

    func session(id: Int) -> StudySession? { sessions.first { $0.id == id } }
    func draft(id: Int) -> String { drafts[id] ?? "" }

    func editDraft(id: Int, text: String) {
        guard session(id: id) != nil else { return }
        drafts[id] = text
    }

    @discardableResult
    func save(id: Int) -> Bool {
        guard let index = sessions.firstIndex(where: { $0.id == id }) else { return false }
        let topic = draft(id: id).trimmingCharacters(in: .whitespacesAndNewlines)
        guard !topic.isEmpty else { return false }
        sessions[index].topic = topic
        drafts[id] = topic
        return true
    }

    func cancel(id: Int) {
        guard let session = session(id: id) else { return }
        drafts[id] = session.topic
    }

    func setCompleted(id: Int, value: Bool) {
        guard let index = sessions.firstIndex(where: { $0.id == id }) else { return }
        sessions[index].isCompleted = value
    }

    func reset() {
        sessions = [StudySession(id: 1, topic: "Kotlin", minutes: 25, isCompleted: false),
                    StudySession(id: 2, topic: "Swift", minutes: 40, isCompleted: false)]
        drafts = Dictionary(uniqueKeysWithValues: sessions.map { ($0.id, $0.topic) })
        includeCompleted = true
    }
}
