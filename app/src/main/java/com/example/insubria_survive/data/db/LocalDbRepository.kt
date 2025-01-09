package com.example.insubria_survive.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.insubria_survive.data.model.Esame
import java.text.SimpleDateFormat
import java.util.Locale

class LocalDbRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    /**
     * Inserisce o aggiorna un Esame nella tabella "esame".
     * Converte il Timestamp (Firebase) in una stringa locale.
     */
    fun insertOrUpdateEsame(esame: Esame) {
        val db: SQLiteDatabase = dbHelper.writableDatabase

        // Se esame.data è un Timestamp, lo convertiamo in stringa
        val dataString = esame.data?.toDate()?.let { date ->
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
        } ?: ""

        val contentValues = ContentValues().apply {
            put("id_esame", esame.id)
            put("corso", esame.corso)
            put("data", dataString)
            put("aula", esame.aula)
            put("padiglione", esame.padiglione)
        }

        Log.d("LocalDbRepository", "Salvataggio esame id=${esame.id}, corso=${esame.corso}")
        // Se esiste già quell'ID, lo rimpiazza
        db.insertWithOnConflict(
            "esame",
            null,
            contentValues,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        db.close()
    }
}
