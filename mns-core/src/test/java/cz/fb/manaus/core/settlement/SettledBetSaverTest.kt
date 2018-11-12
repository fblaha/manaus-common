package cz.fb.manaus.core.settlement

import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.model.settledBet
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SettledBetSaverTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var saver: SettledBetSaver
    @Autowired
    private lateinit var marketRepository: MarketRepository
    @Autowired
    private lateinit var betActionRepository: BetActionRepository

    @Test
    fun testSaver() {
        marketRepository.saveOrUpdate(market)
        betActionRepository.save(betAction.copy(betID = "testSaver"))

        val bet = settledBet.copy(id = "testSaver")
        assertEquals(SaveStatus.OK, saver.saveBet(bet))
        assertEquals(SaveStatus.COLLISION, saver.saveBet(bet))
        assertEquals(SaveStatus.NO_ACTION, saver.saveBet(bet.copy(id = "missing")))
    }
}