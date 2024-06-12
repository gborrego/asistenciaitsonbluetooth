package com.itson.repositories

interface Repository<T,K> {
    fun insert(model: T)
    fun getById(id: K): T?
    fun getAll(): List<T>
    fun delete(id : K)
    fun update(model: T)
}