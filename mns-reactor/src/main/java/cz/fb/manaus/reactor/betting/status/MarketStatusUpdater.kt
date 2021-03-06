package cz.fb.manaus.reactor.betting.status

import cz.fb.manaus.core.model.MarketSnapshotEvent
import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.core.model.status
import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@Profile(ManausProfiles.DB)
class MarketStatusUpdater(
        private val repository: Repository<MarketStatus>
) : MarketSnapshotListener {

    override fun onMarketSnapshot(marketSnapshotEvent: MarketSnapshotEvent): List<BetCommand> {
        val snapshot = marketSnapshotEvent.snapshot
        val market = snapshot.market
        val bets = snapshot.currentBets
                .map { it.remote.status }
                .sortedBy { it.requestedPrice.side }
        val event = market.event
        val state = MarketStatus(
                id = market.id,
                eventDate = event.openDate,
                eventName = event.name,
                lastEvent = Instant.now(),
                bets = bets
        )
        repository.save(state)
        return emptyList()
    }
}