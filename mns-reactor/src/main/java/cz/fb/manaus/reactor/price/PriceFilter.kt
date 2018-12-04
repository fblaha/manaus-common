package cz.fb.manaus.reactor.price


import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.PriceComparator
import cz.fb.manaus.core.model.Side

// TODO data class
open class PriceFilter(private val minCount: Int,
                       private val bulldozeThreshold: Double,
                       private val priceRange: ClosedRange<Double>,
                       private val bulldozer: PriceBulldozer) {

    internal fun getSignificantPrices(minCount: Int, prices: List<Price>): List<Price> {
        val bySide = prices.filter { this.priceRangeFilter(it) }.groupBy { it.side }.withDefault { emptyList() }
        val sortedBack = bySide.getValue(Side.BACK).sortedWith(PriceComparator)
        val sortedLay = bySide.getValue(Side.LAY).sortedWith(PriceComparator)
        val bulldozedBack = bulldozer.bulldoze(bulldozeThreshold, sortedBack)
        val bulldozedLay = bulldozer.bulldoze(bulldozeThreshold, sortedLay)
        val topBack = bulldozedBack.take(minCount)
        val topLay = bulldozedLay.take(minCount)
        return topBack + topLay
    }

    private fun priceRangeFilter(price: Price): Boolean {
        return price.price in priceRange
    }

    fun filter(prices: List<Price>): List<Price> {
        return getSignificantPrices(minCount, prices)
    }
}
