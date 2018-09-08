package cz.fb.manaus.rest

import com.google.common.base.Splitter
import com.google.common.base.Stopwatch
import com.google.common.collect.Ordering.from
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.betting.action.BetUtils
import cz.fb.manaus.reactor.profit.ProfitService
import cz.fb.manaus.reactor.profit.progress.CoverageFunctionProfitService
import cz.fb.manaus.reactor.profit.progress.ProgressProfitService
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.Comparator.comparing
import java.util.Comparator.comparingDouble
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

@Controller
@Profile(ManausProfiles.DB)
class ProfitController {

    @Autowired
    private lateinit var profitService: ProfitService
    @Autowired
    private lateinit var progressProfitService: ProgressProfitService
    @Autowired
    private lateinit var coverageService: CoverageFunctionProfitService
    @Autowired
    private lateinit var provider: ExchangeProvider
    @Autowired
    private lateinit var betLoader: SettledBetLoader
    @Autowired
    private lateinit var betUtils: BetUtils

    // TODO reduce arguments
    @ResponseBody
    @RequestMapping(value = ["/profit/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getProfitRecords(@PathVariable interval: String,
                         @RequestParam(required = false) filter: Optional<String>,
                         @RequestParam(required = false) sort: Optional<String>,
                         @RequestParam(required = false) projection: Optional<String>,
                         @RequestParam(required = false) charge: Optional<Double>,
                         @RequestParam(required = false) ceiling: Optional<Double>,
                         @RequestParam(defaultValue = "true") cache: Boolean): List<ProfitRecord> {
        var settledBets = loadBets(interval, cache)

        val ceil = ceiling.orElse(-1.0)
        if (ceil > 0) {
            settledBets = settledBets.map { betUtils.limitBetAmount(ceil, it) }
        }
        val stopwatch = Stopwatch.createStarted()
        var profitRecords = profitService.getProfitRecords(settledBets, projection,
                false, getChargeRate(charge))
        logTime(stopwatch, "Profit records computed")
        if (filter.isPresent) {
            val filters = parseFilter(filter.get())
            profitRecords = profitRecords
                    .filter { filters.any { token -> token in it.category } }
        }
        if (sort.isPresent) {
            profitRecords = from(COMPARATORS[sort.get()]!!).sortedCopy(profitRecords)
        }
        return profitRecords
    }

    @ResponseBody
    @RequestMapping(value = ["/fc-progress/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getProgressRecords(@PathVariable interval: String,
                           @RequestParam(defaultValue = "5") chunkCount: Int,
                           @RequestParam(required = false) function: Optional<String>,
                           @RequestParam(required = false) charge: Optional<Double>,
                           @RequestParam(required = false) projection: Optional<String>,
                           @RequestParam(defaultValue = "true") cache: Boolean): List<ProfitRecord> {
        val bets = loadBets(interval, cache)
        val stopwatch = Stopwatch.createStarted()
        val chargeRate = getChargeRate(charge)
        val records = progressProfitService.getProfitRecords(bets, function, chunkCount, chargeRate, projection)
        logTime(stopwatch, "Profit records computed")
        return records
    }

    @ResponseBody
    @RequestMapping(value = ["/fc-coverage/" + IntervalParser.INTERVAL], method = [RequestMethod.GET])
    fun getCoverageRecords(@PathVariable interval: String,
                           @RequestParam(required = false) function: Optional<String>,
                           @RequestParam(required = false) charge: Optional<Double>,
                           @RequestParam(required = false) projection: Optional<String>,
                           @RequestParam(defaultValue = "true") cache: Boolean): List<ProfitRecord> {
        val bets = loadBets(interval, cache)
        val stopwatch = Stopwatch.createStarted()
        val chargeRate = getChargeRate(charge)
        val records = coverageService.getProfitRecords(bets, function, chargeRate, projection)
        logTime(stopwatch, "Profit records computed")
        return records
    }

    private fun loadBets(@PathVariable interval: String, @RequestParam(defaultValue = "true") cache: Boolean): List<SettledBet> {
        val stopwatch = Stopwatch.createStarted()
        val bets = betLoader.load(interval, cache)
        logTime(stopwatch, "Bets fetched")
        return bets
    }

    private fun logTime(stopwatch: Stopwatch, messagePrefix: String) {
        val elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.log(Level.INFO, "{0} in ''{1}'' seconds", arrayOf(messagePrefix, elapsed))
    }

    private fun getChargeRate(chargeRate: Optional<Double>): Double {
        return chargeRate.orElse(provider.chargeRate)
    }

    private fun parseFilter(rawFilter: String): List<String> {
        return Splitter.on(',').splitToList(rawFilter)
    }

    companion object {
        val COMPARATORS: Map<String, Comparator<ProfitRecord>> = mapOf(
                "category" to comparing<ProfitRecord, String> { it.category },
                "betProfit" to comparingDouble { it.betProfit },
                "profit" to comparingDouble { it.profit })
        private val log = Logger.getLogger(ProfitController::class.java.simpleName)
    }
}