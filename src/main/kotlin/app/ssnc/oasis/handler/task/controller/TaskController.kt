package app.ssnc.oasis.handler.task.controller

import app.ssnc.oasis.config.ApiConfig
import app.ssnc.oasis.config.ApiConfig.API_PATH
import app.ssnc.oasis.config.ApiConfig.API_VERSION
import app.ssnc.oasis.handler.task.service.TaskService
import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/${API_PATH}/${API_VERSION}/task"])
@Api(value = "task", description = "Rest API for task operations", tags = arrayOf("task API"))
class TaskController {
    @Autowired
    private lateinit var taskService: TaskService

}