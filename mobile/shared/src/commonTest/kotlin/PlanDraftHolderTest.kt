package workshop.studylab.shared

import kotlin.test.Test
import kotlin.test.assertEquals

class PlanDraftHolderTest {
    @Test
    fun editingEitherFieldKeepsTheOtherFieldVerbatim() {
        val holder = PlanDraftHolder()
        holder.editTopic("  Kotlin  flows ")
        holder.editMinutes("0x")
        assertEquals(PlanDraftSnapshot("  Kotlin  flows ", "0x"), holder.snapshot)

        holder.editTopic("")
        assertEquals(PlanDraftSnapshot("", "0x"), holder.snapshot)
    }

    @Test
    fun anEarlierSnapshotKeepsItsOriginalValuesAfterEditsAndClear() {
        val holder = PlanDraftHolder()
        holder.editTopic("Kotlin")
        holder.editMinutes(" 025 ")
        val earlier = holder.snapshot

        holder.editMinutes("30")
        holder.clear()
        assertEquals(PlanDraftSnapshot("Kotlin", " 025 "), earlier)
        assertEquals(PlanDraftSnapshot("", ""), holder.snapshot)
    }

    @Test
    fun aSecondHolderStartsEmptyAndHasItsOwnLifetime() {
        val first = PlanDraftHolder()
        first.editTopic("First")
        first.editMinutes("-")

        val second = PlanDraftHolder()
        assertEquals(PlanDraftSnapshot("", ""), second.snapshot)
        second.editTopic("Second")
        first.clear()
        assertEquals(PlanDraftSnapshot("Second", ""), second.snapshot)
    }
}
