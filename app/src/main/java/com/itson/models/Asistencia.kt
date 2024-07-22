package com.itson.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Asistencia(public val id: Long? = null, public val estado: Estado, public val alumno: Alumno,
                      public val clase: Clase, public val justificante: Justificante? = null,
                      public val fecha: String?) : Parcelable {

    enum class Estado(estado: String){
        PRESENTE("PRESENTE"),
        AUSENTE("AUSENTE"),
        JUSTIFICADO("JUSTIFICADO")
    }
}


