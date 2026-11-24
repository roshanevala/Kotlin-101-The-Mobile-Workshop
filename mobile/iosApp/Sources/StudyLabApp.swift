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
    @State private var showingPlanner = false
    @State private var draftOwner = PlannerDraftOwner()
    @State private var draftDesign = DraftDesign.native

    var body: some View {
        VStack(spacing: 0) {
            VStack(alignment: .leading) {
                Toggle("Show study tip", isOn: $showTip)
                    .accessibilityIdentifier("show-tip")
                HStack {
                    Button("Review prompts") { showingReview = true }
                        .accessibilityIdentifier("open-review")
                    Spacer()
                    Button("Plan a session") { showingPlanner = true }
                        .accessibilityIdentifier("open-plan")
                }
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
        .sheet(isPresented: $showingPlanner) {
            NavigationStack {
                SharedPlanScreen(owner: draftOwner, design: $draftDesign)
                    .toolbar {
                        ToolbarItem(placement: .cancellationAction) {
                            Button("Close") { showingPlanner = false }
                                .accessibilityIdentifier("close-plan")
                        }
                    }
            }
        }
    }
}
