package com.gomu.festup.di

import android.content.Context
import androidx.room.Room
import com.gomu.festup.LocalDatabase.DAO.CuadrillaDao
import com.gomu.festup.LocalDatabase.DAO.CuadrillasAsistentesDao
import com.gomu.festup.LocalDatabase.DAO.EventoDao
import com.gomu.festup.LocalDatabase.DAO.IntegranteDao
import com.gomu.festup.LocalDatabase.DAO.SeguidoresDao
import com.gomu.festup.LocalDatabase.DAO.UsuarioDao
import com.gomu.festup.LocalDatabase.DAO.UsuariosAsistentesDao
import com.gomu.festup.LocalDatabase.Database
import com.gomu.festup.LocalDatabase.Repositories.CuadrillaRepository
import com.gomu.festup.LocalDatabase.Repositories.EventoRepository
import com.gomu.festup.LocalDatabase.Repositories.ICuadrillaRepository
import com.gomu.festup.LocalDatabase.Repositories.IEventoRepository
import com.gomu.festup.LocalDatabase.Repositories.ILoginSettings
import com.gomu.festup.LocalDatabase.Repositories.IUserRepository
import com.gomu.festup.LocalDatabase.Repositories.UserRepository
import com.gomu.festup.Preferences.IGeneralPreferences
import com.gomu.festup.Preferences.PreferencesRepository
import com.gomu.festup.RemoteDatabase.AuthClient
import com.gomu.festup.RemoteDatabase.HTTPClient
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