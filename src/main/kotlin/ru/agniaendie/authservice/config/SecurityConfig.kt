package ru.agniaendie.authservice.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ru.agniaendie.authservice.logger
import ru.agniaendie.authservice.security.filter.JwtFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(@Autowired var jwtFilter: JwtFilter) {

    //TODO remove /error from testFilterChain
    @Bean
    fun testFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .cors { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers("/api/auth/create-user", "/api/auth/authenticate", "/error").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { ex ->
                ex.accessDeniedHandler(AccessDeniedHandlerImpl())
                ex.authenticationEntryPoint { request, response, authException ->
                    logger.error("$authException")
                    logger.error("${http}}")
                    response.status = 401
                }
            }.securityContext { securityContext -> securityContext.requireExplicitSave(false) }
            .sessionManagement { sessionManager ->sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}