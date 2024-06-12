package com.itson.repositories

import android.app.Application
import android.util.Log
import com.itson.AlumnoClaseQueries
import com.itson.ClaseEntityQueries
import com.itson.database.Database
import com.itson.database.DatabaseProvider
import com.itson.models.Clase
import com.itson.utils.asModel

class ClasesRepositoryDB (databaseProvider : DatabaseProvider, application: Application): ClasesRepository {
    private val database: Database = databaseProvider.provideDatabase(application.applicationContext)
    private val claseEntityQueries: ClaseEntityQueries = database.claseEntityQueries
    private val alumnoClaseQueries: AlumnoClaseQueries = database.alumnoClaseQueries


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
            claseEntityQueries.selectClaseById(id).executeAsOneOrNull()?.let{
                val alumnos = alumnoClaseQueries.selectAlumnosForClase(it.id).executeAsList().map { it2 ->
                    it2.asModel(null)
                }
                it.asModel(alumnos)
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