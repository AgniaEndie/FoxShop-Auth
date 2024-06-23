package ru.agniaendie.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthServiceApplication

fun main(args: Array<String>) {
	println(System.getenv("DB_NAME"))

	runApplication<AuthServiceApplication>(*args)
}
