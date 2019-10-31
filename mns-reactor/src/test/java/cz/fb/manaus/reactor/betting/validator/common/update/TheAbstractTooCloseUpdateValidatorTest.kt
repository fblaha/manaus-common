package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.rounding.RoundingService
import cz.fb.manaus.reactor.rounding.decrement
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

class TheAbstractTooCloseUpdateValidatorTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var validator: TestValidator
    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var factory: ReactorTestFactory


    @Test
    fun `accept back`() {
        val oldPrice = Price(2.5, 5.0, Side.BACK)
        val oldBet = betTemplate.copy(requestedPrice = oldPrice)

        val context = factory.newBetEvent(Side.BACK, runnerPrices, oldBet)
        context.newPrice = oldPrice
        assertEquals(ValidationResult.NOP, validator.validate(context))

        context.newPrice = roundingService.decrement(oldPrice, 1, provider.minPrice, provider::matches)
        assertEquals(ValidationResult.NOP, validator.validate(context))

        context.newPrice = roundingService.decrement(oldPrice, 2, provider.minPrice, provider::matches)
        assertEquals(ValidationResult.NOP, validator.validate(context))

        context.newPrice = roundingService.decrement(oldPrice, 3, provider.minPrice, provider::matches)
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))
    }

    @Test
    fun `accept lay`() {
        val newOne = Price(3.15, 3.0, Side.LAY)
        val oldOne = Price(3.1, 3.0, Side.LAY)

        val oldBet = betTemplate.copy(requestedPrice = oldOne)

        val context = factory.newBetEvent(Side.LAY, runnerPrices, oldBet)
        context.newPrice = newOne
        assertEquals(ValidationResult.NOP, validator.validate(context))
        context.newPrice = newOne.copy(price = 3.2)
        assertEquals(ValidationResult.NOP, validator.validate(context))
        context.newPrice = newOne.copy(price = 3.05)
        assertEquals(ValidationResult.NOP, validator.validate(context))
        context.newPrice = newOne.copy(price = 3.25)
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))
    }

    @Test
    fun `minimal price`() {
        val newOne = Price(1.04, 5.0, Side.LAY)
        val oldOne = Price(provider.minPrice, 5.0, Side.LAY)
        val oldBet = betTemplate.copy(requestedPrice = oldOne)

        val context = factory.newBetEvent(Side.LAY, runnerPrices, oldBet)
        context.newPrice = newOne
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))
    }

    @Component
    private class TestValidator : AbstractTooCloseUpdateValidator(setOf(-2, -1, 1, 2))

}