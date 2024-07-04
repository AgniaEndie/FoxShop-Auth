package ru.agniaendie.authservice

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.core.publisher.Mono
import ru.agniaendie.authservice.model.AuthModel
import ru.agniaendie.authservice.model.Refresh
import ru.agniaendie.authservice.model.Role
import ru.agniaendie.authservice.repository.RefreshRepository
import ru.agniaendie.authservice.security.service.JwtService
import java.util.*
import kotlin.test.assertTrue

@SpringBootTest
class JwtServiceTests {

    @Autowired
    private lateinit var jwtService: JwtService

    @MockBean
    private lateinit var refreshRepository: RefreshRepository
    val testUser = AuthModel(UUID.randomUUID().toString(), "testPassword", "", Role.ROLE_NORMAL, "testUser@test.test")

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun isValidJwtTest() {
        val accessToken = jwtService.generateAccessToken(testUser)
        assertTrue(jwtService.validateToken(accessToken))
    }

    @Test
    fun isValidRefreshTest() {
        val refreshToken = jwtService.generateRefreshString()
        val savedRefresh = Refresh(UUID.randomUUID().toString(), refreshToken, testUser.uuid!!, jwtService.expirationRefreshGenerate())
        Mockito.`when`(refreshRepository.save(savedRefresh)).thenReturn(Mono.just(savedRefresh))
        val result = jwtService.generateRefreshToken(refreshToken, testUser).block()
        assertEquals(refreshToken, result!!.token)
    }

}