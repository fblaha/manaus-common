package cz.fb.manaus.core.model

import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarketSnapshotTest {

    @Test
    fun `market coverage`() {
        val selectionId = 1L
        val side = Side.LAY
        val predecessor = Bet(
                marketId = "1",
                selectionId = selectionId,
                requestedPrice = Price(2.0, 2.0, side),
                placedDate = Instant.now().minus(2, ChronoUnit.HOURS),
                matchedAmount = 1.0
        )
        val successor = predecessor.copy(placedDate = Instant.now())
        val coverage = getMarketCoverage(listOf(successor.asTracked, predecessor.asTracked))
        assertEquals(1, coverage.size)
        assertEquals(successor.asTracked, coverage[SideSelection(side, selectionId)])
    }

    @Test
    fun `is active`() {
        val bet =
                Bet(
                        marketId = "1",
                        selectionId = 1L,
                        requestedPrice = Price(2.0, 2.0, Side.LAY),
                        placedDate = Instant.now().minus(2, ChronoUnit.HOURS),
                        matchedAmount = 1.0
                ).asTracked

        val coverage = getMarketCoverage(listOf(bet))
        assertTrue { coverage.isActive(bet.remote.selectionId) }
        assertFalse { coverage.isActive(bet.remote.selectionId + 100) }
    }

}