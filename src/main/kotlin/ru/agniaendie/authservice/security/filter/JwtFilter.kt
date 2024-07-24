package ru.agniaendie.authservice.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import ru.agniaendie.authservice.exception.ExpiredJwtTokenException
import ru.agniaendie.authservice.repository.AuthRepository
import ru.agniaendie.authservice.security.service.JwtService


@Component
class JwtFilter(
    @Autowired var jwtService: JwtService, @Autowired var authRepository: AuthRepository, @Autowired
    @Qualifier("handlerExceptionResolver") var handlerExceptionResolver: HandlerExceptionResolver
) :
    OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        try {
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
                    throw ExpiredJwtTokenException("token is invalid")
                }
            }
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            handlerExceptionResolver.resolveException(request, response, null, e)
        }
    }
}