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
import com.google.firebase.Timestamp
import java.util.*

class LezioniViewModel(private val context: Context) : ViewModel() {

    private val _lessonsListItems = MutableLiveData<List<LezioniListItem>>()
    val lessonsListItems: LiveData<List<LezioniListItem>> = _lessonsListItems

    /**
     * Carica le lezioni dal DB locale e raggruppa per settimana.
     * Se weekFilter è diverso da -1, viene applicato un filtro per mantenere solo le lezioni della settimana indicata.
     */
    fun loadLezioni(weekFilter: Int = -1) {
        val repository = LocalDbRepository(context)
        // Recupera tutte le lezioni ordinate per data di inizio
        val lezioni: List<Lezione> = repository.getAllLezioni()
            .sortedBy { it.data_inizio?.toDate() }

        // Se è stato applicato un filtro per settimana, filtra le lezioni
        val filteredLezioni = if (weekFilter != -1) {
            lezioni.filter { lezione ->
                lezione.data_inizio?.let { getWeekOfYear(it) } == weekFilter
            }
        } else {
            lezioni
        }

        // Raggruppa le lezioni per settimana
        val grouped = filteredLezioni.groupBy { lezione ->
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
    private fun getWeekOfYear(timestamp: Timestamp): Int {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = timestamp.toDate()
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }
}
