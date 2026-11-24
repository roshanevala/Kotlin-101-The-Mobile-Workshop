import Testing
#if PRACTICE
@testable import StudyLabPractice
#else
@testable import StudyLabReference
#endif

@MainActor
struct PlannerDraftTests {
    @Test func bothDesignsPreserveRawEdits() {
        for design in DraftDesign.allCases {
            let draft = PlannerDraft(design: design)
            draft.editMinutes("0x")
            draft.editTopic("  KMP  ")
            #expect(draft.values == DraftValues(topic: "  KMP  ", minutesText: "0x"))
            draft.editMinutes("025")
            #expect(draft.values == DraftValues(topic: "  KMP  ", minutesText: "025"))
        }
    }

    @Test func clearAffectsOnlyTheChosenDraft() {
        let owner = PlannerDraftOwner()
        let native = owner.draft(for: .native)
        let shared = owner.draft(for: .shared)
        native.editTopic("Swift")
        shared.editTopic("Kotlin")
        shared.editMinutes("75")
        native.clear()
        #expect(native.values == DraftValues(topic: "", minutesText: ""))
        #expect(shared.values == DraftValues(topic: "Kotlin", minutesText: "75"))
        shared.clear()
        #expect(shared.values == DraftValues(topic: "", minutesText: ""))
    }

    @Test func newOwnerStartsEmpty() {
        let first = PlannerDraftOwner()
        for design in DraftDesign.allCases {
            first.draft(for: design).editTopic("In progress")
        }
        let fresh = PlannerDraftOwner()
        for design in DraftDesign.allCases {
            #expect(fresh.draft(for: design).values == DraftValues(topic: "", minutesText: ""))
        }
    }

    @Test func reopeningFindsEarlierEdits() {
        let owner = PlannerDraftOwner()
        for design in DraftDesign.allCases {
            let draft = owner.draft(for: design)
            draft.editTopic(design.rawValue)
            draft.editMinutes(" 05 ")
        }
        for design in DraftDesign.allCases {
            let reopened = owner.draft(for: design)
            #expect(reopened.values == DraftValues(topic: design.rawValue, minutesText: " 05 "))
        }
    }

    @Test func factorySuppliesOneHolderPerDesign() {
        var calls: [DraftDesign: Int] = [:]
        let owner = PlannerDraftOwner { design in
            calls[design, default: 0] += 1
            return PlannerDraft(design: design)
        }
        for design in DraftDesign.allCases {
            let first = owner.draft(for: design)
            let second = owner.draft(for: design)
            #expect(first === second)
            #expect(calls[design] == 1)
        }
    }

    @Test func projectionPublishesTheBackingsValues() {
        let backing = RecordingDraft()
        let draft = PlannerDraft(backing: backing)
        #expect(draft.values == DraftValues(topic: "loaded", minutesText: "9"))
        draft.editTopic("input")
        #expect(draft.values == DraftValues(topic: "[input]", minutesText: "9"))
        draft.editMinutes("12")
        #expect(draft.values == DraftValues(topic: "[input]", minutesText: "[12]"))
        draft.clear()
        #expect(draft.values == DraftValues(topic: "cleared", minutesText: ""))
        #expect(backing.events == ["topic:input", "minutes:12", "clear"])
    }
}

// Distinct return values let the test observe whether Swift rereads the backing after an edit.
@MainActor
private final class RecordingDraft: PlanDraftEditing {
    private(set) var values = DraftValues(topic: "loaded", minutesText: "9")
    private(set) var events: [String] = []

    func editTopic(_ text: String) {
        events.append("topic:\(text)")
        values = DraftValues(topic: "[\(text)]", minutesText: values.minutesText)
    }

    func editMinutes(_ text: String) {
        events.append("minutes:\(text)")
        values = DraftValues(topic: values.topic, minutesText: "[\(text)]")
    }

    func clear() {
        events.append("clear")
        values = DraftValues(topic: "cleared", minutesText: "")
    }
}
