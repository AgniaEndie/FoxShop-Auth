package ru.agniaendie.authservice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import ru.agniaendie.authservice.model.AuthModel
import ru.agniaendie.authservice.model.request.AuthenticationAuthModelRequest
import ru.agniaendie.authservice.model.request.CreateAuthModelRequest
import ru.agniaendie.authservice.model.request.RefreshTokenRequest
import ru.agniaendie.authservice.service.AuthService

@RestController
@RequestMapping("/api/auth")
class AuthController(@Autowired val authService: AuthService) {
    @PostMapping("/create-user")
    @Transactional
    fun createAuthModel(@RequestBody request: CreateAuthModelRequest): Mono<AuthModel> {
        return authService.createAuthModel(request)
    }

    @PostMapping("/authenticate")
    suspend fun authenticate(@RequestBody request: AuthenticationAuthModelRequest): ResponseEntity<Any> {
        return authService.authenticate(request)
    }

    @PostMapping("/refresh-token")
    suspend fun refreshRecreation(@RequestBody request: RefreshTokenRequest): ResponseEntity<Any> {
        return authService.refreshTokenRecreation(request)
    }


}