import Testing
#if PRACTICE
@testable import StudyLabPractice
#else
@testable import StudyLabReference
#endif

private enum SourceFailure: Error { case unavailable }

// The test chooses when each suspended request returns, including after cancellation.
struct PendingReview: Sendable {
    fileprivate let task: Task<Void, Never>
    fileprivate let reply: CheckedContinuation<String, any Error>

    func succeed(_ prompt: String) async {
        reply.resume(returning: prompt)
        await task.value
    }

    func fail() async {
        reply.resume(throwing: SourceFailure.unavailable)
        await task.value
    }

    fileprivate func throwCancellation() async {
        reply.resume(throwing: CancellationError())
        await task.value
    }
}

private struct Arrival: Sendable {
    let topic: String
    let reply: CheckedContinuation<String, any Error>
}

actor ControlledReviewSource: ReviewSource {
    private var nextArrival: AsyncStream<Arrival>.Continuation?

    private func watchNext(_ continuation: AsyncStream<Arrival>.Continuation) {
        nextArrival = continuation
    }

    @MainActor
    func start(_ topic: String, on loader: ReviewLoader) async throws -> PendingReview {
        let (stream, arrival) = AsyncStream<Arrival>.makeStream()
        await watchNext(arrival)
        let task = Task {
            await loader.load(topic)
            // End the wait even if a broken loader returns without calling prompt.
            arrival.finish()
        }
        var iterator = stream.makeAsyncIterator()
        let observed = await iterator.next()
        let request = try #require(observed, "load should call its source")
        #expect(request.topic == topic)
        return PendingReview(task: task, reply: request.reply)
    }

    func prompt(for topic: String) async throws -> String {
        guard let arrival = nextArrival else {
            Issue.record("The loader made an extra source call")
            throw SourceFailure.unavailable
        }
        nextArrival = nil
        return try await withCheckedThrowingContinuation { reply in
            if case .terminated = arrival.yield(Arrival(topic: topic, reply: reply)) {
                // The loader returned before its unstructured task reached the source.
                reply.resume(throwing: CancellationError())
            }
            arrival.finish()
        }
    }
}

@MainActor
struct ReviewLoaderTests {
    @Test func transitionsFromIdleThroughLoadingToReady() async throws {
        let source = ControlledReviewSource()
        let loader = ReviewLoader(source: source)
        #expect(loader.state == .idle)
        let request = try await source.start("Kotlin", on: loader)
        #expect(loader.state == .loading("Kotlin"))
        await request.succeed("Explain suspension")
        #expect(loader.state == .ready("Explain suspension"))
    }

    @Test func retriesFailureForTheSameTopic() async throws {
        let source = ControlledReviewSource()
        let loader = ReviewLoader(source: source)
        let first = try await source.start("Swift", on: loader)
        await first.fail()
        #expect(loader.state == .failed)
        let retry = try await source.start("Swift", on: loader)
        #expect(loader.state == .loading("Swift"))
        await retry.succeed("Name an actor boundary")
        #expect(loader.state == .ready("Name an actor boundary"))
    }

    @Test func olderSuccessCannotReplaceNewerLoading() async throws {
        let source = ControlledReviewSource()
        let loader = ReviewLoader(source: source)
        let first = try await source.start("Kotlin", on: loader)
        let second = try await source.start("Swift", on: loader)
        await first.succeed("Old result")
        #expect(loader.state == .loading("Swift"))
        await second.succeed("New result")
        #expect(loader.state == .ready("New result"))
    }

    @Test func explicitCancelRejectsLateSuccess() async throws {
        let source = ControlledReviewSource()
        let loader = ReviewLoader(source: source)
        let request = try await source.start("Kotlin", on: loader)
        loader.cancel()
        #expect(loader.state == .idle)
        await request.succeed("Arrived after cancel")
        #expect(loader.state == .idle)
    }

    @Test func cancelledTaskDiscardsUncooperativeSuccess() async throws {
        let source = ControlledReviewSource()
        let loader = ReviewLoader(source: source)
        let request = try await source.start("Kotlin", on: loader)
        request.task.cancel()
        await request.succeed("Source ignored cancellation")
        #expect(loader.state == .idle)
    }

    @Test func cancelledTaskDiscardsSourceFailure() async throws {
        let source = ControlledReviewSource()
        let loader = ReviewLoader(source: source)
        let request = try await source.start("Swift", on: loader)
        request.task.cancel()
        await request.fail()
        #expect(loader.state == .idle)
    }

    @Test func sourceCancellationReturnsToIdle() async throws {
        let source = ControlledReviewSource()
        let loader = ReviewLoader(source: source)
        let request = try await source.start("Kotlin", on: loader)
        await request.throwCancellation()
        #expect(loader.state == .idle)
    }

    @Test func alreadyCancelledLoadLeavesActiveRequestAlone() async throws {
        let source = ControlledReviewSource()
        let loader = ReviewLoader(source: source)
        let active = try await source.start("Swift", on: loader)
        let cancelled = Task {
            await loader.load("Kotlin")
        }
        // Both closures use MainActor. Cancel before this test next suspends.
        cancelled.cancel()
        await cancelled.value
        #expect(loader.state == .loading("Swift"))
        await active.succeed("Current prompt")
        #expect(loader.state == .ready("Current prompt"))
    }
}
