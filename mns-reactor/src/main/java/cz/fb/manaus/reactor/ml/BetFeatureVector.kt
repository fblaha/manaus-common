package cz.fb.manaus.reactor.ml

import cz.fb.manaus.core.model.Side

data class BetFeatureVector(
        val id: String,
        val side: Side,
        val profit: Double,
        val features: Map<String, Double?>
)