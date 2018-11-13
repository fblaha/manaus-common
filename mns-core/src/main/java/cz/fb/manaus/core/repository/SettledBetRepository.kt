package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.spring.ManausProfiles
import org.dizitart.kno2.filters.and
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.filters.gte
import org.dizitart.kno2.filters.lte
import org.dizitart.kno2.getRepository
import org.dizitart.no2.FindOptions
import org.dizitart.no2.Nitrite
import org.dizitart.no2.SortOrder
import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@Profile(ManausProfiles.DB)
class SettledBetRepository(private val db: Nitrite) {
    internal val repository: ObjectRepository<SettledBet> by lazy { db.getRepository<SettledBet> {} }

    fun read(id: String): SettledBet? {
        return repository.find(SettledBet::id eq id).firstOrDefault()
    }

    fun save(settledBet: SettledBet) {
        repository.insert(settledBet)
    }

    fun find(from: Instant? = null, to: Instant? = null, side: Side? = null, maxResults: Int? = null): List<SettledBet> {
        var filter = ObjectFilters.ALL
        if (from != null) {
            val fromFilter = SettledBet::settled gte from
            filter = filter?.and(fromFilter) ?: fromFilter
        }
        if (to != null) {
            val toFilter = SettledBet::settled lte to
            filter = filter?.and(toFilter) ?: toFilter
        }
        if (side != null) {
            val sideFilter = SettledBet::side eq side
            filter = filter?.and(sideFilter) ?: sideFilter
        }
        var options = FindOptions.sort("settled", SortOrder.Ascending)
        if (maxResults != null) options = options.thenLimit(0, maxResults)
        return repository.find(filter, options).toList()
    }
}