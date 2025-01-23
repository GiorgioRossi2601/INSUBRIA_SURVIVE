package com.example.insubria_survive.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * La classe [DatabaseHelper] gestisce la creazione ed eventuale aggiornamento
 * del database SQLite per l’applicazione.
 *
 * Estende [SQLiteOpenHelper] e definisce le tabelle "esame", "preferenze_esame", "lezione" e "padiglione".
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    /**
     * Metodo chiamato al momento della prima creazione del database.
     * Qui vengono create le tabelle.
     *
     * @param db Il database SQLite su cui eseguire le operazioni.
     */
    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "Creazione del database e delle tabelle necessarie.")

        // Creazione della tabella "esame"
        val createEsameTable = """
            CREATE TABLE IF NOT EXISTS esame (
                id_esame TEXT PRIMARY KEY,
                corso TEXT,
                data TEXT,
                aula TEXT,
                padiglione TEXT
            );
            """.trimIndent()
        Log.d(TAG, "Esecuzione SQL per la creazione della tabella 'esame': $createEsameTable")
        db.execSQL(createEsameTable)

        // Creazione della tabella "preferenze_esame"
        val createPreferenzeTable = """
            CREATE TABLE IF NOT EXISTS preferenze_esame (
                id_preferenza INTEGER PRIMARY KEY AUTOINCREMENT,
                esame_codice TEXT,
                utente_username TEXT,
                stato TEXT,
                UNIQUE (esame_codice, utente_username),
                FOREIGN KEY(esame_codice) REFERENCES esame(id_esame) ON UPDATE CASCADE
            );
            """.trimIndent()
        Log.d(
            TAG,
            "Esecuzione SQL per la creazione della tabella 'preferenze_esame': $createPreferenzeTable"
        )
        db.execSQL(createPreferenzeTable)

        // Creazione della tabella "lezione"
        val createLezioneTable = """
            CREATE TABLE IF NOT EXISTS lezione (
                id_lezione TEXT PRIMARY KEY,
                corso TEXT,
                data_inizio TEXT,
                data_fine TEXT,
                aula TEXT,
                padiglione TEXT
            );
            """.trimIndent()
        Log.d(TAG, "Esecuzione SQL per la creazione della tabella 'lezione': $createLezioneTable")
        db.execSQL(createLezioneTable)

        // Creazione della tabella "padiglione"
        val createPadiglioneTable = """
            CREATE TABLE IF NOT EXISTS padiglione (
                id_padiglione TEXT,
                codice_padiglione TEXT PRIMARY KEY,
                descrizione TEXT,
                ora_apertura TEXT,
                ora_chiusura TEXT,
                posizione TEXT
            );
            """.trimIndent()
        Log.d(
            TAG,
            "Esecuzione SQL per la creazione della tabella 'padiglione': $createPadiglioneTable"
        )
        db.execSQL(createPadiglioneTable)
    }

    /**
     * Metodo chiamato quando è necessario aggiornare il database.
     * In questo esempio vengono eliminate le tabelle esistenti e ricreate.
     *
     * @param db Il database SQLite.
     * @param oldVersion Versione corrente del database.
     * @param newVersion Nuova versione del database.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(
            TAG,
            "Upgrade del database da versione $oldVersion a $newVersion. Eliminazione delle tabelle esistenti..."
        )

        // Eliminazione delle tabelle esistenti (drop & create)
        db.execSQL("DROP TABLE IF EXISTS preferenze_esame")
        db.execSQL("DROP TABLE IF EXISTS esame")
        db.execSQL("DROP TABLE IF EXISTS lezione")
        db.execSQL("DROP TABLE IF EXISTS padiglione")

        // Ricreazione del database
        onCreate(db)
        Log.d(TAG, "Upgrade completato: tabelle eliminate e ricreate con successo.")
    }

    companion object {
        private const val TAG = "DatabaseHelper"
        private const val DB_NAME = "1nsubria_survive.db"
        private const val DB_VERSION = 2
    }
}
