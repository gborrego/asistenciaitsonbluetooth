package com.itson.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Dispositivo(  val id: Long? = null,
                         val nombre: String,
                         val direccion: String,) : Parcelable{

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dispositivo

        if (id != other.id) return false
        return direccion == other.direccion
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + direccion.hashCode()
        return result
    }
}
