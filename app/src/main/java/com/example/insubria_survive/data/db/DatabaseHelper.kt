package com.example.insubria_survive.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crea la tabella "esame"
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS esame (
                id_esame TEXT PRIMARY KEY,
                corso TEXT,
                data TEXT,
                aula TEXT,
                padiglione TEXT
            );
            """
        )
        // Se hai altre tabelle (preferenze_esame, ecc.), definiscile qui
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS esame")
        // Se hai altre tabelle, droppale qui
        onCreate(db)
    }

    companion object {
        private const val DB_NAME = "insubria_survive.db"
        private const val DB_VERSION = 1
    }
}
