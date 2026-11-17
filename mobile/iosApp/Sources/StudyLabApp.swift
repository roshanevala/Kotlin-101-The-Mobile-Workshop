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

    var body: some View {
        VStack(spacing: 0) {
            Toggle("Show study tip", isOn: $showTip)
                .padding(.horizontal)
                .accessibilityIdentifier("show-tip")
            StudyRoot(showTip: showTip)
        }
    }
}
