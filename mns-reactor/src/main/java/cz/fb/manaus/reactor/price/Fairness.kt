package cz.fb.manaus.reactor.price

import com.google.common.base.MoreObjects
import cz.fb.manaus.core.model.Side
import java.util.*
import java.util.Objects.requireNonNull

class Fairness(val back: Double?, val lay: Double?) {

    val moreCredibleSide: Side?
        get() {
            if (lay != null && back != null) {
                val layInverted = 1 / lay
                return if (back > layInverted) {
                    Side.BACK
                } else {
                    Side.LAY
                }
            } else if (lay != null) {
                return Side.LAY
            } else if (back != null) {
                return Side.BACK
            }
            return null
        }

    operator fun get(side: Side): Double? {
        return if (requireNonNull(side) === Side.BACK) back else lay
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
                .add("back", back)
                .add("lay", lay)
                .toString()
    }

    companion object {
        fun toKotlin(values: List<OptionalDouble>): List<Double?> {
            return values.map { if (it.isPresent) it.asDouble else null }
        }
    }

}
