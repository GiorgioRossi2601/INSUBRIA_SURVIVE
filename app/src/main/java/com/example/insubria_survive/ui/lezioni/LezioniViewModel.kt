// LezioniViewModel.kt
package com.example.insubria_survive.ui.lezioni

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Lezione
import com.example.insubria_survive.data.model.LezioniListItem
import com.example.insubria_survive.data.model.LezioniListItem.LessonItem
import com.example.insubria_survive.data.model.LezioniListItem.WeekHeader
import java.text.SimpleDateFormat
import java.util.*

class LezioniViewModel(private val context: Context) : ViewModel() {

    private val _lessonsListItems = MutableLiveData<List<LezioniListItem>>()
    val lessonsListItems: LiveData<List<LezioniListItem>> = _lessonsListItems

    init {
        // Carica automaticamente le lezioni all'avvio del ViewModel
        loadLezioni()
    }

    /**
     * Carica le lezioni dal database locale e le raggruppa per settimana.
     */
    fun loadLezioni() {
        val repository = LocalDbRepository(context)
        // Recupera tutte le lezioni e le ordina in base alla data_inizio
        val lezioni: List<Lezione> = repository.getAllLezioni()
            .sortedBy { it.data_inizio?.toDate() } // oppure in base alla stringa se hai già la conversione

        // Raggruppa le lezioni per settimana: la settimana viene estratta dalla data_inizio
        val grouped = lezioni.groupBy { lezione ->
            lezione.data_inizio?.let { getWeekOfYear(it) } ?: -1
        }.toSortedMap()

        // Crea una lista alternata: header della settimana seguito dalle lezioni
        val listItems = mutableListOf<LezioniListItem>()
        for ((weekNumber, lessons) in grouped) {
            val headerTitle = if (weekNumber != -1) "Settimana $weekNumber" else "Data non disponibile"
            listItems.add(WeekHeader(headerTitle))
            lessons.forEach { lesson ->
                listItems.add(LessonItem(lesson))
            }
        }
        _lessonsListItems.postValue(listItems)
    }

    // Estrae il numero della settimana da un Timestamp
    private fun getWeekOfYear(timestamp: com.google.firebase.Timestamp): Int {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = timestamp.toDate()
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }
}
