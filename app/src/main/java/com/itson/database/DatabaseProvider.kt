package com.itson.database

import android.content.Context

interface DatabaseProvider {
    fun provideDatabase(context: Context): Database
}