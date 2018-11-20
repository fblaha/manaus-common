package cz.fb.manaus.reactor.categorizer

import com.google.common.base.Joiner
import com.google.common.base.MoreObjects
import com.google.common.base.Strings
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.action.BetUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class ProposerCategorizer : RealizedBetCategorizer {

    @Autowired
    private lateinit var betUtils: BetUtils

    private fun getProposers(realizedBet: RealizedBet): List<String> {
        val properties = realizedBet.betAction.properties
        val rawProposers = MoreObjects.firstNonNull(Strings.emptyToNull(properties[BetAction.PROPOSER_PROP]), "none")
        return betUtils.parseProposers(rawProposers)
    }

    private fun getSideAware(prefix: String, side: Side, category: String): String {
        return prefix + Joiner.on('.').join(side.name.toLowerCase(), category)
    }

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val proposers = getProposers(realizedBet)
        val builder = mutableSetOf<String>()
        val side = realizedBet.settledBet.price.side
        for (proposer in proposers) {
            builder.add(getSideAware("proposer_", side, proposer))
        }
        return builder.toSet()
    }
}
