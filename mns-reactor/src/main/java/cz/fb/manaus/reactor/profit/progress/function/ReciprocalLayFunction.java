package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class ReciprocalLayFunction implements ProgressFunction {

    @Override
    public OptionalDouble function(SettledBet bet) {
        return bet.getBetAction().getMarketPrices().getReciprocal(Side.LAY);
    }

}
