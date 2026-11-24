@MainActor
final class PlannerDraftOwner {
    private let makeDraft: @MainActor (DraftDesign) -> PlannerDraft

    init(makeDraft: @escaping @MainActor (DraftDesign) -> PlannerDraft = { PlannerDraft(design: $0) }) {
        self.makeDraft = makeDraft
    }

    func draft(for design: DraftDesign) -> PlannerDraft {
        makeDraft(design)
    }
}
