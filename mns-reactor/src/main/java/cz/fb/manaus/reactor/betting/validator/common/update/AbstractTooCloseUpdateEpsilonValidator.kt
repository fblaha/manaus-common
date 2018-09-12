package cz.fb.manaus.reactor.betting.validator.common.update

import com.google.common.collect.Range
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator

abstract class AbstractTooCloseUpdateEpsilonValidator protected constructor(private val epsilon: Double) : Validator {

    override fun validate(context: BetContext): ValidationResult {
        val oldOne = context.oldBet.get().requestedPrice.price
        val newOne = context.newPrice.get().price
        val epsilon = (oldOne - 1) * this.epsilon
        val closeRange = Range.closed(oldOne - epsilon, oldOne + epsilon)
        return ValidationResult.of(!closeRange.contains(newOne))
    }

    override fun isUpdateOnly(): Boolean {
        return true
    }

}
