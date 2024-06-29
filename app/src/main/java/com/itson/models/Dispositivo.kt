package com.itson.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Dispositivo(  val id: Long? = null,
                         val nombre: String,
                         val direccion: String,) : Parcelable
