package ru.agniaendie.authservice.model

data class Refresh(val uuid: String, val token: String, val person: String)
