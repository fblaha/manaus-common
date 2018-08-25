package cz.fb.manaus.reactor.betting.validator.common.update

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.rounding.RoundingService
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Optional.of
import kotlin.test.assertEquals

class TheAbstractTooCloseUpdateValidatorTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var validator: TestValidator
    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var factory: ReactorTestFactory
    @Autowired
    private lateinit var provider: ExchangeProvider

    private lateinit var prices: MarketPrices
    private lateinit var runnerPrices: RunnerPrices

    @Before
    fun setUp() {
        prices = factory.createMarket(0.1, listOf(0.4, 0.3, 0.3))
        runnerPrices = prices.getRunnerPrices(CoreTestFactory.HOME)
    }

    @Test
    fun `accept back`() {
        val oldPrice = Price(2.5, 5.0, Side.BACK)
        val oldBet = ReactorTestFactory.newBet(oldPrice)

        assertEquals(ValidationResult.REJECT, validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, of(oldBet))
                .withNewPrice(oldPrice)))

        assertEquals(ValidationResult.REJECT, validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, of(oldBet))
                .withNewPrice(roundingService.decrement(oldPrice, 1).get())))

        assertEquals(ValidationResult.REJECT, validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, of(oldBet))
                .withNewPrice(roundingService.decrement(oldPrice, 2).get())))

        assertEquals(ValidationResult.ACCEPT, validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, of(oldBet))
                .withNewPrice(roundingService.decrement(oldPrice, 3).get())))
    }

    @Test
    fun `accept lay`() {
        val newOne = mock<Price>()
        val oldOne = mock<Price>()
        whenever(newOne.side).thenReturn(Side.LAY)
        whenever(oldOne.side).thenReturn(Side.LAY)
        whenever(newOne.price).thenReturn(3.15)
        whenever(oldOne.price).thenReturn(3.1)
        val oldBet = ReactorTestFactory.newBet(oldOne)

        val context = factory.newBetContext(Side.LAY, prices, runnerPrices, of<Bet>(oldBet)).withNewPrice(newOne)
        assertEquals(ValidationResult.REJECT, validator.validate(context))
        whenever(newOne.price).thenReturn(3.2)
        assertEquals(ValidationResult.REJECT, validator.validate(context))
        whenever(newOne.price).thenReturn(3.05)
        assertEquals(ValidationResult.REJECT, validator.validate(context))
        whenever(newOne.price).thenReturn(3.25)
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))
    }

    @Test
    fun `minimal price`() {
        val newOne = mock<Price>()
        val oldOne = mock<Price>()
        whenever(newOne.side).thenReturn(Side.LAY)
        whenever(oldOne.side).thenReturn(Side.LAY)
        whenever(oldOne.price).thenReturn(provider.minPrice)
        whenever(newOne.price).thenReturn(1.04)
        val oldBet = ReactorTestFactory.newBet(oldOne)

        val context = factory.newBetContext(Side.LAY, prices, runnerPrices, of<Bet>(oldBet)).withNewPrice(newOne)
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))
    }

    @Component
    private class TestValidator : AbstractTooCloseUpdateValidator(setOf(-2, -1, 1, 2))

}