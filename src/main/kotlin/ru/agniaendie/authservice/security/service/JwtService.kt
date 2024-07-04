package ru.agniaendie.authservice.security.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.agniaendie.authservice.exception.UnsignedTokenException
import ru.agniaendie.authservice.logger
import ru.agniaendie.authservice.model.AuthModel
import ru.agniaendie.authservice.model.Refresh
import ru.agniaendie.authservice.repository.RefreshRepository
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@Slf4j
class JwtService(
    @Value("\${jwt.secret.private}") val privateKey: String,
    @Value("\${jwt.secret.public}") val publicKey: String,
    @Value("\${jwt.zoneId}") val zoneId: String,
    @Value("\${jwt.access.exp.hours}") val accessExpHours: Long,
    @Value("\${jwt.access.exp.minutes}") val accessExpMinutes: Long,
    @Value("\${jwt.refresh.exp.days}") val refreshExpDays: Long,
    @Autowired var refreshRepository: RefreshRepository,
) {

    fun generateAccessToken(user: AuthModel): String {
        val key = preparePrivateKey()
        val pkcs8EncodedSpec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(key.toByteArray()))

        val factory = KeyFactory.getInstance("RSA")
        val privateKey = factory.generatePrivate(pkcs8EncodedSpec)

        val claims = mapOf(Pair("exp", expirationAccessGenerate().toString()), Pair("sub", user.uuid))

        return Jwts.builder().claims(claims).signWith(privateKey).compact()
    }

    @Transactional
    fun generateRefreshToken(refreshString: String, user: AuthModel): Mono<Refresh> {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val refresh = Refresh(null, refreshString, user.uuid!!, expirationRefreshGenerate())
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                refreshRepository.save(refresh)
                    .awaitFirst()
            }
        }
        return refresh.toMono()
    }

    fun generateRefreshString(): String {
        val alphanumeric = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val length = 32
        val refreshToken = buildString {
            repeat(length) {
                append(alphanumeric.random())
            }
        }
        return refreshToken
    }


    fun expirationAccessGenerate(): Long {
        val local = LocalDateTime.now()
        val zoneId = ZoneId.of(zoneId)
        val instant = local.atZone(zoneId).toInstant()
        return instant.plus(accessExpHours, ChronoUnit.HOURS).plus(accessExpMinutes, ChronoUnit.MINUTES).toEpochMilli()
    }

    fun expirationRefreshGenerate(): LocalDateTime {
        val local = LocalDateTime.now()
        val zoneId = ZoneId.of(zoneId)
        val instant = local.atZone(zoneId).toInstant()
        return LocalDateTime.ofInstant(instant.plus(refreshExpDays, ChronoUnit.DAYS), zoneId)
    }

    fun validateToken(authToken: String): Boolean {
        try {
            val extractedClaims = extractAllClaims(authToken)
            val currentTime = LocalDateTime.now().atZone(ZoneId.of(zoneId)).toInstant()
            return if (extractedClaims["exp"] is Long) {
                extractedClaims["exp"].toString().toLong() > currentTime.toEpochMilli()
            } else {
                false
            }
        } catch (e: UnsignedTokenException) {
            logger.error(e.message)
            return false
        }
    }

    fun extractAllClaims(token: String): Claims {
        try {
            val key = preparePublicKey()
            val factory = KeyFactory.getInstance("RSA")
            val publicKey = factory.generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(key.toByteArray())))
            //SecretKeySpec(preparePublicKey().toByteArray(), "RSA")

            return Jwts.parser().verifyWith(publicKey).build()
                .parseSignedClaims(token).payload
        } catch (e: IllegalArgumentException) {
            throw UnsignedTokenException("Failed to verify token")
        }
    }


    fun preparePrivateKey(): String {
        val clearedRawKey =
            privateKey.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
                .replace("\\s+".toRegex(), "")
        return clearedRawKey
    }

    fun preparePublicKey(): String {
        val clearedRawKey = publicKey.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
            .replace("\\s+".toRegex(), "")
        return clearedRawKey
    }

}