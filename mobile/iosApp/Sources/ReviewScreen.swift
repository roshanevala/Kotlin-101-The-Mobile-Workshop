import SwiftUI

private struct ReviewRequest: Equatable {
    let id = UUID()
    let topic: String
}

struct ReviewScreen: View {
    @State private var loader = ReviewLoader(source: LocalReviewSource())
    @State private var request: ReviewRequest?

    init() {}

    var body: some View {
        List {
            Section("Topic") {
                Button("Kotlin") { select("Kotlin") }
                    .accessibilityIdentifier("review-kotlin")
                Button("Swift") { select("Swift") }
                    .accessibilityIdentifier("review-swift")
            }
            Section("Review prompt") {
                switch loader.state {
                case .idle:
                    Text("Choose a topic to get a review prompt.")
                        .accessibilityIdentifier("review-idle")
                case .loading(let topic):
                    Text("Loading \(topic)…")
                        .accessibilityIdentifier("review-loading")
                    Button("Cancel") { cancel() }
                        .accessibilityIdentifier("review-cancel")
                case .ready(let prompt):
                    Text(prompt)
                        .accessibilityIdentifier("review-ready")
                case .failed:
                    if let request {
                        Text("Couldn’t load the \(request.topic) prompt.")
                            .accessibilityIdentifier("review-failed")
                        Button("Retry") { select(request.topic) }
                            .accessibilityIdentifier("review-retry")
                    } else {
                        Text("Couldn’t load the prompt.")
                            .accessibilityIdentifier("review-failed")
                    }
                }
            }
        }
        .navigationTitle("Review prompts")
        .task(id: request) { [request] in
            guard let request, request == self.request else { return }
            await loader.load(request.topic)
        }
        .onDisappear { cancel() }
    }

    private func select(_ topic: String) {
        loader.cancel()
        request = ReviewRequest(topic: topic)
    }

    private func cancel() {
        loader.cancel()
        request = nil
    }
}
