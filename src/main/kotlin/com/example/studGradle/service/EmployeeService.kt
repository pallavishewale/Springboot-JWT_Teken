package com.example.studGradle.service

import com.example.studGradle.DTO.CustomExceptionMessage
import com.example.studGradle.Repository.EmployeeRepository
import com.example.studGradle.Modal.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ControllerAdvice
import java.io.IOException


@ControllerAdvice
@Service
class employeeService(private val employeeRepository: EmployeeRepository) {

    fun getAllEmployees(pageable: Pageable): Page<Employee> {
        return employeeRepository.findAll(pageable)
    }

    fun getStudentById(id: Long): Employee {
        return if (employeeRepository.existsById(id)) {
            return employeeRepository.getById(id)
        } else throw CustomExceptionMessage("Employee with ID $id not exist in database")

    }

    fun saveStudent(employee: Employee): Employee {
        return if (employeeRepository.findByUsername(employee.username).isEmpty) {
            return employeeRepository.save(employee)
        } else throw CustomExceptionMessage("Username : ${employee.username} already exist in database")

    }

    fun updateEmployeeImage(id: Long, newImageData: ByteArray) {
        val employee = employeeRepository.findById(id)
            .orElseThrow { throw CustomExceptionMessage("Employee not found with ID: $id") }
        try {
            // Update the image data
            employee.image = newImageData
            // Save the updated employee entity
            employeeRepository.save(employee)
        } catch (e: IOException) {
            // Handle validation or other errors
            throw RuntimeException("Failed to update image: ${e.message}")
        }


    }


//    @ExceptionHandler(Exception::class)
//    fun updateStudent(id:Long,employee: Employee): ResponseEntity<Any> {
//        return if (employeeRepository.existsById(id)) {
//            employee.id = id
//            ResponseEntity.ok(employeeRepository.save(employee))
//        } else  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with ID $id not found")
//    }

    fun deleteStudent(id: Long) {
        return if (employeeRepository.existsById(id)) {
           return employeeRepository.deleteById(id)
        } else throw CustomExceptionMessage("Employee with ID $id not exist in database")
    }
}