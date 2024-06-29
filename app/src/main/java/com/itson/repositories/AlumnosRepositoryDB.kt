package com.itson.repositories

import android.app.Application
import android.content.Context
import android.util.Log
import com.itson.AlumnoClaseQueries
import com.itson.AlumnoEntity
import com.itson.AlumnoEntityQueries
import com.itson.ClaseEntityQueries
import com.itson.DispositivoEntityQueries
import com.itson.database.Database
import com.itson.database.DatabaseProvider
import com.itson.models.Alumno
import com.itson.models.Clase
import com.itson.models.Dispositivo
import com.itson.utils.asModel

class AlumnosRepositoryDB(databaseProvider: DatabaseProvider, application: Application): AlumnosRepository {
    private val database: Database = databaseProvider.provideDatabase(application.applicationContext)
    private val alumnoEntityQueries: AlumnoEntityQueries = database.alumnoEntityQueries
    private val dispositivoEntityQueries: DispositivoEntityQueries = database.dispositivoEntityQueries
    private val alumnoClaseQueries:  AlumnoClaseQueries = database.alumnoClaseQueries

    override fun insert(model: Alumno) {
        val(matricula,nombre,apellido,dispositivo) = model
        try {
            alumnoEntityQueries.insertAlumno(matricula,nombre,apellido,dispositivo?.id)
        } catch (e: Exception) {
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo insertar $model")
        }
    }

    override fun getById(id: Long): Alumno? {
        try {
            val alumno = alumnoEntityQueries.selectAlumnoById(id).executeAsOneOrNull()
            if (alumno != null){
                val dispositivo = modelarDispositvo(alumno);
                return alumno.asModel(dispositivo)
            }
            return null;
        } catch (e: Exception) {
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo obtener al alumno con id $id")
        }
    }

    override fun getAll(): List<Alumno> {
        return try {
            alumnoEntityQueries.selectAllAlumnos().executeAsList().map {
                val dispositivo = modelarDispositvo(it);
                it.asModel(dispositivo)
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo obtener la lista de alumnos")
        }
    }

    override fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override fun update(model: Alumno) {
        try {
            val (matricula,nombre,apellido,dispositivo) = model
            alumnoEntityQueries.updateAlumno(nombre,apellido,dispositivo?.id,matricula)
        } catch (e: Exception) {
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo actualizar al alumno $model")
        }
    }

    override fun setToClase(alumno: Alumno, clase: Clase){
        if (clase.id != null) {
            return try {
                alumnoClaseQueries.insertAlumnoClase(alumno.matricula, clase.id)
            } catch (e: Exception) {
                e.message?.let { Log.e("DB Error", it) }
                throw Exception("Error! No se pudo insertar $alumno a $clase")
            }
        }
        throw Exception ("La clase proporcionada no tiene el parametro id")
    }

    override fun getAllByClase(clase: Clase): List<Alumno> {
        if (clase.id != null) {
            return try {
                 alumnoClaseQueries.selectAlumnosForClase(clase.id).executeAsList().map { alumno ->
                    val dispositivo = modelarDispositvo(alumno);
                    alumno.asModel(dispositivo)
                }
            }  catch (e: Exception) {
                e.message?.let { Log.e("DB Error", it) }
                throw Exception("Error! No se pudo obtener la lista de alumnos")
            }
        }
        throw Exception ("La clase proporcionada no tiene el parametro id")
    }

    private fun modelarDispositvo(alumnoEntity: AlumnoEntity): Dispositivo? {
        return alumnoEntity.id_dispositivo?.let { dispositivoId ->
            dispositivoEntityQueries.selectDispositivoById(dispositivoId).executeAsOneOrNull()?.asModel()
        }
    }
}