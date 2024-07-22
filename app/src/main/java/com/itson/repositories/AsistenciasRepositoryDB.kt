package com.itson.repositories

import android.app.Application
import android.content.Context
import android.util.Log
import com.itson.AlumnoEntityQueries
import com.itson.AsistenciaEntity
import com.itson.AsistenciaEntityQueries
import com.itson.ClaseEntityQueries
import com.itson.JustificanteEntityQueries
import com.itson.database.Database
import com.itson.database.DatabaseProvider
import com.itson.models.Alumno
import com.itson.models.Asistencia
import com.itson.models.Clase
import com.itson.models.Justificante
import com.itson.utils.asModel

class AsistenciasRepositoryDB(databaseProvider: DatabaseProvider, application: Application): AsistenciasRepository {
    private val database: Database = databaseProvider.provideDatabase(application.applicationContext)
    //TODOS ESTAS LAS CLASES QUE TERMINAN EN EntityQueries SON AUTOGENERADAS (NO MODIFICAR)
    //Para modificar eso hay que ir a la carpeta sqldeligt/com/itson y modificar los archivos .sq
    //De necesitar mas informacion, esta se puede encontrar en la documentacion de sqldelight
    private val alumnoEntityQueries: AlumnoEntityQueries = database.alumnoEntityQueries
    private val claseEntityQueries: ClaseEntityQueries = database.claseEntityQueries
    private val asistenciaEntityQueries: AsistenciaEntityQueries = database.asistenciaEntityQueries
    private val justificanteEntityQueries: JustificanteEntityQueries = database.justificanteEntityQueries

    override fun insert(model: Asistencia) {
        val( _, estado, alumno, clase, justificante, _) = model
        if (clase.id != null) {
            try {
                asistenciaEntityQueries.insertAsistencia(estado, alumno.matricula, clase.id, justificante?.id)
            } catch (e : Exception) {
                e.message?.let { Log.e("DB Error", it) }
                throw Exception("Error! No se pudo insertar $model")
            }
        }
    }

    override fun getById(id: Long): Asistencia {
        try {
            val asistencia = asistenciaEntityQueries.selectAsistenciaById(id).executeAsOneOrNull()
            if (asistencia != null){
                val alumno = modelAlumno(asistencia);
                val clase = modelClase(asistencia);
                val justificante = modelJustificante(asistencia);
                return asistencia.asModel(alumno, clase, justificante)
            }
            throw Exception("Error! No se pudo obtener la asistencia con id $id")
        } catch (e: Exception) {
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo obtener la asistencia con id $id")
        }
    }

    override fun getByClase(clase: Clase): List<Asistencia> {
        if (clase.id != null) {
            return try {
                asistenciaEntityQueries.selectAsistenciasByClase(clase.id).executeAsList().map {
                    val alumno = modelAlumno(it);
                    val justificante = modelJustificante(it);
                    it.asModel(alumno, clase, justificante)
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("DB Error", it) }
                throw Exception("Error! No se pudo obtener las asistencias con clase $clase")
            }
        }
        throw Exception("La clase proporcionada no tiene el parametro id");
    }

    override fun getByClaseAndFecha(clase: Clase, fecha: String): List<Asistencia> {
        if (clase.id != null) {
            return try {
                asistenciaEntityQueries.selectAsistenciasByClaseAndFecha(clase.id, fecha).executeAsList().map {
                    val alumno = modelAlumno(it);
                    val justificante = modelJustificante(it);
                    it.asModel(alumno, clase, justificante)
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("DB Error", it) }
                throw Exception("Error! No se pudo obtener las asistencias con clase $clase y fecha $fecha")
            }
        }
        throw Exception("La clase proporcionada no tiene el parametro id");
    }

    override fun getByClaseAndAlumno(clase: Clase, alumno: Alumno): List<Asistencia> {
        if (clase.id != null) {
            return try {
                asistenciaEntityQueries.selectAsistenciasByClaseAndAlumno(clase.id, alumno.matricula).executeAsList().map {
                    val justificante = modelJustificante(it)
                    it.asModel(alumno, clase,justificante)
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("DB Error", it) }
                throw Exception("Error! No se pudo obtener las asistencias con clase $clase y alumno $alumno")
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

    private fun modelClase(asistenciaEntity: AsistenciaEntity): Clase {
        return asistenciaEntity.id_clase.let { claseId ->
            claseEntityQueries.selectClaseById(claseId).executeAsOneOrNull()?.asModel()
        } ?: throw IllegalArgumentException("La clase no es válida.")
    }
    private fun modelAlumno(asistenciaEntity: AsistenciaEntity): Alumno {
        return asistenciaEntity.id_alumno.let {
            alumnoEntityQueries.selectAlumnoById(it).executeAsOneOrNull()?.asModel();
        } ?: throw IllegalArgumentException("El alumno no es válido.")
    }
    private fun modelJustificante(asistenciaEntity: AsistenciaEntity): Justificante?{
        return asistenciaEntity.id_justificante?.let {
            justificanteEntityQueries.selectJustificanteById(it).executeAsOneOrNull()?.asModel();
        }
    }
}