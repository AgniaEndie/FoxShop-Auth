package ru.agniaendie.authservice

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories
class AuthServiceApplication

val logger = LoggerFactory.getLogger(AuthServiceApplication::class.java)

fun main(args: Array<String>) {
	runApplication<AuthServiceApplication>(*args)
}