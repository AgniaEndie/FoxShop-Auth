package ru.agniaendie.authservice.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
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

//        if (request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer ")) {
//            val token = request.getHeader(HttpHeaders.AUTHORIZATION).substringAfter("Bearer ")
//            if (jwtService.validateToken(token)) {
//                logger.error("rere1")
//                val username: String = jwtService.extractAllClaims(token)["sub"].toString()
//                authRepository.findByUsername(username).doOnNext { elem ->
//                    val authenticationToken = UsernamePasswordAuthenticationToken(
//                        elem,
//                        elem.role,
//                        elem.authorities
//                    )
//                    val ctx = SecurityContextHolder.createEmptyContext()
//                    SecurityContextHolder.setContext(ctx)
//                    ctx.authentication = authenticationToken
//                }
//            } else {
//                logger.error("rere2")
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
////                    response.writer.write("{'message':'unauthorized'}")
////                    response.writer.flush()
//            }
//        }
//
//        filterChain.doFilter(request, response)
//

        val header = request.getHeader("Authorization")
        if(header != null && header.startsWith("Bearer ")){
            val token = header.substring(7)
            if(jwtService.validateToken(token)){
                val username: String = jwtService.extractAllClaims(token)["sub"].toString()
                val fluxedUser = authRepository.findByUsername(username)
                authRepository.findByUsername(username).doOnNext { elem ->
                    val authenticationToken = UsernamePasswordAuthenticationToken(
                        elem,
                        elem.role,
                        elem.authorities
                    )
                    val ctx = SecurityContextHolder.createEmptyContext()
                    SecurityContextHolder.setContext(ctx)
                    ctx.authentication = authenticationToken
                }
                if(SecurityContextHolder.getContext().authentication == null){
                    logger.error("error at authorization module")
                    response.status = 401
                }
            }else{
                logger.error("token is expired")
                response.status = 401
            }
        }
        filterChain.doFilter(request,response)
    }
}