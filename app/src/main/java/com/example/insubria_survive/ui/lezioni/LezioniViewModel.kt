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

class LezioniViewModel(private val context: Context) : ViewModel() {

    private val _lessonsListItems = MutableLiveData<List<LezioniListItem>>()
    val lessonsListItems: LiveData<List<LezioniListItem>> = _lessonsListItems

    /**
     * Carica le lezioni dal DB locale e raggruppa per settimana.
     *
     * Di default viene utilizzata la settimana e l'anno correnti.
     * Per ogni settimana genera un header nel formato "dd/MM/yyyy - dd/MM/yyyy".
     *
     * Se per quella settimana non sono presenti lezioni, viene aggiunto l’item NoLessonItem.
     *
     * @param weekFilter se non nullo, utilizza questo numero di settimana; altrimenti usa la settimana corrente.
     * @param yearFilter se non nullo, utilizza questo anno; altrimenti usa l'anno corrente.
     */
    fun loadLezioni(weekFilter: Int? = null, yearFilter: Int? = null) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)
        val filterWeek = weekFilter ?: currentWeek
        val filterYear = yearFilter ?: currentYear

        val repository = LocalDbRepository(context)
        val lessons: List<Lezione> = repository.getAllLezioni()
            .sortedBy { it.data_inizio?.toDate() }

        // Filtra le lezioni in base a settimana e anno
        val filteredLessons = lessons.filter { lesson ->
            val calLesson = Calendar.getInstance(Locale.getDefault())
            calLesson.time = lesson.data_inizio?.toDate() ?: Date()
            val lessonWeek = calLesson.get(Calendar.WEEK_OF_YEAR)
            val lessonYear = calLesson.get(Calendar.YEAR)
            (lessonWeek == filterWeek) && (lessonYear == filterYear)
        }

        // Costruiamo l'header della settimana usando il filterYear e filterWeek
        val (monday, sunday) = getWeekRange(filterYear, filterWeek)
        val headerTitle = "${formatDate(monday)} - ${formatDate(sunday)}"
        val listItems = mutableListOf<LezioniListItem>()
        listItems.add(WeekHeader(headerTitle))
        if (filteredLessons.isEmpty()) {
            listItems.add(NoLessonItem)
        } else {
            filteredLessons.forEach { lesson ->
                listItems.add(LessonItem(lesson))
            }
        }
        _lessonsListItems.postValue(listItems)
    }


    /**
     * Calcola il range della settimana (lunedì e domenica) per un certo anno e numero di settimana.
     */
    private fun getWeekRange(year: Int, weekOfYear: Int): Pair<Date, Date> {
        val calendar = Calendar.getInstance(Locale.getDefault())
        // Pulizia dei campi
        calendar.clear()
        // Imposta l'anno e la settimana (usando il primo giorno della settimana come lunedì)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear)
        calendar.firstDayOfWeek = Calendar.MONDAY
        // Imposta lunedì
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val monday = calendar.time

        // Imposta domenica
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val sunday = calendar.time

        return Pair(monday, sunday)
    }

    /**
     * Formattta una data nel formato "dd/MM/yyyy".
     */
    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    /**
     * Metodo già presente per ottenere il numero della settimana da un Timestamp.
     */
    private fun getWeekOfYear(timestamp: Timestamp): Int {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = timestamp.toDate()
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }
}