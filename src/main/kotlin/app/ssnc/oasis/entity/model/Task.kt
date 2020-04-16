package app.ssnc.oasis.entity.model

import app.ssnc.oasis.entity.model.common.Auditable
import app.ssnc.oasis.entity.model.enum.AssignStatus
import app.ssnc.oasis.entity.model.enum.AuditStatus
import app.ssnc.oasis.entity.model.enum.Protocol
import app.ssnc.oasis.entity.model.enum.RuleActions
import app.ssnc.oasis.entity.model.type.json.JsonBinaryType
import app.ssnc.oasis.entity.request.SearchRuleRequest
import app.ssnc.oasis.util.Extension.equalsBuilder
import app.ssnc.oasis.util.Extension.toStringBuilder
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

private const val TASK_GENERATOR = "TaskGenerator"
private const val TASK_ASSIGN_GENERATOR = "TaskAssignGenerator"

@Entity
@Table(name = "task"
//    ,indexes = [Index(name = "idx1_product", columnList = "name", unique = true)]
)
@DynamicUpdate
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Task(
    @Id
    @SequenceGenerator(name = TASK_GENERATOR, sequenceName = "TASK_SEQ", initialValue = 1, allocationSize = 1)
    @GeneratedValue(generator = TASK_GENERATOR)
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null,

    @Column(name = "key", unique = true, nullable = false)
    val key: String,

    @Column(name = "project_id")
    var projectId: Long,

    @Column(name = "title")
    var title: String,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "creator_id", unique = false, nullable = false, insertable = true, updatable = true)
    var creator: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", unique = false, nullable = false, insertable = true, updatable = true)
    var status: AuditStatus = AuditStatus.PENDING,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "assignee_id", unique = false, nullable = true, insertable = true, updatable = true)
    var assignee: TaskAssign? = null,

    @Type(type = "jsonb")
    @Column(name = "details", columnDefinition = "jsonb")
//    var details: MutableList<SearchRuleRequest> = mutableListOf()
    var details: MutableSet<SearchRuleRequest> = mutableSetOf(),

    @Column(name = "misid")
    var misid: String? = null

//    @Column(name = "target_id", nullable = false)
//    var targetId: Long? = null
//    @Enumerated(EnumType.STRING)
//    var priority: TaskPriority = TaskPriority.MEDIUM,

) : Auditable(), Serializable {

    override fun toString() =
        toStringBuilder(
            Task::id,
            Task::key
        )

    override fun equals(other: Any?): Boolean =
        equalsBuilder(
            other,
            Task::id,
            Task::key
        )

    override fun hashCode(): Int =
        Objects.hash(id, key)
}

@Entity
@Table(name = "task_assign"
//    ,indexes = [Index(name = "idx1_product", columnList = "name", unique = true)]
)
@DynamicUpdate
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class TaskAssign(
    @Id
//    @SequenceGenerator(name = TASK_ASSIGN_GENERATOR, sequenceName = "TASK_ASSIGN_SEQ", initialValue = 1, allocationSize = 1)
//    @GeneratedValue(generator = TASK_ASSIGN_GENERATOR)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null,

    @Column(name = "project_id")
    var projectId: Long,

    @Column(name = "task_id")
    var taskId: Long,

    @Column(name = "order_no")
    var orderNo: Int?,

    @Enumerated(EnumType.STRING)
    @Column( name = "status", unique = false, nullable = false, insertable = true, updatable = true)
    var status: AssignStatus = AssignStatus.PENDING,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "creator_id", unique = false, nullable = false, insertable = true, updatable = true)
    var assign: User

) : Auditable(), Serializable {

    override fun toString() =
        toStringBuilder(
            TaskAssign::projectId,
            TaskAssign::orderNo
        )

    override fun equals(other: Any?): Boolean =
        equalsBuilder(
            other,
            TaskAssign::projectId,
            TaskAssign::orderNo
        )

    override fun hashCode(): Int =
        Objects.hash(projectId, orderNo)
}