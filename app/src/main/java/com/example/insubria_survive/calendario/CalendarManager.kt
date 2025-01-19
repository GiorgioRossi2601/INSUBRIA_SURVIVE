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
 *
 * @property context Il contesto Android.
 * @property credential La credenziale dell’account Google.
 */
class CalendarManager(
    private val context: Context,
    private val credential: GoogleAccountCredential
) {
    companion object {
        private const val TAG = "CalendarManager"
    }

    // Inizializziamo il client Calendar utilizzando NetHttpTransport e GsonFactory
    private val httpTransport: HttpTransport = NetHttpTransport()
    private val calendarService: Calendar = Calendar.Builder(
        httpTransport,
        GsonFactory.getDefaultInstance(),
        credential
    )
        .setApplicationName("INSUBRIA_SURVIVE")
        .build()

    /**
     * Crea un evento relativo ad un [Esame] e lo inserisce nel calendario "primary" dell’utente.
     *
     * @param esame L’esame da inserire come evento.
     * @param callback Funzione di callback che restituisce un flag di successo e informazioni (es. link all’evento o messaggio d’errore).
     */
    fun addExamToCalendar(esame: Esame, callback: (success: Boolean, info: String?) -> Unit) {
        val event = CalendarHelper.createEventFromExam(esame)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val createdEvent: Event = calendarService.events().insert("primary", event).execute()
                Log.d(TAG, "Evento creato: ${createdEvent.htmlLink}")
                callback(true, createdEvent.htmlLink)
            } catch (e: Exception) {
                Log.e(TAG, "Errore durante la creazione dell'evento: ${e.message}", e)
                callback(false, e.message)
            }
        }
    }

    /**
     * Crea un evento relativo a una [Lezione] e lo inserisce nel calendario "primary" dell’utente.
     *
     * @param lezione La lezione da inserire come evento.
     * @param callback Funzione di callback che restituisce un flag di successo e informazioni (es. link all’evento o messaggio d’errore).
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
        }
    }
}
