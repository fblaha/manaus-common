package cz.fb.manaus.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^cz\\.fb\\.manaus\\.spring\\.\\w+LocalConfiguration$"),
        basePackages = {"cz.fb.manaus"}
)
public class LocalTestConfiguration {

//    @Bean
//    @Primary
//    @Profile("test")
//    public PropertiesService propertiesService() {
//        return Mockito.mock(PropertiesService.class);
//    }

}
