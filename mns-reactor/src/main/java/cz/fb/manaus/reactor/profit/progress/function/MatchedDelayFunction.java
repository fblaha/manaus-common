package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.OptionalDouble;

@Component
public class MatchedDelayFunction implements ProgressFunction {

    @Override
    public OptionalDouble function(SettledBet bet) {
        Instant placed = bet.getPlacedOrActionDate().toInstant();
        if (bet.getMatched() != null) {
            Instant matched = bet.getMatched().toInstant();
            return OptionalDouble.of((double) placed.until(matched, ChronoUnit.SECONDS));
        }
        return OptionalDouble.empty();
    }

}
