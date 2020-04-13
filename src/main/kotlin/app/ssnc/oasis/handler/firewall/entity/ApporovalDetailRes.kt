package app.ssnc.oasis.handler.firewall.entity

import app.ssnc.oasis.entity.model.Task
import app.ssnc.oasis.entity.model.TaskAssign

data class ApporovalDetailRes (
    val task: Task,
    val assignees: List<TaskAssign>
)
