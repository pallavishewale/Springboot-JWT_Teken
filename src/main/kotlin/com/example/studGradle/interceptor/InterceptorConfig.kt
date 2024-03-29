package com.example.studGradle.interceptor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class InterceptorConfig (private val requestLoggingInterceptor: RequestHandlingInterceptor) :
    WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        println("enter in addInterceptor method ********8")
        registry.addInterceptor(requestLoggingInterceptor)
    }
}