package com.itson.repositories

import android.app.Application
import android.content.Context
import android.util.Log
import com.itson.AlumnoEntityQueries
import com.itson.DispositivoEntityQueries
import com.itson.database.Database
import com.itson.database.DatabaseProvider
import com.itson.models.Alumno
import com.itson.models.Dispositivo
import com.itson.utils.asModel

class DispositivosRepositoryDB (databaseProvider: DatabaseProvider, application: Application): DispositivosRepository{
    private val database: Database = databaseProvider.provideDatabase(application.applicationContext)
    private val dispositivoEntityQueries: DispositivoEntityQueries= database.dispositivoEntityQueries
    private val alumnoEntityQueries: AlumnoEntityQueries= database.alumnoEntityQueries

    override fun insert(model: Dispositivo) {
        TODO("Not yet implemented")
    }

    override fun insertToAlumno(dispositivo: Dispositivo, alumno: Alumno) {
        val(_, nombre, direccion) = dispositivo
        try {
            dispositivoEntityQueries.insertDispositivo(nombre,direccion)
            dispositivoEntityQueries.selectDispositivoByDireccion(direccion).executeAsOneOrNull()?.let {
                return alumnoEntityQueries.setAlumnoDispositivo(it.id, alumno.matricula)
            }
            throw Exception("No se pudo establecer el $dispositivo al $alumno")
        } catch (e: Exception) {
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo insertar $dispositivo a $alumno")
        }
    }

    override fun getByDireccion(direccion: String): Dispositivo? {
        try {
            return dispositivoEntityQueries.selectDispositivoByDireccion(direccion).executeAsOneOrNull()
                ?.asModel()
        } catch (e: Exception){
            e.message?.let { Log.e("DB Error", it) }
            throw Exception("Error! No se pudo obtener el dispo")
        }
    }

    override fun getById(id: Long): Dispositivo {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Dispositivo> {
        return dispositivoEntityQueries.selectAllDispositivos().executeAsList().map { it.asModel() }
    }

    override fun delete(id: Long) {
        dispositivoEntityQueries.deleteDispositivoById(id)
    }

    override fun update(model: Dispositivo) {
        TODO("Not yet implemented")
    }
}