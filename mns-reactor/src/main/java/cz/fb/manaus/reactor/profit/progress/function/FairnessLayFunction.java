package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class FairnessLayFunction implements ProgressFunction {

    @Autowired
    private FairnessPolynomialCalculator calculator;

    @Override
    public OptionalDouble function(SettledBet bet) {
        MarketPrices marketPrices = bet.getBetAction().getMarketPrices();
        return calculator.getFairness(marketPrices.getWinnerCount(), marketPrices.getBestPrices(Side.LAY));
    }

}
