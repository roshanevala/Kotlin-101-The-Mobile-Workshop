import SwiftUI

struct StudyFilters: View {
    @Bindable var store: StudyStore

    var body: some View {
        Toggle("Show completed sessions", isOn: $store.includeCompleted)
            .accessibilityIdentifier("include-completed")
    }
}

struct TopicField: View {
    @Binding var text: String
    @FocusState private var editing: Bool

    var body: some View {
        TextField("Session topic", text: $text)
            .focused($editing)
            .accessibilityIdentifier("topic-field")
            .toolbar {
                ToolbarItemGroup(placement: .keyboard) {
                    Spacer()
                    Button("Done") { editing = false }
                        .accessibilityIdentifier("dismiss-keyboard")
                }
            }
    }
}
