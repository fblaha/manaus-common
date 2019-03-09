package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.spring.ManausProfiles
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.no2.Nitrite
import org.dizitart.no2.objects.ObjectRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class TaskExecutionRepository(private val db: Nitrite) {

    internal val repository: ObjectRepository<TaskExecution> by lazy {
        db.getRepository<TaskExecution> {}
    }

    fun saveOrUpdate(taskExecution: TaskExecution) {
        repository.update(taskExecution, true)
    }

    fun read(id: String): TaskExecution? {
        return repository.find(TaskExecution::name eq id).firstOrDefault()
    }

    fun delete(id: String) {
        repository.remove(TaskExecution::name eq id)
    }

    fun list(): List<TaskExecution> {
        return repository.find().toList()
    }
}