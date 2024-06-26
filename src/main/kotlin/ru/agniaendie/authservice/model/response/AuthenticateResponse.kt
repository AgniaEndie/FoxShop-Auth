package ru.agniaendie.authservice.model.response

data class AuthenticateResponse(val accessToken: String, val refreshToken: String)