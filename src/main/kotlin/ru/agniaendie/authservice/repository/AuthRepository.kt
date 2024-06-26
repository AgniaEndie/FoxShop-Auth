package ru.agniaendie.authservice.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.agniaendie.authservice.model.AuthModel

@Repository
interface AuthRepository : ReactiveCrudRepository<AuthModel, String> {
    override fun findById(id: String): Mono<AuthModel>
    fun findByUsername(username: String): Mono<AuthModel>

    fun findByUuid(uuid: String): Mono<AuthModel>

    @Transactional
    fun save(authModel: AuthModel): Mono<AuthModel>

    @Transactional
    fun deleteByUuid(uuid: String): Mono<Void>

    @Transactional
    fun updateAuthModel(authModel: AuthModel): Mono<AuthModel>
}