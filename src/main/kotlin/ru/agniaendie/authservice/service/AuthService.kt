package ru.agniaendie.authservice.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.agniaendie.authservice.logger
import ru.agniaendie.authservice.model.AuthModel
import ru.agniaendie.authservice.model.Role
import ru.agniaendie.authservice.model.request.AuthenticationAuthModelRequest
import ru.agniaendie.authservice.model.request.CreateAuthModelRequest
import ru.agniaendie.authservice.model.request.RefreshTokenRequest
import ru.agniaendie.authservice.model.response.AuthenticateResponse
import ru.agniaendie.authservice.model.response.Result
import ru.agniaendie.authservice.repository.AuthRepository
import ru.agniaendie.authservice.repository.RefreshRepository
import ru.agniaendie.authservice.security.service.JwtService
import java.util.*

@Service
class AuthService(
    @Autowired val authRepository: AuthRepository,
    @Autowired val jwtService: JwtService,
    @Autowired val refreshRepository: RefreshRepository,
    @Autowired val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun createAuthModel(request: CreateAuthModelRequest): Mono<AuthModel> {
        return authRepository.save(AuthModel(null, request.username, passwordEncoder.encode(request.password), Role.ROLE_NORMAL, request.email))
    }

    suspend fun authenticate(request: AuthenticationAuthModelRequest): Result<ResponseEntity<AuthenticateResponse>> {
        try {
            val user = authRepository.findByUsername(request.username)

            var accessToken = ""
            var refreshToken = ""
            withContext(Dispatchers.IO) {
                user.block()
            }?.let {
                accessToken = jwtService.generateAccessToken(it); refreshToken = jwtService.generateRefreshToken(it)
            }
            return Result.Success(
                ResponseEntity(
                    AuthenticateResponse(accessToken, refreshToken),
                    HttpStatus.OK
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            return Result.Success(
                ResponseEntity(
                    AuthenticateResponse("", ""),
                    HttpStatus.UNAUTHORIZED
                )
            )
        }
    }

    @Transactional
    suspend fun refreshTokenRecreation(request: RefreshTokenRequest): Result<ResponseEntity<AuthenticateResponse>> {
        return try {
            var accessToken: String
            var refreshToken: String
            val refresh = withContext(Dispatchers.IO) {
                refreshRepository.findRefreshByToken(request.refreshToken).block()
            }
            val user = withContext(Dispatchers.IO) {
                authRepository.findByUuid(refresh!!.uuid).block()
            }
            withContext(Dispatchers.IO) {
                accessToken = jwtService.generateRefreshToken(user!!)
                refreshToken = jwtService.generateRefreshToken(user)
            }
            Result.Success(ResponseEntity(AuthenticateResponse(accessToken, refreshToken), HttpStatus.OK))
        } catch (e: Exception) {
            Result.Success(ResponseEntity(AuthenticateResponse("", ""), HttpStatus.UNAUTHORIZED))
        }
    }
}