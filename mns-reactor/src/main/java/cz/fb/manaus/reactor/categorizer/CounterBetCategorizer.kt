package cz.fb.manaus.reactor.categorizer


import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
class CounterBetCategorizer : RealizedBetCategorizer {

    override val isSimulationSupported: Boolean = false

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val marketId = realizedBet.market.id
        val selectionId = realizedBet.settledBet.selectionId
        val side = realizedBet.settledBet.price.side
        val counterSide = side.opposite
        val bets = coverage.getBets(marketId, selectionId, counterSide)
        val avgCounter = bets
                .map { it.settledBet.price.price }
                .average()

        return if (avgCounter > 0) {
            val price = realizedBet.settledBet.price.price
            val prices = mapOf(side to price, counterSide to avgCounter)
            when {
                Price.priceEq(avgCounter, price) -> setOf(PREFIX + "zero")
                prices[Side.BACK]!! > prices[Side.LAY]!! -> setOf(PREFIX + "profit")
                prices[Side.BACK]!! < prices[Side.LAY]!! -> setOf(PREFIX + "loss")
                else -> throw IllegalStateException()
            }
        } else {
            setOf(PREFIX + "none")
        }
    }

    companion object {
        const val PREFIX = "counter_"
    }

}
