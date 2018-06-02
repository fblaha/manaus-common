package cz.fb.manaus.spring;

import com.google.common.collect.Range;
import cz.fb.manaus.reactor.betting.listener.FlowFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@Configuration
@Profile("manila")
@ComponentScan(value = "cz.fb.manaus.manila")
public class ManilaLocalConfiguration {

    @Bean
    public FlowFilter bestChanceFlowFilter() {
        return new FlowFilter(Range.singleton(0), Range.singleton(1), (market, runner) -> true, Set.of());
    }

}
