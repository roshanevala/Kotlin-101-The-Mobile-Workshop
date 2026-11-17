import SwiftUI

struct StudyRoot: View {
    let showTip: Bool
    private var store = StudyStore()
    @State private var path: [Int] = []
    @Environment(\.scenePhase) private var scenePhase

    var body: some View {
        NavigationStack(path: $path) {
            List {
                if showTip {
                    Text("Small sessions. Steady progress.")
                        .accessibilityIdentifier("study-tip")
                }
                Section("Your progress") {
                    Text("Completed \(store.completedCount) of \(store.sessions.count)")
                        .accessibilityIdentifier("progress")
                    StudyFilters(store: store)
                }
                Section("Your sessions") {
                    ForEach(store.visibleSessions, id: \.topic) { session in
                        HStack {
                            NavigationLink(value: 1) {
                                VStack(alignment: .leading) {
                                    Text(session.topic)
                                    Text("Session \(session.id) · \(session.minutes) min")
                                        .font(.caption).foregroundStyle(.secondary)
                                }
                            }
                            .accessibilityIdentifier("session-\(session.id)")
                            Toggle("Complete session \(session.id): \(session.topic)", isOn: Binding(
                                get: { session.isCompleted },
                                set: { store.setCompleted(id: session.id, value: $0) }
                            ))
                            .labelsHidden()
                            .accessibilityIdentifier("complete-\(session.id)")
                        }
                    }
                }
            }
            .navigationTitle("Study Lab")
            .navigationDestination(for: Int.self) { id in StudyDetail(store: store, id: id) }
            .onChange(of: scenePhase) { _, phase in
                if phase == .active { store.reset() }
            }
        }
    }
}

struct StudyDetail: View {
    let store: StudyStore
    let id: Int
    @State private var draft = ""

    var body: some View {
        Form {
            if let session = store.session(id: id) {
                Section("Saved topic") {
                    Text(session.topic).accessibilityIdentifier("saved-topic")
                }
                Section("Edit a draft") {
                    TopicField(text: $draft)
                    Button("Clear draft") { draft = "" }
                        .accessibilityIdentifier("clear-draft")
                    Button("Save topic") { store.save(id: 1) }
                        .disabled(draft.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                        .accessibilityIdentifier("save-topic")
                    Button("Cancel edits") { store.cancel(id: id) }
                        .accessibilityIdentifier("cancel-edits")
                }
                Text("Your draft stays here when you go back. Save commits it; Cancel reloads the saved topic.")
                    .font(.footnote)
            } else {
                Text("Session unavailable. Return to the list.")
            }
        }
        .navigationTitle("Session \(id)")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { draft = store.draft(id: id) }
    }
}
