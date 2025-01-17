package com.example.insubria_survive.calendario

import android.content.Context
import android.util.Log
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Lezione
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Classe che gestisce la comunicazione con la Google Calendar API.
 * Riceve un GoogleAccountCredential e crea un client Calendar utilizzando
 * OkHttpHttpTransport (alternativa a AndroidHttp, che è deprecato).
 */
class CalendarManager(
    private val context: Context,
    private val credential: GoogleAccountCredential
) {
    companion object {
        private val TAG = "CalendarManager"
    }

    // Creiamo il client Calendar utilizzando OkHttpHttpTransport e GsonFactory
    private val httpTransport: HttpTransport = NetHttpTransport()
    private val calendarService: Calendar = Calendar.Builder(
        httpTransport,
        GsonFactory.getDefaultInstance(),
        credential
    ).setApplicationName("INSUBRIA_SURVIVE").build()

    /**
     * Metodo che crea un evento relativo ad un Esame e lo inserisce nel calendario "primary" dell’utente.
     *
     * @param esame L’esame da inserire come evento.
     * @param callback Funzione di callback per comunicare il successo o l’errore dell’operazione.
     */
    fun addExamToCalendar(esame: Esame, callback: (success: Boolean, info: String?) -> Unit) {
        // Utilizziamo il helper per convertire l'esame in un oggetto Event
        val event = CalendarHelper.createEventFromExam(esame)
        // Eseguiamo la chiamata in un thread separato (alternativa: usare Kotlin Coroutines)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Inseriamo l'evento nel calendario "primary"
                val createdEvent: Event = calendarService.events().insert("primary", event).execute()
                Log.d(TAG, "Evento creato: ${createdEvent.htmlLink}")
                // Invoca la callback in caso di successo, ad esempio restituendo il link all’evento
                callback(true, createdEvent.htmlLink)
            } catch (e: Exception) {
                Log.e(TAG, "Errore durante la creazione dell'evento: ${e.message}", e)
                callback(false, e.message)
            }
        }.start()
    }

    /**
     * Aggiunge un evento relativo ad una Lezione nel calendario "primary".
     */
    fun addLessonToCalendar(lezione: Lezione, callback: (success: Boolean, info: String?) -> Unit) {
        val event = CalendarHelper.createEventFromLesson(lezione)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val createdEvent: Event = calendarService.events().insert("primary", event).execute()
                Log.d(TAG, "Evento Lezione creato: ${createdEvent.htmlLink}")
                callback(true, createdEvent.htmlLink)
            } catch (e: Exception) {
                Log.e(TAG, "Errore durante la creazione dell'evento Lezione: ${e.message}", e)
                callback(false, e.message)
            }
        }.start()
    }
}
