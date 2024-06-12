package com.itson.repositories

import android.app.Application
import android.content.Context
import android.util.Log
import com.itson.AlumnoEntityQueries
import com.itson.AsistenciaEntityQueries
import com.itson.ClaseEntityQueries
import com.itson.JustificanteEntityQueries
import com.itson.database.Database
import com.itson.database.DatabaseProvider
import com.itson.models.Asistencia
import com.itson.models.Clase
import com.itson.utils.asModel

class AsistenciasRepositoryDB(databaseProvider: DatabaseProvider, application: Application): AsistenciasRepository {
    private val database: Database = databaseProvider.provideDatabase(application.applicationContext)
    private val alumnoEntityQueries: AlumnoEntityQueries = database.alumnoEntityQueries
    private val claseEntityQueries: ClaseEntityQueries = database.claseEntityQueries
    private val asistenciaEntityQueries: AsistenciaEntityQueries = database.asistenciaEntityQueries
    private val justificanteEntityQueries: JustificanteEntityQueries = database.justificanteEntityQueries

    override fun insert(model: Asistencia) {
        val( _, estado, alumno, clase, justificante, _) = model
        if (justificante != null && clase.id != null) {
            try {
                asistenciaEntityQueries.insertAsistencia(estado, alumno.matricula, clase.id, justificante.id)
            } catch (e : Exception) {
                e.message?.let { Log.e("DB Error", it) }
                throw Exception("Error! No se pudo insertar $model")
            }
        }
    }

    override fun getById(id: Long): Asistencia? {
        return try {
            asistenciaEntityQueries.selectAsistenciaById(id).executeAsOneOrNull().let {
                val alumno = it?.id_alumno?.let { alumnoId ->
                    alumnoEntityQueries.selectAlumnoById(alumnoId).executeAsOne().asModel()
                }
                val clase = it?.id_clase?.let { claseId ->
                    claseEntityQueries.selectClaseById(claseId).executeAsOne().asModel()
                }
                val justificante = it?.id_justificante?.let { justificanteId ->
                    justificanteEntityQueries.selectJustificanteById(justificanteId).executeAsOne()
                        .asModel()
                }
                if (alumno == null || clase == null) {
                    throw Exception("Error! No se pudo obtener la asistencia, no tiene un alumno y/o clase.")
                }
                it.asModel(alumno, clase, justificante)
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo obtener la asistencia con id $id")
        }
    }

    fun getByClaseAndFecha(clase: Clase, fecha: String): List<Asistencia> {
        if (clase.id != null) {
            return try {
                asistenciaEntityQueries.selectAsistenciasByClaseAndFecha(clase.id, fecha).executeAsList().map {
                    val alumno = it.id_alumno.let { alumnoId ->
                        alumnoEntityQueries.selectAlumnoById(alumnoId).executeAsOne().asModel()
                    }
                    it.asModel(alumno, clase)
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("DB Error", it) }
                throw Exception("Error! No se pudo obtener las asistencias con clase $clase y fecha $fecha")
            }
        }
        throw Exception("La clase proporcionada no tiene el parametro id");
    }

    override fun getAll(): List<Asistencia> {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override fun update(model: Asistencia) {
        TODO("Not yet implemented")
    }
}