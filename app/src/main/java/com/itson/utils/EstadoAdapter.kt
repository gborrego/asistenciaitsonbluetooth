package com.itson.utils

import app.cash.sqldelight.ColumnAdapter
import com.itson.models.Asistencia.Estado

val estadoAdapter = object : ColumnAdapter<Estado, String> {
    override fun decode(databaseValue: String): Estado {
        return Estado.valueOf(databaseValue)
    }

    override fun encode(value: Estado): String {
        return value.name
    }
}