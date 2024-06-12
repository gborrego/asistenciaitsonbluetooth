package com.itson.models

data class Alumno(  public val matricula: Long,
                    public val nombre: String,
                    public val apellido: String,
                    public val dispositivo: Dispositivo? = null)
