import Testing
#if PRACTICE
@testable import StudyLabPractice
#else
@testable import StudyLabReference
#endif

@MainActor
struct ReviewRegressionTests {
    @Test func lateFailureAfterRetryingSameTopic() async throws {
        let source = ControlledReviewSource()
        let loader = ReviewLoader(source: source)
        let first = try await source.start("Swift", on: loader)
        let second = try await source.start("Swift", on: loader)
        await first.fail()
        #expect(loader.state == .loading("Swift"))
        await second.succeed("Explain actor isolation")
        #expect(loader.state == .ready("Explain actor isolation"))
    }
}
