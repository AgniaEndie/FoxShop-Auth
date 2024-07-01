package ru.agniaendie.authservice.service

import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import ru.agniaendie.authservice.exception.UsernameOrPasswordException
import ru.agniaendie.authservice.logger
import ru.agniaendie.authservice.model.AuthModel
import ru.agniaendie.authservice.model.Role
import ru.agniaendie.authservice.model.request.AuthenticationAuthModelRequest
import ru.agniaendie.authservice.model.request.CreateAuthModelRequest
import ru.agniaendie.authservice.model.request.RefreshTokenRequest
import ru.agniaendie.authservice.model.response.AuthenticateResponse
import ru.agniaendie.authservice.model.response.exception.Error
import ru.agniaendie.authservice.repository.AuthRepository
import ru.agniaendie.authservice.repository.RefreshRepository
import ru.agniaendie.authservice.security.service.JwtService
import kotlin.math.log

@Service
class AuthService(
    @Autowired val authRepository: AuthRepository,
    @Autowired val jwtService: JwtService,
    @Autowired val refreshRepository: RefreshRepository,
    @Autowired val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun createAuthModel(request: CreateAuthModelRequest): Mono<AuthModel> {
        return authRepository.save(
            AuthModel(
                null,
                request.username,
                passwordEncoder.encode(request.password),
                Role.ROLE_NORMAL,
                request.email
            )
        )
    }

    suspend fun authenticate(request: AuthenticationAuthModelRequest): ResponseEntity<Any> {
        try {
            val user = authRepository.findByUsername(request.username)
            var accessToken: String? = null
            var refreshToken: String? = null

            user.publishOn(Schedulers.boundedElastic()).doOnNext {
                if (passwordEncoder.matches(request.password, it.password)) {
                    accessToken = jwtService.generateAccessToken(it)
                    runBlocking {
                        refreshToken = jwtService.generateRefreshToken(it).awaitFirstOrNull()
                    }

                }
            }.awaitFirstOrNull()

            if (accessToken != null && refreshToken != null) {
                return ResponseEntity(
                    AuthenticateResponse(
                        accessToken,
                        refreshToken
                    ),
                    HttpStatus.OK
                )

            } else {
                throw UsernameOrPasswordException("Invalid username or password")
            }

        } catch (e: UsernameOrPasswordException) {
            return ResponseEntity(
                Error(e.message ?: "Invalid username or password"),
                HttpStatus.UNAUTHORIZED

            )
        } catch (e: Exception) {
            return ResponseEntity(
                AuthenticateResponse("", ""),
                HttpStatus.UNAUTHORIZED

            )
        }
    }


    suspend fun refreshTokenRecreation(request: RefreshTokenRequest): ResponseEntity<Any> {
        try {
            val refresh = refreshRepository.findRefreshByToken(request.refresh)
                .flatMap { refresh ->
                    authRepository.findByUuid(refresh.person)
                        .publishOn(Schedulers.boundedElastic())
                        .map { auth ->
                            val accessToken = jwtService.generateAccessToken(auth)
                            val refreshToken = jwtService.generateRefreshToken(auth).block()

                            refreshRepository.deleteRefreshByUuid(refresh.uuid!!).subscribe()

                            Pair(accessToken, refreshToken)
                        }
                }
                .awaitFirstOrNull()

            val accessToken = refresh?.first
            val refreshToken = refresh?.second

            return ResponseEntity(AuthenticateResponse(accessToken, refreshToken), HttpStatus.OK)
        } catch (e: Exception) {
            return ResponseEntity(Error("Failed to validate refresh token"), HttpStatus.UNAUTHORIZED)
        }
    }


}