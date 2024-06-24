package ru.agniaendie.authservice.security.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class JwtService(@Value("\${jwt.secret}") val secretKey: String) {
    fun generateToken(username: String): String {
        return Jwts.builder().subject("").expiration(Date()).signWith(key()).compact()
    }

    private fun key(): Key {
        return Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    fun validateToken(authToken: String): Boolean {
        TODO("$authToken is not yet implemented")
    }
}