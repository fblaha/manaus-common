package cz.fb.manaus.reactor.profit

import com.google.common.base.Preconditions
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.CategoryService
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Objects.requireNonNull


@Service
class ProfitService {

    @Autowired
    private lateinit var categoryService: CategoryService
    @Autowired
    private lateinit var profitPlugin: ProfitPlugin

    fun getProfitRecords(bets: List<SettledBet>, projection: String?,
                         simulationAwareOnly: Boolean, chargeRate: Double): List<ProfitRecord> {
        var filtered = bets
        val coverage = BetCoverage.from(filtered)
        val charges = profitPlugin.getCharges(filtered, chargeRate)

        if (projection != null) {
            filtered = categoryService.filterBets(filtered, projection, coverage)
        }

        val betRecords = computeProfitRecords(filtered, simulationAwareOnly, charges, coverage)
        return mergeProfitRecords(betRecords)
    }

    fun mergeProfitRecords(records: Collection<ProfitRecord>): List<ProfitRecord> {
        val categories = records.groupBy { it.category }
        return categories.entries
                .map { e -> mergeCategory(e.key, e.value) }
                .sortedBy { it.category }
    }

    fun mergeCategory(category: String, records: Collection<ProfitRecord>): ProfitRecord {
        Preconditions.checkArgument(records.map { it.category }
                .all { category == it })
        val avgPrice = records
                .map { it.avgPrice }
                .average()
        val theoreticalProfit = records.map { it.theoreticalProfit }.sum()
        val charge = records.map { it.charge }.sum()
        val layCount = records.map { it.layCount }.sum()
        val backCount = records.map { it.backCount }.sum()
        val coverCount = records.map { it.coverCount }.sum()
        val result = ProfitRecord(category, theoreticalProfit, layCount, backCount, avgPrice, charge)
        if (coverCount > 0) {
            val diff = records.filter { profitRecord -> profitRecord.coverDiff != null }
                    .map { it.coverDiff }.average()
            result.coverDiff = diff
            result.coverCount = coverCount
        }
        return result
    }

    private fun computeProfitRecords(bets: List<SettledBet>, simulationAwareOnly: Boolean,
                                     charges: Map<String, Double>, coverage: BetCoverage): List<ProfitRecord> {
        // TODO parallel stream was here
        return bets.flatMap { bet ->
            val categories = categoryService.getSettledBetCategories(bet, simulationAwareOnly, coverage)
            categories.map { category ->
                val charge = charges[bet.betAction.betId]!!
                Preconditions.checkState(charge >= 0, charge)
                toProfitRecord(bet, category, charge, coverage)
            }
        }
    }

    fun toProfitRecord(bet: SettledBet, category: String, chargeContribution: Double, coverage: BetCoverage): ProfitRecord {
        val type = requireNonNull(bet.price.side)
        val price = bet.price.price
        val result: ProfitRecord
        result = if (type === Side.BACK) {
            ProfitRecord(category, bet.profitAndLoss, 0, 1, price, chargeContribution)
        } else {
            ProfitRecord(category, bet.profitAndLoss, 1, 0, price, chargeContribution)
        }
        val marketId = bet.betAction.market.id
        val selectionId = bet.selectionId
        if (coverage.isCovered(marketId, selectionId)) {
            val backPrice = coverage.getPrice(marketId, selectionId, Side.BACK)
            val layPrice = coverage.getPrice(marketId, selectionId, Side.LAY)
            result.coverDiff = backPrice - layPrice
            result.coverCount = 1
        }
        return result
    }

}
