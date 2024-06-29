package ru.agniaendie.authservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import java.time.LocalDateTime

data class Refresh(@Id var uuid: String?, val token: String, val person: String, var expired: LocalDateTime) :
    Persistable<String> {
    override fun getId(): String? {
        return uuid
    }

    override fun isNew(): Boolean {
        return id == null
    }

}
