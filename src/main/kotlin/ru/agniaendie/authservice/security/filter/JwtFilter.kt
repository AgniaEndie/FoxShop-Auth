package ru.agniaendie.authservice.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.agniaendie.authservice.security.service.JwtService

@Component
class JwtFilter(@Autowired var jwtService: JwtService) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.error("rere")
        if (request.headerNames.toList().contains("Authorization")) {
            if (request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer ")) {
                val token = request.getHeader(HttpHeaders.AUTHORIZATION).substringAfter("Bearer ")
                if (jwtService.validateToken(token)) {
                    logger.error("here")
                    filterChain.doFilter(request, response)
                } else {
                    logger.error("here1")
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                }

            } else {
                logger.error("here2")
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            }
        } else {
            logger.error("here3")
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
        }
        filterChain.doFilter(request, response)
    }
}