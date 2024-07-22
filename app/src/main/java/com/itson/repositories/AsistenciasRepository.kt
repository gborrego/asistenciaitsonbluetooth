package com.itson.repositories

import com.itson.models.Alumno
import com.itson.models.Asistencia
import com.itson.models.Clase

interface AsistenciasRepository: Repository<Asistencia, Long> {
    fun getByClase(clase: Clase): List<Asistencia>
    fun getByClaseAndFecha(clase: Clase, fecha: String): List<Asistencia>
    fun getByClaseAndAlumno(clase: Clase, alumno: Alumno): List<Asistencia>
}