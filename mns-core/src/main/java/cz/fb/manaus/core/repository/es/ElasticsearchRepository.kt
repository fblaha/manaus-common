package cz.fb.manaus.core.repository.es

import com.google.common.base.CaseFormat
import cz.fb.manaus.core.repository.Repository
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder
import org.springframework.data.elasticsearch.core.query.Query


class ElasticsearchRepository<T>(
        private val clazz: Class<T>,
        private val operations: ElasticsearchOperations,
        private val key: (T) -> String
) : Repository<T> {

    val coordinates: IndexCoordinates = IndexCoordinates.of(
            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.simpleName.toLowerCase())
    )

    override fun saveOrUpdate(entity: T) {
        save(entity)
    }

    override fun save(entity: T) {
        val indexQuery = IndexQueryBuilder()
                .withId(key(entity))
                .withObject(entity).build()
        operations.index(indexQuery, coordinates)
    }

    override fun read(id: String): T? {
        return operations.get(id, clazz, coordinates)
    }

    override fun delete(id: String) {
        operations.delete(id, coordinates)
    }

    override fun list(): List<T> {
        return operations.search(Query.findAll(), clazz, coordinates)
                .map { it.content }.toList()
    }

    override fun purge() {
        operations.delete(Query.findAll(), clazz, coordinates)
    }
}