package com.itson.models

data class Asistencia(public val id: Long? = null, public val estado: Estado, public val alumno: Alumno,
                      public val clase: Clase, public val justificante: Justificante? = null,
                      public val fecha: String?) {

    enum class Estado(estado: String){
        PRESENTE("PRESENTE"),
        AUSENTE("AUSENTE"),
        JUSTIFICADO("JUSTIFICADO")
    }
}


