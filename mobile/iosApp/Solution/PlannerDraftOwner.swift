@MainActor
final class PlannerDraftOwner {
    private let makeDraft: @MainActor (DraftDesign) -> PlannerDraft
    private var drafts: [DraftDesign: PlannerDraft] = [:]

    init(makeDraft: @escaping @MainActor (DraftDesign) -> PlannerDraft = { PlannerDraft(design: $0) }) {
        self.makeDraft = makeDraft
    }

    func draft(for design: DraftDesign) -> PlannerDraft {
        if let existing = drafts[design] { return existing }
        let draft = makeDraft(design)
        drafts[design] = draft
        return draft
    }
}
