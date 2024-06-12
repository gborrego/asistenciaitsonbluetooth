package com.itson.repositories

import com.itson.models.Alumno
import com.itson.models.Clase

interface AlumnosRepository: Repository<Alumno, Long> {
    fun setToClase(alumno: Alumno, clase: Clase)
    fun getAllByClase(clase: Clase): List<Alumno>
}