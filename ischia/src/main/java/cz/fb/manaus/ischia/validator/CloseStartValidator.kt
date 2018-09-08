package cz.fb.manaus.ischia.validator

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@BackLoserBet
@LayLoserBet
@Component
class CloseStartValidator : Validator {

    override fun validate(context: BetContext): ValidationResult {
        val openDate = context.marketPrices.market.event.openDate
        val seconds = Instant.now().until(openDate.toInstant(), ChronoUnit.SECONDS)
        return ValidationResult.of(seconds > 30)
    }

}