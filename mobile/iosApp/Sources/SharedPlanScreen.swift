import SwiftUI

enum SharedPlanResult: Equatable {
    case invalidInput
    case rejectedDuration
    case planned(id: String, topic: String, minutes: Int32)
}

struct SharedPlanScreen: View {
    private enum Field { case topic, minutes }
    @State private var topic = ""
    @State private var minutesText = ""
    @State private var result: SharedPlanResult?
    @FocusState private var editing: Field?

    init() {}

    var body: some View {
        Form {
            Section("Session details") {
                TextField("Topic (optional)", text: $topic)
                    .focused($editing, equals: .topic)
                    .accessibilityIdentifier("plan-topic")
                TextField("Minutes", text: $minutesText)
                    .focused($editing, equals: .minutes)
                    .keyboardType(.numbersAndPunctuation)
                    .accessibilityIdentifier("plan-minutes")
                Button("Create plan") {
                    editing = nil
                    result = SharedPlanAdapter().submit(topic: topic, minutesText: minutesText)
                }
                .accessibilityIdentifier("plan-submit")
            }
            switch result {
            case nil:
                EmptyView()
            case .invalidInput:
                Text("Enter a whole number of minutes.")
                    .accessibilityIdentifier("plan-input-error")
            case .rejectedDuration:
                Text("Choose 1 to 120 minutes.")
                    .accessibilityIdentifier("plan-duration-error")
            case let .planned(id, topic, minutes):
                Section("Your plan") {
                    Text(verbatim: "\(topic) · \(minutes) min")
                        .accessibilityIdentifier("plan-summary")
                    LabeledContent("Session ID") { Text(verbatim: id) }
                        .accessibilityIdentifier("plan-id")
                }
                PlanReceiptSection(planID: id).id(id)
            }
        }
        .navigationTitle("Plan a session")
        .onChange(of: topic) { _, _ in result = nil }
        .onChange(of: minutesText) { _, _ in result = nil }
    }
}
