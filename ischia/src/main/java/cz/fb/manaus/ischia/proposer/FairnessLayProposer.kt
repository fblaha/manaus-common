package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.proposer.common.AbstractFairnessProposer
import org.springframework.stereotype.Component

@Component
@LayLoserBet
class FairnessLayProposer(downgradeStrategy: (BetContext) -> Double) : AbstractFairnessProposer(Side.LAY, downgradeStrategy)
