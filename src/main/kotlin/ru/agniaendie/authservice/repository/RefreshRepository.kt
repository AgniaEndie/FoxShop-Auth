package ru.agniaendie.authservice.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.agniaendie.authservice.model.Refresh

interface RefreshRepository : ReactiveCrudRepository<Refresh, String> {
    fun findRefreshByUuid(uuid: String): Mono<Refresh>

    fun findRefreshByToken(token: String): Mono<Refresh>

    @Transactional
    fun deleteRefreshByUuid(uuid: String): Mono<Void>
}