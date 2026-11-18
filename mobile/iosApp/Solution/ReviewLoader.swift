import Foundation
import Observation

@MainActor
@Observable
final class ReviewLoader {
    private let source: any ReviewSource
    private var activeID: UUID?
    private(set) var state: ReviewState = .idle

    init(source: any ReviewSource) {
        self.source = source
    }

    func load(_ topic: String) async {
        guard !Task.isCancelled else { return }
        let id = UUID()
        activeID = id
        state = .loading(topic)

        let result: ReviewState
        do {
            let prompt = try await source.prompt(for: topic)
            result = .ready(prompt)
        } catch is CancellationError {
            result = .idle
        } catch {
            result = .failed
        }

        guard activeID == id else { return }
        activeID = nil
        state = Task.isCancelled ? .idle : result
    }

    func cancel() {
        activeID = nil
        state = .idle
    }
}
