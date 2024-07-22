package com.itson.repositories

import android.app.Application
import com.itson.database.DatabaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Modulos para inyeccion de dependencias utilizando Dagger hilt
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAlumnosRepository(databaseProvider: DatabaseProvider, application: Application): AlumnosRepository{
        return AlumnosRepositoryDB(databaseProvider,application);
    }
    @Provides
    @Singleton
    fun provideAsistenciasRepository(databaseProvider: DatabaseProvider, application: Application): AsistenciasRepository{
        return AsistenciasRepositoryDB(databaseProvider,application);
    }
    @Provides
    @Singleton
    fun provideClasesRepository(databaseProvider: DatabaseProvider, application: Application): ClasesRepository{
        return ClasesRepositoryDB(databaseProvider,application);
    }
    @Provides
    @Singleton
    fun provideDispositivosRepository(databaseProvider: DatabaseProvider, application: Application): DispositivosRepository{
        return DispositivosRepositoryDB(databaseProvider,application);
    }
    @Provides
    @Singleton
    fun provideJustificantesRepository(databaseProvider: DatabaseProvider, application: Application): JustificantesRepository{
        return JustificantesRepositoryDB(databaseProvider,application);
    }
}