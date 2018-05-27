package cz.fb.manaus.ischia.validator.update;

import cz.fb.manaus.ischia.BackLoserBet;
import cz.fb.manaus.ischia.LayLoserBet;
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractTooCloseUpdateValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@BackLoserBet
@LayLoserBet
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Profile("betfair")
final public class TheSamePriceUpdateValidator extends AbstractTooCloseUpdateValidator {

    public TheSamePriceUpdateValidator() {
        super(Set.of());
    }

    @Override
    public boolean isDowngradeAccepting() {
        return false;
    }
}