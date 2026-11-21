package workshop.studylab.shared

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred

// One request per preview. A decision can arrive before or after Swift starts waiting.
class PlanReceiptRequest(private val planId: String) {
    private val confirmation = CompletableDeferred<Unit>()

    @Throws(IllegalArgumentException::class, CancellationException::class)
    suspend fun awaitReceipt(): String {
        require(planId.isNotBlank()) { "A receipt needs a plan ID." }
        confirmation.await()
        return planId
    }

    fun confirm() {
        confirmation.complete(Unit)
    }

    fun cancel() {
        confirmation.cancel()
    }
}
