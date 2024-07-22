package com.itson.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Alumno(  public val matricula: Long,
                    public val nombre: String,
                    public val apellido: String,
                    public val dispositivo: Dispositivo? = null) : Parcelable{

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Alumno

        return matricula == other.matricula
    }

    override fun hashCode(): Int {
        return matricula.hashCode()
    }
}
