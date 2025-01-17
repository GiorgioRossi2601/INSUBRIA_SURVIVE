package com.example.insubria_survive.data.model

sealed class LezioniListItem {
    data class WeekHeader(val title: String) : LezioniListItem()
    data class LessonItem(val lesson: Lezione) : LezioniListItem()
}
