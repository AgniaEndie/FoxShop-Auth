package ru.agniaendie.authservice.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import ru.agniaendie.authservice.model.AuthModel

@Repository
interface AuthRepository : ReactiveCrudRepository<AuthModel,String> {
}