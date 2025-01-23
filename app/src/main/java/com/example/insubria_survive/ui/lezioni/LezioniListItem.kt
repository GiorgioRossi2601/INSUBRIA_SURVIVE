package com.example.insubria_survive.ui.lezioni

import com.example.insubria_survive.data.model.Lezione

/**
 *
 * */

sealed class LezioniListItem {
    data class WeekHeader(val title: String) : LezioniListItem()
    data class LessonItem(val lesson: Lezione) : LezioniListItem()
    object NoLessonItem : LezioniListItem() // Da usare se in una settimana non ci sono lezioni
}
