import Testing
#if PRACTICE
@testable import StudyLabPractice
#else
@testable import StudyLabReference
#endif

@MainActor
struct ReviewRegressionTests {
    @Test func lateFailureAfterRetryingSameTopic() async throws {
        // Replace this marker with a regression for overlapping attempts at the same topic.
        Issue.record("Add your regression test for a late failure after retrying the same topic")
    }
}
