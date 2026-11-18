import SwiftUI

@main
struct StudyLabApp: App {
    @Environment(\.scenePhase) private var scenePhase

    var body: some Scene {
        WindowGroup { StudyShell() }
            .onChange(of: scenePhase) { _, phase in
                print("StudyScene: \(phase)")
            }
    }
}

struct StudyShell: View {
    @State private var showTip = false
    @State private var showingReview = false

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Toggle("Show study tip", isOn: $showTip)
                    .accessibilityIdentifier("show-tip")
                Divider().fixedSize()
                Button("Review prompts") { showingReview = true }
                    .accessibilityIdentifier("open-review")
            }
            .padding(.horizontal)
            StudyRoot(showTip: showTip)
        }
        .sheet(isPresented: $showingReview) {
            NavigationStack {
                ReviewScreen()
                    .toolbar {
                        ToolbarItem(placement: .cancellationAction) {
                            Button("Close") { showingReview = false }
                                .accessibilityIdentifier("close-review")
                        }
                    }
            }
        }
    }
}
