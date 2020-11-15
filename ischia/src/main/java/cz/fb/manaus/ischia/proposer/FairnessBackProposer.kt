package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.FairnessProposer
import cz.fb.manaus.reactor.betting.strategy.chain
import cz.fb.manaus.reactor.betting.strategy.fixedStrategy
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component

@Component
@LayUniverse
@BackUniverse
class FairnessBackProposer(priceService: PriceService) : PriceProposer by FairnessProposer(
        Side.BACK,
        priceService,
        chain(
                fixedStrategy(Side.LAY, 0.087, { it.version == 1 }),
                fixedStrategy(Side.LAY, 0.082, { it.version in 2..3 }),
                fixedStrategy(Side.LAY, 0.077, { it.version in 4..7 }),
                fixedStrategy(Side.LAY, 0.072),

                fixedStrategy(Side.BACK, 0.08, { it.version == 1 }),
                fixedStrategy(Side.BACK, 0.075, { it.version in 2..3 }),
                fixedStrategy(Side.BACK, 0.07, { it.version in 4..7 }),
                fixedStrategy(Side.BACK, 0.065),
        )
)
