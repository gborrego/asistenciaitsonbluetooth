package com.itson.repositories

import com.itson.models.Alumno
import com.itson.models.Clase

interface ClasesRepository: Repository<Clase, Long> {
    fun getLast(): Clase?
    fun claseContainsAlumno(clase: Clase, alumno: Alumno): Boolean
}