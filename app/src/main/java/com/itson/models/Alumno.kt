package com.itson.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Alumno(  public val matricula: Long,
                    public val nombre: String,
                    public val apellido: String,
                    public val dispositivo: Dispositivo? = null) : Parcelable
