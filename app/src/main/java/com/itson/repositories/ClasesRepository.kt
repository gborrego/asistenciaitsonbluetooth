package com.itson.repositories

import com.itson.models.Clase

interface ClasesRepository {
    fun insertClase(nombre: String, alias: String, ciclo: String, instructor: String)

    fun getAllClases(): List<Clase>

    fun deleteClaseById(id: Long)
}