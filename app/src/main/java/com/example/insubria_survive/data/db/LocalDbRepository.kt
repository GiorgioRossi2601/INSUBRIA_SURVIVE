package com.example.insubria_survive.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Lezione
import com.example.insubria_survive.data.model.Padiglione
import com.example.insubria_survive.data.model.Preferenza
import com.example.insubria_survive.utils.UtilsMethod
import com.google.firebase.firestore.GeoPoint

/**
 * Repository per gestire le operazioni sul database locale.
 * Contiene metodi per l'inserimento, l'aggiornamento e il recupero dei dati
 * dalle tabelle "esame", "preferenze_esame" e "lezione".
 *
 * @param context Il contesto Android.
 */
class LocalDbRepository(context: Context) {

    // Istanza di DatabaseHelper per gestire l'accesso al DB
    private val dbHelper = DatabaseHelper(context)

    companion object {
        private const val TAG = "LocalDbRepository"
    }

    ////////////////////////////////
    // Gestione tabella "esame"
    ////////////////////////////////

    /**
     * Inserisce o aggiorna un [Esame] nella tabella "esame".
     *
     * Converte il Timestamp (Firebase) in una stringa secondo il formato locale.
     *
     * @param esame L'oggetto [Esame] da salvare.
     */
    fun insertOrUpdateEsame(esame: Esame) {
        val db: SQLiteDatabase = dbHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("id_esame", esame.id)
            put("corso", esame.corso)
            put("data", UtilsMethod().firebaseTimestampToString(esame.data!!))
            put("aula", esame.aula)
            put("padiglione", esame.padiglione)
        }

        Log.d(TAG, "Inserimento/aggiornamento esame: $contentValues")
        db.insertWithOnConflict("esame", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
        Log.d(TAG, "Operazione completata: esame salvato con successo.")
    }

    /**
     * Restituisce la lista di tutti gli [Esame] salvati nella tabella "esame".
     *
     * @return La lista degli esami presenti nel DB.
     */
    fun getAllEsami(): List<Esame> {
        val db = dbHelper.readableDatabase
        val esamiList = mutableListOf<Esame>()
        Log.d(TAG, "Esecuzione query per recuperare tutti gli esami.")

        // Gestione automatica della chiusura del cursore con 'use'
        db.rawQuery("SELECT * FROM esame", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndexOrThrow("id_esame"))
                    val corso = cursor.getString(cursor.getColumnIndexOrThrow("corso"))
                    val dataStr = cursor.getString(cursor.getColumnIndexOrThrow("data"))
                    val aula = cursor.getString(cursor.getColumnIndexOrThrow("aula"))
                    val padiglione = cursor.getString(cursor.getColumnIndexOrThrow("padiglione"))

                    val esame = Esame(
                        id = id,
                        corso = corso,
                        data = UtilsMethod().stringToTimestamp(dataStr),
                        aula = aula,
                        padiglione = padiglione
                    )
                    esamiList.add(esame)
                } while (cursor.moveToNext())
            } else {
                Log.d(TAG, "Nessun esame trovato nella tabella 'esame'.")
            }
        }
        Log.d(TAG, "Recuperati ${esamiList.size} esami dalla tabella 'esame'.")
        return esamiList
    }

    ////////////////////////////////
    // Gestione tabella "preferenze_esame"
    ////////////////////////////////

    /**
     * Inserisce o aggiorna una [Preferenza] nella tabella "preferenze_esame".
     * Se una preferenza per (esame_codice, utente_username) esiste già, viene sostituita.
     *
     * @param preferenza L'oggetto [Preferenza] da salvare.
     */
    fun insertOrUpdatePreferenza(preferenza: Preferenza) {
        val db: SQLiteDatabase = dbHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("esame_codice", preferenza.esame_codice)
            put("utente_username", preferenza.utente_codice)
            put("stato", preferenza.stato)
        }

        Log.d(TAG, "Inserimento/aggiornamento preferenza: $contentValues")
        db.insertWithOnConflict(
            "preferenze_esame",
            null,
            contentValues,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        Log.d(TAG, "Operazione completata: preferenza salvata con successo.")
    }

    /**
     * Restituisce una lista di [Preferenza] filtrate in base allo [stato] e all'[username].
     *
     * @param stato Il valore dello stato da usare come filtro.
     * @param username L'username dell'utente.
     * @return La lista delle preferenze corrispondenti.
     */
    fun getPreferenzeByStato(stato: String, username: String): List<Preferenza> {
        val db = dbHelper.readableDatabase
        val preferenzeList = mutableListOf<Preferenza>()
        Log.d(TAG, "Esecuzione query per recuperare le preferenze con stato='$stato' per l'utente '$username'.")

        db.rawQuery(
            "SELECT * FROM preferenze_esame WHERE stato = ? AND utente_username = ?",
            arrayOf(stato, username)
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
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
        }
        Log.d(TAG, "Recuperate ${preferenzeList.size} preferenze per stato='$stato' e utente='$username'.")
        return preferenzeList
    }

    /**
     * Restituisce una [Preferenza] per un determinato esame e utente, se esiste.
     *
     * @param esameCodice Il codice dell'esame.
     * @param username L'username dell'utente.
     * @return L'oggetto [Preferenza] se trovato, altrimenti null.
     */
    fun getPreferenzaByEsameAndUser(esameCodice: String?, username: String): Preferenza? {
        val db = dbHelper.readableDatabase
        Log.d(TAG, "Esecuzione query per recuperare la preferenza per esameCodice='$esameCodice' e utente='$username'.")

        db.rawQuery(
            "SELECT * FROM preferenze_esame WHERE esame_codice = ? AND utente_username = ?",
            arrayOf(esameCodice, username)
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_preferenza")).toString()
                val esameCodiceDb = cursor.getString(cursor.getColumnIndexOrThrow("esame_codice"))
                val utenteUsername = cursor.getString(cursor.getColumnIndexOrThrow("utente_username"))
                val statoValue = cursor.getString(cursor.getColumnIndexOrThrow("stato"))

                Log.d(TAG, "Preferenza trovata: id=$id, esame_codice=$esameCodiceDb, utente=$utenteUsername, stato=$statoValue")
                return Preferenza(id, esameCodiceDb, utenteUsername, statoValue)
            } else {
                Log.d(TAG, "Nessuna preferenza trovata per esameCodice='$esameCodice' e utente='$username'.")
            }
        }
        return null
    }

    ////////////////////////////////
    // Gestione tabella "lezione"
    ////////////////////////////////

    /**
     * Inserisce o aggiorna una [Lezione] nella tabella "lezione".
     *
     * @param lezione L'oggetto [Lezione] da salvare.
     */
    fun insertOrUpdateLezione(lezione: Lezione) {
        val db = dbHelper.writableDatabase

        Log.d(TAG, "Inserimento/aggiornamento lezione: $lezione")
        val contentValues = ContentValues().apply {
            put("id_lezione", lezione.id)
            put("corso", lezione.corso)
            put("data_inizio", UtilsMethod().firebaseTimestampToString(lezione.data_inizio!!))
            put("data_fine", UtilsMethod().firebaseTimestampToString(lezione.data_fine!!))
            put("aula", lezione.aula)
            put("padiglione", lezione.padiglione)
        }
        Log.d(TAG, "Contenuto da inserire/aggiornare: $contentValues")
        db.insertWithOnConflict("lezione", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
    }

    /**
     * Recupera tutte le [Lezione] salvate nella tabella "lezione".
     *
     * @return La lista delle lezioni presenti nel DB.
     */
    fun getAllLezioni(): List<Lezione> {
        val db = dbHelper.readableDatabase
        val lessonsList = mutableListOf<Lezione>()
        Log.d(TAG, "Esecuzione query per recuperare tutte le lezioni.")

        db.rawQuery("SELECT * FROM lezione", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndexOrThrow("id_lezione"))
                    val corso = cursor.getString(cursor.getColumnIndexOrThrow("corso"))
                    val dataInizio = cursor.getString(cursor.getColumnIndexOrThrow("data_inizio"))
                    val dataFine = cursor.getString(cursor.getColumnIndexOrThrow("data_fine"))
                    val aula = cursor.getString(cursor.getColumnIndexOrThrow("aula"))
                    val padiglione = cursor.getString(cursor.getColumnIndexOrThrow("padiglione"))

                    val lezione = Lezione(
                        id = id,
                        corso = corso,
                        data_inizio = UtilsMethod().stringToTimestamp(dataInizio),
                        data_fine = UtilsMethod().stringToTimestamp(dataFine),
                        aula = aula,
                        padiglione = padiglione
                    )
                    lessonsList.add(lezione)
                } while (cursor.moveToNext())
            } else {
                Log.d(TAG, "Nessuna lezione trovata nella tabella 'lezione'.")
            }
        }
        return lessonsList
    }

    ////////////////////////////////
    // Gestione tabella "padiglione"
    ////////////////////////////////

    /**
     * Inserisce o aggiorna un [Padiglione] nella tabella "esame".
     *
     * Converte il Timestamp (Firebase) in una stringa secondo il formato locale.
     *
     * @param padiglione L'oggetto [Padiglione] da salvare.
     */
    fun insertOrUpdatePadiglione(padiglione: Padiglione) {
        val db: SQLiteDatabase = dbHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("id_padiglione", padiglione.id)
            put("codice_padiglione", padiglione.codice_padiglione)
            put("descrizione", padiglione.descrizione)
            put("ora_apertura", padiglione.ora_apertura)
            put("ora_chiusura", padiglione.ora_chiusura)

            if (padiglione.posizione != null) {
                val lat = padiglione.posizione.latitude
                val lng = padiglione.posizione.longitude
                put("posizione", "$lat,$lng")
            }


        }

        Log.d(TAG, "Inserimento/aggiornamento padiglione: $contentValues")
        db.insertWithOnConflict("padiglione", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
        Log.d(TAG, "Operazione completata: padiglione salvato con successo.")
    }

    /**
     * Restituisce la lista di tutti gli [Padiglione] salvati nella tabella "esame".
     *
     * @return La lista degli esami presenti nel DB.
     */
    fun getAllPadiglioni(): List<Padiglione> {
        val db = dbHelper.readableDatabase
        val padiglioniList = mutableListOf<Padiglione>()
        Log.d(TAG, "Esecuzione query per recuperare tutti i padiglioni.")

        // Gestione automatica della chiusura del cursore con 'use'
        db.rawQuery("SELECT * FROM padiglione", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val idPad = cursor.getString(cursor.getColumnIndexOrThrow("id_padiglione"))
                    val codicePad = cursor.getString(cursor.getColumnIndexOrThrow("codice_padiglione"))
                    val descrizionePad = cursor.getString(cursor.getColumnIndexOrThrow("descrizione"))
                    val oraApertura = cursor.getString(cursor.getColumnIndexOrThrow("ora_apertura"))
                    val oraChiusura = cursor.getString(cursor.getColumnIndexOrThrow("ora_chiusura"))
                    val posizioneString = cursor.getString(cursor.getColumnIndexOrThrow("posizione"))
                    // Converto "45.123,9.456" in un GeoPoint
                    val posizionePad: GeoPoint? = if (!posizioneString.isNullOrBlank()) {
                        val parts = posizioneString.split(",")
                        if (parts.size == 2) {
                            val lat = parts[0].toDoubleOrNull() ?: 0.0
                            val lng = parts[1].toDoubleOrNull() ?: 0.0
                            GeoPoint(lat, lng)
                        } else null
                    } else null

                    val padiglione = Padiglione(
                        id = idPad,
                        codice_padiglione = codicePad,
                        descrizione = descrizionePad,
                        ora_apertura = oraApertura,
                        ora_chiusura = oraChiusura,
                        posizione = posizionePad
                    )
                    padiglioniList.add(padiglione)
                } while (cursor.moveToNext())
            } else {
                Log.d(TAG, "Nessun padiglione trovato nella tabella 'padiglione'.")
            }
        }
        Log.d(TAG, "Recuperati ${padiglioniList.size} esami dalla tabella 'padiglione'.")
        return padiglioniList
    }
}
