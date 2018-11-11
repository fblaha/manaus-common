package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.Market

// TODO Categorizer -> Classifier
interface Categorizer : SimulationAware {

    fun getCategories(market: Market): Set<String>

}
