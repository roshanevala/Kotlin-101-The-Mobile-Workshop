import Foundation

protocol ReviewSource: Sendable {
    func prompt(for topic: String) async throws -> String
}

enum ReviewState: Equatable, Sendable {
    case idle
    case loading(String)
    case ready(String)
    case failed
}

actor LocalReviewSource: ReviewSource {
    private enum SourceError: Error { case unavailable }
    private var shouldFailFirstSwiftRequest = true

    func prompt(for topic: String) async throws -> String {
        try await Task.sleep(for: .seconds(1))
        try Task.checkCancellation()

        switch topic {
        case "Kotlin":
            return "When does a Kotlin coroutine check for cancellation?"
        case "Swift":
            if shouldFailFirstSwiftRequest {
                shouldFailFirstSwiftRequest = false
                throw SourceError.unavailable
            }
            return "How does a Swift actor protect its state?"
        default:
            throw SourceError.unavailable
        }
    }
}
