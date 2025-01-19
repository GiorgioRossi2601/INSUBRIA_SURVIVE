package com.example.insubria_survive.calendario

import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Lezione
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.util.Date
import java.util.TimeZone

/**
 * Oggetto helper per la creazione di oggetti [Event] della Google Calendar API
 * a partire da un [Esame] o una [Lezione].
 */
object CalendarHelper {
    private const val DEFAULT_EVENT_DURATION_MILLIS = 2 * 60 * 60 * 1000L // 2 ore

    /**
     * Converte un [Esame] in un oggetto [Event] della Google Calendar API.
     *
     * @param esame L’oggetto [Esame] da convertire.
     * @return Un [Event] corrispondente all’esame.
     */
    fun createEventFromExam(esame: Esame): Event {
        val event = Event().apply {
            summary = esame.corso ?: "Esame"
            description = "Esame programmato. Aula: ${esame.aula ?: "ND"}, Padiglione: ${esame.padiglione ?: "ND"}"
        }

        val startDateTime = esame.data?.toDate()?.let(::getEventDateTime) ?: getEventDateTime(null)
        val endDateTime = calculateEndTime(startDateTime)

        event.start = EventDateTime().apply {
            dateTime = startDateTime
            timeZone = TimeZone.getDefault().id
        }
        event.end = EventDateTime().apply {
            dateTime = endDateTime
            timeZone = TimeZone.getDefault().id
        }

        return event
    }

    /**
     * Converte una [Lezione] in un oggetto [Event] della Google Calendar API.
     *
     * @param lezione L’oggetto [Lezione] da convertire.
     * @return Un [Event] corrispondente alla lezione.
     */
    fun createEventFromLesson(lezione: Lezione): Event {
        val event = Event().apply {
            summary = lezione.corso ?: "Lezione"
            description = "Lezione programmata. Aula: ${lezione.aula ?: "ND"}, Padiglione: ${lezione.padiglione ?: "ND"}"
        }

        val startDateTime = lezione.data_inizio?.toDate()?.let(::getEventDateTime) ?: getEventDateTime(null)
        val endDateTime = lezione.data_fine?.toDate()?.let { DateTime(it) } ?: calculateEndTime(startDateTime)

        event.start = EventDateTime().apply {
            dateTime = startDateTime
            timeZone = TimeZone.getDefault().id
        }
        event.end = EventDateTime().apply {
            dateTime = endDateTime
            timeZone = TimeZone.getDefault().id
        }

        return event
    }

    /**
     * Converte una data [Date] in un oggetto [DateTime] utilizzato dalla Calendar API.
     * Se [date] è null, viene utilizzata la data corrente.
     *
     * @param date La data da convertire.
     * @return Il corrispondente [DateTime].
     */
    private fun getEventDateTime(date: Date?): DateTime {
        val d = date ?: Date()
        return DateTime(d)
    }

    /**
     * Calcola la data/ora di fine evento aggiungendo una durata predefinita all’orario di inizio.
     *
     * @param start L’orario di inizio dell’evento.
     * @return L’orario di fine dell’evento.
     */
    private fun calculateEndTime(start: DateTime): DateTime {
        return DateTime(start.value + DEFAULT_EVENT_DURATION_MILLIS)
    }
}
