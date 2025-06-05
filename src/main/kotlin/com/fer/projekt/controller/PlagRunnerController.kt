package com.fer.projekt.controller;

import com.fer.projekt.service.JPlagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController;

@RestController
class PlagRunnerController(
    private val jPlagService: JPlagService,
) {

    @PostMapping("/run")
    suspend fun runJPlag(
        @RequestBody jPlagRunRequest: JPlagRunRequest
    ): ResponseEntity<*> {
        return try {
            val result = jPlagService.runJplag(
                firstSubmission = jPlagRunRequest.firstSubmission,
                secondSubmission = jPlagRunRequest.secondSubmission,
                language = jPlagRunRequest.language
            )
            ResponseEntity.ok(result)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
