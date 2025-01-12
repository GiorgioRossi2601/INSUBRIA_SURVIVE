package com.example.insubria_survive.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Preferenza
import com.example.insubria_survive.data.model.Stato
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class LocalDbRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    /**
     * Inserisce o aggiorna un Esame nella tabella "esame".
     * Converte il Timestamp (Firebase) in una stringa locale.
     */
    ////////////////////////////////
    // Gestione tabella "esame"
    ////////////////////////////////

    fun insertOrUpdateEsame(esame: Esame) {
        val db: SQLiteDatabase = dbHelper.writableDatabase

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

        Log.d("LocalDbRepository", "Salvataggio esame $contentValues")
        db.insertWithOnConflict("esame", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }
    fun getAllEsami(): List<Esame> {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val esamiList = mutableListOf<Esame>()
        val cursor = db.rawQuery("SELECT * FROM esame", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow("id_esame"))
                val corso = cursor.getString(cursor.getColumnIndexOrThrow("corso"))
                val dataStr = cursor.getString(cursor.getColumnIndexOrThrow("data"))
                val aula = cursor.getString(cursor.getColumnIndexOrThrow("aula"))
                val padiglione = cursor.getString(cursor.getColumnIndexOrThrow("padiglione"))
                // Non abbiamo il campo "stato" in questa tabella, pertanto gli esami salvati qui non hanno stato.
                val esame = Esame(id, corso, stringToTimestamp(dataStr), aula, padiglione)
                esamiList.add(esame)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return esamiList
    }


    ////////////////////////////////
    // Gestione tabella "preferenze_esame"
    ////////////////////////////////

    /**
     * Inserisce o aggiorna una preferenza nella tabella "preferenze_esame".
     * Se già esiste una preferenza per un dato esame e utente, la sostituisce.
     */
    fun insertOrUpdatePreferenza(preferenza: Preferenza) {
        val db: SQLiteDatabase = dbHelper.writableDatabase

        val contentValues = ContentValues().apply {
            // Se usi un AUTO_INCREMENT per id_preferenza, non serve impostarlo
            put("esame_codice", preferenza.esame_codice)
            put("utente_username", preferenza.utente_codice)
            put("stato", preferenza.stato)
        }

        Log.d("LocalDbRepository", "Salvataggio preferenza: $contentValues")
        // Usiamo CONFLICT_REPLACE basato su un vincolo unico (potrebbe essere definito con esame_codice e utente_username)
        db.insertWithOnConflict("preferenze_esame", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    /**
     * Restituisce una lista di Preferenze filtrate in base allo stato e all'username.
     */
    fun getPreferenzeByStato(stato: String, username: String): List<Preferenza> {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val preferenzeList = mutableListOf<Preferenza>()
        val cursor = db.rawQuery(
            "SELECT * FROM preferenze_esame WHERE stato = ? AND utente_username = ?",
            arrayOf(stato, username)
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_preferenza")).toString()
                val esameCodice = cursor.getString(cursor.getColumnIndexOrThrow("esame_codice"))
                val utenteUsername = cursor.getString(cursor.getColumnIndexOrThrow("utente_username"))
                val statoValue = cursor.getString(cursor.getColumnIndexOrThrow("stato"))
                val preferenza = Preferenza(id, esameCodice, utenteUsername, statoValue)
                preferenzeList.add(preferenza)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return preferenzeList
    }

    // Nuovo metodo per recuperare una preferenza in base all'esame e all'utente
    fun getPreferenzaByEsameAndUser(esameCodice: String?, username: String): Preferenza? {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM preferenze_esame WHERE esame_codice = ? AND utente_username = ?",
            arrayOf(esameCodice, username)
        )
        var preferenza: Preferenza? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_preferenza")).toString()
            val esameCodiceDb = cursor.getString(cursor.getColumnIndexOrThrow("esame_codice"))
            val utenteUsername = cursor.getString(cursor.getColumnIndexOrThrow("utente_username"))
            val statoValue = cursor.getString(cursor.getColumnIndexOrThrow("stato"))
            Log.d("LocalDbRepository", "getPreferenzaByEsameAndUser: trovato preferenza: id=$id, esame_codice=$esameCodiceDb, utente=$utenteUsername, stato=$statoValue")
            preferenza = Preferenza(id, esameCodiceDb, utenteUsername, statoValue)

        }else {
            Log.d("LocalDbRepository", "getPreferenzaByEsameAndUser: nessuna preferenza trovata per esameCodice=$esameCodice e utente=$username")
        }
        cursor.close()
        db.close()
        return preferenza
    }



    // Mantieni eventuali metodi esistenti (ad esempio, stringToTimestamp) se necessario.
    private fun stringToTimestamp(dateString: String): Timestamp? {
        if (dateString.isEmpty()) return null
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = sdf.parse(dateString)
            Timestamp(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
