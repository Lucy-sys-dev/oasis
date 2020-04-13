package app.ssnc.oasis.repository

import app.ssnc.oasis.entity.model.SysUserRole
import app.ssnc.oasis.entity.model.User
import app.ssnc.oasis.entity.model.enum.UserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.collections.ArrayList

@Repository
interface EmployeeRepository : CrudRepository<User, Long> {
    fun findEmployeesByEmail(email: String) : Optional<User>
    fun findByUsername(username: String): Optional<User>
    fun findEmployeesByEmailIn(emails: ArrayList<String>): ArrayList<User>
    fun findAll(specification: Specification<User>, pageable: Pageable): Page<User>
}

@Repository
interface SysUserRoleRepository : JpaRepository<SysUserRole, Long> {
    fun findSysUserRolesByName(name: UserRole) : SysUserRole?
}