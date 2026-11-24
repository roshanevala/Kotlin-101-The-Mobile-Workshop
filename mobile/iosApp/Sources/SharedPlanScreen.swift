import SwiftUI

enum SharedPlanResult: Equatable {
    case invalidInput
    case rejectedDuration
    case planned(id: String, topic: String, minutes: Int32)
}

struct SharedPlanScreen: View {
    let owner: PlannerDraftOwner
    @Binding var design: DraftDesign

    var body: some View {
        PlanDraftForm(draft: owner.draft(for: design), design: $design)
            .id(design)
    }
}

private struct PlanDraftForm: View {
    private enum Field { case topic, minutes }
    @State private var draft: PlannerDraft
    @Binding private var design: DraftDesign
    @State private var result: SharedPlanResult?
    @FocusState private var editing: Field?

    init(draft: PlannerDraft, design: Binding<DraftDesign>) {
        _draft = State(initialValue: draft)
        _design = design
    }

    var body: some View {
        Form {
            Section("Draft implementation") {
                Picker("Draft implementation", selection: $design) {
                    Text("Native").tag(DraftDesign.native)
                    Text("Shared").tag(DraftDesign.shared)
                }
                .pickerStyle(.segmented)
                .accessibilityIdentifier("draft-design")
            }
            Section("Session details") {
                TextField("Topic (optional)", text: Binding(
                    get: { draft.values.topic }, set: { draft.editTopic($0) }
                ))
                    .focused($editing, equals: .topic)
                    .accessibilityIdentifier("plan-topic")
                TextField("Minutes", text: Binding(
                    get: { draft.values.minutesText }, set: { draft.editMinutes($0) }
                ))
                    .focused($editing, equals: .minutes)
                    .keyboardType(.numbersAndPunctuation)
                    .accessibilityIdentifier("plan-minutes")
                Button("Create plan") {
                    editing = nil
                    result = SharedPlanAdapter().submit(topic: draft.values.topic, minutesText: draft.values.minutesText)
                }
                .accessibilityIdentifier("plan-submit")
                Button("Clear draft") {
                    editing = nil
                    draft.clear()
                    result = nil
                }
                .disabled(draft.values.topic.isEmpty && draft.values.minutesText.isEmpty && result == nil)
                .accessibilityIdentifier("plan-clear")
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
        .onChange(of: draft.values) { _, _ in result = nil }
    }
}
