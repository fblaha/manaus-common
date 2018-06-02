package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("ischia")
@ComponentScan(value = "cz.fb.manaus.ischia")
@Import({BetfairValues.class, MatchbookValues.class})
public class IschiaLocalConfiguration {

}
