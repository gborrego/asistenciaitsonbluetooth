package com.itson.repositories

import com.itson.ClaseEntityQueries
import com.itson.database.Database
import com.itson.models.Clase
import com.itson.models.Dispositivo
import com.itson.utils.asModel

interface DispositivoRepository {
    fun insertDispositivo(nombre: String, direccion: String)
    fun getAllDispositivos(): List<Dispositivo>
    fun deleteDispositivoById(id: Long)
}