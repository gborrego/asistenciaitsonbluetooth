package com.itson.repositories

import android.app.Application
import android.content.Context
import com.itson.DispositivoEntityQueries
import com.itson.database.Database
import com.itson.database.DatabaseProvider
import com.itson.models.Dispositivo
import com.itson.utils.asModel

class DispositivosRepositoryDB (databaseProvider: DatabaseProvider, context: Context, application: Application): DispositivoRepository{
    private val database: Database = databaseProvider.provideDatabase(application.applicationContext)
    private val dispositivoEntityQueries: DispositivoEntityQueries= database.dispositivoEntityQueries

    override fun insertDispositivo(nombre: String, direccion: String) {
        dispositivoEntityQueries.insertDispositivo(nombre, direccion)
    }

    override fun getAllDispositivos(): List<Dispositivo> {
        return dispositivoEntityQueries.selectAllDispositivos().executeAsList().map { it.asModel() }
    }

    override fun deleteDispositivoById(id: Long) {
        dispositivoEntityQueries.deleteDispositivoById(id)
    }
}