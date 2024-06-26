package ru.agniaendie.authservice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.agniaendie.authservice.logger
import ru.agniaendie.authservice.model.AuthModel
import ru.agniaendie.authservice.model.request.AuthenticationAuthModelRequest
import ru.agniaendie.authservice.model.request.CreateAuthModelRequest
import ru.agniaendie.authservice.model.request.RefreshTokenRequest
import ru.agniaendie.authservice.model.response.AuthenticateResponse
import ru.agniaendie.authservice.model.response.Result
import ru.agniaendie.authservice.service.AuthService

@RestController
@RequestMapping("/api/auth")
class AuthController(@Autowired val authService: AuthService) {
    @PostMapping("/create-user")
    suspend fun createAuthModel(@RequestBody request: CreateAuthModelRequest): Mono<AuthModel> {
        logger.error("test")
        return authService.createAuthModel(request)
    }

    @PostMapping("/authenticate")
    suspend fun authenticate(@RequestBody request: AuthenticationAuthModelRequest): Result<ResponseEntity<AuthenticateResponse>> {
        return authService.authenticate(request)
    }

    @PostMapping("/refresh-recreation")
    suspend fun refreshRecreation(@RequestBody request: RefreshTokenRequest): Result<ResponseEntity<AuthenticateResponse>> {
        return authService.refreshTokenRecreation(request)
    }
}