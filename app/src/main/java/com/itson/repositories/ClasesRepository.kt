package com.itson.repositories

import com.itson.models.Clase

interface ClasesRepository: Repository<Clase, Long> {
    fun getLast(): Clase?

}