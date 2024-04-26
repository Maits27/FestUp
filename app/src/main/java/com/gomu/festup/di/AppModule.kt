package com.gomu.festup.di

import android.content.Context
import androidx.room.Room
import com.gomu.festup.LocalDatabase.Database
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
            .createFromAsset("database/festUp.db")
            .build()
}