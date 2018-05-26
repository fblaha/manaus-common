package cz.fb.manaus.reactor.price;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalDouble;

import static java.util.Objects.requireNonNull;

@Service
public class PriceService {

    @Autowired
    private RoundingService roundingService;
    @Autowired
    private ExchangeProvider provider;

    public double downgrade(double price, double downgradeFraction, Side side) {
        var aboveOne = price - 1;
        var targetFairness = 1 - downgradeFraction;
        Preconditions.checkState(Range.closed(0d, 1d).contains(targetFairness));

        if (requireNonNull(side) == Side.LAY) {
            return 1 + aboveOne * targetFairness;
        } else if (side == Side.BACK) {
            return 1 + aboveOne / targetFairness;
        }
        throw new IllegalStateException();
    }

    public boolean isDowngrade(double newPrice, double oldPrice, Side type) {
        if (Price.priceEq(newPrice, oldPrice)) return false;
        if (type == Side.BACK) {
            return newPrice > oldPrice;
        } else {
            return newPrice < oldPrice;
        }
    }

    /**
     * https://cs.wikipedia.org/wiki/S%C3%A1zkov%C3%BD_kurz
     */
    public double getFairnessFairPrice(double unfairPrice, double fairness) {
        return 1 + (unfairPrice - 1) / fairness;
    }

    /**
     * http://stats.stackexchange.com/questions/140269/how-to-convert-sport-odds-into-percentage
     */
    public double getOverroundFairPrice(double unfairPrice, double overround, int winnerCount, int runnerCount) {
        var probability = 1 / unfairPrice - (overround - winnerCount) / runnerCount;
        Preconditions.checkArgument(probability > 0, List.of(unfairPrice, overround, winnerCount, runnerCount));
        return 1 / probability;
    }

    public OptionalDouble getRoundedFairnessFairPrice(double unfairPrice, double fairness) {
        return roundingService.roundBet(getFairnessFairPrice(unfairPrice, fairness));
    }

}
