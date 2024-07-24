package ru.agniaendie.authservice.exception

class ExpiredJwtTokenException(override var message: String) : RuntimeException()