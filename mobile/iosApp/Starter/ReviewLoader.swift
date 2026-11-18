import Observation

@MainActor
@Observable
final class ReviewLoader {
    private let source: any ReviewSource
    private(set) var state: ReviewState = .idle

    init(source: any ReviewSource) {
        self.source = source
    }

    func load(_ topic: String) async {
        state = .loading(topic)
        do {
            let prompt = try await source.prompt(for: topic)
            state = .ready(prompt)
        } catch {
            state = .failed
        }
    }

    func cancel() {
        state = .idle
    }
}
