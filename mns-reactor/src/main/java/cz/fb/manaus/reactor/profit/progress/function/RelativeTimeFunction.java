package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.OptionalDouble;

public interface RelativeTimeFunction extends ProgressFunction {

    @Override
    default OptionalDouble function(SettledBet bet) {
        Optional<Instant> eventTime = getRelatedTime(bet);
        if (eventTime.isPresent()) {
            Instant openDate = bet.getBetAction().getMarket().getEvent().getOpenDate().toInstant();
            double minutes = eventTime.get().until(openDate, ChronoUnit.MINUTES);
            return OptionalDouble.of(minutes / 60d);
        } else {
            return OptionalDouble.empty();
        }
    }

    Optional<Instant> getRelatedTime(SettledBet bet);

}
