package com.itson.utils

import com.itson.ClaseEntity
import com.itson.DispositivoEntity
import com.itson.models.Clase
import com.itson.models.Dispositivo

fun ClaseEntity.asModel() = Clase(id, nombre, alias, ciclo, instructor);

fun DispositivoEntity.asModel() = Dispositivo(id,nombre,direccion)

