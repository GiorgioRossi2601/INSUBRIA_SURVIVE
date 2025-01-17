package com.example.insubria_survive.calendario

import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Lezione
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.util.*

/**
 * Oggetto helper per la creazione di oggetti Event a partire da un Esame.
 */
object CalendarHelper {
    private const val DEFAULT_EVENT_DURATION_MILLIS = 2 * 60 * 60 * 1000L  // 2 ore

    /**
     * Converte un Esame in un oggetto Event della Google Calendar API.
     *
     * @param esame L’oggetto Esame da convertire.
     * @return Event L’oggetto Event da inserire nel calendario.
     */
    fun createEventFromExam(esame: Esame): Event {
        val event = Event()
        // Imposta il titolo dell’evento con il nome del corso (o "Esame" se non disponibile)
        event.summary = esame.corso ?: "Esame"
        // Descrizione dell’evento (puoi arricchirla con ulteriori informazioni come aula e padiglione)
        event.description = "Esame programmato. Aula: ${esame.aula ?: "ND"}, Padiglione: ${esame.padiglione ?: "ND"}"

        // Convertiamo la data dell'esame (se presente) in un oggetto DateTime (utilizzato dalla Calendar API)
        val startDateTime = getEventDateTime(esame.data?.toDate())
        // Calcoliamo la fine dell’evento aggiungendo una durata predefinita (es. 2 ore)
        val endDateTime = calculateEndTime(startDateTime)

        // Impostiamo i tempi di inizio e fine nell’evento, indicando anche il fuso orario
        event.start = EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone(TimeZone.getDefault().id)
        event.end = EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone(TimeZone.getDefault().id)

        return event
    }

    /**
     * Converte una Lezione in un oggetto Event della Google Calendar API.
     */
    fun createEventFromLesson(lezione: Lezione): Event {
        val event = Event()
        // Il titolo dell'evento è il nome del corso; se non disponibile, "Lezione"
        event.summary = lezione.corso ?: "Lezione"
        // La descrizione include ulteriori informazioni, ad es. aula e padiglione
        event.description = "Lezione programmata. Aula: ${lezione.aula ?: "ND"}, Padiglione: ${lezione.padiglione ?: "ND"}"

        // L'orario di inizio viene derivato da data_inizio della lezione
        val startDateTime = getEventDateTime(lezione.data_inizio?.toDate())
        // Se data_fine è presente nella lezione la utilizziamo, altrimenti aggiungiamo una durata di default
        val endDateTime = lezione.data_fine?.toDate()?.let { DateTime(it) } ?: calculateEndTime(startDateTime)

        event.start = EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone(TimeZone.getDefault().id)
        event.end = EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone(TimeZone.getDefault().id)
        return event
    }

    /**
     * Converte una data (java.util.Date) in un oggetto DateTime richiesto dalla Google Calendar API.
     * Se la data è null, utilizza la data corrente.
     */
    private fun getEventDateTime(date: Date?): DateTime {
        val d = date ?: Date()
        return DateTime(d)
    }

    /**
     * Calcola la data/ora di fine evento aggiungendo una durata predefinita all’orario di inizio.
     */
    private fun calculateEndTime(start: DateTime): DateTime {
        val millis = start.value + DEFAULT_EVENT_DURATION_MILLIS
        return DateTime(millis)
    }
}
