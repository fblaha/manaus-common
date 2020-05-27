package cz.fb.manaus.spring

import cz.fb.manaus.core.model.TYPE_MATCH_ODDS
import cz.fb.manaus.reactor.betting.listener.MarketRunnerPredicate
import cz.fb.manaus.reactor.price.PriceBulldozer
import cz.fb.manaus.reactor.price.PriceFilter
import cz.fb.manaus.spring.conf.MarketRunnerConf
import cz.fb.manaus.spring.conf.PriceConf
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.*


@Configuration
@Profile("ischia")
@ComponentScan(value = ["cz.fb.manaus.ischia"])
@Import(ValidationConfiguration::class, BettorConfiguration::class)
@EnableConfigurationProperties(MarketRunnerConf::class, PriceConf::class)
open class IschiaLocalConfiguration {

    @Bean
    open fun marketRunnerPredicate(marketRunnerConf: MarketRunnerConf): MarketRunnerPredicate {
        val runnerTest = marketRunnerConf.runnerName ?: ""
        return { market, runner ->
            when (market.type ?: error("missing type")) {
                TYPE_MATCH_ODDS -> runnerTest in runner.name.toLowerCase()
                else -> true
            }
        }
    }


    @Bean
    open fun abnormalPriceFilter(priceConf: PriceConf, priceBulldozer: PriceBulldozer): PriceFilter =
            // TODO not used
            PriceFilter(
                    priceConf.limit,
                    priceConf.bulldoze,
                    priceConf.min..priceConf.max,
                    priceBulldozer
            )
}
