package ru.agniaendie.authservice.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ru.agniaendie.authservice.security.filter.JwtFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(@Autowired var jwtFilter: JwtFilter) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { requests ->
            requests.requestMatchers("/", "/test").permitAll()
                .anyRequest().authenticated()
        }.addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

}