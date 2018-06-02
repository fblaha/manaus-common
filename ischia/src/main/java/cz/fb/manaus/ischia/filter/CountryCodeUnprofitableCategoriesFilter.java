package cz.fb.manaus.ischia.filter;

import cz.fb.manaus.core.category.Category;
import cz.fb.manaus.core.category.categorizer.CountryCodeCategorizer;
import cz.fb.manaus.ischia.BackLoserBet;
import cz.fb.manaus.ischia.LayLoserBet;
import cz.fb.manaus.reactor.filter.AbstractUnprofitableCategoriesRegistry;
import cz.fb.manaus.spring.ManausProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@LayLoserBet
@BackLoserBet
@Component
@Profile(ManausProfiles.DB)
public class CountryCodeUnprofitableCategoriesFilter extends AbstractUnprofitableCategoriesRegistry {

    public static final String PREFIX = Category.MARKET_PREFIX + CountryCodeCategorizer.PREFIX;

    protected CountryCodeUnprofitableCategoriesFilter() {
        super("countryCodeLay", Duration.ofDays(30), Optional.empty(), -30,
                PREFIX, Map.of(20, 3));
    }
}
