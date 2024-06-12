package com.itson.repositories

import com.itson.models.Asistencia
import com.itson.models.Clase
import com.itson.models.Justificante

interface JustificantesRepository: Repository<Justificante,Long> {
    fun insertToAsistencia(asistencia: Asistencia, justificante: Justificante)
}