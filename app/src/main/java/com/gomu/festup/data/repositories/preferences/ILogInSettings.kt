package com.gomu.festup.data.repositories.preferences

interface ILoginSettings {
    suspend fun getLastLoggedUser(): String
    suspend fun setLastLoggedUser(user: String)

    suspend fun getLastBearerToken(): String
    suspend fun setLastBearerToken(token: String)

    suspend fun getLastRefreshToken(): String
    suspend fun setLastRefreshToken(token: String)
}