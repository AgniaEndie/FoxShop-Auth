package ru.agniaendie.authservice.config

import jakarta.servlet.http.HttpSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.http
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.server.SecurityWebFilterChain
import ru.agniaendie.authservice.logger
import ru.agniaendie.authservice.security.filter.JwtFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(@Autowired var jwtFilter: JwtFilter) {


    @Bean
    fun testFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf{it.disable()}
            .cors{it.disable()}
            .authorizeHttpRequests { authorize ->
            authorize.requestMatchers("/api/auth/create-user","/error").permitAll()
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

            .build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}