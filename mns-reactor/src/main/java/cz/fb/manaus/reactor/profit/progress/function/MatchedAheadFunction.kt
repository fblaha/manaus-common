package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class MatchedAheadFunction : AheadTimeFunction {

    override fun getRelatedTime(bet: SettledBet): Instant? {
        val matched = bet.matched
        return if (matched == null) {
            null
        } else {
            matched.toInstant()
        }
    }

}
