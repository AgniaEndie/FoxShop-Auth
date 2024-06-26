package ru.agniaendie.authservice.config

import jakarta.servlet.http.HttpSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ru.agniaendie.authservice.security.filter.JwtFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(@Autowired var jwtFilter: JwtFilter) {
    @Bean
    fun securityFilterChain(http: HttpSecurity, httpSession: HttpSession): SecurityFilterChain {
        http.csrf { t -> t.disable() }.cors { t -> t.disable() }.authorizeHttpRequests { requests ->
            requests.requestMatchers(
                "/",
                "/api/auth/create-user",
                "/api/auth/authenticate",
                "/api/auth/refresh-recreation"
            ).permitAll()
                .anyRequest().authenticated()
        }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .anonymous { it.disable() }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java).exceptionHandling { exception ->
                exception
                    .accessDeniedHandler(AccessDeniedHandlerImpl())
            }.securityContext { securityContext -> securityContext.requireExplicitSave(false) }

        return http.build()
    }

}