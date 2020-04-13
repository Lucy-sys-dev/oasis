package app.ssnc.oasis.handler.firewall.service

import app.ssnc.oasis.entity.model.Task
import app.ssnc.oasis.entity.model.TaskAssign
import app.ssnc.oasis.entity.model.enum.AssignStatus
import app.ssnc.oasis.entity.model.enum.AuditStatus
import app.ssnc.oasis.entity.request.FirewallRequest
import app.ssnc.oasis.entity.request.SearchRuleRequest
import app.ssnc.oasis.entity.request.processApporovalFirewallRequest
import app.ssnc.oasis.exception.HandleConstraintViolationException
import app.ssnc.oasis.exception.HandleMethodFailException
import app.ssnc.oasis.exception.ResourceNotFoundException
import app.ssnc.oasis.exception.UniquenessFieldException
import app.ssnc.oasis.handler.firewall.entity.ApporovalDetailRes
import app.ssnc.oasis.handler.firewall.entity.SearchRuleReq
import app.ssnc.oasis.handler.task.service.TaskService
import app.ssnc.oasis.handler.user.service.UserService
import app.ssnc.oasis.util.DateUtil
import app.ssnc.oasis.util.wallbrain.WallBrainRestApiClient
import app.ssnc.oasis.util.web.RestClient
import com.sds.wallbrain.base.*
import com.sds.wallbrain.base.builder.RuleSetGroupInfoBuilder
import com.sds.wallbrain.base.helper.MisIdGenerator
import mu.KLogging
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Service
class FirewallService(
    @Value("\${wallbrain.ws.api-user-id}")
    val apiUserId: String,
    @Value("\${wallbrain.ws.api-user-password}")
    val apiUserPasswd: String,
    @Value("\${wallbrain.ws.baseurl")
    val apiUrl: String
) {
    companion object : KLogging()

    @Autowired
    private lateinit var wallBrainRestApiClient: WallBrainRestApiClient

    @Autowired
    private lateinit var restClient: RestClient

    @Autowired
    private lateinit var taskService: TaskService

    @Autowired
    private lateinit var userService: UserService

    @Throws(UniquenessFieldException::class, HandleConstraintViolationException::class)
    fun searchRule(request: SearchRuleRequest): Any? {
        val data = HashMap<String, String>()
        val searchRule = SearchRuleReq(
            srcAddr = request.src_address,
            dstAddr = request.dest_address,
            dstPort = request.port.toString(),
            protocol = request.protocol.toString(),
            withDiscovery = "true",
            startDate = DateUtil.formatDateToLocalDate(request.start_date, "YYYYMMdd"),
            expireDate = DateUtil.formatDateToLocalDate(request.end_date, "YYYYMMdd")
        )

        val results: Array<FirewallRuleSessionInfoVo> = wallBrainRestApiClient.searchRuleSetGroup("/provision/rule/search", searchRule)

        for (result in results ) {
            if (result.resultStatus == "allowed") {
                data["check"] = "ERROR"
                data["duplication"] = "Y"
                data["message"] = "방화벽 요청 중복"
                return data
            }

            if (result.isCompliance) {
                data["check"] = "ERROR"
                data["compliance"] = "Y"
                data["message"] = result.complianceComment
                return data
            }
        }
        data["check"] = "SUCCESS"
        return data
    }

    fun approvalFirewall(request: FirewallRequest) {
        taskService.findProjectByName("FIREWALL")?.let { project ->

            val task = Task(
                projectId = project.id!!, title = "방화벽 신청",
                key = taskService.generationKey(project.id!!),
                creator = userService.findById(request.creator).get(),
//                assignee = userService.findByName(request.assigns!!.find { it.order == 0 }!!.user_id)!!,
                details = request.rules
            )

            taskService.createTask(task)

            request.assigns!!.forEach { assign ->
                val taskAssign = TaskAssign(projectId = project.id!!, taskId = task.id!!,
                    orderNo = assign.order, status = AssignStatus.PENDING,
                    assign = userService.findByEmail(assign.user_id)!!)
                taskService.createTaskAssigns(taskAssign)
                if (request.assigns.minBy { it.order!! }!!.order == assign.order) {
                    task.assignee = taskAssign
                    taskService.createTask(task)
                }
            }
        }?: run {
            throw ResourceNotFoundException("Project not found")
        }
    }

    fun searchApprovalFirewall(userId: String) : Any? {
        taskService.findProjectByName("FIREWALL")?.let { project ->
            //요청한 사람 기준으로 조회
            return taskService.searchTaskByProject(project.id!!)
        }?: run {
            throw ResourceNotFoundException("Project not found")
        }
    }

    fun searchApprovalDetailFirewall(id: Long) : Any? {
        taskService.searchTaskById(id)?.let { it ->
            val result = ApporovalDetailRes(
                task = it,
                assignees = taskService.searchTaskAssigneeByTask(id)!!)
            return result
        }?: run {
            throw ResourceNotFoundException("Task not found")
        }
    }

    fun processApprovalFirewall(request: processApporovalFirewallRequest) {
        taskService.searchTaskById(request.task)?.let { task ->
            taskService.searchTaskAssignById(request.assignee)?.let { taskAssign ->
                taskAssign.status = request.status
                taskService.createTaskAssigns(taskAssign)
                val exist = taskService.searchTaskAssignByTaskAndOrder(taskId = request.task, order = taskAssign.orderNo!!+1)
                if (exist == null) {
                    task.status = AuditStatus.CONFIRMED
                    // todo FPMS 요청
                    val result = registerRuleFPMS(task.id!!) ?: throw HandleMethodFailException("FPMS I/F failed")
                    task.misid = result.toString()

                } else {
                    task.status = AuditStatus.APPROVAL
                    task.assignee = taskService.searchTaskAssignByTaskAndOrder(taskId = request.task, order = taskAssign.orderNo!!+1)
                }
                taskService.createTask(task)
            } ?: run {
                throw ResourceNotFoundException("Task Assignee not found")
            }
        } ?: run {
            throw ResourceNotFoundException("Task not found")
        }
    }

//    fun SearchRuleRequest.toKey() = SearchRuleRequest(start_date, end_date)

    fun registerRuleFPMS(taskId: Long) : Any? {
        var misId : Any? = null
        taskService.searchTaskById(taskId)?.let { task ->
            //msid generator
            misId = MisIdGenerator.generateSampleMisId()
            val ruleSetInfoVos : ArrayList<RuleSetInfoVo> = ArrayList()


            val setValues = task.details.distinctBy { it -> Pair(it.start_date, it.end_date)}.toList()
            logger.debug { setValues }

            for ( setValue in setValues ) {
                val rules : ArrayList<RuleInfoVo> = ArrayList()
                task.details.filter {
                    it.start_date == setValue.start_date && it.end_date == setValue.end_date
                }.forEach { rule ->
                    val ruleInfoVo = RuleInfoVo()
                    ruleInfoVo.srcAddr = rule.src_address
                    ruleInfoVo.dstAddr = rule.dest_address
                    ruleInfoVo.protocol = (if (rule.protocol.desc == "TCP") RuleProtocols.TCP else RuleProtocols.UDP).toString()
                    ruleInfoVo.dstPort = rule.port.toString()
                    val start_date = rule.start_date.toString()
                    ruleInfoVo.expireDate = rule.end_date.toString()
                    ruleInfoVo.comment = rule.comment
                    rules.add(ruleInfoVo)
                }
                val ruleSetInfoVo = RuleSetInfoVo()
                ruleSetInfoVo.expireDateTime = toStartDateTime(setValue.end_date)
                ruleSetInfoVo.startDateTime = toStartDateTime(setValue.start_date)
                ruleSetInfoVo.ruleInfoVos = rules
                ruleSetInfoVo.requestUserId = task.createdBy
                ruleSetInfoVo.comment = setValue.comment
                ruleSetInfoVo.misId = misId as String?
                ruleSetInfoVos.add(ruleSetInfoVo)
            }

            val builder : RuleSetGroupInfoBuilder = RuleSetGroupInfoBuilder.create()
            val ruleSetGroupInfoVo : RuleSetGroupInfoVo = builder.addAll(ruleSetInfoVos).build()

            val results: RuleSetGroupInfoVo = wallBrainRestApiClient.registerRuleSetupGroup("/rule/"+misId, ruleSetGroupInfoVo)

            if (results.hasError()) return null






//            filter {
//                val ruleSetInfoVo = RuleSetInfoVo()
//                ruleSetInfoVo.expireDateTime = toStartDateTime(DateUtil.stringToLocalDate(it.start_date))
//            }.forEach { rule ->
//                val ruleInfoVo = RuleInfoVo()
//                ruleInfoVo.srcAddr = rule.src_address
//                ruleInfoVo.dstAddr = rule.dest_address
//                ruleInfoVo.protocol = (if (rule.protocol.desc == "TCP") RuleProtocols.TCP else RuleProtocols.UDP).toString()
//                ruleInfoVo.dstPort = rule.port.toString()
//                val start_date = rule.start_date.toString()
//                ruleInfoVo.expireDate = rule.end_date.toString()
//                ruleInfoVo.comment = rule.comment
//                rules.add(ruleInfoVo)
//            }.apply {
//
//            }
//            }.also {
//                val ruleSetInfoVo = RuleSetInfoVo()
//                ruleSetInfoVo.expireDateTime = toStartDateTime(DateUtil.stringToLocalDate(start_date))
//            }


//            task.details.forEach { rule ->
//                val ruleInfoVo = RuleInfoVo()
//                ruleInfoVo.srcAddr = rule.src_address
//                ruleInfoVo.dstAddr = rule.dest_address
//                ruleInfoVo.protocol = (if (rule.protocol.desc == "TCP")  RuleProtocols.TCP else RuleProtocols.UDP).toString()
//                ruleInfoVo.dstPort = rule.port.toString()
//                ruleInfoVo.startDate = rule.start_date.toString()
//                ruleInfoVo.expireDate = rule.end_date.toString()
//                ruleInfoVo.comment = rule.comment
//                rules.add(ruleInfoVo)
//            }





            //val ruleInfoVo: RuleInfoVo = RuleInfoVo("10.10.10.1", "192.168.0.1", "3389", RuleProtocols.TCP)
//            val ruleSetGroupInfoVo : RuleSetGroupInfoVo = RuleSetGroupInfoBuilder.create()
//                .setMisId(misId as String?)
//                .setRequestUserId(task.createdBy)
//                .setStartDateTime(toStartDateTime(DateUtil.stringToLocalDate(dateString = "2020-04-01")))
//                .setExpireDateTime(toEndDateTime(DateUtil.stringToLocalDate(dateString = "2020-04-31")))
//                .setRuleSetInfoVo()
////                .setComment("TEST")
////                .setExpireDay("20200331")
//                .addRules(rules)
//                //.addRule(RuleInfoVo("10.10.10.1", "192.168.0.4", "3389", RuleProtocols.TCP))
//                .build()
//            val results: RuleSetGroupInfoVo = wallBrainRestApiClient.registerRuleSetupGroup("/rule/"+misId, ruleSetGroupInfoVo)
//
//            if (results.hasError()) return null
//
        }
        return misId
    }

    fun toStartDateTime(localDate: LocalDate): DateTime? {
        return DateTime(DateTimeZone.UTC).withDate(
            localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()
        ).withTime(0, 0, 0, 0)
    }

    fun toEndDateTime(localDate: LocalDate): DateTime? {
        return DateTime(DateTimeZone.UTC).withDate(
            localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()
        ).withTime(23, 59, 59, 0)
    }

}

