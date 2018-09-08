package cz.fb.manaus.ischia.validator.update

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractTooCloseUpdateEpsilonValidator
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@BackLoserBet
@LayLoserBet
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Profile("matchbook")
class TheSamePriceUpdateEpsilonValidator : AbstractTooCloseUpdateEpsilonValidator(0.025) {

    override fun isDowngradeAccepting(): Boolean {
        return false
    }
}