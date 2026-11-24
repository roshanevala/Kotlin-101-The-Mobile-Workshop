import Observation
import StudyShared

enum DraftDesign: String, CaseIterable {
    case native, shared
}

struct DraftValues: Equatable {
    let topic: String
    let minutesText: String
}

@MainActor
protocol PlanDraftEditing: AnyObject {
    var values: DraftValues { get }
    func editTopic(_ text: String)
    func editMinutes(_ text: String)
    func clear()
}

private final class NativeDraftBacking: PlanDraftEditing {
    private(set) var values = DraftValues(topic: "", minutesText: "")

    func editTopic(_ text: String) {
        values = DraftValues(topic: text, minutesText: values.minutesText)
    }

    func editMinutes(_ text: String) {
        values = DraftValues(topic: values.topic, minutesText: text)
    }

    func clear() {
        values = DraftValues(topic: "", minutesText: "")
    }
}

private final class KotlinDraftBacking: PlanDraftEditing {
    private let holder = PlanDraftHolder()

    var values: DraftValues {
        let snapshot = holder.snapshot
        return DraftValues(topic: snapshot.topic, minutesText: snapshot.minutesText)
    }

    func editTopic(_ text: String) {
        holder.editTopic(text: text)
    }

    func editMinutes(_ text: String) {
        holder.editMinutes(text: text)
    }

    func clear() {
        holder.clear()
    }
}

@Observable @MainActor
final class PlannerDraft {
    private let backing: any PlanDraftEditing
    private(set) var values: DraftValues

    init(backing: any PlanDraftEditing) {
        self.backing = backing
        values = backing.values
    }

    convenience init(design: DraftDesign) {
        switch design {
        case .native: self.init(backing: NativeDraftBacking())
        case .shared: self.init(backing: KotlinDraftBacking())
        }
    }

    func editTopic(_ text: String) {
        backing.editTopic(text)
        values = backing.values
    }

    func editMinutes(_ text: String) {
        backing.editMinutes(text)
        values = backing.values
    }

    func clear() {
        backing.clear()
        values = backing.values
    }
}
