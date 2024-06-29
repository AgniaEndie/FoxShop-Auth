package ru.agniaendie.authservice.security.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import ru.agniaendie.authservice.exception.ExpiredJwtTokenException
import ru.agniaendie.authservice.model.response.exception.Error

@ControllerAdvice
class MainExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ExpiredJwtTokenException::class)
    fun handleJwtTokenExpiredException(ex: ExpiredJwtTokenException, request: WebRequest): ResponseEntity<Any> {
        logger.error(ex.message, ex)
        return ResponseEntity(Error("expired token"), HttpStatus.UNAUTHORIZED)
    }

}