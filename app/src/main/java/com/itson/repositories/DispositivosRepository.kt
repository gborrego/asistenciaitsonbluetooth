package com.itson.repositories

import com.itson.models.Alumno
import com.itson.models.Dispositivo

interface DispositivosRepository: Repository<Dispositivo, Long>{
    fun insertToAlumno(dispositivo: Dispositivo, alumno: Alumno)
}