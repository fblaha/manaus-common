package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.MarketFootprint
import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.model.homeSettledBet
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImporterTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var importer: Importer


    @Test
    fun import() {
        assertNull(marketRepository.read(market.id))
        assertTrue(betActionRepository.find(market.id).isEmpty())
        assertNull(settledBetRepository.read(homeSettledBet.id))

        val footprint = MarketFootprint(market, listOf(betAction), listOf(homeSettledBet))
        importer.import(footprint)

        assertNotNull(marketRepository.read(market.id))
        assertNotNull(settledBetRepository.read(homeSettledBet.id))
        assertTrue(betActionRepository.find(market.id).isNotEmpty())
    }
}