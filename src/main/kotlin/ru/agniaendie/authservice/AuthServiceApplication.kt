package ru.agniaendie.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories
class AuthServiceApplication

fun main(args: Array<String>) {
	println(System.getenv("DB_NAME"))

	runApplication<AuthServiceApplication>(*args)
}
