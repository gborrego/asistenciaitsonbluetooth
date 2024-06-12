package com.itson.utils

import com.itson.AlumnoEntity
import com.itson.AsistenciaEntity
import com.itson.ClaseEntity
import com.itson.DispositivoEntity
import com.itson.JustificanteEntity
import com.itson.models.Alumno
import com.itson.models.Asistencia
import com.itson.models.Clase
import com.itson.models.Dispositivo
import com.itson.models.Justificante

fun AlumnoEntity.asModel(dispositivo: Dispositivo? = null) = Alumno(matricula,nombre,apellido,dispositivo);
fun ClaseEntity.asModel(alumnos: List<Alumno>? = null) = Clase(id, nombre, alias, ciclo, instructor, alumnos);
fun AsistenciaEntity.asModel(alumno: Alumno, clase: Clase, justificante: Justificante? = null) = Asistencia(id,estado,alumno,clase,justificante,fecha)
fun DispositivoEntity.asModel() = Dispositivo(id,nombre,direccion);
fun JustificanteEntity.asModel() = Justificante(id, nota, foto);