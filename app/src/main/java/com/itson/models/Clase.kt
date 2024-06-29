package com.itson.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Clase(  val id: Long? = null,
                   val nombre: String,
                   val alias: String,
                   val ciclo: String,
                   val instructor: String,
                   val alumnos: List<Alumno>? = null) : Parcelable
