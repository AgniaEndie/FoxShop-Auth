package ru.agniaendie.authservice.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


data class AuthModel(@Id var uuid: String, @JvmField var username: String?, @JvmField var password: String, var role: Role, var email:String) : UserDetails {
    override fun getAuthorities(): List<SimpleGrantedAuthority> {
        return listOf(SimpleGrantedAuthority(role.name))
    }
    @JsonIgnore
    override fun getPassword() = password

    override fun getUsername() = username

}