package com.example.insubria_survive.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Lezione
import com.example.insubria_survive.data.model.Preferenza
import com.example.insubria_survive.utils.UtilsMethod
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Repository per gestire le operazioni sul database locale.
 * Contiene metodi per l'inserimento, l'aggiornamento e il recupero dei dati dalle tabelle "esame" e "preferenze_esame".
 */
class LocalDbRepository(context: Context) {

    // Istanza di DatabaseHelper per gestire l'accesso al DB
    private val dbHelper = DatabaseHelper(context)

    // Tag per il logging
    companion object {
        private const val TAG = "LocalDbRepository"
    }

    ////////////////////////////////
    // Gestione tabella "esame"
    ////////////////////////////////

    /**
     * Inserisce o aggiorna un Esame nella tabella "esame".
     * Converte il Timestamp (Firebase) in una stringa secondo il formato locale.
     *
     * @param esame L'oggetto Esame da salvare.
     */
    fun insertOrUpdateEsame(esame: Esame) {
        // Apertura del database in modalità scrittura
        val db: SQLiteDatabase = dbHelper.writableDatabase

        // Preparazione dei valori da inserire
        val contentValues = ContentValues().apply {
            put("id_esame", esame.id)
            put("corso", esame.corso)
            put("data", UtilsMethod().firebaseTimestampToString(esame.data!!))
            put("aula", esame.aula)
            put("padiglione", esame.padiglione)
        }

        Log.d(TAG, "Inserimento esame: $contentValues")
        // Inserimento o aggiornamento in caso di conflitto (replace)
        db.insertWithOnConflict("esame", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
        //db.close()
        Log.d(TAG, "Operazione completata: esame salvato con successo.")
    }

    /**
     * Restituisce la lista di tutti gli esami salvati nella tabella "esame".
     *
     * @return List<Esame> La lista degli esami presenti nel DB.
     */
    fun getAllEsami(): List<Esame> {
        // Apertura del database in modalità lettura
        val db = dbHelper.readableDatabase
        val esamiList = mutableListOf<Esame>()
        val cursor = db.rawQuery("SELECT * FROM esame", null)
        Log.d(TAG, "Esecuzione query per recuperare tutti gli esami.")

        if (cursor.moveToFirst()) {
            do {
                // Estrazione dei dati di ciascun esame dal cursore
                val id = cursor.getString(cursor.getColumnIndexOrThrow("id_esame"))
                val corso = cursor.getString(cursor.getColumnIndexOrThrow("corso"))
                val dataStr = cursor.getString(cursor.getColumnIndexOrThrow("data"))
                val aula = cursor.getString(cursor.getColumnIndexOrThrow("aula"))
                val padiglione = cursor.getString(cursor.getColumnIndexOrThrow("padiglione"))

                // Creazione dell'oggetto Esame con conversione della data da Stringa a Timestamp
                val esame = Esame(
                    id,
                    corso,
                    UtilsMethod().stringToTimestamp(dataStr),
                    aula,
                    padiglione
                )
                esamiList.add(esame)
            } while (cursor.moveToNext())
        } else {
            Log.d(TAG, "Nessun esame trovato nella tabella 'esame'.")
        }
        cursor.close()
        //db.close()
        Log.d(TAG, "Recuperati ${esamiList.size} esami dalla tabella 'esame'.")
        return esamiList
    }

    ////////////////////////////////
    // Gestione tabella "preferenze_esame"
    ////////////////////////////////

    /**
     * Inserisce o aggiorna una preferenza nella tabella "preferenze_esame".
     * Se una preferenza per (esame_codice, utente_username) esiste già, la sostituisce.
     *
     * @param preferenza L'oggetto Preferenza da salvare.
     */
    fun insertOrUpdatePreferenza(preferenza: Preferenza) {
        // Apertura del database in modalità scrittura
        val db: SQLiteDatabase = dbHelper.writableDatabase

        // Preparazione dei valori da inserire
        val contentValues = ContentValues().apply {
            put("esame_codice", preferenza.esame_codice)
            put("utente_username", preferenza.utente_codice)
            put("stato", preferenza.stato)
        }

        Log.d(TAG, "Inserimento o Aggiornamento preferenza: $contentValues")
        // Inserimento o aggiornamento in caso di conflitto (chiave unica)
        db.insertWithOnConflict(
            "preferenze_esame",
            null,
            contentValues,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        //db.close()
        Log.d(TAG, "Operazione completata: preferenza salvata con successo.")
    }

    /**
     * Restituisce una lista di Preferenze filtrate in base allo stato e all'username.
     *
     * @param stato Il valore dello stato da usare come filtro.
     * @param username L'username dell'utente.
     * @return List<Preferenza> La lista delle preferenze corrispondenti.
     */
    fun getPreferenzeByStato(stato: String, username: String): List<Preferenza> {
        // Apertura del database in modalità lettura
        val db = dbHelper.readableDatabase
        val preferenzeList = mutableListOf<Preferenza>()
        Log.d(TAG, "Esecuzione query per recuperare le preferenze con stato='$stato' per l'utente '$username'.")

        val cursor = db.rawQuery(
            "SELECT * FROM preferenze_esame WHERE stato = ? AND utente_username = ?",
            arrayOf(stato, username)
        )

        if (cursor.moveToFirst()) {
            do {
                // Estrazione dei dati per ciascuna preferenza dal cursore
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_preferenza")).toString()
                val esameCodice = cursor.getString(cursor.getColumnIndexOrThrow("esame_codice"))
                val utenteUsername = cursor.getString(cursor.getColumnIndexOrThrow("utente_username"))
                val statoValue = cursor.getString(cursor.getColumnIndexOrThrow("stato"))

                val preferenza = Preferenza(id, esameCodice, utenteUsername, statoValue)
                preferenzeList.add(preferenza)
            } while (cursor.moveToNext())
        } else {
            Log.d(TAG, "Nessuna preferenza trovata per stato='$stato' e utente='$username'.")
        }
        cursor.close()
        //db.close()
        Log.d(TAG, "Recuperate ${preferenzeList.size} preferenze per stato='$stato' e utente='$username'.")
        return preferenzeList
    }

    /**
     * Restituisce una preferenza per un determinato esame e utente, se esiste.
     *
     * @param esameCodice Il codice dell'esame.
     * @param username L'username dell'utente.
     * @return Preferenza? L'oggetto Preferenza se trovato, altrimenti null.
     */
    fun getPreferenzaByEsameAndUser(esameCodice: String?, username: String): Preferenza? {
        // Apertura del database in modalità lettura
        val db = dbHelper.readableDatabase
        Log.d(TAG, "Esecuzione query per recuperare la preferenza per esameCodice='$esameCodice' e utente='$username'.")

        val cursor = db.rawQuery(
            "SELECT * FROM preferenze_esame WHERE esame_codice = ? AND utente_username = ?",
            arrayOf(esameCodice, username)
        )
        var preferenza: Preferenza? = null
        if (cursor.moveToFirst()) {
            // Estrazione dei dati della preferenza trovata
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_preferenza")).toString()
            val esameCodiceDb = cursor.getString(cursor.getColumnIndexOrThrow("esame_codice"))
            val utenteUsername = cursor.getString(cursor.getColumnIndexOrThrow("utente_username"))
            val statoValue = cursor.getString(cursor.getColumnIndexOrThrow("stato"))

            Log.d(TAG, "Preferenza trovata: id=$id, esame_codice=$esameCodiceDb, utente=$utenteUsername, stato=$statoValue")
            preferenza = Preferenza(id, esameCodiceDb, utenteUsername, statoValue)
        } else {
            Log.d(TAG, "Nessuna preferenza trovata per esameCodice='$esameCodice' e utente='$username'.")
        }
        cursor.close()
        //db.close()
        return preferenza
    }

    ////////////////////////////////
    // Gestione tabella "lezione"
    ////////////////////////////////

    // Inserisce o aggiorna una Lezione nella tabella "lezioni"
    fun insertOrUpdateLezione(lezione: Lezione) {
        val db = dbHelper.writableDatabase

        // Preparazione dei valori da inserire
        Log.d(TAG, "Inserimento/aggiornamento lezione: $lezione")

        val contentValues = ContentValues().apply {
            put("id_lezione", lezione.id)
            put("corso", lezione.corso)
            put("data_inizio", UtilsMethod().firebaseTimestampToString(lezione.data_inizio!!))
            put("data_fine", UtilsMethod().firebaseTimestampToString(lezione.data_fine!!))
            put("aula", lezione.aula)
            put("padiglione", lezione.padiglione)
        }

        Log.d(TAG, "Inserimento/aggiornamento lezione: $contentValues")
        db.insertWithOnConflict("lezione", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // Recupera tutte le lezioni salvate
    fun getAllLezioni(): List<Lezione> {
        val db = dbHelper.readableDatabase
        val lessonsList = mutableListOf<Lezione>()
        val cursor = db.rawQuery("SELECT * FROM lezione", null)
        Log.d(TAG, "Esecuzione query per recuperare tutte le lezioni.")

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow("id_lezione"))
                val corso = cursor.getString(cursor.getColumnIndexOrThrow("corso"))
                val dataInizio = cursor.getString(cursor.getColumnIndexOrThrow("data_inizio"))
                val dataFine = cursor.getString(cursor.getColumnIndexOrThrow("data_fine"))
                val aula = cursor.getString(cursor.getColumnIndexOrThrow("aula"))
                val padiglione = cursor.getString(cursor.getColumnIndexOrThrow("padiglione"))

                val lezione = Lezione(id, corso, UtilsMethod().stringToTimestamp(dataInizio), UtilsMethod().stringToTimestamp(dataFine), aula, padiglione)
                lessonsList.add(lezione)
            } while (cursor.moveToNext())
        } else {
            Log.d(TAG, "Nessuna lezione trovata nella tabella 'lezioni'.")
        }
        cursor.close()
        return lessonsList
    }




}
