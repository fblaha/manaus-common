package cz.fb.manaus.rest;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.reactor.profit.ProfitService;
import cz.fb.manaus.reactor.profit.progress.CoverageFunctionProfitService;
import cz.fb.manaus.reactor.profit.progress.ProgressProfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Ordering.from;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;

@Controller
public class ProfitController {

    public static final Map<String, Comparator<ProfitRecord>> COMPARATORS = of(
            "category", comparing(ProfitRecord::getCategory),
            "betProfit", comparingDouble(ProfitRecord::getBetProfit),
            "profit", comparingDouble(ProfitRecord::getProfit));

    private static final Logger log = Logger.getLogger(ProfitController.class.getSimpleName());

    @Autowired
    private ProfitService profitService;
    @Autowired
    private ProgressProfitService progressProfitService;
    @Autowired
    private CoverageFunctionProfitService coverageService;
    @Autowired
    private ExchangeProvider provider;
    @Autowired
    private SettledBetLoader betLoader;

    @ResponseBody
    @RequestMapping(value = "/profit/" + IntervalParser.INTERVAL, method = RequestMethod.GET)
    public List<ProfitRecord> getProfitRecords(@PathVariable String interval,
                                               @RequestParam(required = false) Optional<String> filter,
                                               @RequestParam(required = false) Optional<String> sort,
                                               @RequestParam(required = false) Optional<String> projection,
                                               @RequestParam(required = false) Optional<String> namespace,
                                               @RequestParam(required = false) Optional<Double> charge,
                                               @RequestParam(defaultValue = "true") boolean cache) {
        List<SettledBet> settledBets = loadBets(interval, cache);
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<ProfitRecord> profitRecords = profitService.getProfitRecords(settledBets, projection,
                false, namespace, getChargeRate(charge));
        logTime(stopwatch, "Profit records computed");
        if (filter.isPresent()) {
            List<String> filters = parseFilter(filter.get());
            profitRecords = profitRecords.stream()
                    .filter(profitRecord -> filters.stream().anyMatch(token -> profitRecord.getCategory().contains(token)))
                    .collect(Collectors.toList());
        }
        if (sort.isPresent()) {
            profitRecords = from(COMPARATORS.get(sort.get())).sortedCopy(profitRecords);
        }
        return profitRecords;
    }

    @ResponseBody
    @RequestMapping(value = "/fc-progress/" + IntervalParser.INTERVAL, method = RequestMethod.GET)
    public List<ProfitRecord> getProgressRecords(@PathVariable String interval,
                                                 @RequestParam(defaultValue = "5") int chunkCount,
                                                 @RequestParam(required = false) Optional<String> function,
                                                 @RequestParam(required = false) Optional<Double> charge,
                                                 @RequestParam(required = false) Optional<String> projection,
                                                 @RequestParam(defaultValue = "true") boolean cache) {
        List<SettledBet> bets = loadBets(interval, cache);
        Stopwatch stopwatch = Stopwatch.createStarted();
        double chargeRate = getChargeRate(charge);
        List<ProfitRecord> records = progressProfitService.getProfitRecords(bets, function, chunkCount, chargeRate, projection);
        logTime(stopwatch, "Profit records computed");
        return records;
    }

    @ResponseBody
    @RequestMapping(value = "/fc-coverage/" + IntervalParser.INTERVAL, method = RequestMethod.GET)
    public List<ProfitRecord> getCoverageRecords(@PathVariable String interval,
                                                 @RequestParam(required = false) Optional<String> function,
                                                 @RequestParam(required = false) Optional<Double> charge,
                                                 @RequestParam(required = false) Optional<String> projection,
                                                 @RequestParam(defaultValue = "true") boolean cache) {
        List<SettledBet> bets = loadBets(interval, cache);
        Stopwatch stopwatch = Stopwatch.createStarted();
        double chargeRate = getChargeRate(charge);
        List<ProfitRecord> records = coverageService.getProfitRecords(bets, function, chargeRate, projection);
        logTime(stopwatch, "Profit records computed");
        return records;
    }

    private List<SettledBet> loadBets(@PathVariable String interval, @RequestParam(defaultValue = "true") boolean cache) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<SettledBet> bets = betLoader.load(interval, cache);
        logTime(stopwatch, "Bets fetched");
        return bets;
    }

    private void logTime(Stopwatch stopwatch, String messagePrefix) {
        long elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS);
        log.log(Level.INFO, "{0} in ''{1}'' seconds", new Object[]{messagePrefix, elapsed});
    }

    private double getChargeRate(Optional<Double> chargeRate) {
        return chargeRate.orElse(provider.getChargeRate());
    }


    private List<String> parseFilter(String rawFilter) {
        return Splitter.on(',').splitToList(rawFilter);
    }

}
