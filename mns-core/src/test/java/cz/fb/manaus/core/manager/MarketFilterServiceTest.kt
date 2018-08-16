package cz.fb.manaus.core.manager

import com.google.common.collect.Range
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class MarketFilterServiceTest : AbstractMarketDataAwareTestCase() {
    @Autowired
    private lateinit var filterService: MarketFilterService

    @Test
    fun `filtering`() {
        checkFilterCount(Range.closed(1000, 1500), false)
        checkFilterCount(Range.singleton(6700), true)
    }

    fun checkFilterCount(expectedRange: Range<Int>, hasBets: Boolean) {
        val cnt = markets.filter { it -> filterService.accept(it, hasBets, setOf()) }
                .count()
        assertTrue(expectedRange.contains(cnt))
    }
}
