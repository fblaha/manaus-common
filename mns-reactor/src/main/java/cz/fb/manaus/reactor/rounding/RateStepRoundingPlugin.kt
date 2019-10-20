package cz.fb.manaus.reactor.rounding

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.provider.ProviderCapability
import org.springframework.stereotype.Component


@Component
class RateStepRoundingPlugin : RoundingPlugin {

    override fun shift(price: Double, steps: Int): Double {
        return price + steps * getStep(price)
    }

    private fun getStep(price: Double): Double {
        return (price - 1) * 0.02
    }

    override fun round(price: Double): Double {
        return Price.round(price)
    }

    override val requiredCapabilities: Set<ProviderCapability>
        get() = setOf(ProviderCapability.LastMatchedPrice)

}
