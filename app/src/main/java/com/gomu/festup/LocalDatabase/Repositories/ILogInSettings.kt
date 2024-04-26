package com.gomu.festup.LocalDatabase.Repositories

interface ILoginSettings {
    suspend fun getLastLoggedUser(): String?
    suspend fun setLastLoggedUser(user: String)
}