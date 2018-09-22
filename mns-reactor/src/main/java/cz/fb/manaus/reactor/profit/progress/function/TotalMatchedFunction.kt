package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.util.*

@Component
class TotalMatchedFunction : ProgressFunction {

    override fun apply(bet: SettledBet): OptionalDouble {
        val matchedAmount = bet.betAction.market.matchedAmount
        return if (matchedAmount == null) OptionalDouble.empty() else OptionalDouble.of(matchedAmount)
    }

}