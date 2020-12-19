package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.test.AbstractTestCase
import cz.fb.manaus.reactor.PricesTestFactory
import cz.fb.manaus.reactor.betting.HOME_EVENT_BACK
import cz.fb.manaus.reactor.betting.HOME_EVENT_LAY
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals


class BestPriceProposerTest : AbstractTestCase() {

    @Autowired
    private lateinit var layProposer: LayProposer

    @Autowired
    private lateinit var backProposer: BackProposer

    @Autowired
    private lateinit var factory: PricesTestFactory

    @Test
    fun `lay propose`() {
        val event = HOME_EVENT_LAY.copy(marketPrices = factory.newMarketPrices(2.0, 4.5))
        assertEquals(ValidationResult.OK, layProposer.validate(event))
        assertEquals(2.01, layProposer.getProposedPrice(event))
    }

    @Test
    fun check() {
        assertEquals(ValidationResult.OK, layProposer.validate(HOME_EVENT_LAY))
        assertEquals(ValidationResult.OK, backProposer.validate(HOME_EVENT_BACK))
    }

    @Test
    fun `back propose`() {
        val prices = factory.newMarketPrices(2.5, 3.5)
        val context = HOME_EVENT_BACK.copy(marketPrices = prices)
        assertEquals(ValidationResult.OK, backProposer.validate(context))
        assertEquals(3.475, backProposer.getProposedPrice(context))
    }

    @Component
    class LayProposer : PriceProposer by BestPriceProposer(0.01)

    @Component
    class BackProposer : PriceProposer by BestPriceProposer(0.01)

}
