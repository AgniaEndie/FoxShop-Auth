package ru.agniaendie.authservice.model

import com.fasterxml.jackson.annotation.JsonIgnore
import lombok.Data
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.agniaendie.authservice.logger
import java.util.*

@Data
data class AuthModel(
    @Id var uuid: String,
    @JvmField var username: String?,
    @JvmField var password: String,
    var role: Role,
    var email: String
) : UserDetails, Persistable<String> {
    override fun getAuthorities(): List<SimpleGrantedAuthority> {
        return listOf(SimpleGrantedAuthority(role.name))
    }

    @JsonIgnore
    override fun getPassword() = password

    override fun getUsername() = username
    override fun getId(): String? {
        return this.uuid
    }

    override fun isNew(): Boolean {
        logger.debug(id)
        if(id == null){
            uuid = UUID.randomUUID().toString()
            return true
        }else{
            return false
        }
    }

}