package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.NameAware
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator

interface PriceProposer : Validator, NameAware {

    val isMandatory: Boolean
        get() = true

    fun getProposedPrice(event: BetEvent): Double?


    override val isDowngradeAccepting: Boolean
        get() = false


    override val isPriceRequired: Boolean
        get() = false


    override fun validate(event: BetEvent): ValidationResult {
        return ValidationResult.OK
    }
}
