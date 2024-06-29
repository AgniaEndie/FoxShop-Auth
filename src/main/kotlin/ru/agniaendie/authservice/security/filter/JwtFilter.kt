package ru.agniaendie.authservice.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import reactor.core.scheduler.Schedulers
import ru.agniaendie.authservice.repository.AuthRepository
import ru.agniaendie.authservice.security.service.JwtService


@Component
class JwtFilter(@Autowired var jwtService: JwtService, @Autowired var authRepository: AuthRepository) :
    OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            if (jwtService.validateToken(token)) {
                val map = jwtService.extractAllClaims(token)
                val uuid: String = map["sub"].toString()
                val monoUser = authRepository.findByUuid(uuid).block()
                val coroutineScope = CoroutineScope(Dispatchers.IO)


                val authenticationToken = UsernamePasswordAuthenticationToken(
                    monoUser,
                    monoUser!!.role,
                    monoUser.authorities
                )
                val ctx = SecurityContextHolder.createEmptyContext()
                ctx.authentication = authenticationToken
                SecurityContextHolder.setContext(ctx)

                if (SecurityContextHolder.getContext().authentication == null) {
                    logger.error("error at authorization module")
                    response.status = 401
                }


            } else {
                logger.error("token is invalid")
                response.status = 401
            }
        }
        filterChain.doFilter(request, response)
    }
}