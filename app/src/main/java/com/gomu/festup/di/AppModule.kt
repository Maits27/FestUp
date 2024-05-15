package com.gomu.festup.di

import android.content.Context
import androidx.room.Room
import com.gomu.festup.data.http.AuthClient
import com.gomu.festup.data.http.HTTPClient
import com.gomu.festup.data.localDatabase.DAO.CuadrillaDao
import com.gomu.festup.data.localDatabase.DAO.CuadrillasAsistentesDao
import com.gomu.festup.data.localDatabase.DAO.EventoDao
import com.gomu.festup.data.localDatabase.DAO.IntegranteDao
import com.gomu.festup.data.localDatabase.DAO.SeguidoresDao
import com.gomu.festup.data.localDatabase.DAO.UsuarioDao
import com.gomu.festup.data.localDatabase.DAO.UsuariosAsistentesDao
import com.gomu.festup.data.localDatabase.Database
import com.gomu.festup.data.repositories.CuadrillaRepository
import com.gomu.festup.data.repositories.EventoRepository
import com.gomu.festup.data.repositories.ICuadrillaRepository
import com.gomu.festup.data.repositories.IEventoRepository
import com.gomu.festup.data.repositories.IUserRepository
import com.gomu.festup.data.repositories.UserRepository
import com.gomu.festup.data.repositories.preferences.IGeneralPreferences
import com.gomu.festup.data.repositories.preferences.ILoginSettings
import com.gomu.festup.data.repositories.preferences.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app:Context) =
        Room.databaseBuilder(app, Database::class.java, "festUpDatabase")
//            .createFromAsset("database/festUp.db")
            .build()

    /***************************  DAOs  ***************************/
    @Singleton
    @Provides
    fun providesUsuarioDao(db: Database) = db.usuarioDao()
    @Singleton
    @Provides
    fun providesCuadrillaDao(db: Database) = db.cuadrillaDao()

    @Singleton
    @Provides
    fun provideEventoDao(db: Database) = db.eventoDao()

    @Singleton
    @Provides
    fun provideIntegranteDao(db: Database) = db.integranteDao()

    @Singleton
    @Provides
    fun provideSeguidoresDao(db: Database) = db.seguidoresDao()

    @Singleton
    @Provides
    fun provideCuadrillasAsistentesDao(db: Database) = db.usuariosAsistentesDao()

    @Singleton
    @Provides
    fun provideUsuariosAsistentesDao(db: Database) = db.cuadrillasAsistentesDao()

    /***************************  Repositorios  ***************************/

    @Singleton
    @Provides
    fun providesUserRepository(
        usuarioDao: UsuarioDao,
        seguidoresDao: SeguidoresDao,
        loginSettings: ILoginSettings,
        authClient: AuthClient,
        httpClient: HTTPClient
    ): IUserRepository =
        UserRepository(usuarioDao, seguidoresDao, loginSettings, authClient, httpClient)

    @Singleton
    @Provides
    fun provideCuadrillaRepository(
        cuadrillaDao: CuadrillaDao,
        integranteDao: IntegranteDao,
        httpClient: HTTPClient
    ): ICuadrillaRepository =
        CuadrillaRepository(cuadrillaDao, integranteDao, httpClient)

    @Singleton
    @Provides
    fun provideEventoRepository(
        eventoDao: EventoDao,
        usuariosAsistentesDao: UsuariosAsistentesDao,
        cuadrillasAsistentesDao: CuadrillasAsistentesDao,
        httpClient: HTTPClient
    ): IEventoRepository =
        EventoRepository(eventoDao, usuariosAsistentesDao, cuadrillasAsistentesDao, httpClient)

    /** ////////////////////////////////////////////////////////
    ////////////   Repositorio de Preferencias   /////////////
    //////////////////////////////////////////////////////////
     */
    @Singleton
    @Provides
    fun provideLoginSettings(@ApplicationContext app: Context): ILoginSettings = PreferencesRepository(app)
    @Singleton
    @Provides
    fun provideUserPreferences(@ApplicationContext app: Context): IGeneralPreferences = PreferencesRepository(app)


}