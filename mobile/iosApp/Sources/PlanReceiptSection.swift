import SwiftUI
import StudyShared

@Observable @MainActor
final class PlanReceiptModel {
    enum State: Equatable {
        case ready, waiting, cancelled, failed
        case confirmed(String)
    }

    private(set) var state: State = .ready
    private let request: PlanReceiptRequest

    init(request: PlanReceiptRequest) {
        self.request = request
    }

    func confirm() {
        request.confirm()
    }

    func cancel() {
        request.cancel()
    }

    func wait() async {
        state = .waiting
        do {
            let id = try await withTaskCancellationHandler {
                try Task.checkCancellation()
                return try await request.awaitReceipt()
            } onCancel: {
                Task { @MainActor in self.cancel() }
            }
            try Task.checkCancellation()
            state = .confirmed(id)
        } catch {
            let cancelled = Task.isCancelled || (error as NSError).kotlinException is KotlinCancellationException
            state = cancelled ? .cancelled : .failed
        }
    }
}

struct PlanReceiptSection: View {
    @State private var receipt: PlanReceiptModel

    init(planID: String) {
        _receipt = State(initialValue: PlanReceiptModel(request: PlanReceiptRequest(planId: planID)))
    }

    var body: some View {
        Section("Confirmation") {
            switch receipt.state {
            case .ready, .waiting:
                Button("Confirm plan") { receipt.confirm() }
                    .accessibilityIdentifier("confirm-plan")
                Button("Cancel confirmation") { receipt.cancel() }
                    .accessibilityIdentifier("cancel-plan-confirmation")
            case .confirmed(let id):
                Text("Confirmed plan \(id)").accessibilityIdentifier("plan-receipt")
            case .cancelled:
                Text("Confirmation cancelled.").accessibilityIdentifier("plan-receipt-cancelled")
            case .failed:
                Text("Couldn’t confirm this plan.").accessibilityIdentifier("plan-receipt-failed")
            }
        }
        .task { await receipt.wait() }
    }
}
