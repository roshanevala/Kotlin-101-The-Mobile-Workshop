import Foundation
import StudyShared
import Testing
#if PRACTICE
@testable import StudyLabPractice
#else
@testable import StudyLabReference
#endif

private final class RecordingPlanIds: NSObject, SessionIdSource {
    private(set) var count = 0

    func nextId() -> String {
        count += 1
        return "import-00\(count)"
    }
}

struct SharedPlanAdapterTests {
    @Test func malformedOrOverflowingTextNeverRequestsAnId() {
        let ids = RecordingPlanIds()
        let adapter = SharedPlanAdapter(ids: ids)
        for text in ["", "twenty", "2.5", "2147483648", "-2147483649"] {
            #expect(adapter.submit(topic: "Swift", minutesText: text) == .invalidInput)
        }
        #expect(ids.count == 0)
    }

    @Test func representableDurationsOutsideTheSharedRangeAreRejected() {
        let ids = RecordingPlanIds()
        let adapter = SharedPlanAdapter(ids: ids)
        for text in ["0", "121", "-2147483648", "2147483647"] {
            #expect(adapter.submit(topic: nil, minutesText: text) == .rejectedDuration)
        }
        #expect(ids.count == 0)
    }

    @Test func acceptedEndpointsKeepTheSharedIdAndMinutes() {
        let ids = RecordingPlanIds()
        let adapter = SharedPlanAdapter(ids: ids)
        #expect(adapter.submit(topic: "Swift", minutesText: " 1\n") ==
                .planned(id: "import-001", topic: "Swift", minutes: 1))
        #expect(adapter.submit(topic: "Swift", minutesText: "120") ==
                .planned(id: "import-002", topic: "Swift", minutes: 120))
        #expect(ids.count == 2)
    }

    @Test func acceptedTopicsUseTheTextReturnedByKotlin() {
        let adapter = SharedPlanAdapter(ids: RecordingPlanIds())
        #expect(adapter.submit(topic: nil, minutesText: "25") ==
                .planned(id: "import-001", topic: "Untitled", minutes: 25))
        #expect(adapter.submit(topic: "  \t", minutesText: "25") ==
                .planned(id: "import-002", topic: "Untitled", minutes: 25))
        #expect(adapter.submit(topic: "  Swift  tasks\n", minutesText: "25") ==
                .planned(id: "import-003", topic: "Swift  tasks", minutes: 25))
    }

    @Test func defaultAdapterUsesThePlatformIdSource() {
        let result = SharedPlanAdapter().submit(topic: "Swift", minutesText: "25")
        guard case let .planned(id, _, _) = result else {
            Issue.record("A 25-minute session should be accepted")
            return
        }
        #expect(UUID(uuidString: id) != nil)
    }
}
