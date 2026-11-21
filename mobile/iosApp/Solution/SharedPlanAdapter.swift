import Foundation
import StudyShared

struct SharedPlanAdapter {
    private let planner: SessionPlanner

    init(ids: any SessionIdSource = PlatformSessionIds_iosKt.platformSessionIds()) {
        planner = SessionPlanner(ids: ids)
    }

    func submit(topic: String?, minutesText: String) -> SharedPlanResult {
        let text = minutesText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard let minutes = Int32(text) else { return .invalidInput }
        guard let session = planner.plan(topic: topic, minutes: minutes) else {
            return .rejectedDuration
        }
        return .planned(id: session.id, topic: session.topic, minutes: session.minutes)
    }
}
