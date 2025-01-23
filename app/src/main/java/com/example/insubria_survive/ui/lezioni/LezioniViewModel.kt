package com.example.insubria_survive.ui.lezioni

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Lezione
import com.example.insubria_survive.data.model.LezioniListItem
import com.example.insubria_survive.data.model.LezioniListItem.LessonItem
import com.example.insubria_survive.data.model.LezioniListItem.NoLessonItem
import com.example.insubria_survive.data.model.LezioniListItem.WeekHeader
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel per gestire il caricamento e il raggruppamento delle lezioni.
 *
 * Carica le lezioni dal database locale, le filtra per settimana e anno e genera
 * una lista di [LezioniListItem] contenente un header settimanale e gli item lezioni (o un item "Nessuna lezione").
 */
class LezioniViewModel(private val context: Context) : ViewModel() {

    private val _lessonsListItems = MutableLiveData<List<LezioniListItem>>()
    val lessonsListItems: LiveData<List<LezioniListItem>> get() = _lessonsListItems

    /**
     * Carica le lezioni dal DB locale e raggruppa per settimana.
     *
     * @param weekFilter Numero di settimana da filtrare (se nullo usa la settimana corrente).
     * @param yearFilter Anno da filtrare (se nullo usa l'anno corrente).
     */
    fun loadLezioni(weekFilter: Int? = null, yearFilter: Int? = null) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)
        val filterWeek = weekFilter ?: currentWeek
        val filterYear = yearFilter ?: currentYear

        val repository = LocalDbRepository(context)
        // Recupera tutte le lezioni e le ordina per data di inizio
        val lessons: List<Lezione> = repository.getAllLezioni().sortedBy { it.data_inizio?.toDate() }

        // Filtra le lezioni in base alla settimana e all'anno
        val filteredLessons = lessons.filter { lesson ->
            val calLesson = Calendar.getInstance(Locale.getDefault()).apply {
                time = lesson.data_inizio?.toDate() ?: Date()
            }
            val lessonWeek = calLesson.get(Calendar.WEEK_OF_YEAR)
            val lessonYear = calLesson.get(Calendar.YEAR)
            (lessonWeek == filterWeek) && (lessonYear == filterYear)
        }

        // Costruisce l'header settimanale con il range lunedì-domenica
        val (monday, sunday) = getWeekRange(filterYear, filterWeek)
        val headerTitle = "${formatDate(monday)} - ${formatDate(sunday)}"
        val listItems = mutableListOf<LezioniListItem>().apply {
            add(WeekHeader(headerTitle))
            if (filteredLessons.isEmpty()) {
                add(NoLessonItem)
            } else {
                filteredLessons.forEach { lesson ->
                    add(LessonItem(lesson))
                }
            }
        }
        _lessonsListItems.postValue(listItems)
    }

    /**
     * Calcola il range settimanale (lunedì e domenica) per un dato anno e settimana.
     *
     * @return Una coppia [Pair] contenente le date di lunedì e domenica.
     */
    private fun getWeekRange(year: Int, weekOfYear: Int): Pair<Date, Date> {
        val calendar = Calendar.getInstance(Locale.getDefault()).apply {
            clear()
            set(Calendar.YEAR, year)
            set(Calendar.WEEK_OF_YEAR, weekOfYear)
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val monday = calendar.time
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val sunday = calendar.time
        return Pair(monday, sunday)
    }

    /**
     * Formatta una data nel formato "dd/MM/yyyy".
     */
    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }
}
