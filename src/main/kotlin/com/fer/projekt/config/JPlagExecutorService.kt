package com.fer.projekt.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class JPlagExecutorService {
    @Bean("jPlagExecutorService")
    fun jPlagExecutorService(): ExecutorService =
        Executors.newVirtualThreadPerTaskExecutor()
}
