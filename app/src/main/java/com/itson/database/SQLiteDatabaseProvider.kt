package com.itson.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class SQLiteDatabaseProvider : DatabaseProvider {
    private var database: Database? = null

    override fun provideDatabase(context: Context): Database {
        if (database == null) {
            val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, context, "asistencia.db")
            database = Database(driver)
        }
        return database!!
    }
}