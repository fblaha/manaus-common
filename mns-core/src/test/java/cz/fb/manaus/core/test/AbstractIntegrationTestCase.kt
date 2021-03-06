package cz.fb.manaus.core.test


import cz.fb.manaus.core.model.BlacklistedCategory
import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.core.repository.mongo.Foo
import cz.fb.manaus.spring.ManausProfiles.DB
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles


@ActiveProfiles(DB)
abstract class AbstractIntegrationTestCase : AbstractTestCase5() {

    @Autowired
    protected lateinit var betActionRepository: BetActionRepository

    @Autowired
    protected lateinit var settledBetRepository: SettledBetRepository

    @Autowired
    protected lateinit var marketRepository: MarketRepository

    @Autowired
    protected lateinit var taskExecutionRepository: Repository<TaskExecution>

    @Autowired
    protected lateinit var blacklistedCategoryRepository: Repository<BlacklistedCategory>

    @Autowired
    protected lateinit var marketStatusRepository: Repository<MarketStatus>

    @Autowired
    protected lateinit var fooRepository: Repository<Foo>

    @Autowired
    private lateinit var repositories: List<Repository<*>>


    @AfterEach
    @BeforeEach
    fun clean() {
        repositories.forEach { it.purge() }
    }
}
