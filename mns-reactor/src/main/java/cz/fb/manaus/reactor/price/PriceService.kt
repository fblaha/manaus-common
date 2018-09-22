package cz.fb.manaus.reactor.price

import com.google.common.base.Preconditions
import com.google.common.collect.Range
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Objects.requireNonNull

@Service
class PriceService {

    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var provider: ExchangeProvider

    fun downgrade(price: Double, downgradeFraction: Double, side: Side): Double {
        val aboveOne = price - 1
        val targetFairness = 1 - downgradeFraction
        Preconditions.checkState(Range.closed(0.0, 1.0).contains(targetFairness))

        if (requireNonNull(side) === Side.LAY) {
            return 1 + aboveOne * targetFairness
        } else if (side === Side.BACK) {
            return 1 + aboveOne / targetFairness
        }
        throw IllegalStateException()
    }

    fun isDowngrade(newPrice: Double, oldPrice: Double, type: Side): Boolean {
        if (Price.priceEq(newPrice, oldPrice)) return false
        return if (type === Side.BACK) {
            newPrice > oldPrice
        } else {
            newPrice < oldPrice
        }
    }

    /**
     * https://cs.wikipedia.org/wiki/S%C3%A1zkov%C3%BD_kurz
     */
    fun getFairnessFairPrice(unfairPrice: Double, fairness: Double): Double {
        return 1 + (unfairPrice - 1) / fairness
    }

    /**
     * http://stats.stackexchange.com/questions/140269/how-to-convert-sport-odds-into-percentage
     */
    fun getOverroundFairPrice(unfairPrice: Double, overround: Double, winnerCount: Int, runnerCount: Int): Double {
        val probability = 1 / unfairPrice - (overround - winnerCount) / runnerCount
        Preconditions.checkArgument(probability > 0, listOf(unfairPrice, overround, winnerCount, runnerCount))
        return 1 / probability
    }

    fun getRoundedFairnessFairPrice(unfairPrice: Double, fairness: Double): Double? {
        return roundingService.roundBet(getFairnessFairPrice(unfairPrice, fairness))
    }

}