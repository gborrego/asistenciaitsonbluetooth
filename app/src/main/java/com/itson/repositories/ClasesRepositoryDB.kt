package com.itson.repositories

import android.app.Application
import android.util.Log
import com.itson.AlumnoClaseQueries
import com.itson.ClaseEntityQueries
import com.itson.DispositivoEntityQueries
import com.itson.database.Database
import com.itson.database.DatabaseProvider
import com.itson.models.Alumno
import com.itson.models.Clase
import com.itson.utils.asModel

class ClasesRepositoryDB (databaseProvider : DatabaseProvider, application: Application): ClasesRepository {
    private val database: Database = databaseProvider.provideDatabase(application.applicationContext)
    //TODOS ESTAS LAS CLASES QUE TERMINAN EN EntityQueries SON AUTOGENERADAS (NO MODIFICAR)
    //Para modificar eso hay que ir a la carpeta sqldeligt/com/itson y modificar los archivos .sq
    //De necesitar mas informacion, esta se puede encontrar en la documentacion de sqldelight
    private val claseEntityQueries: ClaseEntityQueries = database.claseEntityQueries
    private val alumnoClaseQueries: AlumnoClaseQueries = database.alumnoClaseQueries
    private val dispositivoEntityQueries: DispositivoEntityQueries = database.dispositivoEntityQueries


    override fun insert(model: Clase) {
        try {
            val (_, nombre, alias, ciclo, instructor) = model
            claseEntityQueries.insertClase(nombre, alias, ciclo, instructor)
        } catch (e: Exception){
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo insertar $model")
        }
    }

    override fun getById(id: Long): Clase? {
        return try {
            claseEntityQueries.selectClaseById(id).executeAsOneOrNull()?.let{ clase ->
                val alumnos = alumnoClaseQueries.selectAlumnosForClase(clase.id).executeAsList().map { alumno ->
                    val dispositivo = alumno.id_dispositivo?.let { id -> dispositivoEntityQueries.selectDispositivoById(id).executeAsOneOrNull() }?.asModel()
                    alumno.asModel(dispositivo)
                }
                clase.asModel(alumnos)
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo obtener la clase con id $id")
        }
    }

    override fun getAll(): List<Clase> {
        return try{
            claseEntityQueries.selectAllClases().executeAsList().map { it.asModel(null) }
        } catch (e: Exception){
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo obtener la lista de clases")
        }
    }

    override fun getLast(): Clase? {
        return try{
            claseEntityQueries.lastInsertRowId().executeAsOneOrNull()?.let{
                val clase = claseEntityQueries.selectClaseById(it).executeAsOneOrNull();
                return clase?.asModel(null);
            }
        } catch (e: Exception){
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo obtener la clase")
        }
    }

    override fun claseContainsAlumno(clase: Clase, alumno: Alumno): Boolean {
        return try{
            val claseAlumno =
                clase.id?.let { alumnoClaseQueries.selectAlumnoInClase(it,alumno.matricula) }?.executeAsOneOrNull()
            claseAlumno != null
        } catch (e: Exception){
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo obtener la clase")
        }
    }

    override fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override fun update(model: Clase) {
        if (model.id != null) {
            val (id, nombre, alias, ciclo, instructor) = model
            return try {
                claseEntityQueries.updateClase(nombre, alias, ciclo, instructor, model.id)
            } catch (e: Exception) {
                e.message?.let { Log.e("DB Error", it) }
                throw Exception("Error! No se pudo actualizar $model")
            }
        }
        throw Exception ("La clase proporcionada no tiene el parametro id")
    }
}