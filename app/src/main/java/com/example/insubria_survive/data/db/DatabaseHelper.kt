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

        // Crea la tabella "preferenze_esame"
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS preferenze_esame (
                id_preferenza INTEGER PRIMARY KEY AUTOINCREMENT,
                esame_codice TEXT,
                utente_username TEXT,
                stato TEXT,
                FOREIGN KEY(esame_codice) REFERENCES esame(id_esame)
            );
            """
        )

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS esame")
        db.execSQL("DROP TABLE IF EXISTS preferenze_esame")
        onCreate(db)
    }

    companion object {
        private const val DB_NAME = "insubria_survive.db"
        private const val DB_VERSION = 5
    }
}
