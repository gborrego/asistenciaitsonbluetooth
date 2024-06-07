package com.itson.repositories

import android.content.Context
import com.itson.ClaseEntityQueries
import com.itson.database.Database
import com.itson.database.DatabaseProvider
import com.itson.models.Clase
import com.itson.utils.asModel

class ClasesRepositoryDB (databaseProvider : DatabaseProvider, context: Context): ClasesRepository {
    private val database: Database = databaseProvider.provideDatabase(context)
    private val claseEntityQueries: ClaseEntityQueries = database.claseEntityQueries

    override fun insertClase(nombre: String, alias: String, ciclo: String, instructor: String) {
        claseEntityQueries.insertClase(nombre, alias, ciclo, instructor)
    }

    override fun getAllClases(): List<Clase> {
        return claseEntityQueries.selectAllClases().executeAsList().map { it.asModel() }
    }

    override fun deleteClaseById(id: Long) {
        claseEntityQueries.deleteClaseById(id)
    }
}