import Testing
import StudyShared
#if PRACTICE
@testable import StudyLabPractice
#else
@testable import StudyLabReference
#endif

@MainActor
struct PlanReceiptTests {
    @Test func confirmationReturnsTheOriginalID() async {
        let receipt = PlanReceiptModel(request: PlanReceiptRequest(planId: "plan-42"))
        receipt.confirm()
        receipt.cancel()
        await receipt.wait()
        #expect(receipt.state == .confirmed("plan-42"))
    }

    @Test func blankIDArrivesAsARecoverableError() async {
        let receipt = PlanReceiptModel(request: PlanReceiptRequest(planId: " "))
        await receipt.wait()
        #expect(receipt.state == .failed)
    }

    @Test func explicitCancellationReleasesTheKotlinWait() async {
        let receipt = PlanReceiptModel(request: PlanReceiptRequest(planId: "cancel-me"))
        receipt.cancel()
        receipt.confirm()
        await receipt.wait()
        #expect(receipt.state == .cancelled)
    }

    @Test(.timeLimit(.minutes(1)))
    func cancellingTheSwiftTaskReleasesThePendingKotlinReceipt() async throws {
        let request = PlanReceiptRequest(planId: "old-preview")
        let receipt = PlanReceiptModel(request: request)
        // Register a second waiter directly on Kotlin. Its callback observes the actual decision.
        let (delivery, reply) = AsyncStream<Bool>.makeStream()
        request.awaitReceipt { id, error in
            reply.yield(id == nil && error != nil)
            reply.finish()
        }
        let task = Task { await receipt.wait() }
        while receipt.state == .ready {
            try Task.checkCancellation()
            await Task.yield()
        }
        task.cancel()
        var outcome = delivery.makeAsyncIterator()
        #expect(await outcome.next() == true)
        request.confirm() // Also release the waiter if the test reaches its time limit.
        await task.value
        #expect(receipt.state == .cancelled)
    }
}
