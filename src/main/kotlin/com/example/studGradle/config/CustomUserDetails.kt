package com.example.studGradle.config

import com.example.studGradle.Repository.EmployeeRepository
import org.springframework.stereotype.Component


@Component
class CustomUserDetails(val employeeRepository: EmployeeRepository) {
    fun isUserValid(username: String, password: String): Boolean {
        val user = employeeRepository.findByUsername(username)
        if (user.isEmpty) {
            return false
        } else {
            return if (username == user.get().username && password == user.get().password)
                return true
            else
                return false
        }
    }

}