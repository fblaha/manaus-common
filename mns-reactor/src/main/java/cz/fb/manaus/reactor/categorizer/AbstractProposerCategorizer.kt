package cz.fb.manaus.reactor.categorizer

import com.google.common.base.Joiner.on
import com.google.common.base.MoreObjects
import com.google.common.base.Strings.emptyToNull
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.action.BetUtils
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractProposerCategorizer : SettledBetCategorizer {
    @Autowired
    private lateinit var betUtils: BetUtils

    protected fun getProposers(settledBet: SettledBet): List<String> {
        val properties = settledBet.betAction.properties
        val rawProposers = MoreObjects.firstNonNull(emptyToNull(properties[BetAction.PROPOSER_PROP]), "none")
        return betUtils.parseProposers(rawProposers)
    }

    protected fun getSideAware(prefix: String, side: Side, category: String): String {
        return prefix + on('.').join(side.name.toLowerCase(), category)
    }
}