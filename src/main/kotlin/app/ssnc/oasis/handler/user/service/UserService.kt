package app.ssnc.oasis.handler.user.service

import app.ssnc.oasis.entity.model.User
import app.ssnc.oasis.entity.request.CreateEmployeeRequest
import app.ssnc.oasis.entity.request.SearchUserRequest
import app.ssnc.oasis.exception.CustomException
import app.ssnc.oasis.exception.ResourceNotFoundException
import app.ssnc.oasis.repository.EmployeeRepository
import app.ssnc.oasis.repository.SysUserRoleRepository
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService {
    companion object : KLogging()

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var sysUserRoleRepository: SysUserRoleRepository

    fun findByEmail(email: String) : User? {
        return employeeRepository.findEmployeesByEmail(email).get()
            //.orElseThrow {  throw ResourceNotFoundException("User not found") }
    }

    @Throws(ResourceNotFoundException::class)
    fun searchAll(): MutableIterable<User> {
        return employeeRepository.findAll()
    }

    @Throws(ResourceNotFoundException::class)
    fun search(request: SearchUserRequest): User? {
        when (request.key) {
            "NAME" -> return findByName(request.id)
            "ID" -> return findByEmail(request.id)
            else -> throw ResourceNotFoundException("User not found")

        }
    }

    fun findByName(id: String) : User? {
        return employeeRepository.findByUsername(id).get()
            //.orElseThrow {  throw ResourceNotFoundException("User not found") }
    }

    fun findById(id: Long): Optional<User> {
        return employeeRepository.findById(id)
    }

    @Throws(CustomException::class)
    fun create(request: CreateEmployeeRequest) = with(request) {
//        if (findByEmail(email) != null)
//            throw CustomException("UserEmail is already in use", HttpStatus.BAD_REQUEST)

        val userRole = sysUserRoleRepository.findSysUserRolesByName(role) ?: run {
            logger.error("${role} missing from the database!")
            throw CustomException("Could not find user role in the database.", HttpStatus.BAD_REQUEST)
        }

        val newEmployee = User(
            id = -1,
            email = email,
            password = passwordEncoder.encode(password),
            username = usernmae,
            status = status,
            mobile = mobile,
            tel = tel,
            joinDate = joinDate!!,
            position = position,
//            dept = deptService.findById(deptId)
            roles = listOf(userRole)
        )
        val result = employeeRepository.save(newEmployee)

//        val location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{username}")
//            .buildAndExpand(result.username).toUri()

//        return ResponseEntity.created(location).body(ApiResponse(true, "User registered successfully."))
    }

}