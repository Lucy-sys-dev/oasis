package app.ssnc.oasis.repository

import app.ssnc.oasis.entity.model.Project
import app.ssnc.oasis.entity.model.Task
import app.ssnc.oasis.entity.model.TaskAssign
import app.ssnc.oasis.entity.model.User
import app.ssnc.oasis.entity.request.Assign
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaskRepository : CrudRepository<Task, Long> {
    fun findFirstByProjectIdOrderByIdDesc(projectId: Long): Task?
    fun findByCreator(creator: User) : List<Task>?
    fun findByProjectId(projectId: Long): List<Task>?
    fun findByProjectIdAndCreatorOrAssignee(projectId: Long, creator: User, assignee: User ) : List<Task>?
}

@Repository
interface TaskAssignRepository : CrudRepository<TaskAssign, Long> {
    fun findByTaskIdAndOrderNo(taskId: Long, orderNo: Int) : TaskAssign?
    fun findByTaskIdOrderByOrderNo(taskId: Long): List<TaskAssign>?
}

@Repository
interface ProjectRepository: CrudRepository<Project, Long> {
    fun findByNameOrKey(name: String, key: String): Optional<Project>
    fun findByName(name: String): Optional<Project>
    fun findByKey(key: String) : Optional<Project>
}