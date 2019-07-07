package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.MarketFootprint
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class Importer(private val marketRepository: MarketRepository,
               private val settledBetRepository: SettledBetRepository,
               private val betActionRepository: BetActionRepository) {

    fun import(footprint: MarketFootprint) {
        val (market, actions, settledBets) = footprint
        marketRepository.saveOrUpdate(market)
        actions.forEach { betActionRepository.saveOrUpdate(it) }
        settledBets.forEach { settledBetRepository.saveOrUpdate(it) }
    }
}