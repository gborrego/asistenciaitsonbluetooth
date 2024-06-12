package com.itson.repositories

import android.app.Application
import android.content.Context
import android.util.Log
import com.itson.AsistenciaEntityQueries
import com.itson.JustificanteEntityQueries
import com.itson.database.Database
import com.itson.database.DatabaseProvider
import com.itson.models.Asistencia
import com.itson.models.Justificante
import com.itson.utils.asModel

class JustificantesRepositoryDB(databaseProvider: DatabaseProvider, application: Application): JustificantesRepository {
    private val database: Database = databaseProvider.provideDatabase(application.applicationContext)
    private val justificanteEntityQueries: JustificanteEntityQueries = database.justificanteEntityQueries
    private val asistenciaEntityQueries: AsistenciaEntityQueries = database.asistenciaEntityQueries


    override fun insert(model: Justificante) {
        TODO("Not yet implemented")
    }

    override fun insertToAsistencia(asistencia: Asistencia, justificante: Justificante) {
        if (asistencia.id != null){
            return try {
                justificanteEntityQueries.insertJustificante(nota = justificante.nota, foto = justificante.foto)
                val justificanteId = justificanteEntityQueries.lastInsertRowId().executeAsOne();
                asistenciaEntityQueries.setAsistenciaJustificante(justificanteId, asistencia.id)
            } catch (e: Exception) {
                e.message?.let { Log.e("DB Error", it) }
                throw Exception("Error! No se pudo insertar el justificante a la asistencia")
            }
        }
        throw Exception("La asistencia proporcionada no tiene el parametro id")
    }

    override fun getById(id: Long): Justificante? {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Justificante> {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override fun update(model: Justificante) {
        TODO("Not yet implemented")
    }
}