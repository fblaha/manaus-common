package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
class ReciprocalBackFunction : ProgressFunction {

    override fun invoke(bet: SettledBet): Double? {
        val reciprocal = bet.betAction.marketPrices.getReciprocal(Side.BACK)
        return if (reciprocal.isPresent) reciprocal.asDouble else null
    }

}
