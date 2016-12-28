package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

@Component
public class RandomCategorizer implements SettledBetCategorizer {

    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public boolean isSimulationSupported() {
        return false;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        int randInt = random.nextInt(5);
        return Collections.singleton("random_" + randInt);
    }

}
